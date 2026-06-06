package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.ChargeBallManager;
import forever.pajang.minethespire.impl.BlockingValueHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class FrostChargeBallEntity extends ChargeBallEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/projectiles/frost_charge_ball.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/projectiles/frost_charge_ball_activated.png");
    private static final int FROST_LIFETIME_MIN = 20 * 20;
    private static final int FROST_LIFETIME_MAX = 30 * 20;
    private static final float FROST_HEAL_AMOUNT = 5.0F;
    private static final float FROST_FINISH_HEAL_AMOUNT = 20.0F;

    private int lifetimeTicks = -1;

    public FrostChargeBallEntity(EntityType<? extends FrostChargeBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FrostChargeBallEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.FROST_CHARGE_BALL.get(), level, owner);
        lifetimeTicks = randomFrostLifetime();
    }

    @Override
    public ChargeBallEntity createCopy(LivingEntity owner) {
        return new FrostChargeBallEntity(owner.level(), owner);
    }

    @Override
    protected void tickOwned(LivingEntity owner, ChargeBallManager manager) {
        tickOrbit(owner);
        if (lifetimeTicks < 0) {
            lifetimeTicks = randomFrostLifetime();
        }

        if (lifetimeTicks-- <= 0) {
            markActivated();
            activateFrost(true);
            discard();
            return;
        }

        manager.tickChargeBall(this);
    }

    @Override
    protected void applyScheduledEffect() {
        LivingEntity owner = getOwner();
        if (owner == null) {
            return;
        }

        BlockingValueHandler.add(owner, focusAdjustedAmount(owner, FROST_HEAL_AMOUNT));
        if (level() instanceof ServerLevel serverLevel) {
            Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.45F, 1.55F);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 10, 0.12D, 0.12D, 0.12D, 0.02D);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 16, 0.18D, 0.18D, 0.18D, 0.01D);
        }
    }

    @Override
    protected void activateEffect() {
        activateFrost(false);
        discard();
    }

    private void activateFrost(boolean lifetimeExpire) {
        LivingEntity owner = getOwner();
        if (owner == null || level().isClientSide() || isRemoved()) {
            return;
        }

        BlockingValueHandler.add(owner, focusAdjustedAmount(owner, FROST_FINISH_HEAL_AMOUNT));
        if (level() instanceof ServerLevel serverLevel) {
            Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
            serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, lifetimeExpire ? 0.65F : 0.9F, lifetimeExpire ? 1.15F : 1.4F);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 30, 0.25D, 0.25D, 0.25D, 0.03D);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y, pos.z, 12, 0.15D, 0.15D, 0.15D, 0.01D);
        }
    }

    @Override
    protected ItemStack baseRenderStack() {
        return ModItems.FROST_CHARGE_BALL.get().getDefaultInstance();
    }

    @Override
    protected String activatedModelPath() {
        return "frost_charge_ball_activated";
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
        lifetimeTicks = input.getIntOr("LifetimeTicks", -1);
    }

    @Override
    protected void addChargeBallSaveData(ValueOutput output) {
        output.putInt("LifetimeTicks", lifetimeTicks);
    }

    private int randomFrostLifetime() {
        return FROST_LIFETIME_MIN + random.nextInt(FROST_LIFETIME_MAX - FROST_LIFETIME_MIN + 1);
    }

}
