package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.curios.ModCuriosSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

public class RelicItem extends Item {

    public static final Lazy<Collection<RelicItem>> RELICS = Lazy.of(() -> MineTheSpire.REG.getRegisteredItems().stream().map(Supplier::get)
            .filter(RelicItem.class::isInstance).map(RelicItem.class::cast).toList()) ;

    public RelicItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
//        if (!CuriosCompat.isLoaded()) {
//            return super.use(level, player, hand);
//        }
//
//        ItemStack stack = player.getItemInHand(hand);
//        if (stack.isEmpty()) {
//            return InteractionResult.PASS;
//        }
//        if (level.isClientSide()) {
//            return InteractionResult.SUCCESS;
//        }
//
//        return CuriosCompat.equipFirstMatchingCurio(player, stack, getCuriosSlots())
//                .map(replacement -> {
//                    player.setItemInHand(hand, replacement);
//                    return (InteractionResult) InteractionResult.SUCCESS_SERVER;
//                })
//                .orElse(InteractionResult.PASS);
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        super.inventoryTick(itemStack, level, owner, slot);
        if (CuriosCompat.isLoaded()) return;
        tickCurios(itemStack, level, owner, slot);
    }

    public void tickCurios(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {

    }

    public Set<String> getCuriosSlots() {
        return Set.of(ModCuriosSlot.SPIRE_RELIC);
    }

    public boolean tryFindAnyFromCuriosOrEquipment(LivingEntity entity) {
        if (CuriosCompat.isLoaded()) {
            return CuriosCompat.tryFindAny(entity, this::equals);
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (stack.is(this)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean tryConsumeFirstFromCuriosOrEquipment(LivingEntity entity) {
        if (CuriosCompat.isLoaded()) {
            return CuriosCompat.tryConsumeFirst(entity, this::equals);
        } else {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = entity.getItemBySlot(slot);
                if (stack.is(this)) {
                    stack.shrink(1);
                    return true;
                }
            }
            return false;
        }
    }

    @FunctionalInterface
    public interface AttributeModifierAdder {
        void addModifier(Holder<Attribute> attribute, AttributeModifier modifier, String... slot);
    }
}
