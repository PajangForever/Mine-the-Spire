package forever.pajang.pjfmod.register;

import forever.pajang.pjfmod.PajangForeversMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = PajangForeversMod.MODID)
public class DataGen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput output = event.getGenerator().getPackOutput();
        event.createDatapackRegistryObjects(PajangForeversMod.REG.registrySetBuilder);
        PajangForeversMod.REG.dataProviders.forEach(f -> generator.addProvider(true, f.apply(output)));
    }
}

