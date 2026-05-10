package forever.pajang.pjfmod.client;

import forever.pajang.pjfmod.PajangForeversMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = PajangForeversMod.MODID, value = Dist.CLIENT)
public final class ClientEventListeners {
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

    }

    @SubscribeEvent
    public static void onRenderFirstPersonHand(RenderHandEvent event) {

    }
}
