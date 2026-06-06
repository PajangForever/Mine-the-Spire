package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.impl.BlockingValueHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ImperviousItem extends Item {
    private static final float BLOCKING_VALUE_AMOUNT = 200.0F;

    public ImperviousItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide()) {
            BlockingValueHandler.add(entity, BLOCKING_VALUE_AMOUNT);
        }
        return result;
    }
}
