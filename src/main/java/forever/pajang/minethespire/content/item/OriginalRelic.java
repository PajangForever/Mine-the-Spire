package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.compat.curios.CuriosSlot;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class OriginalRelic extends Relic {
    private static final float HEAL_AMOUNT = 6.0F;

    private static final int SPEED_DURATION_TICKS = 100;
    private static final int COOLDOWN_TICKS = 6 * 20;

    public OriginalRelic(Properties properties) {
        super(properties);
    }

    public static boolean burningBloodHeal(LivingEntity owner) {
        if (owner.level().isClientSide()) {
            return false;
        }
        Set<ItemStack> bloods = ModItems.BURNING_BLOOD.get().getFromCuriosOrEquipmentSlot(owner);
        if (bloods.isEmpty()) {
            return false;
        } else {
            owner.heal(HEAL_AMOUNT * bloods.size());
            return true;
        }
    }

    public static boolean ringOfTheSnakeBoostSpeed(LivingEntity owner) {
        if (owner.level().isClientSide()) {
            return false;
        }
        Set<ItemStack> rings = ModItems.RING_OF_THE_SNAKE.get().getFromCuriosOrEquipmentSlot(owner);
        if (rings.isEmpty()) {
            return false;
        } else {
            owner.addEffect(new MobEffectInstance(ModEffects.SERPENT_SPEED, SPEED_DURATION_TICKS, rings.size() - 1, false, false, false));
            return true;
        }
    }

    @Override
    public Set<CuriosSlot> getCuriosSlots() {
        return Set.of(CuriosSlot.ORIGINAL_SPIRE_RELIC);
    }
}
