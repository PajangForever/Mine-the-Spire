package forever.pajang.minethespire.client;

import forever.pajang.minethespire.Config;
import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.impl.CombatState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class CombatStateHudRenderer {
    private static final Identifier COMBAT = MineTheSpire.id("hud/combat_state_combat");
    private static final Identifier IDLE = MineTheSpire.id("hud/combat_state_idle");
    private static final int ICON_SIZE = 18;
    private static final int MARGIN = 8;
    private static final int TEXT_GAP = 4;
    private static final int LINE_HEIGHT = 9;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BACKGROUND_COLOR = 0x80000000;

    private CombatStateHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !Config.SHOW_COMBAT_STATE_HUD.getAsBoolean()) {
            return;
        }

        CombatState state = mc.player.getData(ModAttachments.COMBAT_STATE);
        Identifier icon = state.inCombat() ? COMBAT : IDLE;
        int x = graphics.guiWidth() - ICON_SIZE - MARGIN;
        int y = graphics.guiHeight() - ICON_SIZE - MARGIN;
        int seconds = (state.combatTicks() + 19) / 20;
        String secondsText = "Time: " + seconds + "s";
        String hostilesText = "Hostiles: " + state.hostiles().size();
        int textWidth = Math.max(mc.font.width(secondsText), mc.font.width(hostilesText));
        int textX = x - TEXT_GAP - textWidth;
        graphics.fill(RenderPipelines.GUI, textX - 2, y - 1, x - 1, y + ICON_SIZE + 1, BACKGROUND_COLOR);
        graphics.text(mc.font, secondsText, textX, y, TEXT_COLOR, true);
        graphics.text(mc.font, hostilesText, textX, y + LINE_HEIGHT, TEXT_COLOR, true);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, x, y, ICON_SIZE, ICON_SIZE);
    }
}
