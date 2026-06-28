package forever.pajang.minethespire.register;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.curios.CuriosSlotBuilder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collectors;

public class RegisterCore {
    public static final TagKey<Enchantment> SUPPORT_ALL = TagKey.create(Registries.ENCHANTMENT, MineTheSpire.id("support_all"));

    public final String modid;
    final DeferredRegister<CreativeModeTab> groups;
    final DeferredRegister.Blocks blocks;
    final DeferredRegister.Items items;
    final DeferredRegister.Entities entities;
    final DeferredRegister<MobEffect> effects;
    final DeferredRegister<Potion> potions;
    final DeferredRegister.DataComponents dataComponents;
    final DeferredRegister.DataComponents enchantmentComponents;
    final DeferredRegister<Attribute> attributes;
    final DeferredRegister<AttachmentType<?>> attachmentTypes;
    final RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

    private final boolean runningDataGen = DatagenModLoader.isRunningDataGen();
    protected final Set<BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends DataProvider>> dataProviders = new ReferenceOpenHashSet<>();
    protected final Map<String, CreativeModeTab.Builder> namedGroups = new Object2ReferenceOpenHashMap<>();
    protected final Map<String, Set<Supplier<? extends ItemLike>>> groupItems = new Object2ReferenceOpenHashMap<>();
    protected final Map<String, Set<Supplier<ItemStack>>> groupStacks = new Object2ReferenceOpenHashMap<>();
    protected CreativeModeTab.Builder currentGroup;
    protected String currentGroupName;
    protected final Map<String, String> lang = new Object2ObjectOpenHashMap<>();
    protected final Map<Supplier<String>, String> deferredLang = new Reference2ObjectOpenHashMap<>();
    protected final Map<DeferredHolder<Attribute, Attribute>, Predicate<EntityType<? extends LivingEntity>>> livingAttributes = new Reference2ReferenceOpenHashMap<>();
    protected final Set<Consumer<Supplier<ItemModelGenerators>>> itemModels = new ReferenceOpenHashSet<>();
    protected final Set<BiConsumer<HolderGetter<Item>, RecipeOutput>> itemRecipes = new ReferenceOpenHashSet<>();
    protected final Set<Holder<MobEffect>> renderEffectLevels = new ReferenceOpenHashSet<>();
    protected final Map<ResourceKey<Enchantment>, Function<EnchantmentBuilder.LookupGetter, Enchantment>> enchantments = new Object2ReferenceOpenHashMap<>();
    protected final Set<LootTableBuilder> lootTables = new ReferenceOpenHashSet<>();
    protected final Set<LootModifierBuilder> lootModifiers = new ReferenceOpenHashSet<>();
    protected final Map<ResourceKey<DamageType>, Pair<Supplier<DamageType>, TagKey<DamageType>[]>> damageTypes = new Object2ReferenceOpenHashMap<>();
    protected final Set<Supplier<? extends Item>> registeredItems = new ReferenceOpenHashSet<>();
    protected final Set<CuriosSlotBuilder> curiosSlots = new ReferenceOpenHashSet<>();

    protected final List<Consumer<PotionBrewing.Builder>> brewingRecipes = new ReferenceArrayList<>();

    protected final Map<TagKey<Item>, Set<DeferredItem<? extends Item>>> itemTags = new Object2ReferenceOpenHashMap<>();
    protected final Map<TagKey<Enchantment>, Set<ResourceKey<Enchantment>>> enchantmentTags = new Object2ReferenceOpenHashMap<>();
    protected final Map<TagKey<EntityType<?>>, Set<EntityType<?>>> entityTypeTags = new Object2ReferenceOpenHashMap<>();

