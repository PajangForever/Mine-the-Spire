package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;

public class ChargeBallModel extends EntityModel<ChargeBallRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(MineTheSpire.id("charge_ball"), "main");
    private final ModelPart in;
    private final ModelPart out;

    public ChargeBallModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.in = root.getChild("in");
        this.out = root.getChild("out");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("in",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("out",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2.5F, 0.5F, -2.5F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 16);
    }

    @Override
    public void setupAnim(ChargeBallRenderState state) {
        super.setupAnim(state);
        float rotation = state.ageInTicks * 8.0F * ((float) Math.PI / 180.0F);
        this.in.yRot = -rotation;
        this.out.yRot = rotation;
    }
}
