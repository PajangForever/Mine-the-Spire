package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SerpentSpeedEffect extends MobEffect {
    public SerpentSpeedEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MineTheSpire.id("serpent_speed"), 0.3D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
