package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModDamageTypes;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.specials.CombatState;
import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.OrbType;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.phys.*;

import java.util.Optional;

public class LightningOrbEntity extends OrbEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/orbs/lightning.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/orbs/lightning_evoked.png");
    private static final double LAUNCH_SPEED = 0.7d;
    private static final float PASSIVE_DAMAGE = 3.0F;
    private static final float EVOKED_DAMAGE = 8.0F;

    private int dissipateTicks = -1;
    public LightningOrbEntity(EntityType<? extends LightningOrbEntity> entityType, Level level) {
        super(entityType, level);
    }

    public LightningOrbEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.LIGHTNING_CHARGE_BALL.get(), level, owner);
    }

    @Override
    public OrbType getOrbType() {
        return OrbType.Lightning;
    }

    @Override
    protected void tickNormal(ServerLevel serverLevel, LivingEntity owner) {
        super.tickNormal(serverLevel, owner);
        boolean ownerInCombat = CombatState.isInCombat(owner);
        if (ownerInCombat) {
            dissipateTicks = -1;
        } else {
            if (dissipateTicks < 0) {
                dissipateTicks = 400 + random.nextInt(200);
            }
            else {
                dissipateTicks--;
                if (dissipateTicks == 0) {
                    dissipate();
                }
            }
        }
    }

    @Override
    public OrbEntity createCopy(LivingEntity owner) {
        return new LightningOrbEntity(owner.level(), owner);
    }

    @Override
    public void passiveAction(ServerLevel serverLevel, LivingEntity owner) {
        if ( !CombatState.isInCombat(owner)) {
            return;
        }
        getTarget().ifPresent(target -> damage(target, false));
    }

    @Override
    public void evokeAction(ServerLevel serverLevel, LivingEntity owner) {
        super.evokeAction(serverLevel, owner);
        if (getTarget().isEmpty()) {
            Vec3 flightVec = owner.getLookAngle().normalize();
            setDeltaMovement(flightVec.scale(LAUNCH_SPEED));
        }
        level().playSound(null, getX(), getY(), getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.9F, 1.4F);
    }

    @Override
    protected void tickEvoked(ServerLevel serverLevel, LivingEntity owner) {
        super.tickEvoked(serverLevel, owner);
        Optional<LivingEntity> targetOpt = getTarget();
        HitResult result = ProjectileUtil.getHitResultOnMoveVector(this, e ->
                targetOpt.map(entity -> e == entity).orElseGet(() -> e.canBeHitByProjectile() && e != owner));
        emitEvokedPathParticles();
        switch (result.getType()) {
            case MISS -> {}
            case ENTITY -> onEvokedHitEntity((EntityHitResult) result);
            case BLOCK -> onEvokedHitBlock((BlockHitResult) result);
        }
    }

    @Override
    protected void moveEvokedPath(OrbManager manager) {
        super.moveEvokedPath(manager);
        getTarget().ifPresent(target -> {
            Vec3 movement = target.getEyePosition().subtract(position()).normalize().scale(LAUNCH_SPEED);
            setDeltaMovement(movement);
        });
    }

    private void emitEvokedPathParticles() {
        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 5, 0.07D, 0.07D, 0.07D, 0.02D);
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
        damage(entity, true);
        onEvokeFinish();
    }

    private void damage(Entity target, boolean evoked) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 from = position();
        Vec3 to = target.getEyePosition();
        if (evoked) emitEvokedAttackParticles(serverLevel, from, to, 32);
        else emitPassiveAttackParticles(serverLevel, from, to, 16);
        Optional<LivingEntity> owner = getOwner();
        DamageSource source;
        float damage;
        if (owner.isPresent()) {
            source = level().damageSources().source(ModDamageTypes.ORB_LIGHTNING, this, owner.get());
            damage = focusAdjusted(owner.get(), evoked ? EVOKED_DAMAGE :PASSIVE_DAMAGE);
        } else {
            source = level().damageSources().source(ModDamageTypes.ORB_LIGHTNING, this);
            damage = evoked ? EVOKED_DAMAGE : PASSIVE_DAMAGE;
        }
        target.hurtServer(serverLevel , source, damage);
        level().playSound(null, target.getX(), target.getY(), target.getZ(),
                evoked ? SoundEvents.LIGHTNING_BOLT_THUNDER : SoundEvents.LIGHTNING_BOLT_IMPACT,
                SoundSource.PLAYERS, evoked ? 0.85F : 0.45F, evoked ? 1.65F : 1.9F);
    }

    private void onEvokeFinish() {
        setDeltaMovement(Vec3.ZERO);
        dissipate();
    }

    private static void emitPassiveAttackParticles(ServerLevel level, Vec3 from, Vec3 to, int steps) {
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= steps; i++) {
            Vec3 point = from.add(delta.scale((double) i / steps));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, point.x, point.y, point.z, 2, 0.035D, 0.035D, 0.035D, 0.01D);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 1, 0.015D, 0.015D, 0.015D, 0.0D);
            }
        }
    }

    private static void emitEvokedAttackParticles(ServerLevel level, Vec3 from, Vec3 to, int steps) {
        Vec3 delta = to.subtract(from);
        for (int i = 0; i <= steps; i++) {
            Vec3 point = from.add(delta.scale((double) i / steps));
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 1, 0.025D, 0.025D, 0.025D, 0.0D);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, point.x, point.y, point.z, 2, 0.02D, 0.02D, 0.02D, 0.0D);
            }
        }
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
    protected void readOrbSaveData(ValueInput input) {
        dissipateTicks = input.getIntOr("DissipateTicks", -1);
    }

    @Override
    protected void addOrbSaveData(ValueOutput output) {
        output.putInt("DissipateTicks", dissipateTicks);
    }
}
