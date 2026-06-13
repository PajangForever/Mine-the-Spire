package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.RelicItem;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public final class CuriosCompat {
    static final String CURIOS = "curios";
    private static final boolean LOADED = ModList.get().isLoaded("curios");
    static final Identifier ITEM_HANDLER = id("item_handler");
    static final EntityCapability<ResourceHandler<ItemResource>, Void> CURIOS_ITEM_HANDLER =
            EntityCapability.createVoid(ITEM_HANDLER, ResourceHandler.asClass());
    private static final Map<UUID, Pair<Integer, Long>> CURIOS_SWAP_INDEX = new HashMap<>();

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

    public static ItemStack tryEquipOrSwap(LivingEntity entity, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof RelicItem thisRelic)) return itemStack;
        long timeNow = Util.getMillis();
        AtomicReference<ItemStack> swapped = new AtomicReference<>(itemStack);
        getCuriosItemHandler(entity).ifPresent(handler -> {
            int size = handler.size();
            AtomicBoolean success = new AtomicBoolean(false);
            try (Transaction transaction = Transaction.openRoot()) {
                for (int i = 0; i < size; i++) {
                    ItemResource itemResource = handler.getResource(i);
                    if (itemResource.isEmpty()) {
                        try (Transaction insertOne = Transaction.open(transaction)) {
                            int insertedCount = handler.insert(i, ItemResource.of(itemStack), 1, insertOne);
                            if (insertedCount == 1) {
                                swapped.set(ItemStack.EMPTY);
                                success.set(true);
                                insertOne.commit();
                            }
                        }
                    }
                    if (success.get()) {
                        break;
                    }
                }
                if (success.get()) {
                    transaction.commit();
                } else {
                    Pair<Integer, Long> index = CURIOS_SWAP_INDEX.getOrDefault(entity.getUUID(), Pair.of(0, timeNow));
                    if (timeNow - index.right() > 5000) {
                        CURIOS_SWAP_INDEX.remove(entity.getUUID());
                        index = Pair.of(0, timeNow);                    }
                    for (int i = 0; i < size; i++) {
                        int j = (i + index.left()) % size;
                        ItemResource itemResource = handler.getResource(j);
                        if (itemResource.getItem() instanceof RelicItem thatRelic && thatRelic.getCuriosSlots().stream().anyMatch(thisRelic.getCuriosSlots()::contains)) {
                            try (Transaction swapOne = Transaction.open(transaction)) {
                                int extractedCount = handler.extract(j, itemResource, 1, swapOne);
                                if (extractedCount == 1) {
                                    int insertedCount = handler.insert(j, ItemResource.of(itemStack), 1, swapOne);
                                    if (insertedCount == 1) {
                                        swapped.set(itemResource.toStack(1));
                                        success.set(true);
                                        CURIOS_SWAP_INDEX.put(entity.getUUID(), Pair.of(j + 1, timeNow));
                                        swapOne.commit();
                                    }
                                }
                            }
                        } else continue;
                        if (success.get()) {
                            break;
                        }
                    }
                    if (success.get()) transaction.commit();
                }
            }
        });
        return swapped.get();
    }
}
