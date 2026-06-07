package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkShurikenProjectile extends ThrowableItemProjectile {
    private static final float DAMAGE = 5.0F;
    private static final Logger log = LoggerFactory.getLogger(DarkShurikenProjectile.class);

    public DarkShurikenProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DarkShurikenProjectile(Level level, LivingEntity owner, ItemStack stack) {
        super(ModEntityTypes.DARK_SHURIKEN_PROJECTILE.get(), owner, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.DARK_SHURIKEN.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!level().isClientSide()) {
            Entity target = result.getEntity();
            Entity owner = getOwner();
            DamageSource source = owner == null ? level().damageSources().generic() : level().damageSources().thrown(this, owner);
            target.hurt(source, DAMAGE);
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level() instanceof ServerLevel serverLevel) {
            Vec3 pos = result.getLocation();
            playEffects(serverLevel, pos);
            dropSelf(serverLevel, pos);
            discard();
        }
    }

    private void playEffects(ServerLevel level, Vec3 pos) {
        level.sendParticles(ParticleTypes.WITCH, pos.x, pos.y, pos.z, 18, 0.18D, 0.18D, 0.18D, 0.04D);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y, pos.z, 12, 0.12D, 0.12D, 0.12D, 0.02D);
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 8, 0.1D, 0.1D, 0.1D, 0.01D);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.TRIDENT_HIT_GROUND, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    private void dropSelf(ServerLevel level, Vec3 pos) {
        if (getOwner() instanceof Player player && player.hasInfiniteMaterials()) return;
        ItemStack drop = getItem().copyWithCount(1);
        spawnAtLocation(level, drop, pos.subtract(position()));
    }

    @Override
    protected void updateRotation() {
        Vec2 vec = this.getDeltaMovement().rotation();
        if (!Float.isFinite(vec.x)) return;
        if (!Float.isFinite(vec.y)) return;
        this.setXRot(lerpRotation(this.xRotO, vec.x));
        this.setYRot(lerpRotation(this.yRotO, vec.y));
    }
}
