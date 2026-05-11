package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.ItemStackInnateTracker;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;

public final class ModDataComponents {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final Supplier<DataComponentType<ItemStackInnateTracker>> INNATE_TRACKER = REG.dataComponent("innate_tracker",
                builder -> builder.persistent(ItemStackInnateTracker.CODEC.codec()));

    public static void register() {}
}
