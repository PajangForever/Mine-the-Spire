package forever.pajang.minethespire.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forever.pajang.minethespire.content.specials.CombatState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;

public final class CombatStateCmd {
    public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {
                root.then(Commands.literal("combat")
                        .then(Commands.literal("tick")
                                .executes(context -> sendCombatTicks(context.getSource())))
                        .then(Commands.literal("hostiles")
                                .executes(context -> sendCombatHostiles(context.getSource()))));
    }

    private static int sendCombatTicks(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int ticks = CombatState.get(player).getTickRemains();
        source.sendSuccess(() -> Component.literal("Combat tick remains: " + ticks + " tick"), false);
        return ticks;
    }

    private static int sendCombatHostiles(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        Set<UUID> hostiles = CombatState.get(player).getHostileUUIDs();
        if (hostiles.isEmpty()) {
            source.sendSuccess(() -> Component.literal("Hostile UUIDs: []"), false);
        } else {
            source.sendSuccess(() -> Component.literal("Hostile UUIDs: " + hostiles), false);
        }
        return hostiles.size();
    }
}
