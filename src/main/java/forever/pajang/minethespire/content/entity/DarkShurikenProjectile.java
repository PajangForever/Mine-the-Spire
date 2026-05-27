package forever.pajang.minethespire.content.entity;

import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DarkShurikenProjectile extends ThrowableItemProjectile {
    private static final float DAMAGE = 5.0F;

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
        if (!level().isClientSide()) {
            discard();
        }
    }
}
