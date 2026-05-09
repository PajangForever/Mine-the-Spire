package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.register.RegisterCore;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.BlocksAttacks;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public final class ModItems {
    private static final RegisterCore REG = PajangForeversMod.REG;

    public static final DeferredItem<Item> DARK_SHURIKEN = REG.simpleItem("dark_shuriken").in("main")
            .properties(p -> p.food(new FoodProperties(10, 10, true)).rarity(Rarity.EPIC))
            .model((i, g) -> flatItemWithTexture(i, g, PajangForeversMod.id("item/mark_bloom")))
            .register();

    public static final DeferredItem<GreatswordItem> GREATSWORD = REG.item("greatsword", GreatswordItem::new).in("main")
            .properties(p -> p.sword(ToolMaterial.DIAMOND, 15, -3.5f)
                    .delayedComponent(DataComponents.BLOCKS_ATTACKS, context -> new BlocksAttacks(
                                    0.25F,
                                    1.0F,
                                    List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
                                    new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                                    Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                                    Optional.of(SoundEvents.SHIELD_BLOCK),
                                    Optional.of(SoundEvents.SHIELD_BREAK)
                            )
                    ).durability(60))
            .model((i, g) -> flatItemWithTexture(i, g, Identifier.withDefaultNamespace("item/diamond_sword")))
            .register();

    public static void register() {
    }

    public static void flatItemWithTexture(Supplier<? extends Item> i, Supplier<ItemModelGenerators> g, Identifier texture) {
        g.get().itemModelOutput.accept(i.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i.get()), TextureMapping.layer0(new Material(texture)), g.get().modelOutput)));
    }
}
