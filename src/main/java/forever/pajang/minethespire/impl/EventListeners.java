package forever.pajang.minethespire.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.command.CombatStateCmd;
import forever.pajang.minethespire.command.MindBloomForceClearCmd;
import forever.pajang.minethespire.command.OrbsCmd;
import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.effect.FairyBlessingEffect;
import forever.pajang.minethespire.content.effect.MindBloomEffect;
import forever.pajang.minethespire.content.effect.VeninEffect;
import forever.pajang.minethespire.content.item.DarkShurikenItem;
import forever.pajang.minethespire.content.item.HeavyBladeItem;
import forever.pajang.minethespire.content.item.LizardTailItem;
import forever.pajang.minethespire.content.specials.BlockingValueHandler;
import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.CombatState;
import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.living.LivingUseTotemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = MineTheSpire.MODID)
public final class EventListeners {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (!CuriosCompat.isLoaded()){
            MineTheSpire.LOGGER.warn("Mod Curios is not installed. Some features are disabled.");
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("mts");
        CombatStateCmd.register(root);
        MindBloomForceClearCmd.register(root);
        OrbsCmd.register(root);
        event.getDispatcher().register(root);

    }

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        MineTheSpire.REG.getBrewingRecipes().forEach(recipe -> recipe.accept(event.getBuilder()));
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        List<EntityType<? extends LivingEntity>> livingTypes = event.getTypes();
        BiConsumer<EntityType<? extends LivingEntity>, Holder<Attribute>> adder = event::add;
        MineTheSpire.REG.modifyEntityAttribute(livingTypes, adder);
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        ItemStack mainHandItem = event.getEntity().getMainHandItem();
        DarkShurikenItem.disableLeftClickDefaultAction(mainHandItem, event::setCanceled);
        CombatState.onAttack(event.getEntity(), event.getTarget());
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (MindBloomEffect.tryPreventHeal(entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
        if (event.getEntity().hasEffect(ModEffects.NO_ENTITY)
                && !event.getSource().is(DamageTypes.GENERIC_KILL)
                && !event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)
                && event.getNewDamage() > 1.0F) {
            event.setNewDamage(1.0F);
        }
        BlockingValueHandler.onDamage(event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        CombatState.onHurt(event.getEntity(), event.getSource());
        PainStrikeHandler.onLivingIncomingDamage(event);
    }

    @SubscribeEvent
    public static void onLivingUseTotem(LivingUseTotemEvent event) {
        LivingEntity entity = event.getEntity();
        if (MindBloomEffect.tryProtectTotem(entity) || FairyBlessingEffect.tryProtectTotem(entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (FairyBlessingEffect.tryPreventDeath(entity)) {
            event.setCanceled(true);
        } else if (LizardTailItem.tryPreventDeath(entity)) {
            event.setCanceled(true);
        }

    }

    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity living) {
            CombatState.onRemoved(living);
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();
        if (MindBloomEffect.tryPreventRemoval(entity, effect)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = event.getEffectInstance();
        if (MindBloomEffect.tryPreventRemoval(entity, effect)){
            event.setCanceled(true);
        }
        if (VeninEffect.tryDamageOnExpired(entity, effect)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living) {
            CombatState.tickEntity(living);
            OrbManager.get(living).tick();
            BlockingValueHandler.tick(living);
            HeavyBladeItem.tickStrengthModifier(living);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack mainHandItem = event.getEntity().getMainHandItem();
        DarkShurikenItem.disableLeftClickDefaultAction(mainHandItem, event::setCanceled);
    }

    public static class OnModBus {

        @SubscribeEvent
        public static void modifyDefaultItemComponents(ModifyDefaultComponentsEvent event) {

        }
    }

//    @SubscribeEvent
//    public static void onServerToStart(ServerAboutToStartEvent event) {
//        PlayerInnateTracker.TRACKER.clear();
//    }
//
//    @SubscribeEvent
//    public static void onServerStopped(ServerStoppedEvent event) {
//        PlayerInnateTracker.TRACKER.clear();
//    }
//
//    @SubscribeEvent
//    public static void onSwapEquipment(SwapEquipmentEvent event) {
//        ImplUtils.applyEthereal(event.swappedToHand(), event.player());
//    }
//
//    @SubscribeEvent
//    public static void onChangeSelectedSlot(ChangeSelectedSlotEvent event) {
//        ImplUtils.applyEthereal(event.inventory().getItem(event.before()), event.player());
//    }
//    @SubscribeEvent
//    public static void onTossItem(ItemTossEvent event) {
//        ImplUtils.applyEthereal(event.getEntity());
//    }
//
//    @SubscribeEvent
//    public static void onTableEnchantingItem(PlayerEnchantItemEvent event) {
//        Player player = event.getEntity();
//        ItemStack stack = event.getEnchantedItem();
//        if (EnchantmentHelper.has(stack, ModEnchantments.DATA_INNATE.get())) {
//            stack.set(ModDataComponents.INNATE_TRACKER, new ItemStackInnateTracker(player.getUUID(), 0));
//        }
//    }
//
//    @SubscribeEvent
//    public static void onAnvilUpdate(AnvilUpdateEvent event) {
//        Player player = event.getPlayer();
//        if (!player.level().isClientSide()){
//            ItemStack result = event.getOutput();
//            ImplUtils.updateInnateTracker(result, player);
//        }
//    }
//
//    @SubscribeEvent
//    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
//        Player player = event.getEntity();
//        if (!player.level().isClientSide()) {
//            PlayerInnateTracker tracker = player.getData(ModAttachments.INNATE_TRACKER);
//            tracker.restoreCounter().increment();
//            tracker.innateStacks().forEach(i -> player.addItem(i.copy()));
//            player.setData(ModAttachments.INNATE_TRACKER, tracker);
//        }
//    }

}
