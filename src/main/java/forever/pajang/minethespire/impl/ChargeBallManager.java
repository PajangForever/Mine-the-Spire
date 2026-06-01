package forever.pajang.minethespire.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.entity.ChargeBallEntity;
import forever.pajang.minethespire.content.entity.FrostChargeBallEntity;
import forever.pajang.minethespire.content.entity.LightningChargeBallEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
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

public final class ChargeBallManager {
    public static final MapCodec<ChargeBallManager> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ChargeBallSequence.CODEC.fieldOf("charge_balls").forGetter(ChargeBallManager::chargeBalls)
    ).apply(instance, ChargeBallManager::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeBallManager> STREAM_CODEC = StreamCodec.of(
            (buf, manager) -> manager.encode(buf),
            ChargeBallManager::decode
    );

    private static final double MIN_ATTACK_SPEED = 0.05D;

    private final ChargeBallSequence chargeBalls;
    private final Map<UUID, Long> nextEffectTicks = new HashMap<>();
    private final Map<Class<? extends ChargeBallEntity>, Integer> scheduledGroupSizes = new HashMap<>();
    private transient LivingEntity owner;
    private int lastMaxBalls = -1;

    public ChargeBallManager() {
        this.chargeBalls = new ChargeBallSequence();
    }

    public ChargeBallManager(LivingEntity owner) {
        this();
        this.owner = owner;
    }

    private ChargeBallManager(ChargeBallSequence chargeBalls) {
        this.chargeBalls = chargeBalls;
    }

    public static ChargeBallManager get(LivingEntity owner) {
        ChargeBallManager manager = owner.getData(ModAttachments.CHARGE_BALL_MANAGER);
        manager.owner = owner;
        return manager;
    }

    public boolean tryCreateForCombatEntry() {
        if (owner == null || owner.level().isClientSide() || !ModItems.CRACKED_CORE.get().isInCuriosOrEquipmentSlot(owner)) {
            return false;
        }

        if (!getActiveBalls().isEmpty()) {
            return false;
        }

        return tryCreateLightning();
    }

    public boolean tryCreateLightning() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new LightningChargeBallEntity(owner.level(), owner));
    }

    public boolean tryCreateFrost() {
        if (owner == null) {
            return false;
        }
        return tryCreate(new FrostChargeBallEntity(owner.level(), owner));
    }

    public void removeBall(ChargeBallEntity ball) {
        chargeBalls.removeBall(ball.getUUID());
    }

    public void resetSchedule() {
        resetChargeBallSchedules();
    }

    public void tick() {
        if (owner == null || owner.level().isClientSide()) {
            return;
        }

        int maxBalls = getMaxBalls();
        if (lastMaxBalls >= 0 && maxBalls < lastMaxBalls) {
            removeOverflowBalls(maxBalls);
        }
        if (maxBalls > 0) {
            chargeBalls.setCapacity(maxBalls);
        }
        lastMaxBalls = maxBalls;
    }

    public void tickChargeBall(ChargeBallEntity ball) {
        if (owner == null || owner.level().isClientSide()) {
            return;
        }

        List<ChargeBallEntity> activeBalls = getActiveBallsOfSameType(ball);
        if (activeBalls.isEmpty()) {
            resetChargeBallSchedule(ball.getClass());
            return;
        }

        if (!activeBalls.contains(ball)) {
            return;
        }

        ensureChargeBallSchedule(activeBalls, ball.getClass());
        long gameTime = owner.level().getGameTime();
        Long nextEffectTick = nextEffectTicks.get(ball.getUUID());
        if (nextEffectTick == null || gameTime < nextEffectTick) {
            return;
        }

        ball.applyScheduledEffectFromManager();
        nextEffectTicks.put(ball.getUUID(), nextEffectTick + getAttackIntervalTicks());
    }

    public boolean tryDoubleRelease() {
        return doubleActivateOldestBall();
    }

    public boolean activateBall(ChargeBallEntity ball) {
        if (owner == null || owner.level().isClientSide() || ball == null || ball.isRemoved() || !ball.isOwnedBy(owner)) {
            return false;
        }

        ball.activateFromManager();
        resetChargeBallSchedules();
        return true;
    }

    public boolean activateOldestBall() {
        if (owner == null || owner.level().isClientSide()) {
            return false;
        }

        List<ChargeBallEntity> activeBalls = getActiveBalls();
        if (activeBalls.isEmpty()) {
            return false;
        }

        return activateBall(activeBalls.getFirst());
    }

    public boolean doubleActivateOldestBall() {
        if (owner == null || owner.level().isClientSide()) {
            return false;
        }

        List<ChargeBallEntity> activeBalls = getActiveBalls();
        if (activeBalls.isEmpty()) {
            return false;
        }

        ChargeBallEntity original = activeBalls.getFirst();
        ChargeBallEntity copy = createExtraChargeBall(original);

        playDoubleReleaseEffects(original, copy);
        activateBall(original);
        copy.activateFromManager();
        resetChargeBallSchedules();
        return true;
    }

    public Optional<LivingEntity> getAttackTarget() {
        if (owner == null) {
            return Optional.empty();
        }
        return CombatState.getRandomHostileIn(owner, getAttackRange());
    }

    public int getMaxBalls() {
        if (owner == null) {
            return 0;
        }
        return Math.max(0, Mth.floor(owner.getAttributeValue(ModAttributes.LIGHTNING_CHARGE_BALL_LIMIT)));
    }

    public double getAttackRange() {
        if (owner == null) {
            return 0.0D;
        }
        return Math.max(0.0D, owner.getAttributeValue(ModAttributes.LIGHTNING_CHARGE_BALL_ATTACK_RANGE));
    }

    public int getAttackIntervalTicks() {
        if (owner == null) {
            return 1;
        }
        double attackSpeed = Math.max(MIN_ATTACK_SPEED, owner.getAttributeValue(ModAttributes.LIGHTNING_CHARGE_BALL_ATTACK_SPEED));
        return Math.max(1, Mth.ceil(20.0D / attackSpeed));
    }

    public int getBallIndex(ChargeBallEntity ball) {
        return getOwnedBalls().indexOf(ball);
    }

    public int getBallCount() {
        return getOwnedBalls().size();
    }

    private boolean tryCreate(ChargeBallEntity ball) {
        if (owner.level().isClientSide()) {
            return false;
        }

        int maxBalls = getMaxBalls();
        if (maxBalls <= 0) {
            return false;
        }
        int existingCount = getActiveBalls().size();
        spawnBall(ball, existingCount, maxBalls);
        return true;
    }

    private ChargeBallSequence chargeBalls() {
        return chargeBalls;
    }

    private void spawnBall(ChargeBallEntity ball, int existingCount, int maxBalls) {
        ball.setBirthGameTime(owner.level().getGameTime());
        double angle = (existingCount * Math.PI * 2.0D / Math.max(1, maxBalls)) + owner.getRandom().nextDouble() * 0.5D;
        ball.setPos(owner.getX() + Math.cos(angle), owner.getY() + 1.05D, owner.getZ() + Math.sin(angle));
        owner.level().addFreshEntity(ball);
        chargeBalls.addBall(ball.getUUID(), maxBalls).ifPresent(this::activateBall);
    }

    private ChargeBallEntity createExtraChargeBall(ChargeBallEntity original) {
        ChargeBallEntity copy = original.createCopy(owner);
        copy.setSlotExempt(true);
        copy.setBirthGameTime(owner.level().getGameTime());
        copy.setPos(getMirroredPosition(original));
        owner.level().addFreshEntity(copy);
        return copy;
    }

    private void removeOverflowBalls(int maxBalls) {
        if (maxBalls <= 0) {
            List.copyOf(getActiveBalls()).forEach(this::activateBall);
            return;
        }

        List<ChargeBallEntity> activeBalls = getActiveBalls();
        while (activeBalls.size() > maxBalls) {
            activateOldestBall();
            activeBalls = getActiveBalls();
        }
        chargeBalls.setCapacity(maxBalls);
    }

    private List<ChargeBallEntity> getActiveBalls() {
        return getOwnedBalls().stream()
                .filter(ChargeBallEntity::countsTowardLimit)
                .filter(ball -> !ball.isActivated())
                .toList();
    }

    private List<ChargeBallEntity> getActiveBallsOfSameType(ChargeBallEntity ball) {
        Class<? extends ChargeBallEntity> type = ball.getClass();
        return getActiveBalls().stream()
                .filter(activeBall -> activeBall.getClass() == type)
                .toList();
    }

    private List<ChargeBallEntity> getOwnedBalls() {
        if (owner == null) {
            return List.of();
        }

        ArrayList<ChargeBallEntity> result = new ArrayList<>();
        for (UUID uuid : chargeBalls.ids()) {
            Entity entity = owner.level().getEntityInAnyDimension(uuid);
            if (entity instanceof ChargeBallEntity ball && ball.level() == owner.level() && !ball.isRemoved() && ball.isOwnedBy(owner)) {
                result.add(ball);
            }
            else {
                chargeBalls.removeBall(uuid);
            }
        }
        return List.copyOf(result);
    }

    private Vec3 getMirroredPosition(ChargeBallEntity original) {
        Vec3 eye = owner.getEyePosition();
        Vec3 view = owner.getLookAngle().normalize();
        Vec3 originalPos = original.position();
        Vec3 toOriginal = originalPos.subtract(eye);
        Vec3 center = eye.add(view.scale(toOriginal.dot(view)));
        return center.scale(2.0D).subtract(originalPos);
    }

    private void playDoubleReleaseEffects(ChargeBallEntity original, ChargeBallEntity copy) {
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

    private void encode(RegistryFriendlyByteBuf buf) {
        chargeBalls.encode(buf);
    }

    private static ChargeBallManager decode(RegistryFriendlyByteBuf buf) {
        ChargeBallSequence chargeBalls = ChargeBallSequence.decode(buf);
        return new ChargeBallManager(chargeBalls);
    }

    private void activateBall(UUID uuid) {
        Entity entity = owner.level().getEntityInAnyDimension(uuid);
        if (entity instanceof ChargeBallEntity ball) {
            activateBall(ball);
        }
        else {
            chargeBalls.removeBall(uuid);
        }
    }

    private void ensureChargeBallSchedule(List<ChargeBallEntity> activeBalls, Class<? extends ChargeBallEntity> type) {
        int previousGroupSize = scheduledGroupSizes.getOrDefault(type, -1);
        boolean missingSchedule = activeBalls.stream().anyMatch(ball -> !nextEffectTicks.containsKey(ball.getUUID()));
        if (previousGroupSize == activeBalls.size() && !missingSchedule) {
            return;
        }

        int intervalTicks = getAttackIntervalTicks();
        long startTick = owner.level().getGameTime() + owner.getRandom().nextInt(intervalTicks);
        for (int i = 0; i < activeBalls.size(); i++) {
            long phaseOffset = Math.round((double) intervalTicks * i / activeBalls.size());
            nextEffectTicks.put(activeBalls.get(i).getUUID(), startTick + phaseOffset);
        }
        scheduledGroupSizes.put(type, activeBalls.size());
    }

    private void resetChargeBallSchedule(Class<? extends ChargeBallEntity> type) {
        scheduledGroupSizes.remove(type);
    }

    private void resetChargeBallSchedules() {
        nextEffectTicks.clear();
        scheduledGroupSizes.clear();
    }
}
