package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;

public final class MineTheSpireCurios {
    private MineTheSpireCurios() {
    }

    public static void register() {
        MineTheSpire.REG.text("curios.identifier.tail").en("Tail").register();
    }
}
