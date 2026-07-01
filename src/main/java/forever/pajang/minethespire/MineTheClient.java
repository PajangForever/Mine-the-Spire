package forever.pajang.minethespire;

import forever.pajang.minethespire.compat.curios.CuriosCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MineTheSpire.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MineTheSpire.MODID, value = Dist.CLIENT)
public class MineTheClient {
    public MineTheClient(ModContainer container) {
        IEventBus eventBus = container.getEventBus();
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        CuriosCompat.registerClientEventsIfLoaded(eventBus);
    }

}
