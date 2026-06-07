package forever.pajang.minethespire.mixin;

import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
@Deprecated
@Mixin(targets = "net.minecraft.world.item.Item")
public abstract class ItemMixin implements IItemExtension {

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return IItemExtension.super.supportsEnchantment(stack, enchantment) || enchantment.is(RegisterCore.SUPPORT_ALL);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
//        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_EXHAUST.get())) {
//            int damageBefore = stack.getDamageValue();
//            if (damage > damageBefore) {
//                IItemExtension.super.setDamage(stack, stack.getMaxDamage());
//                return;
//            }
//        }
        IItemExtension.super.setDamage(stack, damage);
    }

}
