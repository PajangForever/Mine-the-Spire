package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class PainStrikeHandler {
    private static final int VULNERABLE_DURATION = 3 * 20;
    private static final int VULNERABLE_AMPLIFIER = 0;

    private PainStrikeHandler() {
    }

    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) {
            return;
        }

        applyVulnerabilityDamageBonus(target, event);
        applyVulnerabilityOnPainStrikeHit(target, event);
    }

    private static void applyVulnerabilityDamageBonus(LivingEntity target, LivingIncomingDamageEvent event) {
        MobEffectInstance effect = target.getEffect(ModEffects.VULNERABLE);
        if (effect == null) {
            return;
        }

        float multiplier = 1.0F + 0.5F * (effect.getAmplifier() + 1);
        event.setAmount(event.getAmount() * multiplier);
    }

    private static void applyVulnerabilityOnPainStrikeHit(LivingEntity target, LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = player.getMainHandItem();
        if (!ModItems.isPainStrike(itemStack.getItem())) {
            return;
        }

        if (player.getAttackStrengthScale(0.5F) <= 0.9F) {
            return;
        }

        target.addEffect(new MobEffectInstance(ModEffects.VULNERABLE, VULNERABLE_DURATION, VULNERABLE_AMPLIFIER), player);
    }
}
