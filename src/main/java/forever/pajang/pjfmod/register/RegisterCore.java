package forever.pajang.pjfmod.register;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class RegisterCore {
    public final String modid;
    final DeferredRegister<CreativeModeTab> groups;
    final DeferredRegister.Blocks blocks;
    final DeferredRegister.Items items;

    protected final boolean runningDataGen = DatagenModLoader.isRunningDataGen();
    protected final Set<Function<PackOutput, ? extends DataProvider>> dataProviders = new HashSet<>();
    protected final RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();

    protected final Map<String, CreativeModeTab.Builder> namedGroups = new HashMap<>();
    protected CreativeModeTab.Builder currentGroup;
    protected final Map<String, String> lang = new HashMap<>();
    protected final Map<Supplier<String>, String> deferredLang = new HashMap<>();
    protected final Set<Consumer<Supplier<ItemModelGenerators>>> itemModels = new HashSet<>();

    protected RegisterCore(String modid) {
        this.modid = modid;
        this.groups = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, modid);
        this.blocks = DeferredRegister.createBlocks(modid);
        this.items = DeferredRegister.createItems(modid);
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

    public void addEnchantments() {
        this.registrySetBuilder.add(Registries.ENCHANTMENT, ctx -> {
            HolderGetter<Item> itemLookup = ctx.lookup(Registries.ITEM);
            Identifier identifier = id("ethereal");
            ctx.register(ResourceKey.create(Registries.ENCHANTMENT, identifier),
                    Enchantment.enchantment(Enchantment.definition(itemLookup.getOrThrow(ItemTags.PICKAXES), 1, 1, Enchantment.constantCost(25), Enchantment.constantCost(50), 8, EquipmentSlotGroup.ANY))
                            .build(identifier)
            );
        });
    }

    public void register(IEventBus modEventBus) {
        blocks.register(modEventBus);
        items.register(modEventBus);
        for (Map.Entry<String, CreativeModeTab.Builder> entry : namedGroups.entrySet()) {
            groups.register(entry.getKey(), entry.getValue()::build);
        }
        groups.register(modEventBus);
        addEnchantments();

        dataProviders.add(out -> new LanguageProvider(out, modid, "en_us") {
            @Override
            protected void addTranslations() {
                lang.forEach(this::add);
                deferredLang.forEach((k, v) -> add(k.get(), v));
            }
        });
        dataProviders.add(out -> new ModelProvider(out, modid) {
            @Override
            protected void registerModels(BlockModelGenerators blockGen, ItemModelGenerators itemGen) {
                itemModels.forEach(c -> c.accept(() -> itemGen));
            }
        });
    }

    public static @NonNull String getDisplayTitle(String path) {
        return Arrays.stream(path.split("_")).map(s ->
                s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining(" "));
    }

}
