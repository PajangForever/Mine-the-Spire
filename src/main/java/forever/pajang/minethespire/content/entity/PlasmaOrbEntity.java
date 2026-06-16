package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.specials.OrbType;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class PlasmaOrbEntity extends OrbEntity {
    private static final Identifier IDLE_TEXTURE = MineTheSpire.id("textures/entity/orbs/plasma.png");
    private static final Identifier ACTIVATED_TEXTURE = MineTheSpire.id("textures/entity/orbs/plasma_evoked.png");

    public PlasmaOrbEntity(EntityType<? extends PlasmaOrbEntity> entityType, Level level) {
        super(entityType, level);
    }

    public PlasmaOrbEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.PLASMA_CHARGE_BALL.get(), level, owner);
    }

    @Override
    public OrbType getOrbType() {
        return OrbType.Plasma;
    }

    @Override
    public OrbEntity createCopy(LivingEntity owner) {
        return new PlasmaOrbEntity(owner.level(), owner);
    }

    @Override
    public void setOwner(LivingEntity owner) {
        super.setOwner(owner);
        attachBuff(owner);
    }

    @Override
    public void onRemoval(RemovalReason reason) {
        removeBuff();
        super.onRemoval(reason);
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        getOwner().ifPresent(this::attachBuff);
    }

    @Override
    public void passiveAction(ServerLevel serverLevel, LivingEntity owner) {
        super.passiveAction(serverLevel, owner);
    }

    @Override
    public void evokeAction(ServerLevel serverLevel, LivingEntity owner) {
        MobEffectInstance existing = owner.getEffect(ModEffects.PLASMA_CHARGE);
        int amplifier = existing == null ? 0 : existing.getAmplifier() + 1;
        owner.removeEffect(ModEffects.PLASMA_CHARGE);
        owner.addEffect(new MobEffectInstance(ModEffects.PLASMA_CHARGE, 600, amplifier, false, true, true));
        playEvokeEffects(owner);
        discard();
    }

    @Override
    protected Identifier normalTexture() {
        return IDLE_TEXTURE;
    }

    @Override
    protected Identifier evokeTexture() {
        return ACTIVATED_TEXTURE;
    }

    public void attachBuff(LivingEntity owner) {
        AttributeInstance instance = owner.getAttribute(ModAttributes.ORB_PASSIVE_SPEED);
        if (instance == null || instance.hasModifier(getBuffId())) {
            return;
        }
        instance.addTransientModifier(new AttributeModifier(getBuffId(), 0.2d, AttributeModifier.Operation.ADD_VALUE));
    }

    public void removeBuff() {
        Optional<LivingEntity> owner = getOwner();
        if (owner.isEmpty() || owner.get().level().isClientSide()) {
            return;
        }

        AttributeInstance instance = owner.get().getAttribute(ModAttributes.ORB_PASSIVE_SPEED);
        if (instance != null) {
            instance.removeModifier(getBuffId());
        }
    }

    private Identifier getBuffId() {
        return MineTheSpire.id("plasma_buff_" + getUUID());
    }

    private void playEvokeEffects(LivingEntity owner) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 pos = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D);
        serverLevel.playSound(null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8F, 1.85F);
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 28, 0.35D, 0.35D, 0.35D, 0.04D);
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, 12, 0.22D, 0.22D, 0.22D, 0.02D);
    }
}
