package forever.pajang.minethespire.compat.jade;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.resources.Identifier;

public final class BlockingValueJade {
    static final Identifier UID = MineTheSpire.id("blocking_value");
    static final String TAG_AMOUNT = "MineTheSpireBlockingValue";

    private BlockingValueJade() {
    }

    public static void register() {
        MineTheSpire.REG.text("config.jade.plugin_minethespire.blocking_value").en("Blocking Value").register();
    }

    static Identifier heartSprite(String name) {
        return MineTheSpire.id("hud/heart/" + name);
    }
}
