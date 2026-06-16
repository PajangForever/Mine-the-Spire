package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.specials.BlockingValueHandler;
import forever.pajang.minethespire.content.specials.OrbType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class FrostOrbEntity extends OrbEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/orbs/frost.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/orbs/frost_evoked.png");

    private int lifetimeTicks = -1;

    public FrostOrbEntity(EntityType<? extends FrostOrbEntity> entityType, Level level) {
        super(entityType, level);
    }

    public FrostOrbEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.FROST_CHARGE_BALL.get(), level, owner);
        lifetimeTicks = randomFrostLifetime();
    }

    @Override
    public OrbType getOrbType() {
        return OrbType.Frost;
    }

    @Override
    public OrbEntity createCopy(LivingEntity owner) {
        return new FrostOrbEntity(owner.level(), owner);
    }

    @Override
    public void tick() {
        super.tick();
        if (lifetimeTicks > 0) {
            lifetimeTicks--;
            if (lifetimeTicks == 0) {
                dissipate();
            }
        }
    }

    @Override
    public void passiveAction(ServerLevel serverLevel, LivingEntity owner) {
        super.passiveAction(serverLevel, owner);
        BlockingValueHandler.add(owner, focusAdjusted(owner, 5f));
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.CHAIN_FALL, SoundSource.PLAYERS, 0.3F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 16, 0.18D, 0.18D, 0.18D, 0.01D);
    }

    @Override
    public void evokeAction(ServerLevel serverLevel, LivingEntity owner) {
        super.evokeAction(serverLevel, owner);
        BlockingValueHandler.add(owner, focusAdjusted(owner, 20f));
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, getX(), getY(), getZ(), SoundEvents.CHAIN_HIT, SoundSource.PLAYERS, 0.9F, 1.4F);
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 30, 0.25D, 0.25D, 0.25D, 0.03D);
        dissipate();
    }

    private int randomFrostLifetime() {
        return 400 + random.nextInt(200);
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
        lifetimeTicks = input.getIntOr("LifetimeTicks", -1);
    }

    @Override
    protected void addOrbSaveData(ValueOutput output) {
        output.putInt("LifetimeTicks", lifetimeTicks);
    }

}
