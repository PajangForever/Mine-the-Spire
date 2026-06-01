package forever.pajang.minethespire.register;

import forever.pajang.minethespire.compat.curios.CuriosSlot;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class CuriosSlotBuilder extends RegisterCore.Builder {
    private int order = 0;
    private int size = 1;
    private Identifier icon = CuriosSlot.EMPTY_ICON;
    private final List<Identifier> validators = new ArrayList<>();
    private boolean addPlayer = false;

    CuriosSlotBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    CuriosSlotBuilder(RegisterCore registerCore, CuriosSlot slot) {
        super(registerCore, slot.name());
        this.order = slot.order();
        this.size = slot.size();
        this.icon = slot.icon();
        this.validators.addAll(slot.validators());
        this.addPlayer = slot.addPlayer();
    }

    public CuriosSlotBuilder order(int order) {
        this.order = order;
        return this;
    }

    public CuriosSlotBuilder size(int size) {
        this.size = size;
        return this;
    }

    public CuriosSlotBuilder icon(Identifier icon) {
        this.icon = icon;
        return this;
    }

    public CuriosSlotBuilder addValidator(Identifier validator) {
        this.validators.add(validator);
        return this;
    }

    public CuriosSlotBuilder addPlayer() {
        this.addPlayer = true;
        return this;
    }

    public CuriosSlot register() {
        CuriosSlot slot = new CuriosSlot(name, order, size, icon, validators, addPlayer);
        registerCore.curiosSlots.add(slot);
        return slot;
    }
}
