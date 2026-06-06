package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModDamageTypes;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.ChargeBallManager;
import forever.pajang.minethespire.impl.CombatState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class DarkChargeBallEntity extends ChargeBallEntity {
    private static final EntityDataAccessor<Float> CHARGE = SynchedEntityData.defineId(DarkChargeBallEntity.class, EntityDataSerializers.FLOAT);
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/projectiles/dark_charge_ball.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/projectiles/dark_charge_ball_activated.png");
    private static final float INITIAL_CHARGE = 6.0F;
    private static final float CHARGE_GAIN = 3.0F;
    private static final double LAUNCH_SPEED = 0.72D;
    private static final double ENTITY_HIT_RADIUS = 0.55D;

    private UUID launchTargetUuid;
    private Vec3 launchDirection = Vec3.ZERO;
    private boolean launching;
    private boolean launchFinished;

    public DarkChargeBallEntity(EntityType<? extends DarkChargeBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DarkChargeBallEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.DARK_CHARGE_BALL.get(), level, owner);
        setCharge(INITIAL_CHARGE);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE, INITIAL_CHARGE);
    }

    @Override
    public ChargeBallEntity createCopy(LivingEntity owner) {
        DarkChargeBallEntity copy = new DarkChargeBallEntity(owner.level(), owner);
        copy.setCharge(getCharge());
        return copy;
    }

    @Override
    protected void tickOwned(LivingEntity owner, ChargeBallManager manager) {
        if (launchFinished) {
            discard();
            return;
        }
        if (launching) {
            tickLaunch(owner);
            return;
        }
        if (isActivated()) {
            discard();
            return;
        }

        tickOrbit(owner);
        manager.tickChargeBall(this);
    }

    @Override
    protected void applyScheduledEffect() {
        LivingEntity owner = getOwner();
        if (owner == null) {
            return;
        }

        if (CombatState.isInCombat(owner)) {
            setCharge(getCharge() + focusAdjustedAmount(owner, CHARGE_GAIN));
            playChargeGainEffects();
        }
        else {
            float charge = getCharge();
            setCharge(Math.max(0.0F, charge - Math.max(charge * 0.2F, 1.0F)));
            if (getCharge() <= 0.0F && level() instanceof ServerLevel serverLevel) {
                dissipate(serverLevel);
                discard();
            }
        }
    }

    @Override
    protected void activateEffect() {
        LivingEntity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }

        Optional<LivingEntity> target = ChargeBallManager.get(owner).getAnyAttackTarget();
        launchTargetUuid = target.map(Entity::getUUID).orElse(null);
        launchDirection = target.map(entity -> entity.position().add(0.0D, entity.getBbHeight() * 0.5D, 0.0D).subtract(position()).normalize())
                .orElse(owner.getLookAngle().normalize());
        launching = true;
        launchFinished = false;
        playActivateEffects();
    }

    private void tickLaunch(LivingEntity owner) {
        noPhysics = false;
        setNoGravity(true);

        LivingEntity target = getLaunchTarget();
        Vec3 direction = target == null ? launchDirection : target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D).subtract(position()).normalize();
        if (direction.lengthSqr() < 1.0E-4D) {
            direction = owner.getLookAngle().normalize();
        }

        Vec3 movement = direction.scale(LAUNCH_SPEED);
