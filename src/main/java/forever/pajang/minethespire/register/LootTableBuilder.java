package forever.pajang.minethespire.register;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.Supplier;

public final class LootTableBuilder extends RegisterCore.Builder {
    private final ResourceKey<LootTable> key;
    private Supplier<LootTable.Builder> tableFactory = LootTable::lootTable;

    LootTableBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
        this.key = ResourceKey.create(Registries.LOOT_TABLE, registerCore.id(name));
    }

    public LootTableBuilder table(Supplier<LootTable.Builder> tableFactory) {
        this.tableFactory = tableFactory;
        return this;
    }

    public LootTableBuilder register() {
        registerCore.lootTables.add(this);
        return this;
    }

    public ResourceKey<LootTable> key() {
        return key;
    }

    public LootTable.Builder build() {
        return tableFactory.get();
    }
}
