package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.register.RegisterCore;

public final class RegisterCurios {
    private RegisterCurios() {
    }

    public static void registerIfLoaded() {
        CuriosCompat.registerEventsIfLoaded();
    }

    public static void registerCurios(RegisterCore registerCore) {
        registerCore.curios(CuriosSlot.SPIRE_RELIC)
                .register();
        registerCore.curios(CuriosSlot.ORIGINAL_SPIRE_RELIC)
                .register();
    }
}
