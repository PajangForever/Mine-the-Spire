package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModPotions {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<Potion, Potion> BOTTLED_FAIRY = REG.potion("bottled_fairy")
            .effect(ModEffects.FAIRY_BLESSING, 3 * 60 * 20)
            .addNormal(ModPotions::createBottledFairyStack)
            .en("Bottled Fairy").register();
    public static final DeferredHolder<Potion, Potion> LONG_BOTTLED_FAIRY = REG.potion("long_bottled_fairy")
            .effect(ModEffects.FAIRY_BLESSING, 8 * 60 * 20).addNormal(ModPotions::createBottledFairyStack)
            .en("Bottled Fairy").register();
    public static final DeferredHolder<Potion, Potion> BLOCKING = REG.potion("blocking")
            .effect(ModEffects.QUICK_BLOCK, 5, 19).brewStart(Items.COPPER_INGOT).addNormal()
            .en("Potion of Blocking").register();
    public static final DeferredHolder<Potion, Potion> BARRICADE = REG.potion("barricade")
            .effect(ModEffects.BARRICADE, 8 * 60 * 20).brewStart(Items.SHIELD).addNormal()
            .en("Potion of Barricade").register();
    public static final DeferredHolder<Potion, Potion> DEADLY_POISON = REG.potion("deadly_poison")
            .effect(ModEffects.VENIN, 5 * 20, 6).brewStart(Items.POISONOUS_POTATO).addSplash()
            .en("Deadly Poison").register();
    public static final DeferredHolder<Potion, Potion> STRONG_DEATH = REG.potion("strong_deadly_poison")
            .effect(ModEffects.VENIN, 5 * 20, 9).brewFrom(DEADLY_POISON, Items.GLOWSTONE_DUST).addSplash()
            .en("Deadly Poison").register();
    public static final DeferredHolder<Potion, Potion> FOCUS = REG.potion("focus")
            .effect(ModEffects.FOCUS_BOOST, 3 * 60 * 20).brewStart(Items.COPPER_BLOCK).addNormal()
            .en("Potion of Focus").register();
    public static final DeferredHolder<Potion, Potion> LONG_FOCUS = REG.potion("long_focus")
            .effect(ModEffects.FOCUS_BOOST, 8 * 60 * 20).brewFrom(FOCUS, Items.REDSTONE).addNormal()
            .en("Potion of Focus").register();
    public static final DeferredHolder<Potion, Potion> STRONG_FOCUS = REG.potion("strong_focus")
            .effect(ModEffects.FOCUS_BOOST, 3 * 60 * 20, 1).brewFrom(FOCUS, Items.GLOWSTONE_DUST).addNormal()
            .en("Potion of Focus").register();
    public static final DeferredHolder<Potion, Potion> VULNERABLE = REG.potion("vulnerable")
            .effect(ModEffects.VULNERABLE, 3 * 60 * 20, 1).brewStart(Items.ROTTEN_FLESH).addSplash().addLingering()
            .en("Potion of Vulnerable").register();
    public static final DeferredHolder<Potion, Potion> LONG_VULNERABLE = REG.potion("long_vulnerable")
            .effect(ModEffects.VULNERABLE, 8 * 60 * 20, 1).brewFrom(VULNERABLE, Items.REDSTONE).addSplash().addLingering()
            .en("Potion of Vulnerable").register();
    public static final DeferredHolder<Potion, Potion> STRONG_VULNERABLE = REG.potion("strong_vulnerable")
            .effect(ModEffects.VULNERABLE, 3 * 60 * 20, 2).brewFrom(VULNERABLE, Items.GLOWSTONE_DUST).addSplash().addLingering()
            .en("Potion of Vulnerable").register();

    static {
        potency(6, "VII");
        potency(7, "VIII");
        potency(8, "IX");
        potency(9, "X");
        potency(10, "XI");
        potency(11, "XII");
        potency(12, "XIII");
        potency(13, "XIV");
        potency(14, "XV");
        potency(15, "XVI");
        potency(16, "XVII");
        potency(17, "XVIII");
        potency(18, "XIX");
        potency(19, "XX");
        potency(20, "XXI");
    }

    public static void register() {
        registerFairyPotionModel();
    }

    private static ItemStack createBottledFairyStack(Holder<Potion> potion) {
        ItemStack stack = PotionContents.createItemStack(Items.POTION, potion);
        String name = "fairy_potion";
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        stack.set(DataComponents.ITEM_MODEL, REG.id(name));
        return stack;
    }

    private static void registerFairyPotionModel() {
        String name = "fairy_potion";
        REG.itemModel(name, g -> {
            Identifier texture = REG.id("item/" + name);
            Identifier id = ModelTemplates.FLAT_ITEM.create(texture, TextureMapping.layer0(new Material(texture)), g.get().modelOutput);
            g.get().itemModelOutput.register(REG.id(name), new ClientItem(ItemModelUtils.plainModel(id), ClientItem.Properties.DEFAULT));
        });
    }

    private static void potency(int i, String string) {
        REG.text("potion.potency." + i).en(string).register();
    }

}
