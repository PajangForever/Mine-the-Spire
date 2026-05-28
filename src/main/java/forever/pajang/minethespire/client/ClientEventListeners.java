package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.impl.DarkShurikenChargeState;
import forever.pajang.minethespire.mixin.client.GameRendererAccessor;
import forever.pajang.minethespire.network.DarkShurikenChargePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
    private ClientEventListeners() {
    }

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
            return;
        }

        DarkShurikenChargeState state = mc.player.getData(ModAttachments.DARK_SHURIKEN_CHARGE_STATE);
        if (state == null) {
            return;
        }

        if (state.isCharging()) {
            if (mc.screen != null || !mc.options.keyAttack.isDown() || !mc.player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
                abortCharge(mc, state);
            }
            else {
                if (!mc.player.isUsingItem()) {
                    mc.player.startUsingItem(InteractionHand.MAIN_HAND);
                }
                if (state.tickCharge()) {
                    if (mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                        mc.player.stopUsingItem();
                    }
                    if (mc.getConnection() != null) {
                        ClientPacketDistributor.sendToServer(DarkShurikenChargePayload.INSTANCE);
                    }
                    state.startBellRings();
                    playBell(mc);
                }
            }
        }

        if (state.tickBell()) {
            playBell(mc);
        }
    }

    private static void beginCharge(Minecraft mc) {
        if (mc.player == null) {
            return;
        }

        DarkShurikenChargeState state = mc.player.getData(ModAttachments.DARK_SHURIKEN_CHARGE_STATE);
        if (state == null || state.isCharging()) {
            return;
        }

        state.beginCharge();
        mc.player.startUsingItem(InteractionHand.MAIN_HAND);
        mc.gameRenderer.displayItemActivation(mc.player.getMainHandItem().copyWithCount(1));
    }

    private static void abortCharge(Minecraft mc, DarkShurikenChargeState state) {
        if (mc.player == null || state == null || !state.isCharging()) {
            return;
        }

        state.abortCharge();
        if (mc.player.isUsingItem() && mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            mc.player.stopUsingItem();
        }
        ((GameRendererAccessor) mc.gameRenderer).minethespire$getScreenEffectRenderer().resetItemActivation();
    }

    private static void playBell(Minecraft mc) {
        if (mc.player != null && mc.level != null) {
            mc.level.playLocalSound(mc.player, SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    public static void displayLizardTailActivation() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.gameRenderer.displayItemActivation(ModItems.LIZARD_TAIL.get().getDefaultInstance());
        }
    }
}
