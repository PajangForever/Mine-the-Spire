package forever.pajang.pjfmod.content;

import forever.pajang.pjfmod.PajangForeversMod;
import forever.pajang.pjfmod.register.RegisterCore;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    private static final RegisterCore REG = PajangForeversMod.REG;

    public static final DeferredItem<Item> DARK_SHURIKEN = REG.simpleItem("dark_shuriken").in("main")
            .properties(p -> p.food(new FoodProperties(10, 10, true)).rarity(Rarity.EPIC))
            .model((i, g) -> g.get().generateFlatItem(i.get(), ModelTemplates.FLAT_ITEM))
            .register();

    public static void register() {
    }
}
