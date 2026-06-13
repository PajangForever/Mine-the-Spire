package forever.pajang.minethespire.content.specials;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.entity.OrbEntity;
import forever.pajang.minethespire.content.entity.DarkOrbEntity;
import forever.pajang.minethespire.content.entity.FrostOrbEntity;
import forever.pajang.minethespire.content.entity.LightningOrbEntity;
import forever.pajang.minethespire.content.entity.PlasmaOrbEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class OrbManager {
    public static final MapCodec<OrbManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            OrbSequence.CODEC.fieldOf("orbs").forGetter(OrbManager::getOrbs)
    ).apply(instance, OrbManager::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OrbManager> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private static final double MIN_ATTACK_SPEED = 0.01D;

    private final OrbSequence orbs;
    private final Map<UUID, Long> nextEffectTicks = new HashMap<>();
    private final Map<Class<? extends OrbEntity>, Integer> scheduledGroupSizes = new HashMap<>();
    private transient LivingEntity owner;
    private int lastMaxOrbs = -1;

    public OrbManager() {
        this.orbs = new OrbSequence();
    }

    public OrbManager(LivingEntity owner) {
        this();
        this.owner = owner;
    }

    private OrbManager(OrbSequence orbs) {
        this.orbs = orbs;
    }

//    private void encode(RegistryFriendlyByteBuf buf) {
//        orbs.encode(buf);
//    }
//
//    private static OrbManager decode(RegistryFriendlyByteBuf buf) {
//        OrbSequence orbs = OrbSequence.decode(buf);
//        return new OrbManager(orbs);
//    }

    public static OrbManager get(LivingEntity owner) {
        OrbManager manager = owner.getData(ModAttachments.ORB_MANAGER);
        manager.owner = owner;
        return manager;
    }

    private List<OrbEntity> getOwnedOrbs() {
        if (owner == null) {
            return List.of();
        }

        ArrayList<OrbEntity> result = new ArrayList<>();
        for (UUID uuid : orbs.uuids()) {
            Entity entity = owner.level().getEntityInAnyDimension(uuid);
            if (entity instanceof OrbEntity orb && orb.level() == owner.level() && !orb.isRemoved() && orb.isOwnedBy(owner)) {
                result.add(orb);
            }
            else {
                orbs.removeOrb(uuid);
            }
        }
        return List.copyOf(result);
    }

    private OrbSequence getOrbs() {
        return orbs;
    }

    public int getMaxOrb() {
        if (owner == null) {
            return 0;
        }
        return Math.max(0, Mth.floor(owner.getAttributeValue(ModAttributes.MAX_ORB)));
    }

    public void removeEntity(OrbEntity orb) {
        orbs.removeOrb(orb.getUUID());
    }

    public boolean containsEntity(OrbEntity orb) {
        return orbs.contains(orb.getUUID());
    }

    public int getIndex(OrbEntity orb) {
        return getOwnedOrbs().indexOf(orb);
    }

    public int getCount() {
        return getOwnedOrbs().size();
    }

    private List<OrbEntity> getActiveOrbs() {
        return getOwnedOrbs().stream()
                .filter(OrbEntity::countsTowardLimit)
                .filter(orb -> !orb.isActivated())
                .toList();
    }

    private List<OrbEntity> getActiveOrbsOfSameType(OrbEntity orb) {
        Class<? extends OrbEntity> type = orb.getClass();
        return getActiveOrbs().stream()
                .filter(activeOrb -> activeOrb.getClass() == type)
                .toList();
    }

    private boolean tryCreate(OrbEntity orb) {
        if (owner.level().isClientSide()) {
            return false;
        }

        int maxOrbs = getMaxOrb();
        if (maxOrbs <= 0) {
            return false;
        }
        int existingCount = getActiveOrbs().size();
        spawnOrb(orb, existingCount, maxOrbs);
        return true;
    }

    public boolean tryCreateLightning() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new LightningOrbEntity(owner.level(), owner));
    }

    public boolean tryCreateFrost() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new FrostOrbEntity(owner.level(), owner));
    }

    public boolean tryCreateDark() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new DarkOrbEntity(owner.level(), owner));
    }

    public boolean tryCreatePlasma() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new PlasmaOrbEntity(owner.level(), owner));
    }

    public void resetSchedule() {
        resetOrbSchedules();
    }

    private void resetOrbSchedules() {
        nextEffectTicks.clear();
        scheduledGroupSizes.clear();
    }

    public void tick() {
        if (owner == null || owner.level().isClientSide()) {
            return;
        }

        int maxOrbs = getMaxOrb();
        if (lastMaxOrbs >= 0 && maxOrbs < lastMaxOrbs) {
            removeOverflowOrbs(maxOrbs);
        }
        lastMaxOrbs = maxOrbs;
    }

    public void tickEntity(OrbEntity orb) {
        if (owner == null || owner.level().isClientSide()) {
            return;
        }

        List<OrbEntity> activeOrbs = getActiveOrbsOfSameType(orb);
        if (activeOrbs.isEmpty()) {
            resetOrbSchedule(orb.getClass());
            return;
        }

        if (!activeOrbs.contains(orb)) {
            return;
        }

        ensureOrbSchedule(activeOrbs, orb.getClass());
        long gameTime = owner.level().getGameTime();
        Long nextEffectTick = nextEffectTicks.get(orb.getUUID());
        if (nextEffectTick == null || gameTime < nextEffectTick) {
            return;
        }

        orb.applyScheduledEffectFromManager();
        nextEffectTicks.put(orb.getUUID(), nextEffectTick + getAttackIntervalTicks());
    }

    private void ensureOrbSchedule(List<OrbEntity> activeOrbs, Class<? extends OrbEntity> type) {
        int previousGroupSize = scheduledGroupSizes.getOrDefault(type, -1);
        boolean missingSchedule = activeOrbs.stream().anyMatch(orb -> !nextEffectTicks.containsKey(orb.getUUID()));
        if (previousGroupSize == activeOrbs.size() && !missingSchedule) {
            return;
        }

        int intervalTicks = getAttackIntervalTicks();
        long startTick = owner.level().getGameTime() + owner.getRandom().nextInt(intervalTicks);
        for (int i = 0; i < activeOrbs.size(); i++) {
            long phaseOffset = Math.round((double) intervalTicks * i / activeOrbs.size());
            nextEffectTicks.put(activeOrbs.get(i).getUUID(), startTick + phaseOffset);
        }
        scheduledGroupSizes.put(type, activeOrbs.size());
    }

    private void resetOrbSchedule(Class<? extends OrbEntity> type) {
        scheduledGroupSizes.remove(type);
    }

    private void evokeOrb(UUID uuid) {
        Entity entity = owner.level().getEntityInAnyDimension(uuid);
        if (entity instanceof OrbEntity orb) {
            evokeOrb(orb);
        }
        else {
            orbs.removeOrb(uuid);
        }
    }

    public boolean evokeOrb(OrbEntity orb) {
        if (owner == null || owner.level().isClientSide() || orb == null || orb.isRemoved() || !orb.isOwnedBy(owner)) {
            return false;
        }

        orb.activateFromManager();
        resetOrbSchedules();
        return true;
    }

    public boolean evokeFirst() {
        if (owner == null || owner.level().isClientSide()) {
            return false;
        }

        List<OrbEntity> activeOrbs = getActiveOrbs();
        if (activeOrbs.isEmpty()) {
            return false;
        }

        return evokeOrb(activeOrbs.getFirst());
    }

    public boolean tryDualcast() {
        return dualcastFirst();
    }

    public boolean dualcastFirst() {
        if (owner == null || owner.level().isClientSide()) {
            return false;
        }

        List<OrbEntity> activeOrbs = getActiveOrbs();
        if (activeOrbs.isEmpty()) {
            return false;
        }

        OrbEntity original = activeOrbs.getFirst();
        OrbEntity copy = createExtraOrb(original);

        playDualcastEffects(original, copy);
        evokeOrb(original);
        copy.activateFromManager();
        resetOrbSchedules();
        return true;
    }

    private Vec3 getMirroredPosition(OrbEntity original) {
        Vec3 eye = owner.getEyePosition();
        Vec3 view = owner.getLookAngle().normalize();
        Vec3 originalPos = original.position();
        Vec3 toOriginal = originalPos.subtract(eye);
        Vec3 center = eye.add(view.scale(toOriginal.dot(view)));
        return center.scale(2.0D).subtract(originalPos);
    }

    private void playDualcastEffects(OrbEntity original, OrbEntity copy) {
        if (!(owner.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = owner.getEyePosition().add(owner.getLookAngle().scale(0.7D));
        serverLevel.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BEACON_POWER_SELECT, owner.getSoundSource(), 0.9F, 1.75F);
        serverLevel.sendParticles(ParticleTypes.END_ROD, center.x, center.y, center.z, 18, 0.25D, 0.2D, 0.25D, 0.02D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, original.getX(), original.getY() + original.getBbHeight() * 0.5D, original.getZ(), 18, 0.18D, 0.18D, 0.18D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, copy.getX(), copy.getY() + copy.getBbHeight() * 0.5D, copy.getZ(), 18, 0.18D, 0.18D, 0.18D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.FIREWORK, center.x, center.y, center.z, 10, 0.18D, 0.18D, 0.18D, 0.03D);
    }

    public Optional<LivingEntity> getAttackTarget() {
        if (owner == null) {
            return Optional.empty();
        }
        return CombatState.getRandomHostileIn(owner, getAttackRange());
    }

    public Optional<LivingEntity> getAnyAttackTarget() {
        if (owner == null) {
            return Optional.empty();
        }
        return CombatState.getRandomHostile(owner, ignored -> true);
    }

    public double getAttackRange() {
        if (owner == null) {
            return 0.0D;
        }
        return Math.max(0.0D, owner.getAttributeValue(ModAttributes.ORB_ATTACK_RANGE));
    }

    public int getAttackIntervalTicks() {
        if (owner == null) {
            return 1;
        }
        double attackSpeed = Math.max(MIN_ATTACK_SPEED, owner.getAttributeValue(ModAttributes.ORB_PASSIVE_SPEED));
        return Math.max(1, Mth.ceil(20.0D / attackSpeed));
    }

    private void spawnOrb(OrbEntity orb, int existingCount, int maxOrbs) {
        orb.setBirthGameTime(owner.level().getGameTime());
        double angle = (existingCount * Math.PI * 2.0D / Math.max(1, maxOrbs)) + owner.getRandom().nextDouble() * 0.5D;
        orb.setPos(owner.getX() + Math.cos(angle), owner.getY() + 1.05D, owner.getZ() + Math.sin(angle));
        owner.level().addFreshEntity(orb);
        orbs.addOrb(orb.getUUID(), maxOrbs).ifPresent(this::evokeOrb);
    }

    private OrbEntity createExtraOrb(OrbEntity original) {
        OrbEntity copy = original.createCopy(owner);
        copy.setSlotExempt(true);
        copy.setBirthGameTime(owner.level().getGameTime());
        copy.setPos(getMirroredPosition(original));
        owner.level().addFreshEntity(copy);
        return copy;
    }

    private void removeOverflowOrbs(int maxOrbs) {
        if (maxOrbs <= 0) {
            List.copyOf(getActiveOrbs()).forEach(this::evokeOrb);
            return;
        }

        List<OrbEntity> activeOrbs = getActiveOrbs();
        while (activeOrbs.size() > maxOrbs) {
            evokeFirst();
            activeOrbs = getActiveOrbs();
        }
        orbs.setCapacity(maxOrbs);
    }
}
