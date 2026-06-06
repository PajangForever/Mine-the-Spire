package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class PlasmaChargeEffect extends MobEffect {
    public PlasmaChargeEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(ModAttributes.LIGHTNING_CHARGE_BALL_ATTACK_SPEED, MineTheSpire.id("plasma_charge"), 0.5D, AttributeModifier.Operation.ADD_VALUE);
    }
}
