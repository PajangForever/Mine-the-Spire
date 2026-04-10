package forever.pajang.pjfmod.register;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ItemBuilder<T extends Item> {
    private final RegisterCore registerCore;
    final String name;
    final Function<Item.Properties, T> constructor;
    protected UnaryOperator<Item.Properties> properties = UnaryOperator.identity();
    protected String group = null;
    protected BiConsumer<Supplier<T>, Supplier<ItemModelGenerators>> modelGen = (_, _) -> {
    };

    protected String en = null;

    public ItemBuilder(RegisterCore registerCore, String name, Function<Item.Properties, T> constructor) {
        this.registerCore = registerCore;
        this.name = name;
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

    public ItemBuilder<T> en(String en) {
        this.en = en;
        return this;
    }

    public DeferredItem<T> register() {
        DeferredItem<T> item = registerCore.items.registerItem(name, constructor, properties);
        (group == null ? registerCore.getCurrentGroup() : registerCore.getGroup(group)).displayItems(Collections.singleton(item));
        registerCore.itemModels.add(gen -> modelGen.accept(item, gen));
        if (this.en == null) this.en = RegisterCore.getDisplayTitle(this.name);
        registerCore.deferredLang.put(() -> item.get().getDescriptionId(), en);
        return item;
    }
}
