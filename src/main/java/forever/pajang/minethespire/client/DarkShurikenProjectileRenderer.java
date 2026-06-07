package forever.pajang.minethespire.client;

import com.mojang.blaze3d.vertex.PoseStack;
import forever.pajang.minethespire.content.entity.DarkShurikenProjectile;
import forever.pajang.minethespire.content.entity.ModProjectile;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class DarkShurikenProjectileRenderer extends EntityRenderer<DarkShurikenProjectile, DarkShurikenProjectileRenderer.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public DarkShurikenProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    @Override
    protected int getBlockLightLevel(DarkShurikenProjectile entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        // item model center at (0, 2/16, 0)
        poseStack.rotateAround(new Quaternionf().rotateY(state.yRot * Mth.PI / 180F)
                .rotateX(state.xRot* Mth.PI / 180F)
                .rotateZ(state.ageInTicks * 30.0F* Mth.PI / 180F), 0F, 2 / 16F, 0F);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(DarkShurikenProjectile entity, RenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.xRot = entity.getXRot(partialTicks) + 90;
        state.yRot = -entity.getYRot(partialTicks);
        this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }

    public static class RenderState extends ThrownItemRenderState {
        public float xRot;
        public float yRot;
    }
}
