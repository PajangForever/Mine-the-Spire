package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.*;
import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.data.recipes.ShapedRecipeBuilder;

public final class ModItems {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredItem<? extends OriginalRelicItem> IRONCLAD_MASK = REG.item("ironclad_mask", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("ironclad_mask"), ModAttributes.State.BURNING_BLOOD.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> SILENT_MASK = REG.item("silent_mask", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("silent_mask"), ModAttributes.State.RING_OF_THE_SNAKE.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> DEFECT_MASK = REG.item("defect_mask", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("defect_mask"), ModAttributes.State.CRACKED_CORE.getIndex()).apply(ItemAttributeModifiers.builder()
                            .add(ModAttributes.MAX_ORB, new AttributeModifier(MineTheSpire.id("defect_mask"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> LIZARD_TAIL = REG.item("lizard_tail", LizardTailItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("lizard_tail"), ModAttributes.State.LIZARD_TAIL.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> MANGO = REG.item("mango", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("mango"), 14.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> PEAR = REG.item("pear", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("pear"), 10.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> STRAWBERRY = REG.item("strawberry", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.COMMON).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("strawberry"), 7.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> BURNING_BLOOD = REG.item("burning_blood", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("burning_blood"), ModAttributes.State.BURNING_BLOOD.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> RING_OF_THE_SNAKE = REG.item("ring_of_the_snake", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("ring_of_the_snake"), ModAttributes.State.RING_OF_THE_SNAKE.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> CRACKED_CORE = REG.item("cracked_core", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("cracked_core"), ModAttributes.State.CRACKED_CORE.getIndex()).apply(ItemAttributeModifiers.builder()
                    .add(ModAttributes.MAX_ORB, new AttributeModifier(MineTheSpire.id("cracked_core"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> DARK_SHURIKEN = REG.item("dark_shuriken", DarkShurikenItem::new)
            .properties(p -> p.rarity(Rarity.EPIC).stacksTo(1))
            .flatModel(MineTheSpire.id("item/mark_bloom"))
            .register();
    public static final DeferredItem<? extends Item> BOUNCING_FLASK = REG.item("bouncing_flask", BouncingFlaskItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> SPIRIT = REG.simpleItem("spirit")
            .properties(p -> p.food(
                    new FoodProperties(1, 0.1F, true),
                    Consumable.builder()
                            .animation(ItemUseAnimation.EAT)
                            .sound(SoundEvents.GENERIC_EAT)
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.INTANGIBLE, 5 * 60 * 20), 1.0F))
                            .build()))
            .recipe(item -> (items, output) -> {
                ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, item.get())
                        .define('o', Items.SLIME_BALL)
                        .define('a', Items.TOTEM_OF_UNDYING)
                        .pattern(" o ")
                        .pattern("oao")
                        .pattern(" o ");
                builder.unlockedBy("has_totem_of_undying", InventoryChangeTrigger.TriggerInstance.hasItems(Items.TOTEM_OF_UNDYING));
                builder.save(output);
            })
            .flatModel(MineTheSpire.id("item/spirit"))
            .en("Spirit")
            .register();

    public static final DeferredItem<? extends Item> LIGHTNING_ORB = REG.item("lightning_orb", LightningOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> FROST_ORB = REG.item("frost_orb", FrostOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> DARK_ORB = REG.item("dark_orb", DarkOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> PLASMA_ORB = REG.item("plasma_orb", PlasmaOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> DUALCAST = REG.item("dualcast", DualcastItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().register();

    public static final DeferredItem<? extends Item> IMPERVIOUS = REG.item("impervious", ImperviousItem::new)
            .properties(p -> p.stacksTo(64).rarity(Rarity.RARE).food(
                    new FoodProperties(0, 0.0F, true),
                    Consumable.builder().animation(ItemUseAnimation.TRIDENT).sound(Holder.direct(SoundEvents.AMETHYST_BLOCK_HIT)).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> ENTRENCH = REG.item("entrench", EntrenchItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> HEAVY_BLADE = REG.item("heavy_blade", HeavyBladeItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.EPIC).sword(ToolMaterial.NETHERITE, 13.0F, -3.75F))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> PAIN_STRIKE = REG.item("pain_strike", PainStrickItem::new)
            .defaultModel().register();

    public static final DeferredItem<? extends AkabekoItem> AKABEKO = REG.item("akabeko", AkabekoItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("akabeko"), ModAttributes.State.AKABEKO.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> ANCHOR = REG.item("anchor", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(ModAttributes.PREPARED_BLOCKING, new AttributeModifier(MineTheSpire.id("anchor"), 30, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> HORN_CLEAT = REG.item("horn_cleat", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(ModAttributes.PREPARED_BLOCKING, new AttributeModifier(MineTheSpire.id("horn_cleat"), 40, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> CAPTAINS_WHEEL = REG.item("captains_wheel", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(ModAttributes.PREPARED_BLOCKING, new AttributeModifier(MineTheSpire.id("captains_wheel"), 50, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> BAG_OF_MARBLES = REG.item("bag_of_marbles", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("bag_of_marbles"), ModAttributes.State.BAG_OF_MARBLES.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> BAG_OF_PREPARATION = REG.item("bag_of_perparation", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(Attributes.MOVEMENT_SPEED, new AttributeModifier(MineTheSpire.id("bag_of_preparation"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> BRONZE_SCALES = REG.item("bronze_scales", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(ModAttributes.THORNS, new AttributeModifier(MineTheSpire.id("bronze_scales"), 3, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> BLOOD_VIAL = REG.item("blood_vial", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("bag_of_marbles"), ModAttributes.State.BLOOD_VIAL.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> CENTENNIAL_PUZZLE = REG.item("centennial_puzzle", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.FLAGS_GROUP_0, MineTheSpire.id("bag_of_marbles"), ModAttributes.State.CENTENNIAL_PUZZLE.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> DATA_DISK = REG.item("data_disk", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(ModAttributes.FOCUS, new AttributeModifier(MineTheSpire.id("data_disk"), 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> HAPPY_FLOWER = REG.item("happy_flower", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_SPEED, new AttributeModifier(MineTheSpire.id("data_disk"), 0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();


    public static class TodoItems{
        //todo: add relics

        public static final DeferredItem<? extends RelicItem> LANTERN = REG.item("lantern", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> MEAL_TICKET = REG.item("meal_ticket", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> NUNCHAKU = REG.item("nunchaku", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> ODDLY_SMOOTH_STONE = REG.item("oddly_smooth_stone", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> PEN_NIB = REG.item("pen_nib", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> RED_SKULL = REG.item("red_skull", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> SNECKO_SKULL = REG.item("snecko_skull", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> THE_BOOT = REG.item("the_boot", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> TOY_ORNITHOPTER = REG.item("toy_ornithopter", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> VAJRA = REG.item("vajra", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> GOLD_PLATED_CABLES = REG.item("gold_plated_cables", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> GREMLIN_HORN = REG.item("gremlin_horn", RelicItem::new)
                .tempModel().register();


        public static final DeferredItem<? extends RelicItem> KUNAI = REG.item("kunai", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> LETTER_OPENER = REG.item("letter_opener", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> MEAT_ON_THE_BONE = REG.item("meat_on_the_bone", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> MERCURY_HOURGLASS = REG.item("mercury_hourglass", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> NINJA_SCROLL = REG.item("ninja_scroll", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> ORNAMENTAL_FAN = REG.item("ornamental_fan", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> PANTOGRAPH = REG.item("pantograph", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> SELF_FORMING_CLAY = REG.item("self_forming_clay", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> SHURIKEN = REG.item("shuriken", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> SYMBIOTIC_VIRUS = REG.item("symbiotic_virus", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> CALIPERS = REG.item("calipers", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> CHAMPION_BELT = REG.item("champion_belt", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> EMOTION_CHIP = REG.item("emotion_chip", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> FOSSILIZED_HELIX = REG.item("fossilized_helix", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> GINGER = REG.item("ginger", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> INCENSE_BURNER = REG.item("incense_burner", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> STONE_CALENDAR = REG.item("stone_calendar", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> THE_SPECIMEN = REG.item("the_specimen", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> THREAD_AND_NEEDLE = REG.item("thread_and_needle", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> TORII = REG.item("torii", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> TUNGSTEN_ROD = REG.item("tungsten_rod", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> TURNIP = REG.item("turnip", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> BLACK_BLOOD = REG.item("black_blood", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> FROZEN_CORE = REG.item("frozen_core", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> INSERTER = REG.item("inserter", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> NUCLEAR_BATTERY = REG.item("nuclear_battery", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> PHILOSOPHERS_STONE = REG.item("philosophers_stone", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> RING_OF_THE_SERPENT = REG.item("ring_of_the_serpent", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> RUNIC_CUBE = REG.item("runic_cube", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> SNECKO_EYE = REG.item("snecko_eye", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> BRIMSTONE = REG.item("brimstone", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> CHEMICAL_X = REG.item("chemical_x", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> RUNIC_CAPACITOR = REG.item("runic_capacitor", RelicItem::new)
                .tempModel().register();

        public static final DeferredItem<? extends RelicItem> TWISTED_FUNNEL = REG.item("twisted_funnel", RelicItem::new)
                .tempModel().register();

        public static void register() {
        }
    }

    public static void register() {
    }

}
