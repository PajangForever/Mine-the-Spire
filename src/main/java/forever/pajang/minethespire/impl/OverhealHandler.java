package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public final class OverhealHandler {
    private OverhealHandler() {
    }

    public static float absorbDamage(LivingEntity entity, float damage) {
        if (damage <= 0.0F || entity.level().isClientSide()) {
            return damage;
        }

        double overheal = getOverheal(entity);
        if (overheal <= 0.0D) {
            return damage;
        }

        float absorbed = (float)Math.min(damage, overheal);
        float remaining = damage - absorbed;
        setOverheal(entity, overheal - absorbed);
        return remaining;
    }

    public static void grant(LivingEntity entity, float amount) {
        if (amount <= 0.0F) {
            return;
        }

        double overheal = getOverheal(entity);
        double nextAmount = Math.max(overheal, amount);
        if (nextAmount > overheal) {
            setOverheal(entity, nextAmount);
        }
    }

    public static void add(LivingEntity entity, float amount) {
        if (amount <= 0.0F || entity.level().isClientSide()) {
            return;
        }

        setOverheal(entity, getOverheal(entity) + amount);
    }

    public static void tick(LivingEntity entity) {
        if (!entity.isAlive() || entity.level().isClientSide()) {
            return;
        }

        double overheal = getOverheal(entity);
        double changeRate = getOverhealChangeRate(entity);
        if (changeRate == 0.0D) {
            return;
        }

        setOverheal(entity, overheal + changeRate);
    }

    public static float onDamage(LivingEntity entity, LivingDamageEvent.Pre event) {
        float remaining = absorbDamage(entity, event.getNewDamage());
        event.setNewDamage(remaining);
        return remaining;
    }

    private static double getOverheal(LivingEntity entity) {
        return entity.getAttributeValue(ModAttributes.OVERHEAL);
    }

    private static double getOverhealChangeRate(LivingEntity entity) {
        return entity.getAttributeValue(ModAttributes.OVERHEAL_CHANGE_RATE);
    }

    private static void setOverheal(LivingEntity entity, double value) {
        AttributeInstance instance = entity.getAttribute(ModAttributes.OVERHEAL);
        if (instance != null) {
            instance.setBaseValue(Math.max(0.0D, value));
        }
    }
}
