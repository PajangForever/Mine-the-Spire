package forever.pajang.minethespire.content.effect;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.ModEffects;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;

public class VigorEffect extends MobEffect {
    public VigorEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, MineTheSpire.id("vigor_add_damage"), AttributeModifier.Operation.ADD_VALUE, amp -> amp + 1d);
    }

    public static void tryRemoveVigorOnAttack(DamageSource source) {
        Entity entity = source.getEntity();
        Entity directEntity = source.getDirectEntity();
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.VIGOR)) {
            living.removeEffect(ModEffects.VIGOR);
        }
        if (directEntity instanceof LivingEntity directLiving && directLiving.hasEffect(ModEffects.VIGOR)) {
            directLiving.removeEffect(ModEffects.VIGOR);
        }
    }

}
