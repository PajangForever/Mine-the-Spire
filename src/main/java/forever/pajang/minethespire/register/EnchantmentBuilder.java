package forever.pajang.minethespire.register;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class EnchantmentBuilder extends RegisterCore.Builder {
    final Set<TagKey<Enchantment>> tags = new HashSet<>();
    Function<HolderGetter<Item>, HolderSet<Item>> supportedItems = _ -> HolderSet.empty();
    Function<HolderGetter<Item>, HolderSet<Item>> primaryItems = _ -> HolderSet.empty();
    int weight = 10;
    int maxLevel = 1;
    Enchantment.Cost minCost = Enchantment.constantCost(10);
    Enchantment.Cost maxCost = Enchantment.constantCost(30);
    int anvilCost = 8;
    List<EquipmentSlotGroup> slots = List.of(EquipmentSlotGroup.ANY);
    String en;
    UnaryOperator<MutableComponent> nameFactory = UnaryOperator.identity();
    Consumer<Enchantment.Builder> modifier = _ -> {};

    public EnchantmentBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    public EnchantmentBuilder supported(Function<HolderGetter<Item>, HolderSet<Item>> supportedItems) {
        this.supportedItems = supportedItems;
        return this;
    }

    public EnchantmentBuilder supportAll() {
        this.tag(RegisterCore.SUPPORT_ALL);
        return this;
    }

    public EnchantmentBuilder primary(Function<HolderGetter<Item>, HolderSet<Item>> primaryItems) {
        this.primaryItems = primaryItems;
        return this;
    }

    public EnchantmentBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public EnchantmentBuilder maxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public EnchantmentBuilder minCost(Enchantment.Cost minCost) {
        this.minCost = minCost;
        return this;
    }

    public EnchantmentBuilder maxCost(Enchantment.Cost maxCost) {
        this.maxCost = maxCost;
        return this;
    }

    public EnchantmentBuilder anvilCost(int anvilCost) {
        this.anvilCost = anvilCost;
        return this;
    }

    public EnchantmentBuilder slots(List<EquipmentSlotGroup> slots) {
        this.slots = slots;
        return this;
    }
    public EnchantmentBuilder tag(TagKey<Enchantment>... tags) {
        this.tags.addAll(Arrays.stream(tags).toList());
        return this;
    }

    public EnchantmentBuilder en(String en) {
        this.en = en;
        return this;
    }

    public EnchantmentBuilder nameStyle(UnaryOperator<MutableComponent> nameFactory) {
        this.nameFactory = nameFactory;
        return this;
    }

    public EnchantmentBuilder modify(Consumer<Enchantment.Builder> modifier) {
        this.modifier = this.modifier.andThen(modifier);
        return this;
    }

    public ResourceKey<Enchantment> register() {
        Identifier id = registerCore.id(name);
        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, id);
        if (registerCore.runningDataGen()){
            registerCore.enchantments.put(key, lookup -> {
                HolderGetter<Item> itemHolderGetter = lookup.get(Registries.ITEM);
                Enchantment.Builder builder = Enchantment.enchantment(new Enchantment.EnchantmentDefinition(
                        supportedItems.apply(itemHolderGetter), Optional.ofNullable(primaryItems.apply(itemHolderGetter)),
                        weight, maxLevel, minCost, maxCost, anvilCost, slots)).withCustomName(nameFactory);
                modifier.accept(builder);
                return builder.build(id);
            });
            registerCore.text(Util.makeDescriptionId("enchantment", id)).en(this.en == null ? RegisterCore.getDisplayTitle(name) : this.en).register();
                this.tags.forEach(tag -> registerCore.enchantmentTags.computeIfAbsent(tag, _ -> new HashSet<>()).add(key));
        }
        return key;
    }

    @FunctionalInterface
    public interface LookupGetter {
        <T> HolderGetter<T> get(ResourceKey<? extends Registry<T>> key);
    }

}
