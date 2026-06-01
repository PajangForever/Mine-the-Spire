package forever.pajang.minethespire.register;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.curios.RegisterCurios;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class ModDataProviders {
    private static LootTableBuilder SPIRIT_LOOT_TABLE;
    private static LootTableBuilder LIZARD_TAIL_LOOT_TABLE;
    private static LootTableBuilder BURNING_BLOOD_LOOT_TABLE;
    private static LootTableBuilder SNAKE_RING_LOOT_TABLE;
    private static LootTableBuilder BROKEN_CORE_LOOT_TABLE;
    private static final List<String> VILLAGE_CHEST_TABLES = List.of(
            "village_armorer",
            "village_butcher",
            "village_cartographer",
            "village_desert_house",
            "village_fisher",
            "village_fletcher",
            "village_mason",
            "village_plains_house",
            "village_savanna_house",
            "village_shepherd",
            "village_snowy_house",
            "village_taiga_house",
            "village_tannery",
            "village_temple",
            "village_toolsmith",
            "village_weaponsmith"
    );

    private ModDataProviders() {
    }

    public static void register(RegisterCore registerCore) {
        if (!registerCore.runningDataGen()) {
            return;
        }
        registerLootTables(registerCore);
        registerLootModifiers(registerCore);
        RegisterCurios.registerCurios(registerCore);

        registerCore.dataProviders.add((output, lookupProvider) -> new RecipeProvider.Runner(output, lookupProvider) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return new RecipeProvider(registries, output) {
                    @Override
                    protected void buildRecipes() {
                        HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);
                        registerCore.itemRecipes.forEach(recipe -> recipe.accept(items, this.output));
                    }
                };
            }

            @Override
            public String getName() {
                return "Mine the Spire Recipes";
            }
        });

        registerCore.dataProviders.add((output, lookupProvider) -> new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(_ -> lootOutput ->
                        registerCore.lootTables.forEach(builder -> lootOutput.accept(builder.key(), builder.build())),
                        LootContextParamSets.CHEST)
        ), lookupProvider));

        registerCore.dataProviders.add((output, lookupProvider) -> new GlobalLootModifierProvider(output, lookupProvider, MineTheSpire.MODID) {
            @Override
            protected void start() {
                registerCore.lootModifiers.forEach(builder -> add(builder.name, builder.build()));
            }
        });

        if (CuriosCompat.isLoaded()) {
            registerCore.dataProviders.add(CuriosCompat::createDataProvider);
        }
    }
    private static void registerLootTables(RegisterCore registerCore) {
        SPIRIT_LOOT_TABLE = registerChestLootTable(registerCore, "spirit", ModItems.SPIRIT, 1, 19);
        LIZARD_TAIL_LOOT_TABLE = registerChestLootTable(registerCore, "lizard_tail", ModItems.LIZARD_TAIL, 3, 1);
        BURNING_BLOOD_LOOT_TABLE = registerChestLootTable(registerCore, "burning_blood", ModItems.BURNING_BLOOD, 1, 19);
        SNAKE_RING_LOOT_TABLE = registerChestLootTable(registerCore, "snake_ring", ModItems.RING_OF_THE_SNAKE, 1, 19);
        BROKEN_CORE_LOOT_TABLE = registerChestLootTable(registerCore, "broken_core", ModItems.CRACKED_CORE, 1, 19);
    }

    private static void registerLootModifiers(RegisterCore registerCore) {
        registerVillageLootModifiers(registerCore, "spirit", SPIRIT_LOOT_TABLE);
        registerVillageLootModifiers(registerCore, "lizard_tail", LIZARD_TAIL_LOOT_TABLE);
        registerVillageLootModifiers(registerCore, "burning_blood", BURNING_BLOOD_LOOT_TABLE);
        registerVillageLootModifiers(registerCore, "snake_ring", SNAKE_RING_LOOT_TABLE);
        registerVillageLootModifiers(registerCore, "broken_core", BROKEN_CORE_LOOT_TABLE);
    }

    private static LootItemCondition[] villageChestCondition(String table) {
        return new LootItemCondition[]{
                LootTableIdCondition.builder(Identifier.withDefaultNamespace("chests/village/" + table)).build()
        };
    }

    private static LootTableBuilder registerChestLootTable(RegisterCore registerCore, String name, Supplier<? extends Item> item, int itemWeight, int emptyWeight) {
        return registerCore.lootTable("chests/" + name)
                .table(() -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(item.get()).setWeight(itemWeight))
                                .add(EmptyLootItem.emptyItem().setWeight(emptyWeight))))
                .register();
    }

    private static void registerVillageLootModifiers(RegisterCore registerCore, String prefix, LootTableBuilder table) {
        for (String villageTable : VILLAGE_CHEST_TABLES) {
            registerCore.lootModifier(prefix + "_in_" + villageTable)
                    .conditions(villageChestCondition(villageTable))
                    .weight(0)
                    .table(table.key())
                    .register();
        }
    }
}
