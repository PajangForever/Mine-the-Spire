package forever.pajang.minethespire.register;

import forever.pajang.minethespire.MineTheSpire;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MineTheSpire.MODID)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        event.createDatapackRegistryObjects(MineTheSpire.REG.registrySetBuilder);
        MineTheSpire.REG.dataProviders.forEach(f -> generator.addProvider(true, f.apply(output, lookupProvider)));
    }
}

