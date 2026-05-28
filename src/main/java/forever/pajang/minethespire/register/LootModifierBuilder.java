package forever.pajang.minethespire.register;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;

public final class LootModifierBuilder extends RegisterCore.Builder {
    private LootItemCondition[] conditions = new LootItemCondition[0];
    private int weight = 0;
    private ResourceKey<LootTable> table;

    LootModifierBuilder(RegisterCore registerCore, String name) {
        super(registerCore, name);
    }

    public LootModifierBuilder conditions(LootItemCondition... conditions) {
        this.conditions = conditions;
        return this;
    }

    public LootModifierBuilder weight(int weight) {
        this.weight = weight;
        return this;
    }

    public LootModifierBuilder table(ResourceKey<LootTable> table) {
        this.table = table;
        return this;
    }

    public LootModifierBuilder table(String path) {
        this.table = ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.withDefaultNamespace(path));
        return this;
    }

    public LootModifierBuilder register() {
        registerCore.lootModifiers.add(this);
        return this;
    }

    public AddTableLootModifier build() {
        return new AddTableLootModifier(conditions, weight, table);
    }
}
