package forever.pajang.minethespire.register;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
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

public class PotionBuilder extends RegisterCore.Builder {
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private String group;
    private String en;
    private Function<Holder<Potion>, ItemStack> normalFactory = _ -> ItemStack.EMPTY;
    private Function<Holder<Potion>, ItemStack> splashFactory = _ -> ItemStack.EMPTY;
    private Function<Holder<Potion>, ItemStack> lingeringFactory = _ -> ItemStack.EMPTY;
    private final List<Consumer<DeferredHolder<Potion, Potion>>> brewing = new ArrayList<>();

    PotionBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
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

    public PotionBuilder addNormal(Function<Holder<Potion>, ItemStack> stackFactory) {
        this.normalFactory = stackFactory;
        return this;
    }

    public PotionBuilder addSplash(Function<Holder<Potion>, ItemStack> stackFactory) {
        this.splashFactory = stackFactory;
        return this;
    }

    public PotionBuilder addLingering(Function<Holder<Potion>, ItemStack> stackFactory) {
        this.lingeringFactory = stackFactory;
        return this;
    }

    public PotionBuilder addNormal() {
        this.normalFactory = holder -> PotionContents.createItemStack(Items.POTION, holder);
        return this;
    }

    public PotionBuilder addSplash() {
        this.splashFactory = holder -> PotionContents.createItemStack(Items.SPLASH_POTION, holder);
        return this;
    }

    public PotionBuilder addLingering() {
        this.lingeringFactory = holder -> PotionContents.createItemStack(Items.LINGERING_POTION, holder);
        return this;
    }

    public PotionBuilder brewStart(Item ingredient) {
        brewing.add(potion -> registerCore.brewingRecipes.add(b -> b.addStartMix(ingredient, potion)));
        return this;
    }

    public PotionBuilder brewFrom(Holder<Potion> input, Item ingredient) {
        brewing.add(potion -> registerCore.brewingRecipes.add(b -> b.addMix(input, ingredient, potion)));
        return this;
    }

    public PotionBuilder brewingRecipe(Supplier<IBrewingRecipe> recipe) {
        registerCore.brewingRecipes.add(b -> b.addRecipe(recipe.get()));
        return this;
    }

    public DeferredHolder<Potion, Potion> register() {
        DeferredHolder<Potion, Potion> potion = registerCore.potions.register(name, () -> new Potion(name, effects.toArray(MobEffectInstance[]::new)));
        brewing.forEach(registration -> registration.accept(potion));
        registerCore.addToCreative(group, () -> normalFactory.apply(potion));
        registerCore.addToCreative(group, () -> splashFactory.apply(potion));
        registerCore.addToCreative(group, () -> lingeringFactory.apply(potion));
        if (registerCore.runningDataGen()) {
            String display = this.en == null ? RegisterCore.getDisplayTitle(this.name) : this.en;
            registerCore.text("item.minecraft.potion.effect." + name).en(display).register();
            registerCore.text("item.minecraft.splash_potion.effect." + name).en("Splash " + display).register();
            registerCore.text("item.minecraft.lingering_potion.effect." + name).en("Lingering " + display).register();
        }
        return potion;
    }
}
