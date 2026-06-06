package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class BlockingValueHudRenderer {
    private static final Identifier FULL = MineTheSpire.id("hud/heart/blocking_value_full");
    private static final Identifier HALF = MineTheSpire.id("hud/heart/blocking_value_half");

    private BlockingValueHudRenderer() {
    }

    public static void renderLayer(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.gameMode == null) {
            return;
        }
        if (minecraft.options.hideGui || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }

        minecraft.gui.leftHeight += render(graphics, minecraft.gui.leftHeight);
    }

    public static int render(GuiGraphicsExtractor graphics, int leftHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }

        float blockingValue = (float)player.getAttributeValue(ModAttributes.BLOCKING_VALUE);
        if (blockingValue <= 0.0F) {
            return 0;
        }

        int xLeft = graphics.guiWidth() / 2 - 91;
        int yBase = graphics.guiHeight() - leftHeight;
        float maxHealth = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(Mth.ceil(player.getHealth()), 1));
        int healthContainers = Mth.ceil(maxHealth / 2.0F);
        int totalAbsorption = Mth.ceil(player.getAbsorptionAmount());
        int absorptionContainers = Mth.ceil(totalAbsorption / 2.0F);
        int numHealthRows = Mth.ceil((maxHealth + totalAbsorption) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (numHealthRows - 2), 3);
        int vanillaHealthHeight = (numHealthRows - 1) * rowHeight + 10;
        int currentAmount = Mth.ceil(blockingValue);
        int containers = Mth.ceil(blockingValue / 2.0F);
        int firstBlockingValueContainer = healthContainers + absorptionContainers;
        int totalRows = Mth.ceil((firstBlockingValueContainer + containers) / 10.0F);

        yBase += vanillaHealthHeight;

        for (int containerIndex = firstBlockingValueContainer + containers - 1; containerIndex >= firstBlockingValueContainer; containerIndex--) {
            int row = containerIndex / 10;
            int column = containerIndex % 10;
            int xo = xLeft + column * 8;
            int yo = yBase - row * rowHeight;
            int blockingValueContainerIndex = containerIndex - firstBlockingValueContainer;
            int halves = blockingValueContainerIndex * 2;
            boolean half = halves + 1 == currentAmount;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, half ? HALF : FULL, xo, yo, 9, 9);
        }

        return Math.max(0, totalRows - numHealthRows) * rowHeight;
    }
}
