package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.impl.ChargeBallManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public abstract class ChargeBallEntity extends Entity {
    private static final EntityDataAccessor<Boolean> ACTIVATED = SynchedEntityData.defineId(ChargeBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SLOT_EXEMPT = SynchedEntityData.defineId(ChargeBallEntity.class, EntityDataSerializers.BOOLEAN);
    private static final double ORBIT_RADIUS = 1.0D;
    private static final Vec3 WORLD_UP = new Vec3(0.0D, 1.0D, 0.0D);
    private static final double OWNER_MAX_DISTANCE = 48.0D;
    private static final int ACTIVATED_MAX_LIFETIME = 10 * 20;

    private UUID ownerUuid;
    private boolean removedFromOwnerManager;
    private long birthGameTime;
    private int activatedTicks;

    protected ChargeBallEntity(EntityType<? extends ChargeBallEntity> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
        setNoGravity(true);
    }

    protected ChargeBallEntity(EntityType<? extends ChargeBallEntity> entityType, Level level, LivingEntity owner) {
        this(entityType, level);
        setOwner(owner);
    }

    public void setOwner(LivingEntity owner) {
        this.ownerUuid = owner.getUUID();
    }

    public long getBirthGameTime() {
        return birthGameTime;
    }

    public void setBirthGameTime(long birthGameTime) {
        this.birthGameTime = birthGameTime;
    }

    public boolean isOwnedBy(LivingEntity owner) {
        return ownerUuid != null && ownerUuid.equals(owner.getUUID());
    }

    public boolean isActivated() {
        return entityData.get(ACTIVATED);
    }

    public boolean countsTowardLimit() {
        return !entityData.get(SLOT_EXEMPT);
    }

    public void setSlotExempt(boolean slotExempt) {
        entityData.set(SLOT_EXEMPT, slotExempt);
    }

    public Identifier getRenderTexture() {
        return isActivated() ? activatedTexture() : idleTexture();
    }

    public ItemStack createRenderStack() {
        ItemStack baseStack = baseRenderStack();
        if (!isActivated()) {
            return baseStack;
        }
        ItemStack activatedStack = baseStack.copy();
        activatedStack.set(DataComponents.ITEM_MODEL, MineTheSpire.id(activatedModelPath()));
        return activatedStack;
    }

    public Component getChargeText() {
        return null;
    }

    public abstract ChargeBallEntity createCopy(LivingEntity owner);

    public final void applyScheduledEffectFromManager() {
        if (!level().isClientSide() && !isRemoved() && !isActivated()) {
            applyScheduledEffect();
        }
    }

    public final void activateFromManager() {
        if (!level().isClientSide() && !isRemoved() && !isActivated()) {
            markActivated();
            activateEffect();
        }
    }

    protected abstract void tickOwned(LivingEntity owner, ChargeBallManager manager);

    protected abstract void applyScheduledEffect();

    protected abstract void activateEffect();

    protected abstract ItemStack baseRenderStack();

    protected abstract String activatedModelPath();

    protected abstract Identifier idleTexture();

    protected abstract Identifier activatedTexture();

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ACTIVATED, false);
        builder.define(SLOT_EXEMPT, false);
    }

    @Override
    public void tick() {
        super.tick();
        noPhysics = true;
        setNoGravity(true);

        if (level().isClientSide()) {
            return;
        }

        LivingEntity owner = getOwner();
        if (owner == null || owner.level() != level() || owner.isRemoved() || !owner.isAlive() || distanceToSqr(owner) > OWNER_MAX_DISTANCE * OWNER_MAX_DISTANCE) {
            discard();
            return;
        }

        ChargeBallManager manager = ChargeBallManager.get(owner);
        if (isActivated() && ++activatedTicks >= ACTIVATED_MAX_LIFETIME) {
            discard();
            return;
        }
        if (!isActivated() && !manager.containsBall(this)) {
            discard();
            return;
        }
        if (!isRemoved()) {
            tickOwned(owner, manager);
        }
    }

    protected void tickOrbit(LivingEntity owner) {
        ChargeBallManager manager = ChargeBallManager.get(owner);
        int count = Math.max(1, manager.getBallCount());
        int index = manager.getBallIndex(this);
        if (index < 0) {
            index = Math.floorMod(getId(), count);
        }
        float orbitPhase = (index * Mth.TWO_PI / count) + tickCount * 0.08F;
        double radius = ORBIT_RADIUS + 0.15D * Mth.sin((tickCount + getId()) * 0.07F);
        Vec3 normal = owner.getLookAngle().normalize();
        Vec3 right = normal.cross(WORLD_UP);
        if (right.lengthSqr() < 1.0E-4D) {
            right = Vec3.directionFromRotation(0.0F, owner.getYRot() + 90.0F);
        }
        else {
            right = right.normalize();
        }
        Vec3 up = right.cross(normal).normalize();
        Vec3 center = owner.position().add(0.0D, 1.05D, 0.0D);
        Vec3 orbitOffset = right.scale(Mth.cos(orbitPhase) * radius).add(up.scale(Mth.sin(orbitPhase) * radius));
        Vec3 targetPos = center.add(orbitOffset);
        Vec3 movement = targetPos.subtract(position()).scale(0.5d);
        move(MoverType.SELF, movement);
//        setDeltaMovement(movement);
    }

    protected void markActivated() {
        entityData.set(ACTIVATED, true);
        activatedTicks = 0;
        removeFromOwnerManager();
    }

    protected LivingEntity getOwner() {
        if (ownerUuid == null) {
            return null;
        }

        Entity entity = level().getEntityInAnyDimension(ownerUuid);
        return entity instanceof LivingEntity living ? living : null;
    }

    protected boolean isOwnedEntity(Entity entity) {
        return entity instanceof LivingEntity living && isOwnedBy(living);
    }

    protected float focusAdjustedAmount(LivingEntity owner, float baseAmount) {
        return Math.max(0.0F, baseAmount + (float) owner.getAttributeValue(ModAttributes.FOCUS));
    }

    protected void dissipate(ServerLevel level) {
        Vec3 pos = position().add(0.0D, getBbHeight() * 0.5D, 0.0D);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 0.75F, 1.5F);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 40, 0.35D, 0.35D, 0.35D, 0.08D);
        level.sendParticles(ParticleTypes.POOF, pos.x, pos.y, pos.z, 18, 0.25D, 0.25D, 0.25D, 0.01D);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean isPickable() {
        return false;
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
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float damage) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith(Entity entity) {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        removeFromOwnerManager();
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        Optional<String> owner = input.getString("Owner");
        owner.ifPresent(value -> {
            try {
                ownerUuid = UUID.fromString(value);
            }
            catch (IllegalArgumentException ignored) {
                ownerUuid = null;
            }
        });
        entityData.set(ACTIVATED, input.getBooleanOr("Activated", false));
        entityData.set(SLOT_EXEMPT, input.getBooleanOr("SlotExempt", false));
        birthGameTime = input.getLongOr("BirthGameTime", 0L);
        activatedTicks = input.getIntOr("ActivatedTicks", 0);
        readChargeBallSaveData(input);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        if (ownerUuid != null) {
            output.putString("Owner", ownerUuid.toString());
        }
        output.putBoolean("Activated", entityData.get(ACTIVATED));
        output.putBoolean("SlotExempt", entityData.get(SLOT_EXEMPT));
        output.putLong("BirthGameTime", birthGameTime);
        output.putInt("ActivatedTicks", activatedTicks);
        addChargeBallSaveData(output);
    }

    protected void readChargeBallSaveData(ValueInput input) {
    }

    protected void addChargeBallSaveData(ValueOutput output) {
    }

    private void removeFromOwnerManager() {
        if (removedFromOwnerManager || level().isClientSide()) {
            return;
        }

        LivingEntity owner = getOwner();
        if (owner != null) {
            ChargeBallManager.get(owner).removeBall(this);
        }
        removedFromOwnerManager = true;
    }
}
