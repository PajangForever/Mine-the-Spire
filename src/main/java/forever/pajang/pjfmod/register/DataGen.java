package forever.pajang.pjfmod.register;

import forever.pajang.pjfmod.PajangForeversMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = PajangForeversMod.MODID)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        event.createDatapackRegistryObjects(PajangForeversMod.REG.registrySetBuilder);
        PajangForeversMod.REG.dataProviders.forEach(f -> generator.addProvider(true, f.apply(output, lookupProvider)));
    }
}

