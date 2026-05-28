package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModAttributes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<Attribute, Attribute> OVERHEAL = REG.attribute("overheal")
            .defaultValue(0.0D)
            .en("Overheal")
            .register();
    public static final DeferredHolder<Attribute, Attribute> OVERHEAL_CHANGE_RATE = REG.attribute("overheal_change_rate")
            .min(-1024.0D)
            .max(1024.0D)
            .defaultValue(-1.0D)
            .en("Overheal Change Rate")
            .register();

    private ModAttributes() {
    }

    public static void register() {
    }
}
