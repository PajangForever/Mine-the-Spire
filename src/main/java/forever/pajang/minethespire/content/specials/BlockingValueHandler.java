package forever.pajang.minethespire.content.specials;

import forever.pajang.minethespire.content.ModAttributes;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public final class BlockingValueHandler {
    private BlockingValueHandler() {
    }

    public static void grant(LivingEntity entity, float amount) {
        if (amount <= 0.0F) {
            return;
        }

        double blockingValue = getBlockingValue(entity);
        double nextAmount = Math.max(blockingValue, amount);
        if (nextAmount > blockingValue) {
            setBlockingValue(entity, nextAmount);
        }
    }

    public static void add(LivingEntity entity, float amount) {
        if (amount <= 0.0F || entity.level().isClientSide()) {
            return;
        }

        setBlockingValue(entity, getBlockingValue(entity) + amount);
    }

    public static void multiply(LivingEntity entity, double multiplier) {
        if (entity.level().isClientSide()) {
            return;
        }

        setBlockingValue(entity, getBlockingValue(entity) * multiplier);
    }

    public static void tick(LivingEntity entity) {
        if (!entity.isAlive() || entity.level().isClientSide()) {
            return;
        }

        double blockingValue = getBlockingValue(entity);
        double changeRate = getBlockingValueChangeRate(entity);
        if (changeRate == 0.0D) {
            return;
        }

        setBlockingValue(entity, blockingValue + changeRate);
    }

    public static void absorbDamage(LivingEntity entity, DamageSource source, Supplier<Float> getter, FloatConsumer setter) {
        float remaining;
        float damage = getter.get();
        if (damage <= 0.0F || entity.level().isClientSide()) {
            remaining = damage;
        } else {
            double blockingValue = getBlockingValue(entity);
            if (blockingValue <= 0.0D) {
                remaining = damage;
            } else {
                float absorbed = (float) Math.min(damage, blockingValue);
                float remaining1 = damage - absorbed;
                setBlockingValue(entity, blockingValue - absorbed);
                remaining = remaining1;
            }
        }
        setter.accept(remaining);
    }

    private static double getBlockingValue(LivingEntity entity) {
        return entity.getAttributeValue(ModAttributes.BLOCKING_VALUE);
    }

    private static double getBlockingValueChangeRate(LivingEntity entity) {
        return entity.getAttributeValue(ModAttributes.BLOCKING_VALUE_CHANGE_RATE);
    }

    private static void setBlockingValue(LivingEntity entity, double value) {
        AttributeInstance instance = entity.getAttribute(ModAttributes.BLOCKING_VALUE);
        if (instance != null) {
            instance.setBaseValue(Math.max(0.0D, value));
        }
    }
}
