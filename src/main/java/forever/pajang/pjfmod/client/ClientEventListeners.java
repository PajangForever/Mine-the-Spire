package forever.pajang.pjfmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.content.ModItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = PajangForeversMod.MODID, value = Dist.CLIENT)
public final class ClientEventListeners {
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player, HumanoidArm arm, ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
                int invert = arm == HumanoidArm.RIGHT ? 1 : -1;
                poseStack.translate(invert * 0.56F, -0.52F + 0.7f, -0.72F);
                return true;
            }
        }, ModItems.GREATSWORD);

    }

    @SubscribeEvent
    public static void onRenderFirstPersonHand(RenderHandEvent event) {

    }
}
