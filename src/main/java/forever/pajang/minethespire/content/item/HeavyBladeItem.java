package forever.pajang.minethespire.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

import static forever.pajang.minethespire.MineTheSpire.REG;

public class HeavyBladeItem extends Item {
    static Component EXTRA = REG.text().type("tooltip").info("heavy_blade", "extra_strength_tooltip").en("Strength Affect this weapon 3 times: ").register();
    static Component DAMAGE = REG.text().type("tooltip").info("heavy_blade", "attack_damage").en(" Attack Damage").register();
    private static final double DAMAGE_PER_STRENGTH_LEVEL = 6.0D;

    public HeavyBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        tooltipAdder.accept(EXTRA.copy().withStyle(ChatFormatting.GOLD));
        tooltipAdder.accept(Component.literal(" +" + formatBonus(context.player())).withStyle(ChatFormatting.GREEN)
                .append(DAMAGE.copy().withStyle(ChatFormatting.GREEN)));
    }

    @Override
    public float getAttackDamageBonus(Entity victim, float damage, DamageSource source) {
        Entity entity = source.getEntity();
        if (entity instanceof LivingEntity living) {
            MobEffectInstance strength = living.getEffect(MobEffects.STRENGTH);
            if (strength == null) return 0f;
            return (float) (DAMAGE_PER_STRENGTH_LEVEL * (strength.getAmplifier() + 1));
        }
        return 0f;
    }

    private static String formatBonus(Player player) {
        double bonus;
        if (player == null) {
            bonus = 0.0D;
        }
        else {
            MobEffectInstance strength = player.getEffect(MobEffects.STRENGTH);
            bonus = strength == null ? 0.0D : DAMAGE_PER_STRENGTH_LEVEL * (strength.getAmplifier() + 1);
        }
        return bonus == Math.rint(bonus) ? Integer.toString((int) bonus) : Double.toString(bonus);
    }
}
