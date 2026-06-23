package forever.pajang.minethespire.content.item;

import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.entity.DarkShurikenProjectile;
import forever.pajang.minethespire.content.entity.ModProjectile;
import forever.pajang.minethespire.mixin.client.GameRendererAccessor;
import forever.pajang.minethespire.network.DarkShurikenChargePayload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.function.Consumer;

import static forever.pajang.minethespire.MineTheSpire.REG;

public class DarkShurikenItem extends Item {
    static Component BENEFIT = REG.text().type("tooltip").info("dark_shuriken", "benefit").en("Fully Enchant all your equipments").register();
    static Component HARM = REG.text().type("tooltip").info("dark_shuriken", "harm").en("You can no longer heal").register();

    public static final int MIND_BLOOM_CHARGE_TICKS = 20;
    public static int CLIENT_TICK_CHARGED = 0;
    public static boolean CLIENT_CHARGING = false;

    public DarkShurikenItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        builder.accept(BENEFIT.copy().withStyle(ChatFormatting.GREEN));
        builder.accept(HARM.copy().withStyle(ChatFormatting.RED));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            DarkShurikenProjectile projectile = new DarkShurikenProjectile(level, player, stack.copyWithCount(1));
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 0.0F);
            projectile.setXRot(player.getYRot());
            projectile.setYRot(player.getYRot());
            level.addFreshEntity(projectile);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!player.hasInfiniteMaterials()) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return MIND_BLOOM_CHARGE_TICKS;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TOOT_HORN;
    }

    // client

    public static boolean onAttackButtonDown(Minecraft mc, BooleanConsumer handSwingSetter) {
        if (mc.player == null || mc.screen != null) {
            return false;
        }
        if (mc.player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            handSwingSetter.accept(false);
            if (!CLIENT_CHARGING) {
                beginCharge(mc);
            }
            return true;
        }
        return false;
    }

    public static void disableLeftClickDefaultAction(ItemStack inHandItem, BooleanConsumer eventCanceller) {
        if (inHandItem.is(ModItems.DARK_SHURIKEN.get())) {
            eventCanceller.accept(true);
        }
    }

    public static void tickCharge(Minecraft mc) {
        if (mc.player == null || mc.level == null) return;
        if (CLIENT_CHARGING) {
            if (mc.screen != null || !mc.options.keyAttack.isDown() || !mc.player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
                abortCharge(mc);
            }
            else {
                if (!mc.player.isUsingItem()) {
                    mc.player.startUsingItem(InteractionHand.MAIN_HAND);
                }
                CLIENT_TICK_CHARGED++;
                if (CLIENT_TICK_CHARGED >= MIND_BLOOM_CHARGE_TICKS) {
                    if (mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                        mc.player.stopUsingItem();
                    }
                    if (mc.getConnection() != null) {
                        ClientPacketDistributor.sendToServer(DarkShurikenChargePayload.INSTANCE);
                    }
                    CLIENT_TICK_CHARGED = 0;
                    CLIENT_CHARGING = false;
                }
            }
        }
    }

    public static void beginCharge(Minecraft mc) {
        if (mc.player == null) return;
        CLIENT_CHARGING = true;
        mc.player.startUsingItem(InteractionHand.MAIN_HAND);
        mc.gameRenderer.displayItemActivation(mc.player.getMainHandItem().copyWithCount(1));
    }

    private static void abortCharge(Minecraft mc) {
        if (mc.player == null) return;
        CLIENT_CHARGING = false;
        if (mc.player.isUsingItem() && mc.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            mc.player.stopUsingItem();
        }
        ((GameRendererAccessor) mc.gameRenderer).minethespire$getScreenEffectRenderer().resetItemActivation();
    }

    // server

    public static void triggerServer(Player player) {
        if (player.level().isClientSide() || !player.getMainHandItem().is(ModItems.DARK_SHURIKEN.get())) {
            return;
        }

        player.addEffect(new MobEffectInstance(ModEffects.MIND_BLOOM, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 1.5F, 1.2F);
        enchantEquipment(player);
        if (!player.hasInfiniteMaterials()) {
            player.getMainHandItem().shrink(1);
        }
    }

    private static void enchantEquipment(Player player) {
        var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        EquipmentSlot.VALUES.forEach(equipmentSlot -> enchantStack(player.getItemBySlot(equipmentSlot), registry));
    }

    private static void enchantStack(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> registry) {
        if (stack.isEmpty()) {
            return;
        }
        for (Holder<Enchantment> holder : registry.listElements().toList()) {
            if (stack.supportsEnchantment(holder)) {
                stack.enchant(holder, holder.value().definition().maxLevel());
            }
        }
    }
}
