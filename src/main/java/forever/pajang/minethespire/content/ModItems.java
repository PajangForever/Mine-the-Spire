package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.*;
import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.core.Holder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Optional;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;

public final class ModItems {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredItem<? extends RelicItem> LIZARD_TAIL = REG.item("lizard_tail", LizardTailItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.ACTIVATABLE_STATES, MineTheSpire.id("lizard_tail_protection"), ModAttributes.State.LIZARD_TAIL.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> MANGO = REG.item("mango", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("mango_add_max_health"), 14.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> PEAR = REG.item("pear", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("pear_add_max_health"), 10.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends RelicItem> STRAWBERRY = REG.item("strawberry", RelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.COMMON).attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.MAX_HEALTH, new AttributeModifier(MineTheSpire.id("strawberry_add_max_health"), 7.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> BURNING_BLOOD = REG.item("burning_blood", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.ACTIVATABLE_STATES, MineTheSpire.id("burning_blood_effect"), ModAttributes.State.BURNING_BLOOD.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> RING_OF_THE_SNAKE = REG.item("ring_of_the_snake", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.ACTIVATABLE_STATES, MineTheSpire.id("ring_of_the_snake_effect"), ModAttributes.State.RING_OF_THE_SNAKE.getIndex()).apply(ItemAttributeModifiers.builder()).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends OriginalRelicItem> CRACKED_CORE = REG.item("cracked_core", OriginalRelicItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)
                    .attributes(ActivatableStatesAttribute.modifyItem(ModAttributes.ACTIVATABLE_STATES, MineTheSpire.id("cracked_core_effect"), ModAttributes.State.CRACKED_CORE.getIndex()).apply(ItemAttributeModifiers.builder()
                    .add(ModAttributes.MAX_ORB, new AttributeModifier(MineTheSpire.id("cracked_core_add_max_balls"), 2.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.ANY)).build()))
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
                            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.NO_ENTITY, 5 * 60 * 20), 1.0F))
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

    public static final DeferredItem<? extends Item> LIGHTNING_CHARGE_BALL = REG.item("lightning_charge_ball", LightningOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().en("Lightning Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> FROST_CHARGE_BALL = REG.item("frost_charge_ball", FrostOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().en("Frost Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> DARK_CHARGE_BALL = REG.item("dark_charge_ball", DarkOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().en("Dark Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> PLASMA_CHARGE_BALL = REG.item("plasma_charge_ball", PlasmaOrbItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .defaultModel().en("Plasma Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> DUALCAST = REG.item("dualcast", DualcastItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().register();

    public static final DeferredItem<? extends Item> IMPERVIOUS = REG.item("impervious", ImperviousItem::new)
            .properties(p -> p.stacksTo(64).rarity(Rarity.RARE).food(
                    new FoodProperties(0, 0.0F, true),
                    Consumable.builder().animation(ItemUseAnimation.TRIDENT).sound(Holder.direct(SoundEvents.AMETHYST_BLOCK_HIT)).build()))
            .defaultModel().register();

    public static final DeferredItem<? extends Item> ENTRENCH = REG.item("entrench", EntrenchItem::new)
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .flatModel(vanillaItemTexture("shield"))
            .en("Entrench")
            .register();

    public static final DeferredItem<? extends Item> HEAVY_BLADE = REG.item("heavy_blade", HeavyBladeItem::new)
            .properties(p -> p.stacksTo(1).rarity(Rarity.EPIC).sword(ToolMaterial.NETHERITE, 13.0F, -3.75F))
            .flatModel(MineTheSpire.id("item/heavy_blade"))
            .en("Heavy Blade")
            .register();

    static {
        REG.text("tooltip.minethespire.heavy_blade.extra_prefix").en("Extra ").register();
        REG.text("tooltip.minethespire.heavy_blade.strength").en("Strength").register();
        REG.text("tooltip.minethespire.heavy_blade.extra_suffix").en(" Bonus:").register();
        REG.text("tooltip.minethespire.heavy_blade.attack_damage").en(" Attack Damage").register();
    }

    public static final DeferredItem<? extends Item> PAIN_STRIKE = painStrike(
            "pain_strike", "iron_axe", Items.IRON_AXE, ToolMaterial.IRON, 7.0F, -3.28F, "Pain Strike", false
    );

    public static void register() {
    }

    public static boolean isPainStrike(Item item) {
        return item == PAIN_STRIKE.get();
    }

    private static DeferredItem<? extends Item> painStrike(
            String name,
            String baseTexture,
            Item baseAxe,
            net.minecraft.world.item.ToolMaterial material,
            float attackDamageBaseline,
            float attackSpeedBaseline,
            String en,
            boolean fireResistant
    ) {
        return REG.item(name, p -> new AxeItem(material, attackDamageBaseline, attackSpeedBaseline, fireResistant ? p.fireResistant() : p))
                .recipe(item -> (items, output) -> {
                    ResourceKey<Recipe<?>> recipeId = ResourceKey.create(Registries.RECIPE, MineTheSpire.id(name + "_smithing"));
                    RecipeUnlockAdvancementBuilder advancements = new RecipeUnlockAdvancementBuilder();
                    advancements.unlockedBy("has_" + baseTexture, InventoryChangeTrigger.TriggerInstance.hasItems(baseAxe));
                    SmithingTransformRecipe recipe = new SmithingTransformRecipe(
                            new Recipe.CommonInfo(true),
                            Optional.empty(),
                            Ingredient.of(baseAxe),
                            Optional.of(Ingredient.of(Items.ROTTEN_FLESH)),
                            new ItemStackTemplate(item.get())
                    );
                    output.accept(recipeId, recipe, advancements.build(output, recipeId, RecipeCategory.COMBAT));
                })
                .flatModel(MineTheSpire.id("item/" + name))
                .en(en)
                .register();
    }

    private static Identifier vanillaItemTexture(String textureName) {
        return Identifier.withDefaultNamespace("item/" + textureName);
    }

}
