package forever.pajang.minethespire.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("screenEffectRenderer")
    ScreenEffectRenderer minethespire$getScreenEffectRenderer();
}
