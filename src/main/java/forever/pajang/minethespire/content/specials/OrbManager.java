package forever.pajang.minethespire.content.specials;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.entity.OrbEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public final class OrbManager {
    public static final MapCodec<OrbManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            OrbSequence.CODEC.fieldOf("orbs").forGetter(OrbManager::getOrbs)
    ).apply(instance, OrbManager::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, OrbManager> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final OrbSequence orbs;
    private LivingEntity owner;
    private int lastMaxOrbs = -1;
    private boolean isDirty = false;

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

    private void markDirty() {
        isDirty = true;
    }

    private void clearDirty() {
        isDirty = false;
    }

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
            Entity entity = getEntity(uuid);
            if (entity instanceof OrbEntity orb && orb.level() == owner.level() && !orb.isRemoved() && orb.isOwnedBy(owner)) {
                result.add(orb);
            }
            else {
                orbs.removeElement(uuid);
                markDirty();
            }
        }
        return List.copyOf(result);
    }

    private Optional<OrbEntity> addOrb(OrbEntity orb) {
        int maxOrb = getMaxOrb();
        if (maxOrb <= 0) {
            return Optional.empty();
        }
        markDirty();
        Optional<UUID> oldest = orbs.addOrb(orb.getUUID(), maxOrb);
        if (oldest.isPresent()) {
            Entity e = getEntity(oldest.get());
            if (e instanceof OrbEntity o) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    private OrbSequence getOrbs() {
        return orbs;
    }

    public Entity getEntity(UUID uuid) {
        return owner.level().getEntityInAnyDimension(uuid);
    }

    public int getMaxOrb() {
        if (owner == null) {
            return 0;
        }
        return Math.max(0, Mth.floor(owner.getAttributeValue(ModAttributes.MAX_ORB)));
    }

    public void removeOrb(OrbEntity orb) {
        markDirty();
        orbs.removeOrb(orb.getUUID());
    }

    public void clearOrbs() {
        getOrbs().uuids().forEach(uuid -> {
            Entity entity = getEntity(uuid);
            if (entity instanceof OrbEntity orb) {
                orb.dissipate();
            }
        });
        markDirty();
        orbs.clear();
    }

    public boolean containsOrb(OrbEntity orb) {
        return orbs.contains(orb.getUUID());
    }

    public int getIndex(OrbEntity orb) {
        return getOwnedOrbs().indexOf(orb);
    }

    public int getOrbCount() {
        return getOwnedOrbs().size();
    }

    public boolean tryChannel(OrbType type) {
        return type.getChanneler().test(this);
    }

    boolean tryChannel(BiFunction<Level, LivingEntity, ? extends OrbEntity> constructor) {
        if (owner.level().isClientSide()) {
            return false;
        }

        int maxOrbs = getMaxOrb();
        if (maxOrbs <= 0) {
            return false;
        }
        OrbEntity orb = constructor.apply(owner.level(), owner);
        orb.setPos(owner.getX(), owner.getY(1.0), owner.getZ());
        owner.level().addFreshEntity(orb);
        addOrb(orb).ifPresent(this::evokeOrb);
        return true;
    }

    public Vec3 getOrbOrbit(OrbEntity orb) {
        int count = getOrbCount();
        int index = getIndex(orb);
        if (index < 0) return orb.position();
        final double rotationSpeed = 0.08d;
        double theta = (index * 2d * Math.PI / count) + owner.tickCount * rotationSpeed;
        double r = 1d + 0.15d * Mth.sin((orb.tickCount) * 0.07F);
        Vec3 normal = owner.getLookAngle().normalize();
        Vec3 right = normal.cross(Vec3.Y_AXIS);
        if (right.lengthSqr() < 1e-4) {
            right = Vec3.directionFromRotation(0.0F, owner.getYRot() + 90.0F);
        } else {
            right = right.normalize();
        }
        Vec3 up = right.cross(normal).normalize();
        Vec3 center = owner.getEyePosition();
        Vec3 offset = right.scale(Mth.cos(theta) * r).add(up.scale(Mth.sin(theta) * r));
        return(center.add(offset));
    }

    public boolean getPassiveAction(OrbEntity orb) {
        if (orb.level().isClientSide()) return false;
        double speed = Math.max(owner.getAttributeValue(ModAttributes.ORB_PASSIVE_SPEED), 0.01d);
        double cycle = Math.max(Math.ceil(20d / speed), 1d);
        OrbType type = orb.getOrbType();
        int count = 0;
        int index = -1;
        List<OrbEntity> ownedOrbs = getOwnedOrbs();
        for (OrbEntity o : ownedOrbs) {
            if (o.getOrbType() == type) {
                if (o == orb) {
                    index = count;
                }
                count++;
            }
        }
        if (count < 1 || index < 0) return false;
        int phase = ((int) Math.ceil(cycle * index / count));
        boolean action = phase == owner.tickCount % cycle;
        if (action) {
            orb.setTarget(getAttackTarget().orElse(null));
        }
        return action;
    }

    public void tick() {
        if (owner == null) {
            return;
        }
        tryAdjustOrbSequence();
        if (isDirty) {
            owner.syncData(ModAttachments.ORB_MANAGER);
            clearDirty();
        }
    }

    private void tryAdjustOrbSequence() {
        int maxOrbs = getMaxOrb();
        if (lastMaxOrbs >= 0 && maxOrbs < lastMaxOrbs) {
            List<UUID> tails = orbs.setCapacity(maxOrbs);
            tails.stream().map(this::getEntity).filter(OrbEntity.class::isInstance).map(OrbEntity.class::cast).forEach(OrbEntity::dissipate);
        }
        lastMaxOrbs = maxOrbs;
    }

    public boolean evokeOrb(OrbEntity orb) {
        if (orb == null) {
            return false;
        }

        if (!orb.level().isClientSide() && !orb.isRemoved() && !orb.isEvoked()) {
            orb.setTarget(getAttackTarget().orElse(null));
            orb.evokeAction((ServerLevel) orb.level(), owner);
        }
        removeOrb(orb);
        return true;
    }

    public boolean evokeFirst() {
        List<OrbEntity> orbs = getOwnedOrbs();
        if (orbs.isEmpty()) {
            return false;
        }

        return evokeOrb(orbs.getFirst());
    }

    public boolean dualcastFirst() {
        if (owner == null) return false;
        List<OrbEntity> orbs = getOwnedOrbs();
        if (orbs.isEmpty()) {
            return false;
        }

        OrbEntity original = orbs.getFirst();
        OrbEntity copy = createExtraOrb(original);

        playDualcastEffects(original, copy);
        evokeOrb(original);
        evokeOrb(copy);
        return true;
    }

    private OrbEntity createExtraOrb(OrbEntity original) {
        OrbEntity copy = original.createCopy(owner);
        copy.setPos(getMirroredPosition(original));
        owner.level().addFreshEntity(copy);
        return copy;
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

        serverLevel.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BEACON_POWER_SELECT, owner.getSoundSource(), 0.9F, 1.75F);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, original.getX(), original.getY() + original.getBbHeight() * 0.5D, original.getZ(), 18, 0.18D, 0.18D, 0.18D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, copy.getX(), copy.getY() + copy.getBbHeight() * 0.5D, copy.getZ(), 18, 0.18D, 0.18D, 0.18D, 0.04D);
    }

    public Optional<LivingEntity> getAttackTarget() {
        if (owner == null) {
            return Optional.empty();
        }
        return CombatState.getRandomHostileIn(owner, getAttackRange());
    }

    public double getAttackRange() {
        if (owner == null) {
            return 0.0D;
        }
        return Math.max(0.0D, owner.getAttributeValue(ModAttributes.ORB_ATTACK_RANGE));
    }

}
