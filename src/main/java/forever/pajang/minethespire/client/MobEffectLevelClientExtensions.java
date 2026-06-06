package forever.pajang.minethespire.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

public class MobEffectLevelClientExtensions implements IClientMobEffectExtensions {
    public static final MobEffectLevelClientExtensions INSTANCE = new MobEffectLevelClientExtensions();
    private static final int ICON_SIZE = 18;

    private MobEffectLevelClientExtensions() {
    }

    @Override
    public boolean renderGuiIcon(MobEffectInstance effect, Gui gui, GuiGraphicsExtractor graphics, int x, int y, float z, float alpha) {
        int iconX = x + 3;
        int iconY = y + 3;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()), iconX, iconY, ICON_SIZE, ICON_SIZE, ARGB.white(alpha));
        renderLevel(effect, graphics, iconX, iconY, ARGB.colorFromFloat(alpha, 1.0F, 1.0F, 1.0F));
        return true;
    }

    @Override
    public boolean renderInventoryIcon(MobEffectInstance effect, AbstractContainerScreen<?> screen, GuiGraphicsExtractor graphics, int x, int y, int z) {
        int iconY = y + 7;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()), x, iconY, ICON_SIZE, ICON_SIZE);
        renderLevel(effect, graphics, x, iconY, 0xFFFFFFFF);
        return true;
    }

    private static void renderLevel(MobEffectInstance effect, GuiGraphicsExtractor graphics, int iconX, int iconY, int color) {
        String text = Integer.toString(effect.getAmplifier() + 1);
        Font font = Minecraft.getInstance().font;
        int textX = iconX + ICON_SIZE - font.width(text);
        int textY = iconY + ICON_SIZE - font.lineHeight;
        graphics.text(font, text, textX, textY, color, true);
    }
}
