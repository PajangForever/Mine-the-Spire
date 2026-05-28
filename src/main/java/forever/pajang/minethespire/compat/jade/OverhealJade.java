package forever.pajang.minethespire.compat.jade;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.resources.Identifier;

public final class OverhealJade {
    static final Identifier UID = MineTheSpire.id("overheal");
    static final String TAG_AMOUNT = "MineTheSpireOverheal";

    private OverhealJade() {
    }

    public static void register() {
        MineTheSpire.REG.text("config.jade.plugin_minethespire.overheal").en("Overheal").register();
    }

    static Identifier heartSprite(String name) {
        return MineTheSpire.id("hud/heart/" + name);
    }
}
