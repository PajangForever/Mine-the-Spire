package forever.pajang.minethespire.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.joml.Quaternionf;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public final class CuriosClient {

    public static class Events{
        @SubscribeEvent
        public static void registerRenderers (FMLClientSetupEvent event){
            ICurioRenderer.register(ModItems.DEFECT_MASK.asItem(), MaskRenderer::new);
        }
    }

    private static final class MaskRenderer implements ICurioRenderer {
        @Override
        public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
                ItemStack stack,
                SlotContext slotContext,
                PoseStack poseStack,
                SubmitNodeCollector submitNodeCollector,
                int packedLight,
                S renderState,
                RenderLayerParent<S, M> renderLayerParent,
                EntityRendererProvider.Context context,
                float yRotation,
                float xRotation) {
            M model = renderLayerParent.getModel();
            if (!(model instanceof HeadedModel headedModel)) {
                return;
            }

            ItemStackRenderState itemRenderState = new ItemStackRenderState();
            context.getItemModelResolver().updateForLiving(itemRenderState, stack, ItemDisplayContext.HEAD, slotContext.entity());
            if (itemRenderState.isEmpty()) {
                return;
            }

            poseStack.pushPose();
            model.root().translateAndRotate(poseStack);
            headedModel.translateToHead(poseStack);
            poseStack.mulPose(new Quaternionf().rotationZ(45f * Mth.PI / 180F));
            poseStack.translate(-0.2, 0.55, -0.55);
            CustomHeadLayer.translateToHead(poseStack, CustomHeadLayer.Transforms.DEFAULT);
            itemRenderState.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, renderState.outlineColor);
            poseStack.popPose();
        }
    }
}
