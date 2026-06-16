package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@EventBusSubscriber(modid = MineTheSpire.MODID, value = Dist.CLIENT)
public final class ClientModEventListeners {
    private ClientModEventListeners() {
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.DARK_SHURIKEN_PROJECTILE.get(), DarkShurikenProjectileRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.BOUNCING_FLASK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.LIGHTNING_CHARGE_BALL.get(), OrbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.FROST_CHARGE_BALL.get(), OrbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.DARK_CHARGE_BALL.get(), OrbRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PLASMA_CHARGE_BALL.get(), OrbRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(OrbRenderer.LAYER_LOCATION, OrbModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, MineTheSpire.id("combat_state"), CombatStateHudRenderer::render);
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, MineTheSpire.id("blocking_value"), BlockingValueHudRenderer::renderLayer);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerMobEffect(MobEffectLevelClientExtensions.INSTANCE, ModEffects.VENIN, ModEffects.PLASMA_CHARGE);
    }
}