//        setDeltaMovement(movement);
        move(MoverType.SELF, movement);
        emitTrailParticles();

        if (target != null && intersectsTarget(target)) {
            hitEntity(target);
            return;
        }

        if (launchTargetUuid == null) {
            LivingEntity hitEntity = findHitEntity(owner);
            if (hitEntity != null) {
                hitEntity(hitEntity);
                return;
            }
        }

        if (horizontalCollision || verticalCollision) {
            if (level() instanceof ServerLevel serverLevel) {
                dissipate(serverLevel);
            }
            finishLaunch();
        }
    }

    private LivingEntity getLaunchTarget() {
        if (launchTargetUuid == null) {
            return null;
        }
        Entity entity = level().getEntityInAnyDimension(launchTargetUuid);
        return entity instanceof LivingEntity living && living.isAlive() && !living.isRemoved() ? living : null;
    }

    private LivingEntity findHitEntity(LivingEntity owner) {
        AABB area = getBoundingBox().inflate(ENTITY_HIT_RADIUS);
        return level().getEntities(EntityTypeTest.forClass(LivingEntity.class), area, entity -> entity != owner && !isOwnedEntity(entity) && entity.isAlive() && !entity.isRemoved())
                .stream()
                .findFirst()
                .orElse(null);
    }

    private boolean intersectsTarget(LivingEntity target) {
        return target.getBoundingBox().inflate(ENTITY_HIT_RADIUS).intersects(getBoundingBox().inflate(ENTITY_HIT_RADIUS));
    }

    private void hitEntity(LivingEntity target) {
        DamageSource source = ModDamageTypes.chargeBallDark(level(), this, getOwner());
        float damage = getCharge();
        try {
            target.hurt(source, damage);
            playHitEffects(target);
        }
        finally {
            finishLaunch();
        }
    }

    private void finishLaunch() {
        if (launchFinished) {
            return;
        }
        launchFinished = true;
        launching = false;
        launchTargetUuid = null;
        launchDirection = Vec3.ZERO;
//        setDeltaMovement(Vec3.ZERO);
        remove(Entity.RemovalReason.DISCARDED);
    }

    private void playChargeGainEffects() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.55F, 0.55F);
        serverLevel.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 12, 0.18D, 0.18D, 0.18D, 0.02D);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 10, 0.15D, 0.15D, 0.15D, 0.01D);
    }

    private void playActivateEffects() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_SHOOT, SoundSource.PLAYERS, 0.8F, 0.7F);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, 18, 0.18D, 0.18D, 0.18D, 0.03D);
        serverLevel.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 24, 0.22D, 0.22D, 0.22D, 0.04D);
    }

    private void playHitEffects(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.45F, 0.85F);
        serverLevel.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 32, 0.28D, 0.28D, 0.28D, 0.05D);
        serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 18, 0.2D, 0.2D, 0.2D, 0.03D);
    }

    private void emitTrailParticles() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 4, 0.05D, 0.05D, 0.05D, 0.005D);
    }

    @Override
    protected ItemStack baseRenderStack() {
        return ModItems.DARK_CHARGE_BALL.get().getDefaultInstance();
    }

    @Override
    protected String activatedModelPath() {
        return "dark_charge_ball_activated";
    }

    @Override
    protected Identifier idleTexture() {
        return IDLE_TEXTURE;
    }

    @Override
    protected Identifier activatedTexture() {
        return ACTIVATED_TEXTURE;
    }

    @Override
    public Component getChargeText() {
        return Component.literal(Integer.toString(Math.round(getCharge())));
    }

    @Override
    protected void readChargeBallSaveData(ValueInput input) {
        setCharge(input.getFloatOr("Charge", INITIAL_CHARGE));
        launching = input.getBooleanOr("Launching", false);
        launchFinished = input.getBooleanOr("LaunchFinished", false);
        launchDirection = new Vec3(
                input.getDoubleOr("LaunchDirectionX", 0.0D),
                input.getDoubleOr("LaunchDirectionY", 0.0D),
                input.getDoubleOr("LaunchDirectionZ", 0.0D)
        );
        input.getString("LaunchTarget").ifPresent(value -> {
            try {
                launchTargetUuid = UUID.fromString(value);
            }
            catch (IllegalArgumentException ignored) {
                launchTargetUuid = null;
            }
        });
    }

    @Override
    protected void addChargeBallSaveData(ValueOutput output) {
        output.putFloat("Charge", getCharge());
        output.putBoolean("Launching", launching);
        output.putBoolean("LaunchFinished", launchFinished);
        output.putDouble("LaunchDirectionX", launchDirection.x);
        output.putDouble("LaunchDirectionY", launchDirection.y);
        output.putDouble("LaunchDirectionZ", launchDirection.z);
        if (launchTargetUuid != null) {
            output.putString("LaunchTarget", launchTargetUuid.toString());
        }
    }

    private float getCharge() {
        return entityData.get(CHARGE);
    }

    private void setCharge(float charge) {
        entityData.set(CHARGE, Math.max(0.0F, charge));
    }
}
