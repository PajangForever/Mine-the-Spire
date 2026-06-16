package forever.pajang.minethespire.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;

public class OrbRenderState extends EntityRenderState {
    public final AnimationState animationState = new AnimationState();
    public Identifier texture;
    public boolean activated;
}
