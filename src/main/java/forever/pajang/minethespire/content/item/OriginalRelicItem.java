package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.compat.curios.ModCuriosSlot;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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
        boolean found = ModItems.BURNING_BLOOD.get().tryFindAnyFromCuriosOrEquipment(owner);
        if (found) {
            owner.heal(HEAL_AMOUNT);
            return true;
        } else return false;
    }

    public static boolean ringOfTheSnakeBoostSpeed(LivingEntity owner) {
        if (owner.level().isClientSide()) {
            return false;
        }
        boolean found = ModItems.RING_OF_THE_SNAKE.get().tryFindAnyFromCuriosOrEquipment(owner);
        if (found) {
            owner.addEffect(new MobEffectInstance(ModEffects.SERPENT_SPEED, SPEED_DURATION_TICKS, 0, false, false, false));
            return true;
        } else return false;
    }

    @Override
    public Set<String> getCuriosSlots() {
        return Set.of(ModCuriosSlot.ORIGINAL_SPIRE_RELIC);
    }
}
