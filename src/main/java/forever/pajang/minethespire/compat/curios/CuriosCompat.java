package forever.pajang.minethespire.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class CuriosCompat {
    public static final String CURIOS = "curios";

    private CuriosCompat() {
    }

    public static boolean isLoaded() {
        ModList modList = ModList.get();
        return modList != null && modList.isLoaded(CURIOS);
    }

    public static void registerEventsIfLoaded() {
        if (isLoaded()) {
            CuriosApiProxy.registerEvents();
        }
    }

    public static boolean hasCurio(LivingEntity entity, Predicate<ItemStack> predicate) {
        return isLoaded() && CuriosApiProxy.hasCurio(entity, predicate);
    }

    public static void forEachCurio(LivingEntity entity, Consumer<ItemStack> action) {
        if (!isLoaded()) {
            return;
        }
        CuriosApiProxy.forEachCurio(entity, action);
    }

    public static Set<ItemStack> getCurioItems(LivingEntity entity, Predicate<ItemStack> predicate) {
        return isLoaded() ? CuriosApiProxy.getCurioItems(entity, predicate) : new HashSet<>();
    }

    public static boolean consumeFirstCurio(LivingEntity entity, Predicate<ItemStack> predicate) {
        return isLoaded() && CuriosApiProxy.consumeFirstCurio(entity, predicate);
    }

    public static DataProvider createDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return CuriosDataGenerator.createDataProvider(output, lookupProvider);
    }
}
