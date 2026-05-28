package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import forever.pajang.minethespire.content.item.LizardTailItem;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;


public final class ModItems {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredItem<? extends Item> DARK_SHURIKEN = REG.item("dark_shuriken", DarkShurikenItem::new).in("main")
            .properties(p -> p.rarity(Rarity.EPIC))
            .model((i, g) -> flatItemWithTexture(i, g, MineTheSpire.id("item/mark_bloom")))
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
                builder.unlockedBy("has_totem_of_undying", RecipeUnlockedTrigger.unlocked(builder.defaultId()));
                builder.save(output);
            })
            .model((i, g) -> flatItemWithTexture(i, g, MineTheSpire.id("item/spirit")))
            .en("Spirit")
            .register();
    public static final DeferredItem<? extends Item> LIZARD_TAIL = REG.item("lizard_tail", LizardTailItem::new).in("main")
            .properties(p -> p.stacksTo(1).rarity(Rarity.UNCOMMON))
            .model((i, g) -> flatItemWithTexture(i, g, MineTheSpire.id("item/lizard_tail")))
            .en("Lizard Tail")
            .register();

    public static void register() {
    }

    public static void flatItemWithTexture(Supplier<? extends Item> i, Supplier<ItemModelGenerators> g, Identifier texture) {
        g.get().itemModelOutput.accept(i.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i.get()), TextureMapping.layer0(new Material(texture)), g.get().modelOutput)));
    }
}
