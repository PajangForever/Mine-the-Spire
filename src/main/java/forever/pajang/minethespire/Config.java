package forever.pajang.minethespire;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_COMBAT_STATE_HUD = BUILDER
            .comment("Whether to render the combat state HUD icon and text")
            .define("showCombatStateHud", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
