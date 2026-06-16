package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.content.ModDamageTypes;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.specials.CombatState;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class BouncingFlaskProjectile extends ModProjectile {
    private static final int MAX_BOUNCES = 2;
    private static final int VENIN_DURATION = 5 * 20;
    private static final int MAX_LIFETIME = 10 * 20;
    private static final double SPEED_MULTIPLIER = 0.75d;

    private int bounces;
    private LivingEntity lastHit;
    private int lastHitCooldown;

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
    protected double getDefaultGravity() {
        return 0.05d;
    }

    @Override
    public void tick() {
        if (tickCount > MAX_LIFETIME) {
            discard();
            return;
        }

        if (lastHitCooldown > 0) {
            lastHitCooldown--;
        }
        super.tick();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (!(entity instanceof LivingEntity living) || !isValidLivingTarget(living)) {
            return false;
        }
        if (tickCount < 10 && entity == getOwner()) return false;
        return lastHitCooldown <= 0 || entity != lastHit && super.canHitEntity(entity);
    }

    private boolean isValidLivingTarget(LivingEntity entity) {
        return entity.level() == level() && entity.isAlive() && !entity.isRemoved();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide()) {
            return;
        }

        if (!(result.getEntity() instanceof LivingEntity target)) {
            return;
        }

        sendSlimeParticles(result.getLocation());
        boolean canContinueBouncing = tryConsumeBounce();
        hitTarget(target);
        applyVenin(target);
        lastHit = target;
        lastHitCooldown = 8;
        playEffect();
        if (!canContinueBouncing) {
            splashAndDiscard(position());
            return;
        }

        applyNextBounceMovement(target);
        playBounceSound(position());
    }

    private void hitTarget(LivingEntity target) {
        Entity owner = getOwner();
        DamageSource source = owner == null
                ? level().damageSources().source(ModDamageTypes.BOUNCING_HIT, this)
                : level().damageSources().source(ModDamageTypes.BOUNCING_HIT, this, owner);
        target.hurtServer((ServerLevel) target.level(), source, 0.0F);
    }

    private void applyVenin(LivingEntity target) {
        MobEffectInstance current = target.getEffect(ModEffects.VENIN);
        int amplifier = current == null ? 2 : current.getAmplifier() + 3;
        target.removeEffect(ModEffects.VENIN);
        target.addEffect(new MobEffectInstance(ModEffects.VENIN, VENIN_DURATION, amplifier), getEffectSource());
    }

    private void playEffect() {
        ((ServerLevel) level()).sendParticles(ParticleTypes.ITEM_SLIME, getX(), getY(), getZ(), 24, getBbWidth() * 0.35D, getBbHeight() * 0.25D, getBbWidth() * 0.35D, 0.08D);
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.65F, 1.35F);
    }

    private boolean tryConsumeBounce() {
        if (bounces >= MAX_BOUNCES) {
            return false;
        }
        bounces++;
        return true;
    }

    private void playBounceSound(Vec3 pos) {
        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.SLIME_BLOCK_HIT, SoundSource.PLAYERS, 0.65F, 1.25F);
    }

    private void splashAndDiscard(Vec3 pos) {
        level().playSound(null, pos.x, pos.y, pos.z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.PLAYERS, 0.8F, 1.0F);
        ((ServerLevel) level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ModItems.BOUNCING_FLASK.get()), pos.x, pos.y, pos.z, 10, 0.18D, 0.08D, 0.18D, 0.02D);
        discard();
    }

    private void sendSlimeParticles(Vec3 pos) {
        ((ServerLevel) level()).sendParticles(ParticleTypes.ITEM_SLIME, pos.x, pos.y, pos.z, 24, 0.25D, 0.12D, 0.25D, 0.08D);
    }

    private void applyNextBounceMovement(LivingEntity hitEntity) {
        Vec3 posThis = topCenterOf(this);
        if (getOwner() instanceof LivingEntity owner && !owner.isRemoved() && owner.isAlive()) {
            Optional<LivingEntity> target = CombatState.getRandomHostile(owner, entity -> entity != hitEntity && isValidLivingTarget(entity) && entity.distanceToSqr(hitEntity) < 8.0 * 8.0);
            if (target.isPresent()) {
                applyMovement(posThis, topCenterOf(target.get()), target.get().getKnownMovement());
                return;
            }
        }
        if (isValidLivingTarget(hitEntity)) {
            applyMovement(posThis, topCenterOf(hitEntity), hitEntity.getKnownMovement());
        } else applyMovement(posThis, getRandomTarget(), Vec3.ZERO);
    }

    private Vec3 getRandomTarget() {
        return new Vec3((random.nextDouble() - 0.5d) * 16d, (random.nextDouble() - 0.5d) * 8d, (random.nextDouble() - 0.5d) * 16d);
    }

    private static Vec3 topCenterOf(Entity entity) {
        return new Vec3(entity.getX(0.5), entity.getY(1.0), entity.getZ(0.5));
    }

    private void applyMovement(Vec3 origin, Vec3 target, Vec3 targetMovement) {
        final double g = getGravity();
        final double f = 0.99;
        final double T = 24d;
        Vec3 delta = target.add(targetMovement.scale(T * 2)).subtract(origin);
        double vt = g * f / (1d - f);
        double s = (1d - Math.pow(f, T)) / (1d - f);
        double vy = (delta.y() + vt * (T - s)) / s;
        double vx = delta.x() / s;
        double vz = delta.z() / s;
        setDeltaMovement(vx, vy, vz);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide()) {
            return;
        }

        sendSlimeParticles(result.getLocation());
        if (!tryConsumeBounce()) {
            splashAndDiscard(result.getLocation());
            return;
        }

        Direction direction = result.getDirection();
        Vec3 movement = getDeltaMovement();
        Vec3 reflected = bounceSolid(movement, direction);
        if (reflected.lengthSqr() < 1.0E-4D) {
            reflected = direction.getUnitVec3();
        }
        setDeltaMovement(reflected.normalize().scale(movement.length() * SPEED_MULTIPLIER));
        setPos(result.getLocation().add(direction.getUnitVec3().scale(0.03d)));
        playBounceSound(result.getLocation());
    }

    private static Vec3 bounceSolid(Vec3 movement, Direction direction) {
        return switch (direction.getAxis()) {
            case X -> new Vec3(-movement.x, movement.y, movement.z);
            case Y -> new Vec3(movement.x, -movement.y, movement.z);
            case Z -> new Vec3(movement.x, movement.y, -movement.z);
        };
    }


    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Bounces", bounces);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        bounces = input.getIntOr("Bounces", 0);
    }

}
