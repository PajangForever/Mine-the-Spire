package forever.pajang.minethespire.compat.jei;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;

import java.util.List;

@JeiPlugin
public class MineTheSpireJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return MineTheSpire.id("jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<IJeiBrewingRecipe> recipes = MineTheSpire.REG.getBrewingRecipeDisplays().stream()
                .map(display -> createBrewingRecipe(registration, display))
                .toList();
        registration.addRecipes(RecipeTypes.BREWING, recipes);
    }

    private static IJeiBrewingRecipe createBrewingRecipe(IRecipeRegistration registration, RegisterCore.BrewingRecipeDisplay display) {
        return registration.getVanillaRecipeFactory().createBrewingRecipe(
                display.ingredients(),
                display.inputs(),
                display.output(),
                MineTheSpire.id(display.uidPath())
        );
    }
}
