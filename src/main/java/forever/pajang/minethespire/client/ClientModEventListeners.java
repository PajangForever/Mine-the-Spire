package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MineTheSpire.MODID, value = Dist.CLIENT)
public final class ClientModEventListeners {
    private ClientModEventListeners() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.DARK_SHURIKEN_PROJECTILE.get(), DarkShurikenProjectileRenderer::new);
    }
}
