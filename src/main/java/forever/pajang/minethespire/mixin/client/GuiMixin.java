package forever.pajang.minethespire.mixin.client;

import forever.pajang.minethespire.client.OverhealHudRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow public int leftHeight;

    @Inject(method = "extractHealthLevel", at = @At("TAIL"))
    private void minethespire$renderOverheal(GuiGraphicsExtractor graphics, CallbackInfo ci) {
        this.leftHeight += OverhealHudRenderer.render(graphics, this.leftHeight);
    }
}
