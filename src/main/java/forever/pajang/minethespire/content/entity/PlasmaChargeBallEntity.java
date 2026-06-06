package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.ChargeBallManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PlasmaChargeBallEntity extends ChargeBallEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/projectiles/plasma_charge_ball.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/projectiles/plasma_charge_ball_activated.png");
    private static final double SPEED_BONUS = 0.2D;
    private static final int PLASMA_CHARGE_DURATION = 30 * 20;

    public PlasmaChargeBallEntity(EntityType<? extends PlasmaChargeBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PlasmaChargeBallEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.PLASMA_CHARGE_BALL.get(), level, owner);
    }

    @Override
    public ChargeBallEntity createCopy(LivingEntity owner) {
        return new PlasmaChargeBallEntity(owner.level(), owner);
    }

    @Override
    protected void tickOwned(LivingEntity owner, ChargeBallManager manager) {
        tickOrbit(owner);
        ensureSpeedModifier(owner, manager);
        emitIdleParticles();
    }

    @Override
    protected void applyScheduledEffect() {
    }

    @Override
    protected void markActivated() {
        removeSpeedModifier();
        super.markActivated();
    }

    @Override
    protected void activateEffect() {
        LivingEntity owner = getOwner();
        if (owner == null || level().isClientSide() || isRemoved()) {
            discard();
            return;
        }

        MobEffectInstance existing = owner.getEffect(ModEffects.PLASMA_CHARGE);
        int amplifier = existing == null ? 0 : existing.getAmplifier() + 1;
        owner.addEffect(new MobEffectInstance(ModEffects.PLASMA_CHARGE, PLASMA_CHARGE_DURATION, amplifier, false, true, true));
        playActivateEffects(owner);
        discard();
    }

    @Override
    protected ItemStack baseRenderStack() {
        return ModItems.PLASMA_CHARGE_BALL.get().getDefaultInstance();
    }

    @Override
    protected String activatedModelPath() {
        return "plasma_charge_ball_activated";
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
    public void remove(RemovalReason reason) {
        removeSpeedModifier();
        super.remove(reason);
    }

    private void ensureSpeedModifier(LivingEntity owner, ChargeBallManager manager) {
        AttributeInstance instance = owner.getAttribute(ModAttributes.LIGHTNING_CHARGE_BALL_ATTACK_SPEED);
        if (instance == null || instance.hasModifier(speedModifierId())) {
            return;
        }

        instance.addTransientModifier(new AttributeModifier(speedModifierId(), SPEED_BONUS, AttributeModifier.Operation.ADD_VALUE));
        manager.resetSchedule();
    }

    private void removeSpeedModifier() {
        LivingEntity owner = getOwner();
        if (owner == null || owner.level().isClientSide()) {
            return;
        }

        AttributeInstance instance = owner.getAttribute(ModAttributes.LIGHTNING_CHARGE_BALL_ATTACK_SPEED);
        if (instance != null && instance.removeModifier(speedModifierId())) {
            ChargeBallManager.get(owner).resetSchedule();
        }
    }

    private Identifier speedModifierId() {
        return MineTheSpire.id("plasma_charge_ball/" + getUUID());
    }

    private void emitIdleParticles() {
        if (!(level() instanceof ServerLevel serverLevel) || tickCount % 10 != 0) {
            return;
        }

        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 2, 0.08D, 0.08D, 0.08D, 0.003D);
    }

    private void playActivateEffects(LivingEntity owner) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 pos = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8F, 1.85F);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 28, 0.35D, 0.35D, 0.35D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 12, 0.22D, 0.22D, 0.22D, 0.02D);
    }
}
