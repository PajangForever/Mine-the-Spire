package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.compat.curios.CuriosSlot;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public class CrackedCoreRelic extends OriginalRelic {
    private static final AttributeModifier BALL_LIMIT_MODIFIER =
            new AttributeModifier(
                    MineTheSpire.id("broken_core_charge_ball_limit"),
                    2.0D,
                    AttributeModifier.Operation.ADD_VALUE);

    public CrackedCoreRelic(Properties properties) {
        super(properties);
    }

    @Override
    public void addAttributeModifiers(ItemStack stack, AttributeModifierAdder adder) {
        adder.addModifier(ModAttributes.LIGHTNING_CHARGE_BALL_LIMIT, BALL_LIMIT_MODIFIER, CuriosSlot.ORIGINAL_SPIRE_RELIC.name());
    }
}
