package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModDamageTypes;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.ChargeBallManager;
import forever.pajang.minethespire.impl.CombatState;
import net.minecraft.core.particles.ParticleTypes;
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

import java.util.UUID;

public class LightningChargeBallEntity extends ChargeBallEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/projectiles/lightning_charge_ball.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/projectiles/lightning_charge_ball_activated.png");
    private static final double LAUNCH_SPEED = 0.7D;
    private static final float BEAM_DAMAGE = 3.0F;
    private static final float LAUNCH_DAMAGE = 8.0F;
    private static final int OUT_OF_COMBAT_LIFETIME_MIN = 15 * 20;
    private static final int OUT_OF_COMBAT_LIFETIME_MAX = 25 * 20;
    private static final double ENTITY_HIT_RADIUS = 0.55D;

    private int outOfCombatDespawnTicks = -1;
    private boolean launching;
    private boolean freeFlightLaunch;
    private UUID launchTargetUuid;
    private Vec3 launchDirection = Vec3.ZERO;

    public LightningChargeBallEntity(EntityType<? extends LightningChargeBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningChargeBallEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.LIGHTNING_CHARGE_BALL.get(), level, owner);
    }

    @Override
    public ChargeBallEntity createCopy(LivingEntity owner) {
        return new LightningChargeBallEntity(owner.level(), owner);
    }

    @Override
    protected void tickOwned(LivingEntity owner, ChargeBallManager manager) {
        if (launching) {
            tickLaunch(owner);
            return;
        }

        tickOrbit(owner);

        boolean ownerInCombat = CombatState.isInCombat(owner);
        if (!ownerInCombat) {
            manager.resetSchedule();
            if (outOfCombatDespawnTicks < 0) {
                outOfCombatDespawnTicks = randomOutOfCombatLifetime();
            }
            else if (--outOfCombatDespawnTicks <= 0) {
                dissipate((ServerLevel) level());
                discard();
                return;
            }
        }
        else {
            outOfCombatDespawnTicks = -1;
        }

        manager.tickChargeBall(this);
    }

    @Override
    protected void applyScheduledEffect() {
        LivingEntity owner = getOwner();
        if (owner == null || !CombatState.isInCombat(owner)) {
            return;
        }
        ChargeBallManager.get(owner).getAttackTarget().ifPresent(target -> applyLightningDamage(target, focusAdjustedAmount(owner, BEAM_DAMAGE), false));
    }

    @Override
    protected void activateEffect() {
        activateLightningLaunch();
    }

    public void attackLightningTarget(LivingEntity target, boolean special) {
        LivingEntity owner = getOwner();
        float baseDamage = special ? LAUNCH_DAMAGE : BEAM_DAMAGE;
        applyLightningDamage(target, owner == null ? baseDamage : focusAdjustedAmount(owner, baseDamage), special);
    }

    private void activateLightningLaunch() {
        if (level().isClientSide() || isRemoved()) {
            return;
        }

        LivingEntity owner = getOwner();
        if (owner == null) {
            discard();
            return;
        }

        LivingEntity target = ChargeBallManager.get(owner).getAttackTarget().orElse(null);
        if (target == null) {
            freeFlightLaunch = true;
            launchTargetUuid = null;
            launchDirection = owner.getLookAngle().normalize();
        }
        else {
            freeFlightLaunch = false;
            launchTargetUuid = target.getUUID();
            launchDirection = Vec3.ZERO;
            if (level() instanceof ServerLevel serverLevel) {
                Vec3 from = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
                Vec3 to = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
                emitActivatedBeamParticles(serverLevel, from, to, 16);
            }
        }

        if (launchDirection.lengthSqr() < 1.0E-4D) {
            launchDirection = owner.getLookAngle().normalize();
        }
        if (launchDirection.lengthSqr() < 1.0E-4D) {
            launchDirection = Vec3.directionFromRotation(0.0F, owner.getYRot());
        }

        launching = true;
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9F, 1.4F);
    }

    private void tickLaunch(LivingEntity owner) {
        if (freeFlightLaunch) {
            tickFreeFlightLaunch(owner);
            return;
        }

        LivingEntity target = getLaunchTarget();
        if (target == null) {
            freeFlightLaunch = true;
            launchTargetUuid = null;
            launchDirection = getDeltaMovement().lengthSqr() < 1.0E-4D ? owner.getLookAngle().normalize() : getDeltaMovement().normalize();
            tickFreeFlightLaunch(owner);
            return;
        }

        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 toTarget = targetPos.subtract(position());
        if (toTarget.lengthSqr() <= 0.64D) {
            applyLightningDamage(target, focusAdjustedAmount(owner, LAUNCH_DAMAGE), true);
            finishLaunch();
            return;
        }

        Vec3 movement = toTarget.normalize().scale(LAUNCH_SPEED);
//        setDeltaMovement(movement);
        move(MoverType.SELF, movement);
        emitBeamParticles((ServerLevel) level(), position(), targetPos, 6);
    }

    private void tickFreeFlightLaunch(LivingEntity owner) {
        noPhysics = false;
        setNoGravity(true);

        Vec3 direction = launchDirection.lengthSqr() < 1.0E-4D ? owner.getLookAngle().normalize() : launchDirection.normalize();
        Vec3 movement = direction.scale(LAUNCH_SPEED);
       // setDeltaMovement(movement);
        move(MoverType.SELF, movement);
        emitLightningTrailParticles();

        LivingEntity hitEntity = findHitEntity(owner);
        if (hitEntity != null) {
            applyLightningDamage(hitEntity, focusAdjustedAmount(owner, LAUNCH_DAMAGE), true);
            finishLaunch();
            return;
        }

        if (horizontalCollision || verticalCollision) {
            if (level() instanceof ServerLevel serverLevel) {
                dissipate(serverLevel);
            }
            finishLaunch();
        }
    }

    private LivingEntity findHitEntity(LivingEntity owner) {
        AABB area = getBoundingBox().inflate(ENTITY_HIT_RADIUS);
        return level().getEntities(EntityTypeTest.forClass(LivingEntity.class), area, entity -> entity != owner && !isOwnedEntity(entity) && entity.isAlive() && !entity.isRemoved())
                .stream()
                .findFirst()
                .orElse(null);
    }

    private LivingEntity getLaunchTarget() {
        if (launchTargetUuid == null) {
            return null;
        }
        Entity entity = level().getEntityInAnyDimension(launchTargetUuid);
        return entity instanceof LivingEntity living && living.isAlive() && !living.isRemoved() ? living : null;
    }

    private void emitLightningTrailParticles() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 5, 0.07D, 0.07D, 0.07D, 0.02D);
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 2, 0.04D, 0.04D, 0.04D, 0.005D);
    }

    private void finishLaunch() {
        launching = false;
        freeFlightLaunch = false;
        launchTargetUuid = null;
        launchDirection = Vec3.ZERO;
//        setDeltaMovement(Vec3.ZERO);
        discard();
    }

    private void applyLightningDamage(LivingEntity target, float damage, boolean special) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 from = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        Vec3 to = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        if (special) {
            emitActivatedBeamParticles(serverLevel, from, to, 32);
        }
        else {
            emitBeamParticles(serverLevel, from, to, 16);
        }
        DamageSource source = ModDamageTypes.chargeBallLightning(level(), this, getOwner());
        target.hurt(source, damage);
        level().playSound(null, target.getX(), target.getY(), target.getZ(),
                special ? SoundEvents.LIGHTNING_BOLT_THUNDER : SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS, special ? 0.85F : 0.45F, special ? 1.65F : 1.9F);
    }

    private static void emitBeamParticles(ServerLevel level, Vec3 from, Vec3 to, int steps) {
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= steps; i++) {
            Vec3 point = from.add(delta.scale((double) i / steps));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 2, 0.035D, 0.035D, 0.035D, 0.01D);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
            }
        }
    }

    private static void emitActivatedBeamParticles(ServerLevel level, Vec3 from, Vec3 to, int steps) {
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= steps; i++) {
            Vec3 point = from.add(delta.scale((double) i / steps));
            level.sendParticles(ParticleTypes.FIREWORK, point.x, point.y, point.z, 3, 0.04D, 0.04D, 0.04D, 0.02D);
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 1, 0.025D, 0.025D, 0.025D, 0.0D);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 2, 0.02D, 0.02D, 0.02D, 0.0D);
            }
        }
    }

    @Override
    protected ItemStack baseRenderStack() {
        return ModItems.LIGHTNING_CHARGE_BALL.get().getDefaultInstance();
    }

    @Override
    protected String activatedModelPath() {
        return "lightning_charge_ball_activated";
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
    protected void readChargeBallSaveData(ValueInput input) {
        outOfCombatDespawnTicks = input.getIntOr("OutOfCombatDespawnTicks", -1);
        launching = input.getBooleanOr("Launching", false);
        freeFlightLaunch = input.getBooleanOr("FreeFlightLaunch", false);
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
        output.putInt("OutOfCombatDespawnTicks", outOfCombatDespawnTicks);
        output.putBoolean("Launching", launching);
        output.putBoolean("FreeFlightLaunch", freeFlightLaunch);
        output.putDouble("LaunchDirectionX", launchDirection.x);
        output.putDouble("LaunchDirectionY", launchDirection.y);
        output.putDouble("LaunchDirectionZ", launchDirection.z);
        if (launchTargetUuid != null) {
            output.putString("LaunchTarget", launchTargetUuid.toString());
        }
    }

    private int randomOutOfCombatLifetime() {
        return OUT_OF_COMBAT_LIFETIME_MIN + random.nextInt(OUT_OF_COMBAT_LIFETIME_MAX - OUT_OF_COMBAT_LIFETIME_MIN + 1);
    }
}
