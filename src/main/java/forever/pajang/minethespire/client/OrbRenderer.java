package forever.pajang.minethespire.client;

import com.mojang.blaze3d.vertex.PoseStack;
import forever.pajang.minethespire.content.entity.OrbEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class OrbRenderer extends EntityRenderer<OrbEntity, OrbRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = OrbModel.LAYER_LOCATION;

    private final OrbModel model;

    public OrbRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new OrbModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    protected int getBlockLightLevel(OrbEntity entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void submit(OrbRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitNodeCollector.submitModel(this.model, state, poseStack,
                RenderTypes.breezeWind(state.texture, this.xOffset(state.ageInTicks) % 1.0F, 0.0F),
                state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public OrbRenderState createRenderState() {
        return new OrbRenderState();
    }

    @Override
    public void extractRenderState(OrbEntity entity, OrbRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.animationState.startIfStopped(entity.tickCount);
        state.texture = entity.getRenderTexture();
        state.activated = entity.isEvoked();
        state.nameTag = entity.getDisplayTag();
        if (state.nameTag != null) {
            state.nameTagAttachment = new Vec3(0.0D, entity.getBbHeight(), 0.0D);
        }
    }

    protected float xOffset(float t) {
        return t * 0.03F;
    }

}
