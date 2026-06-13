package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.RelicItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.CuriosDataComponents;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.common.slot.SlotTypePredicate;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosEvents {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MineTheSpire.LOGGER.info("Mod Curios is loaded. Most features are enabled.");
    }

    public static final class ModBusEvent {

        @SubscribeEvent
        public static void registerCapability(RegisterCapabilitiesEvent event) {
            event.registerItem(CuriosCapability.ITEM, (stack, _) -> {
                if (stack.getItem() instanceof RelicItem) {
                    return new ICurio() {
                        @Override
                        public ItemStack getStack() {
                            return stack;
                        }

                        @Override
                        public void curioTick(SlotContext slotContext) {
                            ICurio.super.curioTick(slotContext);
                            if (!slotContext.entity().level().isClientSide()) {
                                ((RelicItem) stack.getItem()).tickCurios(getStack(), (ServerLevel) slotContext.entity().level(), slotContext.entity(), null);
                            }
                        }


                    };
                } else return () -> stack;
            }, RelicItem.RELICS.get().toArray(RelicItem[]::new));
        }

        @SubscribeEvent
        public static void registerCuriosDataComponents(ModifyDefaultComponentsEvent event) {
            event.modifyMatching((item, _) -> item.asItem() instanceof RelicItem, ModBusEvent::makeCuriosAttributeModifier);
        }

        private static void makeCuriosAttributeModifier(DataComponentMap.Builder components, HolderLookup.Provider context, Item item) {
            ItemAttributeModifiers modifiers = components.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (modifiers == null) return;
            CurioAttributeModifiers.Builder builder = CurioAttributeModifiers.builder();
            SlotTypePredicate predicate;
            if (item instanceof RelicItem relic) {
                String[] slots = relic.getCuriosSlots().toArray(String[]::new);
                predicate = SlotTypePredicate.builder().withId(slots).build();
            } else {
                predicate = SlotTypePredicate.ANY;
            }
            modifiers.modifiers().forEach(entry -> builder.addModifier(entry.attribute(), entry.modifier(), predicate));
            components.set(CuriosDataComponents.ATTRIBUTE_MODIFIERS, builder.build());
            components.set(DataComponents.ATTRIBUTE_MODIFIERS, null);
        }
    }


}
