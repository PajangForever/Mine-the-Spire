package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.specials.OrbManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DualcastItem extends Item {
    public static final int MAX_ENERGY = 128;
    private static final int BAR_COLOR = 0x3FA7FF;

    public DualcastItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (entity instanceof Player) {
            setEnergy(stack, Math.min(MAX_ENERGY, getEnergy(stack) + 1));
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getEnergy(stack) < MAX_ENERGY) {
            if (!level.isClientSide()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.45F, 1.35F);
            }
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (!OrbManager.get(player).dualcastFirst()) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.45F, 1.0F);
                return InteractionResult.FAIL;
            }
            setEnergy(stack, 0);
        }

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * Mth.clamp(getEnergy(stack), 0, MAX_ENERGY) / MAX_ENERGY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    private static int getEnergy(ItemStack stack) {
        Integer energy = stack.get(ModDataComponents.EXTRA_ENERGY.get());
        return energy == null ? 0 : Mth.clamp(energy, 0, MAX_ENERGY);
    }

    private static void setEnergy(ItemStack stack, int energy) {
        stack.set(ModDataComponents.EXTRA_ENERGY.get(), Mth.clamp(energy, 0, MAX_ENERGY));
    }
}
