package forever.pajang.pjfmod.register;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.content.ModEnchantments;
import forever.pajang.pjfmod.content.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
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
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collectors;

public class RegisterCore {
    public static final TagKey<Enchantment> SUPPORT_ALL = TagKey.create(Registries.ENCHANTMENT, PajangForeversMod.id("support_all"));

    public final String modid;
    final DeferredRegister<CreativeModeTab> groups;
    final DeferredRegister.Blocks blocks;
    final DeferredRegister.Items items;
    final DeferredRegister.DataComponents dataComponents;
    final DeferredRegister.DataComponents enchantmentComponents;
    final DeferredRegister<AttachmentType<?>> attachmentTypes;
    final RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

    private final boolean runningDataGen = DatagenModLoader.isRunningDataGen();
    protected final Set<BiFunction<PackOutput, CompletableFuture<HolderLookup.Provider>, ? extends DataProvider>> dataProviders = new HashSet<>();
    protected final Map<String, CreativeModeTab.Builder> namedGroups = new HashMap<>();
    protected CreativeModeTab.Builder currentGroup;
    protected final Map<String, String> lang = new HashMap<>();
    protected final Map<Supplier<String>, String> deferredLang = new HashMap<>();
    protected final Set<Consumer<Supplier<ItemModelGenerators>>> itemModels = new HashSet<>();
    protected final Map<ResourceKey<Enchantment>, Function<EnchantmentBuilder.LookupGetter, Enchantment>> enchantments = new HashMap<>();

    protected final Map<TagKey<Item>, Set<DeferredItem<? extends Item>>> itemTags = new HashMap<>();
    protected final Map<TagKey<Enchantment>, Set<ResourceKey<Enchantment>>> enchantmentTags = new HashMap<>();

    protected RegisterCore(String modid) {
        this.modid = modid;
        this.groups = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);
        this.blocks = DeferredRegister.createBlocks(modid);
        this.items = DeferredRegister.createItems(modid);
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
        currentGroup = getGroup(name);
        return currentGroup;
    }

    protected CreativeModeTab.Builder getCurrentGroup() {
        if (currentGroup == null) {
            setGroup(modid);
        }
        return currentGroup;
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

    public EnchantmentBuilder enchantment(String path) {
        return new EnchantmentBuilder(this, path);
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

    public void register(IEventBus modEventBus) {
        blocks.register(modEventBus);
        items.register(modEventBus);
        for (Map.Entry<String, CreativeModeTab.Builder> entry : namedGroups.entrySet()) {
            groups.register(entry.getKey(), entry.getValue()::build);
        }
        groups.register(modEventBus);
        attachmentTypes.register(modEventBus);
        dataComponents.register(modEventBus);
        enchantmentComponents.register(modEventBus);

        if (runningDataGen) {
            registrySetBuilder.add(Registries.ENCHANTMENT, ctx ->
                    enchantments.forEach((key, factory) ->
                            ctx.register(key, factory.apply(ctx::lookup))));

            dataProviders.add((out, _) -> new LanguageProvider(out, modid, "en_us") {
                @Override
                protected void addTranslations() {
                    lang.forEach(this::add);
                    deferredLang.forEach((k, v) -> add(k.get(), v));
                }
            });
            dataProviders.add((out, _) -> new ModelProvider(out, modid) {
                @Override
                protected void registerModels(BlockModelGenerators blockGen, ItemModelGenerators itemGen) {
                    itemModels.forEach(c -> c.accept(() -> itemGen));
                }
            });
            dataProviders.add((out, lookup) -> new ItemTagsProvider(out, lookup, modid) {
                @Override
                protected void addTags(HolderLookup.Provider lookup) {
                    itemTags.forEach((key, items) -> tag(key).addAll(items.stream().map(DeferredHolder::get)));
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
        }
    }

    public static @NonNull String getDisplayTitle(String path) {
        return Arrays.stream(path.split("_")).map(s ->
                s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
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
