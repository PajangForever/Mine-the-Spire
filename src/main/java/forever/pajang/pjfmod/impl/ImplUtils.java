package forever.pajang.pjfmod.impl;

import forever.pajang.pjfmod.content.ModAttachments;
import forever.pajang.pjfmod.content.ModDataComponents;
import forever.pajang.pjfmod.content.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;

public class ImplUtils {

    public static MinecraftServer getCurrentServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean applyEthereal(ItemStack stack, LivingEntity entity) {
        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_ETHEREAL.get()) && !stack.isEmpty()) {
            Holder<SoundEvent> breakSound = stack.get(DataComponents.BREAK_SOUND);
            SoundEvent etherealSound = SoundEvents.LAVA_EXTINGUISH;
            if (!entity.isSilent()) {
                entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), etherealSound, entity.getSoundSource(), 0.8F, 0.8F + entity.getRandom().nextFloat() * 0.4F, false);
                if (breakSound != null) {
                    entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), breakSound.value(), entity.getSoundSource(), 0.8F, 0.5F + entity.getRandom().nextFloat() * 0.4F, false);
                }
            }
            entity.spawnItemParticles(stack, 4 + stack.getCount() * 2);
            emitParticles(entity, ParticleTypes.SMOKE, 4 + stack.getCount() * 2);
            stack.setCount(0);
            return true;
        }
        else return false;
    }

    public static boolean applyEthereal(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_ETHEREAL.get()) && !stack.isEmpty()) {
            itemEntity.setNeverPickUp();
            itemEntity.setRemainingFireTicks(1200);
            itemEntity.lifespan = itemEntity.getAge() + 60;
            return true;
        }
        else return false;
    }
    
    public static void emitParticles(Entity entity, ParticleOptions particleOptions, int count) {
        for (int i = 0; i < count; i++) {
            Vec3 d = new Vec3((entity.getRandom().nextFloat() - 0.5) * 0.1, entity.getRandom().nextFloat() * 0.1 + 0.1, 0.0);
            d = d.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
            d = d.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
            double y1 = -entity.getRandom().nextFloat() * 0.6 - 0.3;
            Vec3 p = new Vec3((entity.getRandom().nextFloat() - 0.5) * 0.3, y1, 0.6);
            p = p.xRot(-entity.getXRot() * (float) (Math.PI / 180.0));
            p = p.yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
            p = p.add(entity.getX(), entity.getEyeY(), entity.getZ());
            entity.level().addParticle(particleOptions, p.x, p.y, p.z, d.x, d.y + 0.05, d.z);
        }
    }

    public static boolean isSlotEquipment(Slot slot, Player player) {
        return slot.getContainerSlot() == player.getInventory().getSelectedSlot() || Inventory.EQUIPMENT_SLOT_MAPPING.containsKey(slot.getContainerSlot());
    }

    public static @Nullable Holder<Item> applyInnate(@Nullable Holder<Item> item, PatchedDataComponentMap components) {
        if (item != null && item.value() != Items.AIR && getCurrentServer() != null && !components.isPatchEmpty()) {
            Holder<Item> dummy = Items.AIR.builtInRegistryHolder();
            if (hasInnate(components)) {
                components.clearPatch();
                return dummy;
            }
        }
        return item;
    }

    public static boolean hasInnate(DataComponentMap components) {
        ItemEnchantments enchantments = components.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        return enchantments.keySet().stream().anyMatch(e -> e.value().effects().has(ModEnchantments.DATA_INNATE));
    }

    static void updateInnateTracker(ItemStack stack, Player player) {
        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_INNATE.get())) {
            PlayerInnateTracker tracker = player.getData(ModAttachments.INNATE_TRACKER);
            int c = tracker.restoreCounter().intValue();
            stack.set(ModDataComponents.INNATE_TRACKER, new ItemStackInnateTracker(player.getUUID(), c));
            PlayerInnateTracker newTracker = new PlayerInnateTracker(new ArrayList<>(tracker.innateStacks()), Set.of(), new MutableInt(c));
            if (tracker.innateStacks().stream().noneMatch(s -> ItemStack.isSameItemSameComponents(s, stack))) {
                newTracker.innateStacks().add(stack.copy());
            }
            player.setData(ModAttachments.INNATE_TRACKER, newTracker);
        }
    }
}

