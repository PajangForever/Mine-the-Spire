package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = MineTheSpire.MODID, value = Dist.CLIENT)
public final class ClientEventListeners {
    private ClientEventListeners() {
    }

    @SubscribeEvent
    public static void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.isAttack()){
            DarkShurikenItem.onAttackButtonDown(mc, event::setSwingHand);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        DarkShurikenItem.tickCharge(mc);
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
        Holder<MobEffect>[] effects = MineTheSpire.REG.getRenderEffectLevels();
        if (effects.length > 0){
            event.registerMobEffect(MobEffectLevelClientExtensions.INSTANCE, effects);
        }
    }
}
