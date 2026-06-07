package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.content.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class MindBloomEffect extends MobEffect {
    public MindBloomEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean tryPreventRemoval(LivingEntity entity, MobEffectInstance instance) {
        return instance != null && instance.is(ModEffects.MIND_BLOOM) && !entity.isDeadOrDying();
    }

    public static boolean tryPreventHeal(LivingEntity entity) {
        return entity.hasEffect(ModEffects.MIND_BLOOM);
    }
}
