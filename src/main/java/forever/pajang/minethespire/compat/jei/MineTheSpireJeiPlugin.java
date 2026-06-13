package forever.pajang.minethespire.compat.jei;

import forever.pajang.minethespire.MineTheSpire;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;

@JeiPlugin
public class MineTheSpireJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid() {
        return MineTheSpire.id("jei");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

    }

}
