package forever.pajang.minethespire.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import forever.pajang.minethespire.content.entity.DarkShurikenProjectile;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;

public class DarkShurikenProjectileRenderer extends EntityRenderer<DarkShurikenProjectile, DarkShurikenProjectileRenderState> {
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
    public void submit(DarkShurikenProjectileRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(1.1F, 1.1F, 1.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.ageInTicks * 30.0F));
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public DarkShurikenProjectileRenderState createRenderState() {
        return new DarkShurikenProjectileRenderState();
    }

    @Override
    public void extractRenderState(DarkShurikenProjectile entity, DarkShurikenProjectileRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.xRot = entity.getXRot(partialTicks);
        state.yRot = entity.getYRot(partialTicks);
        this.itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }
}