    protected RegisterCore(String modid) {
        this.modid = modid;
        this.groups = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);
        this.blocks = DeferredRegister.createBlocks(modid);
        this.items = DeferredRegister.createItems(modid);
        this.entities = DeferredRegister.createEntities(modid);
        this.effects = DeferredRegister.create(Registries.MOB_EFFECT, modid);
        this.potions = DeferredRegister.create(Registries.POTION, modid);
        this.attributes = DeferredRegister.create(Registries.ATTRIBUTE, modid);
        this.attachmentTypes = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, modid);
        this.dataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, modid);
        this.enchantmentComponents = DeferredRegister.createDataComponents(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, modid);
    }

    public boolean runningDataGen() {
        return runningDataGen;
    }

    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(modid, path);
    }

    public static RegisterCore create(String modid) {
        return new RegisterCore(modid);
    }

    public CreativeModeTab.Builder getGroup(String name) {
        return namedGroups.computeIfAbsent(name, _ -> CreativeModeTab.builder())
                .title(this.text().type("group").info(name.toLowerCase().replace('_', '.')).en(getDisplayTitle(name)).register())
                .icon(() -> Items.TROPICAL_FISH.asItem().getDefaultInstance());
    }
    public CreativeModeTab.Builder setGroup(String name) {
        currentGroupName = name;
        currentGroup = getGroup(name);
        return currentGroup;
    }

    public void addToCreative(String group, Supplier<ItemStack> stack) {
        if (group == null) {
            addToCreative(stack);
        } else {
            groupStacks.computeIfAbsent(group, _ -> new LinkedHashSet<>()).add(stack);
        }
    }

    public void addToCreative(Supplier<ItemStack> stack) {
        addToCreative(getCurrentGroupName(), stack);
    }

    protected CreativeModeTab.Builder getCurrentGroup() {
        if (currentGroup == null) {
            setGroup(modid);
        }
        return currentGroup;
    }

    protected String getCurrentGroupName() {
        getCurrentGroup();
        return currentGroupName;
    }

    public LangBuilder.FixedKey text(String key) {
        return new LangBuilder.FixedKey(this, key);
    }

    public LangBuilder.CombinedKey text() {
        return new LangBuilder.CombinedKey(this);
    }

    public ItemBuilder<Item> simpleItem(String path) {
        return new ItemBuilder<>(this, path, Item::new);
    }

    public <T extends Item> ItemBuilder<T> item(String path, Function<Item.Properties, T> constructor) {
        return new ItemBuilder<>(this, path, constructor);
    }

    public Identifier itemModel(String path, Consumer<Supplier<ItemModelGenerators>> model) {
        Identifier id = id("item/" + path);
        itemModels.add(model::accept);
        return id;
    }

    public <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> entity(String path, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        text().type("entity").info(path).register();
        return entities.registerEntityType(path, factory, category, builder);
    }

    public <T extends MobEffect> EffectBuilder<T> effect(String path, BiFunction<MobEffectCategory, Integer, T> constructor) {
        return new EffectBuilder<>(this, path, constructor);
    }

    public PotionBuilder potion(String path) {
        return new PotionBuilder(this, path);
    }

    public AttributeBuilder.Ranged attribute(String path) {
        return new AttributeBuilder.Ranged(this, path);
    }

    public AttributeBuilder.StateSet stateSet(String path) {
        return new AttributeBuilder.StateSet(this, path);
    }

    public EnchantmentBuilder enchantment(String path) {
        return new EnchantmentBuilder(this, path);
    }

    public LootTableBuilder lootTable(String path) {
        return new LootTableBuilder(this, path);
    }

    public LootModifierBuilder lootModifier(String name) {
        return new LootModifierBuilder(this, name);
    }

    public ResourceKey<DamageType> damageType(String path, Supplier<DamageType> damageType, TagKey<DamageType>... tags) {
        ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, id(path));
        damageTypes.put(key, new ReferenceReferenceImmutablePair<>(damageType,tags));
        return key;
    }

    public CuriosSlotBuilder curios(String name) {
        return new CuriosSlotBuilder(this, name);
    }

    public DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> enchantmentComponent(String path) {
        return enchantmentComponents.registerComponentType(path, b -> b.persistent(Unit.CODEC));
    }

    public <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> dataComponent(String path, UnaryOperator<DataComponentType.Builder<T>> operator) {
        return dataComponents.registerComponentType(path, operator);
    }

    public <T> Supplier<AttachmentType<T>> attachmentType(String name, Function<IAttachmentHolder, T> defaultValueConstructor, Consumer<AttachmentType.Builder<T>> builderConsumer) {
        return this.attachmentTypes.register(name, () -> {
            AttachmentType.Builder<T> builder = AttachmentType.builder(defaultValueConstructor);
            builderConsumer.accept(builder);
            return builder.build();
        });
    }

    public Holder<MobEffect>[] getRenderEffectLevels() {
        return renderEffectLevels.toArray(Holder[]::new);
    }

    public Set<Supplier<? extends Item>> getRegisteredItems() {
        return registeredItems;
    }

    public Set<CuriosSlotBuilder> getCuriosSlots() {
        return curiosSlots;
    }

    public List<Consumer<PotionBrewing.Builder>> getBrewingRecipes() {
        return brewingRecipes;
    }

    public void register(IEventBus modEventBus) {
        blocks.register(modEventBus);
        items.register(modEventBus);
        entities.register(modEventBus);
        effects.register(modEventBus);
        attributes.register(modEventBus);
        potions.register(modEventBus);
        attachmentTypes.register(modEventBus);
        dataComponents.register(modEventBus);
        enchantmentComponents.register(modEventBus);

        for (Map.Entry<String, CreativeModeTab.Builder> entry : namedGroups.entrySet()) {
            String groupName = entry.getKey();
            entry.getValue().displayItems((parameters, output) -> {
                groupStacks.getOrDefault(groupName, Collections.emptySet()).stream()
                        .map(Supplier::get)
                        .filter(stack -> !stack.isEmpty())
                        .filter(stack -> stack.getItem().isEnabled(parameters.enabledFeatures()))
                        .forEach(output::accept);
            });
            groups.register(entry.getKey(), entry.getValue()::build);
        }
        groups.register(modEventBus);

        if (runningDataGen) {
            registrySetBuilder.add(Registries.ENCHANTMENT, ctx ->
                    enchantments.forEach((key, factory) ->
                            ctx.register(key, factory.apply(ctx::lookup))));

            registrySetBuilder.add(Registries.DAMAGE_TYPE, ctx ->
                    damageTypes.forEach((key, pair) ->
                            ctx.register(key, pair.left().get())));

            dataProviders.add((out, _) -> new LanguageProvider(out, modid, "en_us") {
                @Override
                protected void addTranslations() {
                    Map<String, String> translations = new LinkedHashMap<>();
                    lang.forEach((key, value) -> addTranslation(translations, key, value));
                    deferredLang.forEach((key, value) -> addTranslation(translations, key.get(), value));
                    translations.forEach(this::add);
                }
            });
            dataProviders.add((out, _) -> new ModelProvider(out, modid) {
                @Override
                protected void registerModels(BlockModelGenerators blockGen, ItemModelGenerators itemGen) {
                    itemModels.forEach(c -> c.accept(() -> itemGen));
                }
            });
            dataProviders.add((output, lookupProvider) -> new RecipeProvider.Runner(output, lookupProvider) {
                @Override
                protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                    return new RecipeProvider(registries, output) {
                        @Override
                        protected void buildRecipes() {
                            HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);
                            itemRecipes.forEach(recipe -> recipe.accept(items, this.output));
                        }
                    };
                }

                @Override
                public String getName() {
                    return "Recipes for " + MineTheSpire.MODID;
                }
            });
            dataProviders.add((out, lookup) -> new ItemTagsProvider(out, lookup, modid) {
                @Override
                protected void addTags(HolderLookup.Provider lookup) {
                    itemTags.forEach((key, items) -> tag(key).addAll(items.stream().map(DeferredHolder::get)));
                }
            });
            dataProviders.add((out, lookup) -> new EntityTypeTagsProvider(out, lookup, modid) {
                @Override
                protected void addTags(HolderLookup.Provider lookup) {
                    entityTypeTags.forEach((key, types) -> {
                        var tagAppender = tag(key);
                        types.forEach(tagAppender::add);
                    });
                }
            });
            dataProviders.add((out, lookup) -> new EnchantmentTagsProvider(out, lookup, modid) {
                @Override
                protected void addTags(HolderLookup.Provider lookup) {
                    enchantmentTags.forEach((key, entries) -> {
                        TagAppender<ResourceKey<Enchantment>, Enchantment> tagAppender = tag(key);
                        entries.forEach(tagAppender::addOptional);
                    });
                }
            });
            dataProviders.add((out, lookup) -> new DamageTypeTagsProvider(out, lookup, modid) {
                @Override
                protected void addTags(HolderLookup.Provider lookup) {
                    damageTypes.forEach((key, pair) -> {
                        TagKey<DamageType>[] tags = pair.right();
                        if (tags != null) {
                            for (TagKey<DamageType> tag : tags) {
                                tag(tag).addOptional(key);
                            }
                        }
                    });
                }
            });
            if (CuriosCompat.isLoaded()) {
                dataProviders.add(CuriosCompat::createDataProvider);
            }
        }
    }

    private static void addTranslation(Map<String, String> translations, String key, String value) {
        String previous = translations.putIfAbsent(key, value);
        if (previous != null && !Objects.equals(previous, value)) {
            throw new IllegalStateException("Conflicting translation key " + key + ": " + previous + " / " + value);
        }
    }

    public static @NotNull String getDisplayTitle(String path) {
        return Arrays.stream(path.split("_")).map(s ->
                s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
    }

    public void modifyEntityAttribute(List<EntityType<? extends LivingEntity>> livingTypes, BiConsumer<EntityType<? extends LivingEntity>, Holder<Attribute>> adder) {
        livingAttributes.forEach((attribute, entities) -> {
            for (EntityType<? extends LivingEntity> entityType : livingTypes) {
                if (entities.test(entityType)) {
                    adder.accept(entityType, attribute);
                }
            }
        });
    }

    public static abstract class Builder {
        protected final RegisterCore registerCore;
        protected final String name;

        protected Builder(RegisterCore core, String name) {
            this.registerCore = core;
            this.name = name;
        }


    }
}
