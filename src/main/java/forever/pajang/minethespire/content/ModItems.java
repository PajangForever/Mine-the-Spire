package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;


public final class ModItems {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredItem<? extends Item> DARK_SHURIKEN = REG.item("dark_shuriken", DarkShurikenItem::new).in("main")
            .properties(p -> p.rarity(Rarity.EPIC))
            .model((i, g) -> flatItemWithTexture(i, g, MineTheSpire.id("item/mark_bloom")))
            .register();

    public static void register() {
    }

    public static void flatItemWithTexture(Supplier<? extends Item> i, Supplier<ItemModelGenerators> g, Identifier texture) {
        g.get().itemModelOutput.accept(i.get(), ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(i.get()), TextureMapping.layer0(new Material(texture)), g.get().modelOutput)));
    }
}
