package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.entity.BouncingFlaskProjectile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

import static forever.pajang.minethespire.MineTheSpire.REG;

public class BouncingFlaskItem extends Item {
    static Component TIP = REG.text().type("tooltip").info("bouncing_flask", "tip").en("Apply Venin III 3 times").register();
    private static final float THROW_POWER = 1.35F;

    public BouncingFlaskItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(TIP.copy().withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            BouncingFlaskProjectile projectile = new BouncingFlaskProjectile(level, player, stack.copyWithCount(1));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_POWER, 0.0F);
            level.addFreshEntity(projectile);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
}
