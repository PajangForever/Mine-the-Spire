package forever.pajang.minethespire.compat.curios;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import top.theillusivec4.curios.common.data.CuriosSlotResources;

public final class PatchForCurios {
    @SubscribeEvent
    public static void curiosBugPatch(ServerStartedEvent event) {
        if (event.getServer().isSingleplayer()) {
            CuriosSlotResources.CLIENT.setSlots(CuriosSlotResources.SERVER.getSlots());
            CuriosSlotResources.CLIENT.setAllEntitySlots(CuriosSlotResources.SERVER.getAllEntitySlots());
        }
    }
}
