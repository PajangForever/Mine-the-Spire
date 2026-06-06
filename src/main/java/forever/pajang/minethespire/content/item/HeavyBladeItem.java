package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class HeavyBladeItem extends Item {
    private static final Identifier STRENGTH_MODIFIER_ID = MineTheSpire.id("heavy_blade_strength");
    private static final double DAMAGE_PER_STRENGTH_LEVEL = 6.0D;

    public HeavyBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        tooltipAdder.accept(Component.translatable("tooltip.minethespire.heavy_blade.extra_prefix").withStyle(ChatFormatting.DARK_BLUE)
                .append(Component.translatable("tooltip.minethespire.heavy_blade.strength").withStyle(ChatFormatting.DARK_RED))
                .append(Component.translatable("tooltip.minethespire.heavy_blade.extra_suffix").withStyle(ChatFormatting.DARK_BLUE)));
        tooltipAdder.accept(Component.literal("+" + formatBonus(getStrengthBonus(context.player()))).withStyle(ChatFormatting.DARK_BLUE)
                .append(Component.translatable("tooltip.minethespire.heavy_blade.attack_damage").withStyle(ChatFormatting.DARK_BLUE)));
    }

    public static void tickStrengthModifier(LivingEntity holder) {
        if (holder.level().isClientSide()) {
            return;
        }

        AttributeInstance attackDamage = holder.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) {
            return;
        }

        ItemStack mainHand = holder.getMainHandItem();
        if (!(mainHand.getItem() instanceof HeavyBladeItem)) {
            attackDamage.removeModifier(STRENGTH_MODIFIER_ID);
            return;
        }

        MobEffectInstance strength = holder.getEffect(MobEffects.STRENGTH);
        if (strength == null) {
            attackDamage.removeModifier(STRENGTH_MODIFIER_ID);
            return;
        }

        double amount = DAMAGE_PER_STRENGTH_LEVEL * (strength.getAmplifier() + 1);
        attackDamage.addOrUpdateTransientModifier(new AttributeModifier(STRENGTH_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE));
    }

    private static double getStrengthBonus(Player player) {
        if (player == null) {
            return 0.0D;
        }

        MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
        return strength == null ? 0.0D : DAMAGE_PER_STRENGTH_LEVEL * (strength.getAmplifier() + 1);
    }

    private static String formatBonus(double bonus) {
        return bonus == Math.rint(bonus) ? Integer.toString((int) bonus) : Double.toString(bonus);
    }
}
