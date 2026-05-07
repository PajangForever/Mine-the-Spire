package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.impl.ItemStackInnateTracker;
import forever.pajang.pjfmod.register.RegisterCore;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public final class ModEnchantments {
    private static final RegisterCore REG = PajangForeversMod.REG;

    public static final Supplier<DataComponentType<Unit>> DATA_EXHAUST = REG.enchantmentComponent("exhaust");

    public static final Supplier<DataComponentType<Unit>> DATA_ETHEREAL = REG.enchantmentComponent("ethereal");

    public static final Supplier<DataComponentType<Unit>> DATA_INNATE = REG.enchantmentComponent("innate");

    public static final ResourceKey<Enchantment> EXHAUST = REG.enchantment("exhaust")
            .supported(g -> g.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE))
            .modify(b -> b.withEffect(DATA_EXHAUST.get())).register();

    public static final ResourceKey<Enchantment> ETHEREAL = REG.enchantment("ethereal")
            .supportAll().modify(b -> b.withEffect(DATA_ETHEREAL.get())).register();

    public static final ResourceKey<Enchantment> INNATE = REG.enchantment("innate")
            .supportAll().modify(b -> b.withEffect(DATA_INNATE.get())).register();

    public static final ResourceKey<Enchantment> RETAIN = REG.enchantment("retain")
            .supportAll().register();


    public static void register() {}
}
