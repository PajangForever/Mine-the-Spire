package forever.pajang.minethespire.client;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class OverhealHudRenderer {
    private static final Identifier FULL = MineTheSpire.id("hud/heart/overheal_full");
    private static final Identifier HALF = MineTheSpire.id("hud/heart/overheal_half");

    private OverhealHudRenderer() {
    }

    public static int render(GuiGraphicsExtractor graphics, int leftHeight) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return 0;
        }

        float overheal = (float)player.getAttributeValue(ModAttributes.OVERHEAL);
        if (overheal <= 0.0F) {
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
        int currentAmount = Mth.ceil(overheal);
        int containers = Mth.ceil(overheal / 2.0F);
        int firstOverhealContainer = healthContainers + absorptionContainers;
        int totalRows = Mth.ceil((firstOverhealContainer + containers) / 10.0F);

        yBase += vanillaHealthHeight;

        for (int containerIndex = firstOverhealContainer + containers - 1; containerIndex >= firstOverhealContainer; containerIndex--) {
            int row = containerIndex / 10;
            int column = containerIndex % 10;
            int xo = xLeft + column * 8;
            int yo = yBase - row * rowHeight;
            int overhealContainerIndex = containerIndex - firstOverhealContainer;
            int halves = overhealContainerIndex * 2;
            boolean half = halves + 1 == currentAmount;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, half ? HALF : FULL, xo, yo, 9, 9);
        }

        return Math.max(0, totalRows - numHealthRows) * rowHeight;
    }
}
