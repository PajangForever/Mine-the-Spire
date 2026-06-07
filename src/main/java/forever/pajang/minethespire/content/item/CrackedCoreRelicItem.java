package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class CrackedCoreRelicItem extends OriginalRelicItem {
    private static final AttributeModifier BALL_LIMIT_MODIFIER =
            new AttributeModifier(
                    MineTheSpire.id("cracked_core_add_ball_limit"),
                    2.0D,
                    AttributeModifier.Operation.ADD_VALUE);

    public CrackedCoreRelicItem(Properties properties) {
        if (!CuriosCompat.isLoaded()) properties.attributes(ItemAttributeModifiers.builder()
                .add(ModAttributes.MAX_CHARGE_BALL, BALL_LIMIT_MODIFIER, EquipmentSlotGroup.ANY).build());
        super(properties);
    }

}
