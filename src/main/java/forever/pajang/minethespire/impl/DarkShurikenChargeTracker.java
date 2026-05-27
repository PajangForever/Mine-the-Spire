package forever.pajang.minethespire.impl;

import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DarkShurikenChargeTracker {
    public static final Map<UUID, ChargeState> CHARGES = new ConcurrentHashMap<>();
    public static final Map<UUID, BellState> BELL_RINGS = new ConcurrentHashMap<>();
    private static final int BELL_RING_INTERVAL_TICKS = 10;
    private static final int BELL_RING_COUNT = 3;

    private DarkShurikenChargeTracker() {
    }

    public static void start(Player player) {
        CHARGES.put(player.getUUID(), new ChargeState());
    }

    public static void stop(Player player) {
        CHARGES.remove(player.getUUID());
    }

    public static void clear(Player player) {
        UUID uuid = player.getUUID();
        CHARGES.remove(uuid);
        BELL_RINGS.remove(uuid);
    }

    public static void stop(UUID uuid) {
        CHARGES.remove(uuid);
    }

    public static boolean isCharging(Player player) {
        return CHARGES.containsKey(player.getUUID());
    }

    public static void tick(Player player) {
        tickBellRings(player);

        ChargeState state = CHARGES.get(player.getUUID());
        if (state == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(ModItems.DARK_SHURIKEN.get())) {
            stop(player);
            return;
        }
        state.ticks++;
        if (state.ticks >= DarkShurikenItem.MIND_BLOOM_CHARGE_TICKS) {
            grantMindBloom(player);
            stop(player);
        }
    }

    private static void grantMindBloom(Player player) {
        player.addEffect(new MobEffectInstance(ModEffects.MIND_BLOOM, MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
        enchantEquipment(player);
        startBellRings(player);
    }

    private static void startBellRings(Player player) {
        playBell(player);
        BELL_RINGS.put(player.getUUID(), new BellState(1, BELL_RING_INTERVAL_TICKS));
    }

    private static void tickBellRings(Player player) {
        BellState state = BELL_RINGS.get(player.getUUID());
        if (state == null) {
            return;
        }
        state.cooldown--;
        if (state.cooldown > 0) {
            return;
        }
        playBell(player);
        state.rings++;
        if (state.rings >= BELL_RING_COUNT) {
            BELL_RINGS.remove(player.getUUID());
        } else {
            state.cooldown = BELL_RING_INTERVAL_TICKS;
        }
    }

    private static void playBell(Player player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void enchantEquipment(Player player) {
        var registry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        enchantStack(player.getItemBySlot(EquipmentSlot.HEAD), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.CHEST), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.LEGS), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.FEET), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.OFFHAND), registry);
        enchantStack(player.getItemBySlot(EquipmentSlot.MAINHAND), registry);
    }

    private static void enchantStack(ItemStack stack, net.minecraft.core.HolderLookup.RegistryLookup<Enchantment> registry) {
        if (stack.isEmpty()) {
            return;
        }
        for (Holder<Enchantment> holder : registry.listElements().toList()) {
            Enchantment enchantment = holder.value();
            if (enchantment.canEnchant(stack)) {
                stack.enchant(holder, enchantment.definition().maxLevel());
            }
        }
    }

    public static final class ChargeState {
        int ticks;
    }

    public static final class BellState {
        int rings;
        int cooldown;

        BellState(int rings, int cooldown) {
            this.rings = rings;
            this.cooldown = cooldown;
        }
    }
}
