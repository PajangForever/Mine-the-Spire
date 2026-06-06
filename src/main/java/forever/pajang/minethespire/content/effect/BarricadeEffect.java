package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class BarricadeEffect extends MobEffect {
    public BarricadeEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(ModAttributes.BLOCKING_VALUE_CHANGE_RATE, MineTheSpire.id("barricade"), 0.5D, AttributeModifier.Operation.ADD_VALUE);
    }
}
