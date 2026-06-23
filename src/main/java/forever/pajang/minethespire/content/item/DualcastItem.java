package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.specials.OrbManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DualcastItem extends Item {
    public static final int MAX_CHARGE = 114514;

    public DualcastItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player player) {
            double atkSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);
            setCharge(stack,  Math.min(MAX_CHARGE, (int) (getCharge(stack) + MAX_CHARGE * atkSpeed / 400)));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getCharge(stack) < MAX_CHARGE) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.45F, 1.35F);
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (!OrbManager.get(player).dualcastFirst()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_DISPENSE, SoundSource.PLAYERS, 0.45F, 1.0F);
                return InteractionResult.FAIL;
            }
            setCharge(stack, 0);
        }

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * Mth.clamp(getCharge(stack), 0, MAX_CHARGE) / MAX_CHARGE);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x3FA7FF;
    }

    private static int getCharge(ItemStack stack) {
        Integer energy = stack.get(ModDataComponents.SKILL_ITEM_CHARGE.get());
        return energy == null ? 0 : Mth.clamp(energy, 0, MAX_CHARGE);
    }

    private static void setCharge(ItemStack stack, int charge) {
        stack.set(ModDataComponents.SKILL_ITEM_CHARGE.get(), Mth.clamp(charge, 0, MAX_CHARGE));
    }
}
