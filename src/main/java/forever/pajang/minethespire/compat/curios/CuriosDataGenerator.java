package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.Relic;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.CuriosTags;
import top.theillusivec4.curios.api.type.data.ISlotData;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class CuriosDataGenerator {
    private CuriosDataGenerator() {
    }

    static DataProvider createDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new CuriosDataProvider(MineTheSpire.MODID, output, lookupProvider) {
            @Override
            public void generate(HolderLookup.Provider registries) {
                MineTheSpire.REG.getCuriosSlots().forEach(slot -> CuriosDataGenerator.generateData(slot, this));
            }
        };
    }

    private static void generateData(CuriosSlot slot, CuriosDataProvider provider) {
        ISlotData slotData = provider.createSlot(slot.name()).order(slot.order()).size(slot.size()).icon(slot.icon());
        slot.validators().forEach(slotData::addValidator);
        if (slot.addPlayer()) slotData.addEntities(CuriosTags.PLAYER_LIKE);
        provider.tag(slotData).addAll(MineTheSpire.REG.getRegisteredItems().stream()
                .map(Supplier::get)
                .filter(Relic.class::isInstance)
                .map(Relic.class::cast)
                .filter(relic -> relic.getCuriosSlots().contains(slot))
                .map(relic -> (Item) relic)
                .toList());
        registerLang(slot.name());
    }

    private static void registerLang(String slotName) {
        String idKey = "curios.identifier." + slotName;
        String modKey = "curios.modifiers." + slotName;
        String displayName = Arrays.stream(slotName.split("_"))
                .map(s -> s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
        String modName = "When on " + displayName + ":";
        MineTheSpire.REG.text(idKey).en(displayName).register();
        MineTheSpire.REG.text(modKey).en(modName).register();
    }
}
