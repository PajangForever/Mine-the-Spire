package forever.pajang.minethespire.compat.jade;

import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum BlockingValueDataProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            float blockingValue = (float)living.getAttributeValue(ModAttributes.BLOCKING_VALUE);
            if (blockingValue > 0.0F) {
                data.putFloat(BlockingValueJade.TAG_AMOUNT, blockingValue);
            }
        }
    }

    @Override
    public boolean shouldRequestData(EntityAccessor accessor) {
        return accessor.getEntity() instanceof LivingEntity;
    }

    @Override
    public Identifier getUid() {
        return BlockingValueJade.UID;
    }
}
