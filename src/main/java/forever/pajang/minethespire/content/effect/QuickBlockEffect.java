package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public class QuickBlockEffect extends InstantenousMobEffect {
    private static final int DURATION_TICKS = 5;

    public QuickBlockEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(ModAttributes.BLOCKING_VALUE_CHANGE_RATE, MineTheSpire.id("quick_block"), 21.0D, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void applyInstantenousEffect(ServerLevel level, @Nullable Entity source, @Nullable Entity owner, LivingEntity mob, int amplification, double scale) {
        int duration = Math.max(1, (int) Math.floor(DURATION_TICKS * scale));
        mob.addEffect(new MobEffectInstance(ModEffects.QUICK_BLOCK, duration, amplification), owner);
    }
}
