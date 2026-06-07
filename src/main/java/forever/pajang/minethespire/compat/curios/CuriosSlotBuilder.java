package forever.pajang.minethespire.compat.curios;

import forever.pajang.minethespire.register.RegisterCore;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class CuriosSlotBuilder extends RegisterCore.Builder {
    public static final Identifier TAG_VALIDATOR = Identifier.fromNamespaceAndPath(CuriosCompat.CURIOS, "tag");
    public static final Identifier EMPTY_ICON = Identifier.fromNamespaceAndPath(CuriosCompat.CURIOS, "slot/empty_curio_slot");

    private int order = 999;
    private int size = 1;
    private Identifier icon = EMPTY_ICON;
    private Set<EntityType<?>> entityTypes = new HashSet<>();
    private boolean addPlayer = true;
    private String en;

    public CuriosSlotBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
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

    public CuriosSlotBuilder en(String translation) {
        this.en = translation;
        return this;
    }

    public CuriosSlotBuilder addEntityTypes(Collection<EntityType<?>> entityTypes) {
        this.entityTypes.addAll(entityTypes);
        return this;
    }

    public CuriosSlotBuilder addEntityType(EntityType<?> entityType) {
        this.entityTypes.add(entityType);
        return this;
    }

    public CuriosSlotBuilder removePlayerLike() {
        this.addPlayer = false;
        return this;
    }

    public String register() {
        registerCore.getCuriosSlots().add(this);
        registerLang(this.name);
        return this.name;
    }

    private void registerLang(String slotName) {
        String idKey = "curios.identifier." + slotName;
        String modifierKey = "curios.modifiers." + slotName;
        String displayName = RegisterCore.getDisplayTitle(slotName);
        String modifierName = "When on " + displayName + ":";
        registerCore.text(idKey).en(displayName).register();
        registerCore.text(modifierKey).en(modifierName).register();
    }

    String getName() {
        return name;
    }

    int getOrder() {
        return order;
    }

    int getSize() {
        return size;
    }

    Identifier getIcon() {
        return icon;
    }

    Set<EntityType<?>> getEntityTypes() {
        return entityTypes;
    }

    boolean isPlayerAdd() {
        return addPlayer;
    }
}
