package forever.pajang.minethespire.register;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.CuriosResources;
import top.theillusivec4.curios.api.type.data.ISlotData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class CuriosBuilder extends RegisterCore.Builder {
    private int order = 0;
    private int size = 1;
    private String icon = "slot/empty_curio_slot";
    private final List<String> validators = new ArrayList<>();
    private boolean addPlayer = false;
    private final List<Supplier<? extends Item>> taggedItems = new ArrayList<>();

    CuriosBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    public CuriosBuilder order(int order) {
        this.order = order;
        return this;
    }

    public CuriosBuilder size(int size) {
        this.size = size;
        return this;
    }

    public CuriosBuilder icon(String icon) {
        this.icon = icon;
        return this;
    }

    public CuriosBuilder addValidator(String validator) {
        this.validators.add(validator);
        return this;
    }

    public CuriosBuilder addPlayer() {
        this.addPlayer = true;
        return this;
    }

    public CuriosBuilder tag(Supplier<? extends Item> item) {
        this.taggedItems.add(item);
        return this;
    }

    public CuriosBuilder register() {
        registerCore.curiosBuilders.add(this);
        return this;
    }

    public void generate(CuriosDataProvider provider) {
        ISlotData slot = provider.createSlot(name)
                .order(order)
                .size(size)
                .icon(CuriosResources.resource(icon));
        validators.forEach(validator -> slot.addValidator(CuriosResources.resource(validator)));
        if (addPlayer) {
            provider.createEntities(name + "_wearers")
                    .addPlayer()
                    .addSlots(slot);
        }
        taggedItems.forEach(item -> provider.tag(name).add(item.get()));
    }
}
