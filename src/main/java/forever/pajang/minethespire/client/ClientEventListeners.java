package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import forever.pajang.minethespire.mixin.client.GameRendererAccessor;
import forever.pajang.minethespire.network.DarkShurikenChargePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = MineTheSpire.MODID, value = Dist.CLIENT)
public final class ClientEventListeners {
    private static boolean chargingDarkShuriken;
    private static int darkShurikenChargeTicks;

    @SubscribeEvent
    public static void onInput(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || !event.isAttack()) {
            return;
        }
        if (mc.player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            event.setCanceled(true);
            event.setSwingHand(false);
            beginCharge(mc);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            chargingDarkShuriken = false;
            darkShurikenChargeTicks = 0;
            return;
        }
        if (!chargingDarkShuriken) {
            return;
        }
        if (mc.screen != null || !mc.options.keyAttack.isDown() || !mc.player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            endCharge(mc, true);
            return;
        }
        if (!mc.player.isUsingItem()) {
            mc.player.startUsingItem(InteractionHand.MAIN_HAND);
        }
        if (darkShurikenChargeTicks < DarkShurikenItem.MIND_BLOOM_CHARGE_TICKS) {
            darkShurikenChargeTicks++;
        }
        else if (mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            mc.player.stopUsingItem();
        }
    }

    private static void beginCharge(Minecraft mc) {
        if (chargingDarkShuriken) {
            return;
        }
        chargingDarkShuriken = true;
        darkShurikenChargeTicks = 0;
        mc.player.startUsingItem(InteractionHand.MAIN_HAND);
        mc.gameRenderer.displayItemActivation(mc.player.getMainHandItem().copyWithCount(1));
        if (mc.getConnection() != null) {
            ClientPacketDistributor.sendToServer(new DarkShurikenChargePayload(true));
        }
    }

    private static void endCharge(Minecraft mc, boolean notifyServer) {
        if (!chargingDarkShuriken) {
            return;
        }
        chargingDarkShuriken = false;
        darkShurikenChargeTicks = 0;
        if (mc.player.isUsingItem() && mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            mc.player.stopUsingItem();
        }
        ((GameRendererAccessor) mc.gameRenderer).minethespire$getScreenEffectRenderer().resetItemActivation();
        if (notifyServer && mc.getConnection() != null) {
            ClientPacketDistributor.sendToServer(new DarkShurikenChargePayload(false));
        }
    }
}
