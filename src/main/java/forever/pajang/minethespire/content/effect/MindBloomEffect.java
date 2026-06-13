package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.ConfigTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MindBloomEffect extends MobEffect {
    public static volatile boolean ON_CMD_CLEAR = false;

    public MindBloomEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean tryPreventRemoval(LivingEntity entity, MobEffectInstance instance) {
        if (ON_CMD_CLEAR) return false;
        return instance != null && instance.is(ModEffects.MIND_BLOOM) && !entity.isDeadOrDying();
    }

    public static boolean tryPreventHeal(LivingEntity entity) {
        return entity.hasEffect(ModEffects.MIND_BLOOM);
    }

    public static boolean tryPreventSetHigherHealth(LivingEntity entity, float health) {
        if (ConfigTheSpire.FIERCE_MIND_BLOOM.getAsBoolean()) {
            return health > entity.getHealth() && tryPreventHeal(entity);
        }
        else return false;
    }

    public static boolean tryProtectTotem(LivingEntity entity) {
        if (entity.hasEffect(ModEffects.MIND_BLOOM)) {
            return ConfigTheSpire.FIERCE_MIND_BLOOM.getAsBoolean();
        } else return false;
    }
}
