package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.compat.curios.ModCuriosSlot;
import forever.pajang.minethespire.content.ModAttributes;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.specials.OrbType;
import forever.pajang.minethespire.impl.ActivatableStatesAttribute;
import forever.pajang.minethespire.content.specials.OrbManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Set;

public class OriginalRelicItem extends RelicItem {
    private static final float HEAL_AMOUNT = 6.0F;

    private static final int SPEED_DURATION_TICKS = 100;
    private static final int COOLDOWN_TICKS = 6 * 20;

    public OriginalRelicItem(Properties properties) {
        super(properties);
    }

    public static boolean burningBloodHeal(LivingEntity owner) {
        if (owner.level().isClientSide()) {
            return false;
        }
//        boolean found = ModItems.BURNING_BLOOD.get().tryFindAnyFromCuriosOrEquipment(owner);
        AttributeInstance attribute = owner.getAttribute(ModAttributes.ACTIVATABLE_STATES);
        if (attribute != null && ActivatableStatesAttribute.getBoolean(ModAttributes.State.BURNING_BLOOD.getIndex(), attribute.getValue())) {
            owner.heal(HEAL_AMOUNT);
            return true;
        } else return false;
    }

    public static boolean ringOfTheSnakeBoostSpeed(LivingEntity owner) {
        if (owner.level().isClientSide()) {
            return false;
        }
        AttributeInstance attribute = owner.getAttribute(ModAttributes.ACTIVATABLE_STATES);
        if (attribute != null && ActivatableStatesAttribute.getBoolean(ModAttributes.State.RING_OF_THE_SNAKE.getIndex(), attribute.getValue())) {
            owner.addEffect(new MobEffectInstance(ModEffects.SERPENT_SPEED, SPEED_DURATION_TICKS, 0, false, true, true));
            return true;
        } else return false;
    }

    public static boolean crackedCoreSummonFirstBall(LivingEntity owner) {
        OrbManager orbManager = OrbManager.get(owner);
        if (owner.level().isClientSide()){
            return false;
        }

        AttributeInstance attribute = owner.getAttribute(ModAttributes.ACTIVATABLE_STATES);
        if (attribute == null || !ActivatableStatesAttribute.getBoolean(ModAttributes.State.CRACKED_CORE.getIndex(), attribute.getValue())) {
            return false;
        }

        if (orbManager.getOrbCount() > 0) {
            return false;
        }

        return orbManager.tryChannel(OrbType.Lightning);
    }

    @Override
    public Set<String> getCuriosSlots() {
        return Set.of(ModCuriosSlot.ORIGINAL_SPIRE_RELIC);
    }
}
