package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;

import java.util.Optional;

public class BottledFairyBrewingRecipe implements IBrewingRecipe {
    private static final int EIGHT_MINUTES = 8 * 60 * 20;

    @Override
    public boolean isInput(ItemStack input) {
        return isPotionContainer(input) && (isRegenerationPotion(input) || isBottledFairyPotion(input));
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.is(Items.TOTEM_OF_UNDYING)
                || ingredient.is(Items.REDSTONE)
                || ingredient.is(Items.GUNPOWDER)
                || ingredient.is(Items.DRAGON_BREATH);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!isPotionContainer(input)) {
            return ItemStack.EMPTY;
        }

        if (ingredient.is(Items.TOTEM_OF_UNDYING) && isRegenerationPotion(input)) {
            Holder<Potion> output = createsLongVersion(input) ? ModPotions.LONG_BOTTLED_FAIRY : ModPotions.BOTTLED_FAIRY;
            return createBottledFairyItem(input, output);
        }

        if (ingredient.is(Items.REDSTONE) && isBottledFairyPotion(input, ModPotions.BOTTLED_FAIRY)) {
            return createBottledFairyItem(input, ModPotions.LONG_BOTTLED_FAIRY);
        }

        Optional<Holder<Potion>> bottledFairyPotion = getBottledFairyPotion(input);
        if (bottledFairyPotion.isPresent() && ingredient.is(Items.GUNPOWDER) && input.is(Items.POTION)) {
            return createBottledFairyItem(Items.SPLASH_POTION.getDefaultInstance(), bottledFairyPotion.get());
        }
        if (bottledFairyPotion.isPresent() && ingredient.is(Items.DRAGON_BREATH) && input.is(Items.SPLASH_POTION)) {
            return createBottledFairyItem(Items.LINGERING_POTION.getDefaultInstance(), bottledFairyPotion.get());
        }

        return ItemStack.EMPTY;
    }

    private static boolean isPotionContainer(ItemStack stack) {
        return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION);
    }

    private static boolean isRegenerationPotion(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.potion()
                .filter(BottledFairyBrewingRecipe::isVanillaRegenerationPotion)
                .isPresent()
                || hasRegenerationEffect(contents);
    }

    private static boolean isBottledFairyPotion(ItemStack stack) {
        return getBottledFairyPotion(stack).isPresent();
    }

    private static boolean isBottledFairyPotion(ItemStack stack, Holder<Potion> potion) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(potion);
    }

    private static Optional<Holder<Potion>> getBottledFairyPotion(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (contents.is(ModPotions.BOTTLED_FAIRY)) {
            return Optional.of(ModPotions.BOTTLED_FAIRY);
        }
        if (contents.is(ModPotions.LONG_BOTTLED_FAIRY)) {
            return Optional.of(ModPotions.LONG_BOTTLED_FAIRY);
        }
        return Optional.empty();
    }

    private static ItemStack createBottledFairyItem(ItemStack input, Holder<Potion> potion) {
        ItemStack output = PotionContents.createItemStack(input.getItem(), potion);
        output.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        return output;
    }

    private static boolean createsLongVersion(ItemStack stack) {
        PotionContents contents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (contents.potion().filter(BottledFairyBrewingRecipe::isUpgradedVanillaRegenerationPotion).isPresent()) {
            return true;
        }

        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.is(MobEffects.REGENERATION)
                    && (effect.getDuration() >= EIGHT_MINUTES || effect.getAmplifier() >= 1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasRegenerationEffect(PotionContents contents) {
        for (MobEffectInstance effect : contents.getAllEffects()) {
            if (effect.is(MobEffects.REGENERATION)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVanillaRegenerationPotion(Holder<Potion> potion) {
        return potion.is(Potions.REGENERATION)
                || potion.is(Potions.LONG_REGENERATION)
                || potion.is(Potions.STRONG_REGENERATION);
    }

    private static boolean isUpgradedVanillaRegenerationPotion(Holder<Potion> potion) {
        return potion.is(Potions.LONG_REGENERATION) || potion.is(Potions.STRONG_REGENERATION);
    }
}
