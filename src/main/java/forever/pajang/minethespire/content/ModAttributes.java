package forever.pajang.minethespire.content;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModAttributes {
    private static final RegisterCore REG = MineTheSpire.REG;

    public static final DeferredHolder<Attribute, Attribute> BLOCKING_VALUE = REG.attribute("blocking_value")
            .max(256.0D).defaultValue(0.0D).register();

    public static final DeferredHolder<Attribute, Attribute> BLOCKING_VALUE_CHANGE_RATE = REG.attribute("blocking_value_change_rate")
            .min(-1024.0D).max(1024.0D).defaultValue(-0.5D).register();

    public static final DeferredHolder<Attribute, Attribute> FOCUS = REG.attribute("focus")
            .min(-1024.0D).max(1024.0D).defaultValue(0.0D).register();

    public static final DeferredHolder<Attribute, Attribute> MAX_CHARGE_BALL = REG.attribute("max_charge_ball")
            .min(0.0D).max(64.0D).defaultValue(1.0D).register();

    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_CHARGE_BALL_ATTACK_RANGE = REG.attribute("charge_ball_attack_range")
            .max(128.0D).defaultValue(8.0D).register();

    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_CHARGE_BALL_ATTACK_SPEED = REG.attribute("charge_ball_attack_speed")
            .max(64.0D).defaultValue(0.5D).register();

    private ModAttributes() {
    }

    public static void register() {
    }
}
