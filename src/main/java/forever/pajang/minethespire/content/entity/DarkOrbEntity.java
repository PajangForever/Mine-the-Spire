package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModDamageTypes;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.specials.CombatState;
import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.OrbType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class DarkOrbEntity extends OrbEntity {
    private static final EntityDataAccessor<Float> CHARGE = SynchedEntityData.defineId(DarkOrbEntity.class, EntityDataSerializers.FLOAT);
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/orbs/dark.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/orbs/dark_evoked.png");
    private static final float INITIAL_CHARGE = 6.0F;
    private static final double LAUNCH_SPEED = 0.7d;

    public DarkOrbEntity(EntityType<? extends DarkOrbEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DarkOrbEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.DARK_CHARGE_BALL.get(), level, owner);
        setCharge(INITIAL_CHARGE);
    }

    @Override
    public OrbType getOrbType() {
        return OrbType.Dark;
    }

    @Override
    public OrbEntity createCopy(LivingEntity owner) {
        DarkOrbEntity copy = new DarkOrbEntity(owner.level(), owner);
        copy.setCharge(getCharge());
        return copy;
    }

    @Override
    public void passiveAction(ServerLevel serverLevel, LivingEntity owner) {
        super.passiveAction(serverLevel, owner);

        if (CombatState.isInCombat(owner)) {
            setCharge(getCharge() + focusAdjusted(owner, 3f));
            playPassiveEffects();
        }
        else {
            float charge = getCharge();
            setCharge(Math.max(0, charge - Math.max(charge * 0.2f, 1f)));
            if (getCharge() <= 0f) {
                dissipate();
            }
        }
    }

    @Override
    public void evokeAction(ServerLevel serverLevel, LivingEntity owner) {
        super.evokeAction(serverLevel, owner);
        if (getTarget().isEmpty()) {
            Vec3 flightVec = owner.getLookAngle().normalize();
            setDeltaMovement(flightVec.scale(LAUNCH_SPEED));
        }
        playEvokeEffects();
    }

    @Override
    protected void tickEvoked(ServerLevel serverLevel, LivingEntity owner) {
        super.tickEvoked(serverLevel, owner);
        Optional<LivingEntity> targetOpt = getTarget();
        emitEvokedPathParticles();
        if (level().isClientSide()) return;
        HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, e ->
                targetOpt.map(entity -> e == entity).orElseGet(() -> e.canBeHitByProjectile() && e != owner));
        switch (result.getType()) {
            case MISS -> {}
            case ENTITY -> onEvokedHitEntity((EntityHitResult) result);
            case BLOCK -> onEvokedHitBlock((BlockHitResult) result);
        }
    }

    @Override
    protected void moveEvokedPath(OrbManager manager) {
        super.moveEvokedPath(manager);
        Optional<LivingEntity> targetOpt = getTarget();
        if (targetOpt.isPresent()) {
            LivingEntity target = targetOpt.get();
            Vec3 movement = target.getEyePosition().subtract(position()).normalize().scale(LAUNCH_SPEED);
            setDeltaMovement(movement);
        }
    }

    private void emitEvokedPathParticles() {
        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 5, 0.07D, 0.07D, 0.07D, 0.02D);
    }

    private void playPassiveEffects() {
        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.BREEZE_CHARGE, SoundSource.PLAYERS, 0.2F, 0.5F);
        serverLevel.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 12, 0.18D, 0.18D, 0.18D, 0.02D);
    }

    private void playEvokeEffects() {
        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.ENDER_CHEST_OPEN, SoundSource.PLAYERS, 0.8F, 0.7F);
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, pos.x, pos.y, pos.z, 18, 0.18D, 0.18D, 0.18D, 0.03D);
    }

    private void playHitEffects(Entity target) {
        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = target.position().add(0.0D, target.getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.45F, 0.85F);
        serverLevel.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 32, 0.28D, 0.28D, 0.28D, 0.05D);
        serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 18, 0.2D, 0.2D, 0.2D, 0.03D);
    }

    private void onEvokedHitBlock(BlockHitResult result) {
        BlockState state = level().getBlockState(result.getBlockPos());
//        if (state.is(BlockTags.MOSS_REPLACEABLE)) {
//            level().destroyBlock(result.getBlockPos(), true);
//        }
        onEvokeFinish();
    }

    private void onEvokedHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        damage(entity);
        onEvokeFinish();
    }

    private void damage(Entity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Optional<LivingEntity> owner = getOwner();
        DamageSource source = owner.map(entity -> level().damageSources().source(ModDamageTypes.ORB_DARK, this, entity))
                .orElseGet(() -> level().damageSources().source(ModDamageTypes.ORB_DARK, this));
        target.hurtServer(serverLevel , source, getCharge());
        playHitEffects(target);
    }

    private void onEvokeFinish() {
        setDeltaMovement(Vec3.ZERO);
        dissipate();
    }

    private float getCharge() {
        return entityData.get(CHARGE);
    }

    private void setCharge(float charge) {
        entityData.set(CHARGE, charge);
    }

    @Override
    protected Identifier normalTexture() {
        return IDLE_TEXTURE;
    }

    @Override
    protected Identifier evokeTexture() {
        return ACTIVATED_TEXTURE;
    }

    @Override
    public Component getDisplayTag() {
        return Component.literal(Integer.toString(Math.round(getCharge())));
    }

    @Override
    protected void readOrbSaveData(ValueInput input) {
        setCharge(input.getFloatOr("Charge", INITIAL_CHARGE));
    }

    @Override
    protected void addOrbSaveData(ValueOutput output) {
        output.putFloat("Charge", getCharge());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CHARGE, INITIAL_CHARGE);
    }
}
