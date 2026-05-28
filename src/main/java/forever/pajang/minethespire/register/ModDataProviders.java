package forever.pajang.minethespire.register;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import top.theillusivec4.curios.api.CuriosDataProvider;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ModDataProviders {
    private static LootTableBuilder SPIRIT_LOOT_TABLE;
    private static LootTableBuilder LIZARD_TAIL_LOOT_TABLE;
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
        registerCurios(registerCore);

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

        registerCore.dataProviders.add((output, lookupProvider) -> new CuriosDataProvider(MineTheSpire.MODID, output, lookupProvider) {
            @Override
            public void generate(HolderLookup.Provider registries) {
                registerCore.curiosBuilders.forEach(builder -> builder.generate(this));
            }
        });
    }

    private static void registerLootTables(RegisterCore registerCore) {
        SPIRIT_LOOT_TABLE = registerCore.lootTable("chests/spirit")
                .table(() -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(ModItems.SPIRIT.get()).setWeight(1))
                                .add(EmptyLootItem.emptyItem().setWeight(19))))
                .register();

        LIZARD_TAIL_LOOT_TABLE = registerCore.lootTable("chests/lizard_tail")
                .table(() -> LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(ModItems.LIZARD_TAIL.get()).setWeight(3))
                                .add(EmptyLootItem.emptyItem().setWeight(1))))
                .register();
    }

    private static void registerLootModifiers(RegisterCore registerCore) {
        registerCore.lootModifier("spirit_in_village_armorer")
                .conditions(villageChestCondition("village_armorer"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_butcher")
                .conditions(villageChestCondition("village_butcher"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_cartographer")
                .conditions(villageChestCondition("village_cartographer"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_desert_house")
                .conditions(villageChestCondition("village_desert_house"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_fisher")
                .conditions(villageChestCondition("village_fisher"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_fletcher")
                .conditions(villageChestCondition("village_fletcher"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_mason")
                .conditions(villageChestCondition("village_mason"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_plains_house")
                .conditions(villageChestCondition("village_plains_house"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_savanna_house")
                .conditions(villageChestCondition("village_savanna_house"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_shepherd")
                .conditions(villageChestCondition("village_shepherd"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_snowy_house")
                .conditions(villageChestCondition("village_snowy_house"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_taiga_house")
                .conditions(villageChestCondition("village_taiga_house"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_tannery")
                .conditions(villageChestCondition("village_tannery"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_temple")
                .conditions(villageChestCondition("village_temple"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_toolsmith")
                .conditions(villageChestCondition("village_toolsmith"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("spirit_in_village_weaponsmith")
                .conditions(villageChestCondition("village_weaponsmith"))
                .weight(0)
                .table(SPIRIT_LOOT_TABLE.key())
                .register();

        registerCore.lootModifier("lizard_tail_in_village_armorer")
                .conditions(villageChestCondition("village_armorer"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_butcher")
                .conditions(villageChestCondition("village_butcher"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_cartographer")
                .conditions(villageChestCondition("village_cartographer"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_desert_house")
                .conditions(villageChestCondition("village_desert_house"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_fisher")
                .conditions(villageChestCondition("village_fisher"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_fletcher")
                .conditions(villageChestCondition("village_fletcher"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_mason")
                .conditions(villageChestCondition("village_mason"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_plains_house")
                .conditions(villageChestCondition("village_plains_house"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_savanna_house")
                .conditions(villageChestCondition("village_savanna_house"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_shepherd")
                .conditions(villageChestCondition("village_shepherd"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_snowy_house")
                .conditions(villageChestCondition("village_snowy_house"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_taiga_house")
                .conditions(villageChestCondition("village_taiga_house"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_tannery")
                .conditions(villageChestCondition("village_tannery"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_temple")
                .conditions(villageChestCondition("village_temple"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_toolsmith")
                .conditions(villageChestCondition("village_toolsmith"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
        registerCore.lootModifier("lizard_tail_in_village_weaponsmith")
                .conditions(villageChestCondition("village_weaponsmith"))
                .weight(0)
                .table(LIZARD_TAIL_LOOT_TABLE.key())
                .register();
    }

    private static void registerCurios(RegisterCore registerCore) {
        registerCore.curios("tail")
                .order(260)
                .size(1)
                .icon("slot/empty_curio_slot")
                .addValidator("tag")
                .addPlayer()
                .tag(ModItems.LIZARD_TAIL)
                .register();
    }

    private static LootItemCondition[] villageChestCondition(String table) {
        return new LootItemCondition[]{
                LootTableIdCondition.builder(Identifier.withDefaultNamespace("chests/village/" + table)).build()
        };
    }
}
