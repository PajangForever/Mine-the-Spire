package forever.pajang.minethespire.compat.curios;

import net.minecraft.resources.Identifier;

import java.util.List;

public record CuriosSlot(
        String name,
        int order,
        int size,
        Identifier icon,
        List<Identifier> validators,
        boolean addPlayer
) {
    public static final Identifier EMPTY_ICON = Identifier.fromNamespaceAndPath(CuriosCompat.CURIOS, "slot/empty_curio_slot");
    public static final Identifier TAG_VALIDATOR = Identifier.fromNamespaceAndPath(CuriosCompat.CURIOS, "tag");

    public static final CuriosSlot SPIRE_RELIC = playerSlot("spire_relic", 260, 4);
    public static final CuriosSlot ORIGINAL_SPIRE_RELIC = playerSlot("original_spire_relic", 261, 1);

    public CuriosSlot {
        validators = List.copyOf(validators);
    }

    public static CuriosSlot slot(String name) {
        return new CuriosSlot(name, 0, 1, EMPTY_ICON, List.of(), false);
    }

    public static CuriosSlot playerSlot(String name, int order, int size) {
        return new CuriosSlot(name, order, size, EMPTY_ICON, List.of(), true);
    }
}
