package forever.pajang.minethespire.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

final class CuriosApiProxy {
    private CuriosApiProxy() {
    }

    static void registerEvents() {
        NeoForge.EVENT_BUS.register(CuriosEvents.class);
    }

    static boolean hasCurio(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity == null) {
            return false;
        }
        ICuriosItemHandler handler = CuriosApi.getCuriosInventoryOrNull(entity);
        return handler != null && handler.findFirstCurio(predicate).isPresent();
    }

    static void forEachCurio(LivingEntity entity, Consumer<ItemStack> action) {
        if (entity == null) {
            return;
        }
        ICuriosItemHandler handler = CuriosApi.getCuriosInventoryOrNull(entity);
        if (handler == null) {
            return;
        }
        handler.getCurios().forEach((slotId, stacksHandler) -> {
            for (int i = 0; i < stacksHandler.getSlots(); i++) {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    action.accept(stack);
                }
            }
        });
    }

    static Set<ItemStack> getCurioItems(LivingEntity entity, Predicate<ItemStack> predicate) {
        Set<ItemStack> result = new HashSet<>();
        if (entity == null) {
            return result;
        }
        ICuriosItemHandler handler = CuriosApi.getCuriosInventoryOrNull(entity);
        if (handler == null) {
            return result;
        }
        handler.getCurios().forEach((slotId, stacksHandler) -> {
            for (int i = 0; i < stacksHandler.getSlots(); i++) {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                if (!stack.isEmpty() && predicate.test(stack)) {
                    result.add(stack);
                }
            }
        });
        return result;
    }

    static boolean consumeFirstCurio(LivingEntity entity, Predicate<ItemStack> predicate) {
        if (entity == null) {
            return false;
        }
        ICuriosItemHandler handler = CuriosApi.getCuriosInventoryOrNull(entity);
        if (handler == null) {
            return false;
        }
        SlotResult result = handler.findFirstCurio(predicate).orElse(null);
        if (result == null) {
            return false;
        }
        ItemStack stack = result.stack().copy();
        stack.shrink(1);
        handler.setEquippedCurio(result.slotContext().identifier(), result.slotContext().index(), stack);
        return true;
    }
}
