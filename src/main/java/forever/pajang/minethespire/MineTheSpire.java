package forever.pajang.minethespire;

import forever.pajang.minethespire.content.ModAttachments;
import forever.pajang.minethespire.content.ModDataComponents;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.ModEnchantments;
import forever.pajang.minethespire.content.ModItems;
import forever.pajang.minethespire.content.ModEntityTypes;
import forever.pajang.minethespire.register.RegisterCore;
import forever.pajang.minethespire.network.ModNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(MineTheSpire.MODID)
public class MineTheSpire {
    public static final String MODID = "minethespire";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final RegisterCore REG = RegisterCore.create(MODID);

    public MineTheSpire(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModItems.register();
        ModEntityTypes.register();
        ModEffects.register();
        ModAttachments.register();
        ModDataComponents.register();
        ModEnchantments.register();
        modEventBus.addListener(ModNetworking::register);
        REG.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @Deprecated
    public static void debug() {
        LOGGER.debug("Triggered!");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }
        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());
        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
