package forever.pajang.minethespire.client;

import com.mojang.blaze3d.vertex.PoseStack;
import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.entity.ChargeBallEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;

public class LightningChargeBallRenderer extends EntityRenderer<ChargeBallEntity, ChargeBallRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MineTheSpire.id("charge_ball"), "main");

    private final ChargeBallModel model;

    public LightningChargeBallRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ChargeBallModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    protected int getBlockLightLevel(ChargeBallEntity entity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void submit(ChargeBallRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        submitNodeCollector.submitModel(
                this.model,
                state,
                poseStack,
                RenderTypes.breezeWind(state.texture, this.xOffset(state.ageInTicks) % 1.0F, 0.0F),
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                state.outlineColor,
                null
        );
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public ChargeBallRenderState createRenderState() {
        return new ChargeBallRenderState();
    }

    @Override
    public void extractRenderState(ChargeBallEntity entity, ChargeBallRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.texture = entity.getRenderTexture();
        state.activated = entity.isActivated();
    }

    protected float xOffset(float t) {
        return t * 0.03F;
    }

}
