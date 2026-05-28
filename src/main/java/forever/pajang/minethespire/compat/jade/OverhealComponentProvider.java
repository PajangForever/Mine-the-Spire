package forever.pajang.minethespire.compat.jade;

import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.JadeUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum OverhealComponentProvider implements IEntityComponentProvider {
    INSTANCE;
    private static final Identifier FULL = OverhealJade.heartSprite("overheal_full");
    private static final Identifier HALF = OverhealJade.heartSprite("overheal_half");
    private static final int TEXT_THRESHOLD = 20;

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (!config.get(OverhealJade.UID)) {
            return;
        }

        float amount = accessor.getServerData().getFloatOr(OverhealJade.TAG_AMOUNT, 0.0F);
        if (amount <= 0.0F) {
            return;
        }

        if (amount <= TEXT_THRESHOLD) {
            tooltip.add(heartElements(amount));
            return;
        }

        tooltip.add(compactHeartElements(amount));
    }

    @Override
    public Identifier getUid() {
        return OverhealJade.UID;
    }

    private static String formatAmount(float amount) {
        if (amount == (long)amount) {
            return Long.toString((long)amount);
        }
        return String.format(Locale.ROOT, "%.1f", amount);
    }

    private static List<LayoutElement> heartElements(float amount) {
        int currentAmount = (int)Math.ceil(amount);
        int containers = (int)Math.ceil(amount / 2.0F);
        List<LayoutElement> hearts = new ArrayList<>(containers);
        for (int i = 0; i < containers; i++) {
            int halves = i * 2;
            boolean half = halves + 1 == currentAmount;
            hearts.add(JadeUI.sprite(half ? HALF : FULL, 9, 9));
        }
        return hearts;
    }

    private static List<LayoutElement> compactHeartElements(float amount) {
        List<LayoutElement> elements = new ArrayList<>(2);
        elements.add(JadeUI.sprite(FULL, 9, 9));
        elements.add(JadeUI.text(Component.literal("").append(IThemeHelper.get().success(formatAmount(amount)))));
        return elements;
    }
}
