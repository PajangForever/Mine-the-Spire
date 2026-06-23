package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

import static forever.pajang.minethespire.MineTheSpire.REG;

public class PainStrickItem extends Item {
    static Component TIP = REG.text().type("tooltip").info("pain_strick", "tip").en("On a full-charged attack, apply Vulnerable").register();

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(TIP.copy().withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    public PainStrickItem(Properties properties) {
        super(properties.sword(ToolMaterial.IRON, 5f, -3.35f));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) {
        if (!stack.is(this)) return;
        if (attacker instanceof Player player && player.getAttackStrengthScale(0.5F) < 0.9F) return;
        victim.addEffect(new MobEffectInstance(ModEffects.VULNERABLE, 80, 0), attacker);
    }


}
