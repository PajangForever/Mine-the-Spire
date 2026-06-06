package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.impl.BlockingValueHandler;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntrenchItem extends Item {
    public EntrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            BlockingValueHandler.multiply(player, 2.0D);
            playEffects((ServerLevel) level, player);
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }

        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private static void playEffects(ServerLevel level, Player player) {
        double x = player.getX();
        double y = player.getY() + player.getBbHeight() * 0.5D;
        double z = player.getZ();
        level.playSound(null, x, player.getY(), z, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8F, 0.75F);
        level.sendParticles(ParticleTypes.END_ROD, x, y, z, 24, 0.45D, player.getBbHeight() * 0.35D, 0.45D, 0.03D);
        level.sendParticles(ParticleTypes.POOF, x, y, z, 18, 0.35D, player.getBbHeight() * 0.25D, 0.35D, 0.01D);
    }
}
