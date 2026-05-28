package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.impl.BottledFairyBrewingRecipe;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

public final class ModPotions {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<Potion, Potion> BOTTLED_FAIRY = REG.potion("bottled_fairy")
            .effect(ModEffects.FAIRY_BLESSING, 3 * 60 * 20)
            .stack(ModPotions::createBottledFairyStack)
            .brewingRecipe(BottledFairyBrewingRecipe::new)
            .en("Bottled Fairy")
            .register();
    public static final DeferredHolder<Potion, Potion> LONG_BOTTLED_FAIRY = REG.potion("long_bottled_fairy")
            .name("bottled_fairy")
            .effect(ModEffects.FAIRY_BLESSING, 8 * 60 * 20)
            .stack(ModPotions::createBottledFairyStack)
            .en("Bottled Fairy")
            .register();
    public static final DeferredHolder<Potion, Potion> BLOCKING = REG.potion("blocking")
            .effect(ModEffects.QUICK_BLOCK, 5)
            .brewFrom(Potions.AWKWARD, Items.COPPER_INGOT)
            .en("Potion of Blocking")
            .register();

    static {
        REG.brewingRecipeDisplay(
                "brewing/bottled_fairy_from_regeneration",
                Items.TOTEM_OF_UNDYING::getDefaultInstance,
                () -> potionStack(Items.POTION, Potions.REGENERATION),
                () -> createBottledFairyStack(Items.POTION, BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/splash_bottled_fairy_from_regeneration",
                Items.TOTEM_OF_UNDYING::getDefaultInstance,
                () -> potionStack(Items.SPLASH_POTION, Potions.REGENERATION),
                () -> createBottledFairyStack(Items.SPLASH_POTION, BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/lingering_bottled_fairy_from_regeneration",
                Items.TOTEM_OF_UNDYING::getDefaultInstance,
                () -> potionStack(Items.LINGERING_POTION, Potions.REGENERATION),
                () -> createBottledFairyStack(Items.LINGERING_POTION, BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplayVariants(
                "brewing/long_bottled_fairy_from_upgraded_regeneration",
                () -> List.of(Items.TOTEM_OF_UNDYING.getDefaultInstance()),
                () -> List.of(potionStack(Items.POTION, Potions.LONG_REGENERATION), potionStack(Items.POTION, Potions.STRONG_REGENERATION)),
                () -> createBottledFairyStack(Items.POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplayVariants(
                "brewing/splash_long_bottled_fairy_from_upgraded_regeneration",
                () -> List.of(Items.TOTEM_OF_UNDYING.getDefaultInstance()),
                () -> List.of(potionStack(Items.SPLASH_POTION, Potions.LONG_REGENERATION), potionStack(Items.SPLASH_POTION, Potions.STRONG_REGENERATION)),
                () -> createBottledFairyStack(Items.SPLASH_POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplayVariants(
                "brewing/lingering_long_bottled_fairy_from_upgraded_regeneration",
                () -> List.of(Items.TOTEM_OF_UNDYING.getDefaultInstance()),
                () -> List.of(potionStack(Items.LINGERING_POTION, Potions.LONG_REGENERATION), potionStack(Items.LINGERING_POTION, Potions.STRONG_REGENERATION)),
                () -> createBottledFairyStack(Items.LINGERING_POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/long_bottled_fairy_from_redstone",
                Items.REDSTONE::getDefaultInstance,
                () -> createBottledFairyStack(Items.POTION, BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/splash_long_bottled_fairy_from_redstone",
                Items.REDSTONE::getDefaultInstance,
                () -> createBottledFairyStack(Items.SPLASH_POTION, BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.SPLASH_POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/lingering_long_bottled_fairy_from_redstone",
                Items.REDSTONE::getDefaultInstance,
                () -> createBottledFairyStack(Items.LINGERING_POTION, BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.LINGERING_POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/splash_bottled_fairy",
                Items.GUNPOWDER::getDefaultInstance,
                () -> createBottledFairyStack(Items.POTION, BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.SPLASH_POTION, BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/lingering_bottled_fairy",
                Items.DRAGON_BREATH::getDefaultInstance,
                () -> createBottledFairyStack(Items.SPLASH_POTION, BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.LINGERING_POTION, BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/splash_long_bottled_fairy",
                Items.GUNPOWDER::getDefaultInstance,
                () -> createBottledFairyStack(Items.POTION, LONG_BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.SPLASH_POTION, LONG_BOTTLED_FAIRY)
        );
        REG.brewingRecipeDisplay(
                "brewing/lingering_long_bottled_fairy",
                Items.DRAGON_BREATH::getDefaultInstance,
                () -> createBottledFairyStack(Items.SPLASH_POTION, LONG_BOTTLED_FAIRY),
                () -> createBottledFairyStack(Items.LINGERING_POTION, LONG_BOTTLED_FAIRY)
        );
    }

    public static void register() {}

    public static ItemStack createBottledFairyStack(Holder<Potion> potion) {
        return createBottledFairyStack(Items.POTION, potion);
    }

    private static ItemStack createBottledFairyStack(Item item, Holder<Potion> potion) {
        ItemStack stack = PotionContents.createItemStack(item, potion);
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return stack;
    }

    private static ItemStack potionStack(Item item, Holder<Potion> potion) {
        return PotionContents.createItemStack(item, potion);
    }
}
