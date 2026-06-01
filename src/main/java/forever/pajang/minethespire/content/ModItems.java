package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.*;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;
import java.util.Optional;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;

public final class ModItems {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredItem<? extends Item> DARK_SHURIKEN = REG.item("dark_shuriken", DarkShurikenItem::new).in("main")
            .properties(p -> p.rarity(Rarity.EPIC))
            .flatModel(MineTheSpire.id("item/mark_bloom"))
            .register();
    public static final DeferredItem<? extends Item> SPIRIT = REG.simpleItem("spirit").in("main")
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

    public static final DeferredItem<? extends Relic> LIZARD_TAIL = REG.item("lizard_tail", LizardTailItem::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON))
            .defaultModel().en("Lizard Tail").register();

    public static final DeferredItem<? extends OriginalRelic> BURNING_BLOOD = REG.item("burning_blood", OriginalRelic::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().register();

    public static final DeferredItem<? extends OriginalRelic> RING_OF_THE_SNAKE = REG.item("ring_of_the_snake", OriginalRelic::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().register();

    public static final DeferredItem<? extends CrackedCoreRelic> CRACKED_CORE = REG.item("cracked_core", CrackedCoreRelic::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().register();

    public static final DeferredItem<? extends Item> LIGHTNING_CHARGE_BALL = REG.item("lightning_charge_ball", LightningChargeBallItem::new).in("main")
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .model((i, g) -> cubeItemWithTexture(i, g, MineTheSpire.id("item/lightning_charge_ball")))
            .en("Lightning Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> FROST_CHARGE_BALL = REG.item("frost_charge_ball", FrostChargeBallItem::new).in("main")
            .properties(p -> p.stacksTo(16).rarity(Rarity.UNCOMMON))
            .model((i, g) -> cubeItemWithTexture(i, g, MineTheSpire.id("item/frost_charge_ball")))
            .en("Frost Charge Ball")
            .register();

    public static final DeferredItem<? extends Item> DOUBLE_RELEASE = REG.item("double_release", DoubleReleaseItem::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE)).defaultModel().en("Double Release").register();
    public static final DeferredItem<? extends Item> WOODEN_PAIN_STRIKE = painStrike(
            "wooden_pain_strike", "wooden_axe", Items.WOODEN_AXE, ToolMaterial.WOOD, 7.0F, -3.36F, "Wooden Pain Strike", false
    );
    public static final DeferredItem<? extends Item> STONE_PAIN_STRIKE = painStrike(
            "stone_pain_strike", "stone_axe", Items.STONE_AXE, ToolMaterial.STONE, 8.0F, -3.36F, "Stone Pain Strike", false
    );
    public static final DeferredItem<? extends Item> COPPER_PAIN_STRIKE = painStrike(
            "copper_pain_strike", "copper_axe", Items.COPPER_AXE, ToolMaterial.COPPER, 8.0F, -3.36F, "Copper Pain Strike", false
    );
    public static final DeferredItem<? extends Item> GOLDEN_PAIN_STRIKE = painStrike(
            "golden_pain_strike", "golden_axe", Items.GOLDEN_AXE, ToolMaterial.GOLD, 7.0F, -3.20F, "Golden Pain Strike", false
    );
    public static final DeferredItem<? extends Item> IRON_PAIN_STRIKE = painStrike(
            "iron_pain_strike", "iron_axe", Items.IRON_AXE, ToolMaterial.IRON, 7.0F, -3.28F, "Iron Pain Strike", false
    );
    public static final DeferredItem<? extends Item> DIAMOND_PAIN_STRIKE = painStrike(
            "diamond_pain_strike", "diamond_axe", Items.DIAMOND_AXE, ToolMaterial.DIAMOND, 6.0F, -3.20F, "Diamond Pain Strike", false
    );
    public static final DeferredItem<? extends Item> NETHERITE_PAIN_STRIKE = painStrike(
            "netherite_pain_strike", "netherite_axe", Items.NETHERITE_AXE, ToolMaterial.NETHERITE, 6.0F, -3.20F, "Netherite Pain Strike", true
    );

    public static void register() {
    }

    public static boolean isPainStrike(Item item) {
        return item == WOODEN_PAIN_STRIKE.get()
                || item == STONE_PAIN_STRIKE.get()
                || item == COPPER_PAIN_STRIKE.get()
                || item == GOLDEN_PAIN_STRIKE.get()
                || item == IRON_PAIN_STRIKE.get()
                || item == DIAMOND_PAIN_STRIKE.get()
                || item == NETHERITE_PAIN_STRIKE.get();
    }

    public static void cubeItemWithTexture(Supplier<? extends Item> i, Supplier<ItemModelGenerators> g, Identifier texture) {
        g.get().itemModelOutput.accept(i.get(), ItemModelUtils.plainModel(ModelTemplates.CUBE_ALL.create(ModelLocationUtils.getModelLocation(i.get()), TextureMapping.cube(new Material(texture)), g.get().modelOutput)));
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
                .in("main")
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
                .flatModel(vanillaItemTexture(baseTexture))
                .en(en)
                .register();
    }

    private static Identifier vanillaItemTexture(String textureName) {
        return Identifier.withDefaultNamespace("item/" + textureName);
    }
}
