package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.impl.ItemStackInnateTracker;
import forever.pajang.pjfmod.register.RegisterCore;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public final class ModDataComponents {
    private static final RegisterCore REG = PajangForeversMod.REG;

    public static final Supplier<DataComponentType<ItemStackInnateTracker>> INNATE_TRACKER = REG.dataComponent("innate_tracker",
                builder -> builder.persistent(ItemStackInnateTracker.CODEC.codec()));

    public static void register() {}
}
