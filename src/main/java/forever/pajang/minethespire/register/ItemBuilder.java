package forever.pajang.minethespire.register;

import net.minecraft.core.HolderGetter;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemBuilder<T extends Item> extends RegisterCore.Builder {
    final Function<Item.Properties, T> constructor;
    protected UnaryOperator<Item.Properties> properties = UnaryOperator.identity();
    protected String group = null;
    protected BiConsumer<Supplier<T>, Supplier<ItemModelGenerators>> modelGen = (_, _) -> {};
    protected Function<Supplier<T>, BiConsumer<HolderGetter<Item>, RecipeOutput>> recipeGen = _ -> (_, _) -> {};
    protected String en = null;
    protected final Set<TagKey<Item>> tags = new HashSet<>();

    public ItemBuilder(RegisterCore registerCore, String name, Function<Item.Properties, T> constructor) {
        super(registerCore, name);
        this.constructor = constructor;
    }

    public ItemBuilder<T> properties(UnaryOperator<Item.Properties> properties) {
        UnaryOperator<Item.Properties> previous = this.properties;
        this.properties = p -> properties.apply(previous.apply(p));
        return this;
    }

    public ItemBuilder<T> in(String group) {
        this.group = group;
        return this;
    }

    public ItemBuilder<T> model(BiConsumer<Supplier<T>, Supplier<ItemModelGenerators>> modelGen) {
        this.modelGen = this.modelGen.andThen(modelGen);
        return this;
    }

    public ItemBuilder<T> flatModel(Identifier texture) {
        return model((i, g) -> g.get().itemModelOutput.accept(i.get(),
                ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(
                        ModelLocationUtils.getModelLocation(i.get()),
                        TextureMapping.layer0(new Material(texture)),
                        g.get().modelOutput))));
    }

    public ItemBuilder<T> defaultModel() {
        return flatModel(registerCore.id("item/" + name));
    }

    public ItemBuilder<T> recipe(Function<Supplier<T>, BiConsumer<HolderGetter<Item>, RecipeOutput>> recipeGen) {
        Function<Supplier<T>, BiConsumer<HolderGetter<Item>, RecipeOutput>> previous = this.recipeGen;
        this.recipeGen = item -> {
            BiConsumer<HolderGetter<Item>, RecipeOutput> prior = previous.apply(item);
            BiConsumer<HolderGetter<Item>, RecipeOutput> next = recipeGen.apply(item);
            return (provider, output) -> {
                prior.accept(provider, output);
                next.accept(provider, output);
            };
        };
        return this;
    }

    public ItemBuilder<T> en(String en) {
        this.en = en;
        return this;
    }

    public ItemBuilder<T> tag(TagKey<Item>... tags) {
        this.tags.addAll(Arrays.stream(tags).toList());
        return this;
    }

    public DeferredItem<T> register() {
        DeferredItem<T> item = registerCore.items.registerItem(name, constructor, properties);
        registerCore.registeredItems.add(item);
        if (group == null) {
            registerCore.addToGroup(registerCore.getCurrentGroupName(), item);
        } else {
            registerCore.addToGroup(group, item);
        }
        if (registerCore.runningDataGen()){
            registerCore.itemModels.add(gen -> modelGen.accept(item, gen));
            registerCore.itemRecipes.add(recipeGen.apply(item));
            if (this.en == null) this.en = RegisterCore.getDisplayTitle(this.name);
            registerCore.deferredLang.put(() -> item.get().getDescriptionId(), en);
            this.tags.forEach(tag -> registerCore.itemTags.computeIfAbsent(tag, _ -> new HashSet<>()).add(item));
        }
        return item;
    }

}
