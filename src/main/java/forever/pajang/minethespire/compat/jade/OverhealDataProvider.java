package forever.pajang.minethespire.compat.jade;

import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public enum OverhealDataProvider implements IServerDataProvider<EntityAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, EntityAccessor accessor) {
        if (accessor.getEntity() instanceof LivingEntity living) {
            float overheal = (float)living.getAttributeValue(ModAttributes.OVERHEAL);
            if (overheal > 0.0F) {
                data.putFloat(OverhealJade.TAG_AMOUNT, overheal);
            }
        }
    }

    @Override
    public boolean shouldRequestData(EntityAccessor accessor) {
        return accessor.getEntity() instanceof LivingEntity;
    }

    @Override
    public Identifier getUid() {
        return OverhealJade.UID;
    }
}
