package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.content.ModAttributes;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public abstract class OrbEntity extends Entity {
    private static final double OWNER_MAX_DISTANCE = 48.0D;
    private static final int EVOKED_MAX_LIFETIME = 10 * 20;
    private static final EntityDataAccessor<Integer> EVOKE_TICKS = SynchedEntityData.defineId(OrbEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> OWNER = SynchedEntityData.defineId(OrbEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> TARGET = SynchedEntityData.defineId(OrbEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);

    protected OrbEntity(EntityType<? extends OrbEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        setNoGravity(true);
    }

    protected OrbEntity(EntityType<? extends OrbEntity> entityType, Level level, LivingEntity owner) {
        this(entityType, level);
        setOwner(owner);
    }

    public void setOwner(LivingEntity owner) {
        getEntityData().set(OWNER, Optional.of(EntityReference.of(owner)));
    }

    public boolean isOwnedBy(LivingEntity entity) {
        Optional<LivingEntity> opt = getOwner();
        return opt.isPresent() && opt.get() == entity;
    }

    protected Optional<LivingEntity> getOwner() {
        return getEntityData().get(OWNER).map(reference -> EntityReference.getLivingEntity(reference, level()));
    }

    public boolean isEvoked() {
        return getEvokedTicks() >= 0;
    }

    public Optional<LivingEntity> getTarget() {
        return getEntityData().get(TARGET)
                .map(reference -> EntityReference.getLivingEntity(reference, level()));
    }

    public void setTarget(LivingEntity target) {
        getEntityData().set(TARGET, target == null ? Optional.empty() : Optional.of(EntityReference.of(target)));
    }

    public abstract OrbType getOrbType();

    public abstract OrbEntity createCopy(LivingEntity owner);

    public void passiveAction(ServerLevel serverLevel, LivingEntity owner){
    };

    public void evokeAction(ServerLevel serverLevel, LivingEntity owner) {
        setEvokedTicks(0);
    };

    protected abstract Identifier normalTexture();

    protected abstract Identifier evokeTexture();

    @Override
    public void tick() {
        super.tick();
        Optional<LivingEntity> ownerOpt = getOwner();
        if (!level().isClientSide()){
            ServerLevel serverLevel = (ServerLevel) level();
            if (ownerOpt.isEmpty() || isInvalid(ownerOpt.get())) {
                dissipate();
                return;
            }
            LivingEntity owner = ownerOpt.get();
            if (isEvoked()) {
                tickEvoked(serverLevel, owner);
            } else {
                tickNormal(serverLevel, owner);
            }
            setPos(position().add(getDeltaMovement()));
        } else {
            setPos(position().add(getDeltaMovement()));
        }
//        else {
//            if (ownerOpt.isEmpty()) return;
//            if (isEvoked()) {
//                moveEvokedPath(OrbManager.get(ownerOpt.get()));
//            } else {
//                moveOrbit(OrbManager.get(ownerOpt.get()));
//            }
//        }
//        setPos(position().add(getDeltaMovement()));

    }

    protected void tickNormal(ServerLevel serverLevel, LivingEntity owner) {
        OrbManager manager = OrbManager.get(owner);
        if (!manager.containsOrb(this)) {
            dissipate();
            return;
        }
        moveOrbit(manager);
        if (manager.getPassiveAction(this)) {
            if (!isEvoked()) {
                passiveAction(serverLevel, owner);
            }
        }
    }

    protected void moveOrbit(OrbManager manager) {
        Vec3 orbitPos = manager.getOrbOrbit(this);
        setDeltaMovement(orbitPos.subtract(position()).scale(0.8d));
    }

    protected void tickEvoked(ServerLevel serverLevel, LivingEntity owner) {
        setEvokedTicks(getEvokedTicks() + 1);
        moveEvokedPath(OrbManager.get(owner));
    }

    protected void moveEvokedPath(OrbManager manager) {

    }

    protected boolean isInvalid(LivingEntity owner) {
        if (isRemoved() || !isAlive()) {
            return true;
        }
        if (owner == null || owner.level() != level() || owner.isRemoved() || !owner.isAlive() || distanceToSqr(owner) > OWNER_MAX_DISTANCE * OWNER_MAX_DISTANCE) {
            return true;
        }
        if (isEvoked() && getEvokedTicks() > EVOKED_MAX_LIFETIME) {
            return true;
        }

        return false;
    }

    public float focusAdjusted(LivingEntity owner, float base) {
        return Math.max(0.0F, base + (float) owner.getAttributeValue(ModAttributes.FOCUS));
    }

    public void dissipate() {
        if (level().isClientSide()) return;
        ServerLevel level = (ServerLevel) level();
        Vec3 pos = position();
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.75F, 1.5F);
        level.sendParticles(ParticleTypes.WHITE_SMOKE, pos.x, pos.y, pos.z, 20, 0.35D, 0.35D, 0.35D, 0.08D);
        discard();
    }

    public Identifier getRenderTexture() {
        return isEvoked() ? evokeTexture() : normalTexture();
    }

    public Component getDisplayTag() {
        return null;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float damage) {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public void onRemoval(RemovalReason reason) {
        getOwner().ifPresent(owner -> {
            OrbManager manager = OrbManager.get(owner);
            manager.removeOrb(this);
        });
        super.onRemoval(reason);
    }

    @Override
    protected final void readAdditionalSaveData(ValueInput input) {
        getEntityData().set(OWNER, Optional.ofNullable(EntityReference.read(input, "Owner")));
        getEntityData().set(TARGET, Optional.ofNullable(EntityReference.read(input, "EvokedTarget")));
        setEvokedTicks(input.getIntOr("EvokeTicks", -1));
        readOrbSaveData(input);
    }

    @Override
    protected final void addAdditionalSaveData(ValueOutput output) {
        getEntityData().get(OWNER).ifPresent(reference -> EntityReference.store(reference, output, "Owner"));
        getEntityData().get(TARGET).ifPresent(reference -> EntityReference.store(reference, output, "EvokedTarget"));
        output.putInt("EvokeTicks", getEvokedTicks());
        addOrbSaveData(output);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(EVOKE_TICKS, -1);
        builder.define(OWNER, Optional.empty());
        builder.define(TARGET, Optional.empty());
        syncOrbData(builder);
    }

    public int getEvokedTicks() {
        return getEntityData().get(EVOKE_TICKS);
    }

    public void setEvokedTicks(int ticks) {
        getEntityData().set(EVOKE_TICKS, ticks);
    }

    protected void readOrbSaveData(ValueInput input) {
    }

    protected void addOrbSaveData(ValueOutput output) {
    }

    protected void syncOrbData(SynchedEntityData.Builder builder) {
    }
}
