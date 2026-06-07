package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

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

}
