package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.CombatState;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BouncingFlaskProjectile extends ThrowableItemProjectile {
    private static final int MAX_BOUNCES = 2;
    private static final int VENIN_DURATION = 5 * 20;
    private static final int VENIN_LEVEL_INCREMENT = 3;
    private static final int HOMING_ARC_DELAY = 7;
    private static final int ENTITY_REHIT_COOLDOWN = 8;
    private static final int MAX_LIFETIME = 10 * 20;
    private static final double FALLBACK_TARGET_RADIUS = 6.0D;
    private static final double MIN_REDIRECT_SPEED = 0.65D;
    private static final double FIRST_BOUNCE_SPEED_MULTIPLIER = 0.5D;
    private static final double HOMING_ARC_UPWARD_BIAS = 0.8D;

    private int bounces;
    private int homingDelay;
    private UUID homingTargetUuid;
    private UUID lastHitEntityUuid;
    private int lastHitEntityCooldown;
    private boolean firstBounceSlowApplied;

    public BouncingFlaskProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BouncingFlaskProjectile(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntityTypes.BOUNCING_FLASK.get(), owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BOUNCING_FLASK.get();
    }

    @Override
    public void tick() {
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }

        if (lastHitEntityCooldown > 0) {
            lastHitEntityCooldown--;
        }

        updateHomingMovement();
        super.tick();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !isValidLivingTarget(living)) {
            return false;
        }
        Entity owner = getOwner();
        if (entity == owner) {
            return false;
        }
        return (lastHitEntityCooldown <= 0 || !entity.getUUID().equals(lastHitEntityUuid)) && super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide()) {
            return;
        }

        if (!(result.getEntity() instanceof LivingEntity target)) {
            return;
        }

        boolean canContinueBouncing = tryConsumeBounce();

        hurtForOwnerAttribution(target);
        applyVenin(target);
        lastHitEntityUuid = target.getUUID();
        lastHitEntityCooldown = ENTITY_REHIT_COOLDOWN;
        emitVeninParticles(target);
        playPotionHitSound(target.position());
        if (!canContinueBouncing) {
            discard();
            return;
        }

        chooseNextBounceAfterEntityHit(target);
        playBounceSound(target.position());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide()) {
            return;
        }

        if (!tryConsumeBounce()) {
            splashAndDiscard(result.getLocation());
            return;
        }

        clearHoming();
        Direction direction = result.getDirection();
        Vec3 movement = getDeltaMovement();
        Vec3 reflected = reflect(movement, direction);
        if (reflected.lengthSqr() < 1.0E-4D) {
            reflected = direction.getUnitVec3();
        }
        setDeltaMovement(reflected.normalize().scale(consumeBounceRedirectSpeed()));
        nudgeAwayFromBlock(result, direction);
        playBounceSound(result.getLocation());
    }

    private boolean tryConsumeBounce() {
        if (bounces >= MAX_BOUNCES) {
            return false;
        }
        bounces++;
        return true;
    }

    private void applyVenin(LivingEntity target) {
        MobEffectInstance current = target.getEffect(ModEffects.VENIN);
        int amplifier = current == null ? VENIN_LEVEL_INCREMENT - 1 : current.getAmplifier() + VENIN_LEVEL_INCREMENT;
        target.addEffect(new MobEffectInstance(ModEffects.VENIN, VENIN_DURATION, amplifier), getEffectSource());
    }

    private void hurtForOwnerAttribution(LivingEntity target) {
        Entity owner = getOwner();
        DamageSource source = owner == null ? level().damageSources().generic() : level().damageSources().thrown(this, owner);
        target.hurt(source, 0.0F);
    }

    private void chooseNextBounceAfterEntityHit(LivingEntity hitEntity) {
        double speed = consumeBounceRedirectSpeed();
        LivingEntity owner = getLivingOwner();
        Optional<LivingEntity> hostileTarget = owner == null
                ? Optional.empty()
                : CombatState.getRandomHostile(owner, entity -> entity != hitEntity && isValidLivingTarget(entity));
        if (hostileTarget.isPresent()) {
            beginHomingArc(hitEntity, hostileTarget.get(), speed);
            return;
        }

        if (isValidLivingTarget(hitEntity)) {
            beginHomingArc(hitEntity, hitEntity, speed);
            return;
        }

        LivingEntity nearbyTarget = findNearbyFallbackTarget();
        if (nearbyTarget != null) {
            beginHomingArc(hitEntity, nearbyTarget, speed);
        }
        else {
            bounceRandomly(speed);
        }
    }

    private void updateHomingMovement() {
        if (level().isClientSide() || homingTargetUuid == null) {
            return;
        }

        if (homingDelay > 0) {
            homingDelay--;
            return;
        }

        LivingEntity target = getHomingTarget();
        if (target == null) {
            LivingEntity nearbyTarget = findNearbyFallbackTarget();
            if (nearbyTarget != null) {
                beginHomingArc(null, nearbyTarget, redirectSpeed());
                return;
            }
            else {
                clearHoming();
                bounceRandomly(redirectSpeed());
                return;
            }
        }

        Vec3 targetPos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        Vec3 direction = targetPos.subtract(position());
        if (direction.lengthSqr() > 1.0E-4D) {
            setDeltaMovement(direction.normalize().scale(redirectSpeed()));
        }
    }

    private LivingEntity getHomingTarget() {
        if (homingTargetUuid == null) {
            return null;
        }
        Entity entity = level().getEntityInAnyDimension(homingTargetUuid);
        return entity instanceof LivingEntity living && isValidLivingTarget(living) ? living : null;
    }

    private LivingEntity getLivingOwner() {
        Entity owner = getOwner();
        return owner instanceof LivingEntity living && !living.isRemoved() && living.isAlive() ? living : null;
    }

    private LivingEntity findNearbyFallbackTarget() {
        Entity owner = getOwner();
        AABB area = getBoundingBox().inflate(FALLBACK_TARGET_RADIUS);
        List<LivingEntity> candidates = level().getEntities(EntityTypeTest.forClass(LivingEntity.class), area,
                entity -> entity != owner && isValidLivingTarget(entity));
        if (candidates.isEmpty()) {
            return null;
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private boolean isValidLivingTarget(LivingEntity entity) {
        return entity.level() == level() && entity.isAlive() && !entity.isRemoved();
    }

    private void beginHomingArc(LivingEntity previousTarget, LivingEntity target, double speed) {
        homingTargetUuid = target.getUUID();
        homingDelay = HOMING_ARC_DELAY;
        setDeltaMovement(arcDirectionFromTo(previousTarget, target).scale(speed));
    }

    private void clearHoming() {
        homingTargetUuid = null;
        homingDelay = 0;
    }

    private void bounceRandomly(double speed) {
        clearHoming();
        Vec3 direction = new Vec3(
                random.nextDouble() - 0.5D,
                0.25D + random.nextDouble() * 0.75D,
                random.nextDouble() - 0.5D
        );
        if (direction.lengthSqr() < 1.0E-4D) {
            direction = new Vec3(0.0D, 1.0D, 0.0D);
        }
        setDeltaMovement(direction.normalize().scale(speed));
    }

    private double redirectSpeed() {
        return Math.max(MIN_REDIRECT_SPEED, getDeltaMovement().length());
    }

    private double consumeBounceRedirectSpeed() {
        double speed = redirectSpeed();
        if (!firstBounceSlowApplied && bounces == 1) {
            firstBounceSlowApplied = true;
            speed *= FIRST_BOUNCE_SPEED_MULTIPLIER;
        }
        return Math.max(MIN_REDIRECT_SPEED * FIRST_BOUNCE_SPEED_MULTIPLIER, speed);
    }

    private Vec3 arcDirectionFromTo(LivingEntity previousTarget, LivingEntity target) {
        if (previousTarget == target) {
            return new Vec3(0.0D, 1.0D, 0.0D);
        }

        Vec3 from = previousTarget == null ? position() : previousTarget.position();
        Vec3 to = target.position();
        Vec3 toTarget = to.subtract(from);
        Vec3 horizontal = new Vec3(toTarget.x, 0.0D, toTarget.z);
        if (horizontal.lengthSqr() < 1.0E-4D) {
            Vec3 movement = getDeltaMovement();
            horizontal = new Vec3(movement.x, 0.0D, movement.z);
        }
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = new Vec3(random.nextDouble() - 0.5D, 0.0D, random.nextDouble() - 0.5D);
        }
        if (horizontal.lengthSqr() < 1.0E-4D) {
            horizontal = new Vec3(1.0D, 0.0D, 0.0D);
        }
        return horizontal.normalize().add(0.0D, HOMING_ARC_UPWARD_BIAS, 0.0D).normalize();
    }

    private static Vec3 reflect(Vec3 movement, Direction direction) {
        return switch (direction.getAxis()) {
            case X -> new Vec3(-movement.x, movement.y, movement.z);
            case Y -> new Vec3(movement.x, -movement.y, movement.z);
            case Z -> new Vec3(movement.x, movement.y, -movement.z);
        };
    }

    private void nudgeAwayFromBlock(BlockHitResult result, Direction direction) {
        Vec3 normal = direction.getUnitVec3();
        setPos(result.getLocation().add(normal.scale(0.03D)));
    }

    private void playBounceSound(Vec3 pos) {
        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.SLIME_BLOCK_HIT, SoundSource.PLAYERS, 0.65F, 1.25F);
    }

    private void playPotionHitSound(Vec3 pos) {
        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.WANDERING_TRADER_DRINK_POTION, SoundSource.PLAYERS, 0.65F, 1.35F);
    }

    private void splashAndDiscard(Vec3 pos) {
        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.8F, 1.0F);
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y, pos.z, 24, 0.25D, 0.12D, 0.25D, 0.08D);
            serverLevel.sendParticles(ParticleTypes.POOF, pos.x, pos.y, pos.z, 10, 0.18D, 0.08D, 0.18D, 0.02D);
        }
        discard();
    }

    private void emitVeninParticles(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.ITEM_SLIME,
                target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                18, target.getBbWidth() * 0.35D, target.getBbHeight() * 0.25D, target.getBbWidth() * 0.35D, 0.08D);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Bounces", bounces);
        output.putInt("HomingDelay", homingDelay);
        output.putInt("LastHitEntityCooldown", lastHitEntityCooldown);
        output.putBoolean("FirstBounceSlowApplied", firstBounceSlowApplied);
        if (homingTargetUuid != null) {
            output.putString("HomingTarget", homingTargetUuid.toString());
        }
        if (lastHitEntityUuid != null) {
            output.putString("LastHitEntity", lastHitEntityUuid.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        bounces = input.getIntOr("Bounces", 0);
        homingDelay = input.getIntOr("HomingDelay", 0);
        lastHitEntityCooldown = input.getIntOr("LastHitEntityCooldown", 0);
        firstBounceSlowApplied = input.getBooleanOr("FirstBounceSlowApplied", false);
        homingTargetUuid = readUuid(input, "HomingTarget");
        lastHitEntityUuid = readUuid(input, "LastHitEntity");
    }

    private static UUID readUuid(ValueInput input, String key) {
        Optional<String> value = input.getString(key);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(value.get());
        }
        catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
