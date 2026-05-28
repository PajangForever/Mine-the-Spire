package forever.pajang.minethespire.register;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PotionBuilder extends RegisterCore.Builder {
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private String potionName;
    private String group = "main";
    private String en = null;
    private Function<Holder<Potion>, ItemStack> stackFactory = holder -> PotionContents.createItemStack(Items.POTION, holder);
    private final List<Consumer<DeferredHolder<Potion, Potion>>> brewingRegistrations = new ArrayList<>();

    PotionBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
        this.potionName = name;
    }

    public PotionBuilder name(String potionName) {
        this.potionName = potionName;
        return this;
    }

    public PotionBuilder in(String group) {
        this.group = group;
        return this;
    }

    public PotionBuilder en(String en) {
        this.en = en;
        return this;
    }

    public PotionBuilder effect(Holder<MobEffect> effect, int duration) {
        this.effects.add(new MobEffectInstance(effect, duration));
        return this;
    }

    public PotionBuilder effect(Holder<MobEffect> effect, int duration, int amplifier) {
        this.effects.add(new MobEffectInstance(effect, duration, amplifier));
        return this;
    }

    public PotionBuilder stack(Function<Holder<Potion>, ItemStack> stackFactory) {
        this.stackFactory = stackFactory;
        return this;
    }

    public PotionBuilder brewFrom(Holder<Potion> input, Item ingredient) {
        brewingRegistrations.add(potion -> registerCore.brewingRecipes.add(event -> event.getBuilder().addMix(input, ingredient, potion)));
        return this;
    }

    public PotionBuilder brewingRecipe(Supplier<IBrewingRecipe> recipe) {
        registerCore.brewingRecipes.add(event -> event.getBuilder().addRecipe(recipe.get()));
        return this;
    }

    public DeferredHolder<Potion, Potion> register() {
        DeferredHolder<Potion, Potion> potion = registerCore.potions.register(name, () -> new Potion(potionName, effects.toArray(MobEffectInstance[]::new)));
        brewingRegistrations.forEach(registration -> registration.accept(potion));
        registerCore.addStackToGroup(group, () -> stackFactory.apply(potion));
        if (registerCore.runningDataGen()) {
            String display = this.en == null ? RegisterCore.getDisplayTitle(this.potionName) : this.en;
            registerCore.lang.put("item.minecraft.potion.effect." + potionName, display);
            registerCore.lang.put("item.minecraft.splash_potion.effect." + potionName, "Splash " + display);
            registerCore.lang.put("item.minecraft.lingering_potion.effect." + potionName, "Lingering " + display);
        }
        return potion;
    }
}
