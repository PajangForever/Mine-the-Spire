package forever.pajang.minethespire.content.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

public abstract class ModProjectile extends ThrowableItemProjectile {
    public ModProjectile(EntityType<? extends ThrowableItemProjectile> type, LivingEntity owner, Level level, ItemStack itemStack) {
        super(type, owner, level, itemStack);
    }

    public ModProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void updateRotation() {
        // make it facing towards its flying direction
        Vec2 vec = this.getDeltaMovement().rotation();
        this.setXRot(lerpRotation(this.xRotO, vec.x));
        this.setYRot(lerpRotation(this.yRotO, vec.y));
    }
}
