package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class ActivatableStatesAttribute extends Attribute {
    private final MutableComponent[] descriptions;

    public ActivatableStatesAttribute(String selfDescriptionKey, MutableComponent[] descriptions) {
        super(selfDescriptionKey,0.0);
        if (descriptions.length > 8) {
            throw new IllegalArgumentException("Too many states! Max is 8");
        }
        this.descriptions = descriptions;
    }

    @Override
    public double sanitizeValue(double value) {
        if (value < 0 || !Double.isFinite(value)) {
            return 0.0;
        }
        return Math.floor(value);
    }

    @Override
    public MutableComponent toValueComponent(AttributeModifier.@Nullable Operation op, double value, TooltipFlag flag) {
        if (op == AttributeModifier.Operation.ADD_VALUE) {
            double floored = Math.floor(value);
            double log64value = Math.log(floored) / Math.log(64);
            double flooredLog = Math.floor(log64value + 0.1);
            if (Math.abs(log64value - flooredLog) < 1e-14) {
                return getDescription((int) flooredLog);
            }
        }
        return ModAttributes.STATE_INVALID_OPERATION.copy().withColor(0x7f0000);
    }

    @Override
    public MutableComponent toComponent(AttributeModifier modif, TooltipFlag flag) {
        return ModAttributes.STATE_ACTIVATE.copy().append(toValueComponent(modif.operation(), modif.amount(), flag))
                .append(getDebugInfo(modif, flag));
    }

    @Override
    public Component getDebugInfo(AttributeModifier modif, TooltipFlag flag) {
        if (flag.isAdvanced()) {
            if (modif.operation() != AttributeModifier.Operation.ADD_VALUE) {
                return Component.literal(" [Operation is not ADD_VALUE]").withColor(0x7f0000);
            }
            double floored = Math.floor(modif.amount());
            double log64value = Math.log(floored) / Math.log(64);
            double flooredLog = Math.floor(log64value + 0.1);
            if (Math.abs(log64value - flooredLog) < 1e-14) {
                int index = (int) flooredLog;
                return Component.literal(" [Indexed %s | +%.3e]".formatted(index, modif.amount())).withColor(0x7f7f7f);
            } else {
                return Component.literal(" [Index Failed | +%.3e | +64^(%.3f)]".formatted(modif.amount(), log64value)).withColor(0x7F0000);
            }

        }
        return Component.empty();
    }

    public MutableComponent getDescription(int index) {
        if (index < 0 || index >= descriptions.length) {
            return Component.literal("Invalid state index [%d]! Don't equip it or it may destroy the attribute system!".formatted(index)).withColor(0xff0000);
        }
        MutableComponent description = descriptions[index];
        return Objects.requireNonNullElseGet(description, () -> Component.literal("Unknown State [%d]".formatted(index)).withColor(0xcfcf00));
    }

    public static AttributeModifier createActivator(Identifier id, int index) {
        if (index < 0 || index >= 8) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for ActivatableStatesAttribute Modifier. Requires [0, 7]");
        }
        return new AttributeModifier(id, (1L << 6*index), AttributeModifier.Operation.ADD_VALUE);
    }

    public static UnaryOperator<ItemAttributeModifiers.Builder> modifyItem(Holder<Attribute> attribute, Identifier id, int index) {
        return builder -> builder.add(attribute, createActivator(id, index), EquipmentSlotGroup.ANY);
    }

    public static boolean getBoolean(int index, double value) {
        if (index < 0 || index >= 8) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for ActivatableStatesAttribute. Requires [0, 7]");
        }
        long l = (long) value;
        l >>>= (6*index);
        return (l & 63L) > 0;
    }
}
