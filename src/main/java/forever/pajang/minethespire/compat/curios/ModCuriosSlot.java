package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;

public final class ModCuriosSlot {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final String ORIGINAL_SPIRE_RELIC = REG.curios("orginal_spire_relic").size(1).order(998)
            .register();

    public static final String SPIRE_RELIC = REG.curios("spire_relic").size(4).order(999)
            .register();

    public static void register() {}
}
