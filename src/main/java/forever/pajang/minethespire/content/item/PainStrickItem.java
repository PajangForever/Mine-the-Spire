package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class PainStrickItem extends Item {

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
