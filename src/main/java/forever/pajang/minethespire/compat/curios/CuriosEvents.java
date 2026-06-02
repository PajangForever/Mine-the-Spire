package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.content.item.Relic;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

public class CuriosEvents {
    @SubscribeEvent
    public static void onCurioAttributeModifiers(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof Relic relic) {
            relic.addCuriosAttributeModifiers(stack, event::addModifier);
        }
    }
}
