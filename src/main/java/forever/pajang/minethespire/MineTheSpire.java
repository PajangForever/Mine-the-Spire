package forever.pajang.minethespire;

import forever.pajang.minethespire.compat.curios.CuriosCompat;
import forever.pajang.minethespire.compat.jade.BlockingValueJade;
import forever.pajang.minethespire.content.*;
//import forever.pajang.minethespire.content.ModEnchantments;
import forever.pajang.minethespire.register.ModDataProviders;
import forever.pajang.minethespire.register.RegisterCore;
import forever.pajang.minethespire.network.ModNetworking;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MineTheSpire.MODID)
public class MineTheSpire {
    public static final String MODID = "minethespire";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final RegisterCore REG = RegisterCore.create(MODID);

    public MineTheSpire(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.register();
        ModEntityTypes.register();
        ModEffects.register();
        ModPotions.register();
        ModAttachments.register();
        ModAttributes.register();
        ModDataComponents.register();
        ModEnchantments.register();
        CuriosCompat.registerEventsIfLoaded(modEventBus);
        BlockingValueJade.register();
        ModDataProviders.register(REG);
        modEventBus.addListener(ModNetworking::register);
        REG.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Deprecated
    public static void debug() {
        LOGGER.debug("Triggered!");
    }
}
