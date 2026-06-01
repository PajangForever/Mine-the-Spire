package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.curios.CuriosSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Relic extends Item {
    public Relic(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        if (CuriosCompat.isLoaded()) return;
        tick(itemStack, level, owner, slot);
    }

    public void tick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {

    }

    public void addAttributeModifiers(ItemStack stack, AttributeModifierAdder adder) {

    }

    public Set<CuriosSlot> getCuriosSlots() {
        return Set.of(CuriosSlot.SPIRE_RELIC);
    }

    public boolean isInCuriosOrEquipmentSlot(LivingEntity entity) {
        Predicate<ItemStack> predicate = stack -> stack.is(this);
        if (CuriosCompat.isLoaded()) {
            return CuriosCompat.hasCurio(entity, predicate);
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (predicate.test(entity.getItemBySlot(slot))) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCuriosOrInventorySlot(Player player) {
        Predicate<ItemStack> predicate = stack -> stack.is(this);
        if (CuriosCompat.isLoaded()) {
            return CuriosCompat.hasCurio(player, predicate);
        }
        else return player.getInventory().contains(predicate);
    }

    public Set<ItemStack> getFromCuriosOrEquipmentSlot(LivingEntity entity) {
        Predicate<ItemStack> predicate = stack -> stack.is(this);
        if (CuriosCompat.isLoaded()) {
            return CuriosCompat.getCurioItems(entity, predicate);
        }

        Set<ItemStack> result = new HashSet<>();
        if (entity instanceof Player player) {
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (predicate.test(stack)) {
                    result.add(stack);
                }
            }
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (predicate.test(stack)) {
                result.add(stack);
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface AttributeModifierAdder {
        void addModifier(Holder<Attribute> attribute, AttributeModifier modifier, String... slot);
    }
}
