package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.MineTheSpire;
import forever.pajang.minethespire.content.item.RelicItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.CuriosTags;
import top.theillusivec4.curios.api.type.data.ISlotData;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

final class CuriosDataGenerator {

    private CuriosDataGenerator() {}

    static DataProvider createDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        return new CuriosDataProvider(MineTheSpire.MODID, output, lookupProvider) {
            @Override
            public void generate(HolderLookup.Provider registries) {
                MineTheSpire.REG.getCuriosSlots().forEach(slot -> CuriosDataGenerator.generateData(slot, this, RelicItem.RELICS.get()));
            }
        };
    }

    private static void generateData(CuriosSlotBuilder slot, CuriosDataProvider provider, Collection<RelicItem> relics) {
        ISlotData slotData = provider.createSlot(slot.getName()).order(slot.getOrder()).size(slot.getSize()).icon(slot.getIcon());
        slotData.addValidator(CuriosSlotBuilder.TAG_VALIDATOR);
        if (!slot.getEntityTypes().isEmpty()) slotData.addEntity(slot.getEntityTypes().toArray(EntityType[]::new));
        if (slot.isPlayerAdd()) slotData.addEntities(CuriosTags.PLAYER_LIKE);
        provider.tag(slotData).addAll(relics.stream().filter(relic -> relic.getCuriosSlots().contains(slot.getName()))
                .map(Item.class::cast).toList());
    }

}
