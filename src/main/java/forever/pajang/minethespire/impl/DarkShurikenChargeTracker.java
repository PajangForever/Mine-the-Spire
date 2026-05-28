package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class DarkShurikenChargeTracker {
    private DarkShurikenChargeTracker() {
    }

    public static void trigger(Player player) {
        if (player.level().isClientSide() || !player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            return;
        }

        player.addEffect(new MobEffectInstance(ModEffects.MIND_BLOOM, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
        enchantEquipment(player);
    }

    private static void enchantEquipment(Player player) {
        var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        enchantStack(player.getItemBySlot(EquipmentSlot.HEAD), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.CHEST), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.LEGS), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.FEET), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.OFFHAND), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.MAINHAND), registry);
    }

    private static void enchantStack(ItemStack stack, net.minecraft.core.HolderLookup.RegistryLookup<Enchantment> registry) {
        if (stack.isEmpty()) {
            return;
        }
        for (Holder<Enchantment> holder : registry.listElements().toList()) {
            Enchantment enchantment = holder.value();
            if (enchantment.canEnchant(stack)) {
                stack.enchant(holder, enchantment.definition().maxLevel());
            }
        }
    }
}
