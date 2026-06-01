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
            .defaultValue(-0.5D)
            .en("Overheal Change Rate")
            .register();
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_CHARGE_BALL_LIMIT = REG.attribute("max_charge_ball")
            .max(64.0D)
            .defaultValue(1.0D)
            .en("Max Charge Ball")
            .register();
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_CHARGE_BALL_ATTACK_RANGE = REG.attribute("charge_ball_attack_range")
            .max(128.0D)
            .defaultValue(8.0D)
            .en("Charge Ball Attack Range")
            .register();
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_CHARGE_BALL_ATTACK_SPEED = REG.attribute("charge_ball_attack_speed")
            .max(64.0D)
            .defaultValue(0.5D)
            .en("Charge Ball Attack Speed")
            .register();

    private ModAttributes() {
    }

    public static void register() {
    }
}
