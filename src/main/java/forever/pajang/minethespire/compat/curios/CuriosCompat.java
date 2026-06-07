package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class CuriosCompat {
    static final String CURIOS = "curios";
    private static final boolean LOADED = ModList.get().isLoaded("curios");
    static final Identifier ITEM_HANDLER = id("item_handler");
    static final EntityCapability<ResourceHandler<ItemResource>, Void> CURIOS_ITEM_HANDLER =
            EntityCapability.createVoid(ITEM_HANDLER, ResourceHandler.asClass());

    private CuriosCompat() {}

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(CURIOS, path);
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    public static void registerEventsIfLoaded(IEventBus modEventBus) {
        if (isLoaded()) {
            NeoForge.EVENT_BUS.register(CuriosEvents.class);
            modEventBus.register(CuriosEvents.ModBusEvent.class);
            NeoForge.EVENT_BUS.register(PatchForCurios.class);
        }
    }

    public static DataProvider createDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return CuriosDataGenerator.createDataProvider(output, lookupProvider);
    }

    public static Optional<ResourceHandler<ItemResource>> getCuriosItemHandler(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(CURIOS_ITEM_HANDLER));
    }

    public static boolean tryFindAny(LivingEntity entity, Predicate<Item> predicate) {
        AtomicBoolean found = new AtomicBoolean(false);
        getCuriosItemHandler(entity).ifPresent(handler -> {
            int size = handler.size();
            for (int i = 0; i < size; i++) {
                ItemResource itemResource = handler.getResource(i);
                if (predicate.test(itemResource.getItem())) {
                    found.set(true);
                    break;
                }
            }
        });
        return found.get();
    }

    public static boolean tryConsumeFirst(LivingEntity entity, Predicate<Item> predicate) {
        AtomicBoolean found = new AtomicBoolean(false);
        getCuriosItemHandler(entity).ifPresent(handler -> {
            int size = handler.size();
            for (int i = 0; i < size; i++) {
                ItemResource itemResource = handler.getResource(i);
                if (predicate.test(itemResource.getItem())) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int extracted = handler.extract(i, itemResource, 1, transaction);
                        if (extracted == 1) {
                            found.set(true);
                            transaction.commit();
                        }
                    }
                    break;
                }
            }
        });
        return found.get();
    }
}
