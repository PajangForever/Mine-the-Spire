package forever.pajang.minethespire;

import forever.pajang.minethespire.register.LangBuilder;
import forever.pajang.minethespire.register.RegisterCore;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigTheSpire {
    private static final ModConfigSpec.Builder CLIENT = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder COMMON = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder SERVER = new ModConfigSpec.Builder();

    private static final RegisterCore REG = MineTheSpire.REG;
    private static final String ID_COMBAT_STATE = lang().info("combat_state").en("Render Combat State Info").registerAndGetKey();
    private static final String ID_MAX_COMBAT_TICKS = lang().info("max_combat_ticks").en("Max Combat Ticks").registerAndGetKey();
    private static final String ID_QUICK_EXIT_COMBAT_TICKS = lang().info("quick_exit_combat_ticks").en("Quick Exit Combat Ticks").registerAndGetKey();
    private static final String MIND_BLOOM_DISABLE_DEATH_PROTECTIONS = lang().info("fierce_mind_bloom").en("Fierce Mind Bloom").registerAndGetKey();

    public static final ModConfigSpec.BooleanValue SHOW_COMBAT_STATE = CLIENT
            .translation(ID_COMBAT_STATE).define("combat_state", false);

    public static final ModConfigSpec.IntValue MAX_COMBAT_TICKS = SERVER
            .translation(ID_MAX_COMBAT_TICKS).defineInRange("max_combat_ticks", 300, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue QUICK_EXIT_COMBAT_TICKS = SERVER
            .translation(ID_QUICK_EXIT_COMBAT_TICKS).comment("After all hostiles are gone, remaining Combat Ticks will decrease to this value.")
            .defineInRange("quick_exist_combat_ticks", 60, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.BooleanValue FIERCE_MIND_BLOOM = SERVER
            .translation(MIND_BLOOM_DISABLE_DEATH_PROTECTIONS).comment("Effect \"Mind Bloom\" will entirely prevent healing and disable death protections, e.g. Totem of Undying")
            .define("fierce_mind_bloom", true);

    private static LangBuilder.CombinedKey lang() {
        return REG.text().type("config");
    }

    static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
        container.registerConfig(ModConfig.Type.COMMON, COMMON.build());
        container.registerConfig(ModConfig.Type.SERVER, SERVER.build());
    }
}
