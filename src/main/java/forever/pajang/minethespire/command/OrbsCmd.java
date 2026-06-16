package forever.pajang.minethespire.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import forever.pajang.minethespire.content.specials.OrbManager;
import forever.pajang.minethespire.content.specials.OrbType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.server.command.EnumArgument;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class OrbsCmd {
    public static EnumArgument<OrbType> ORB_ARGUMENT = EnumArgument.enumArgument(OrbType.class);

    public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("orb")
                .then(Commands.literal("clear")
                        .executes(OrbsCmd::clearPlayer)
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(OrbsCmd::clearLivings))
                )
                .then(Commands.literal("channel")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("orbType", ORB_ARGUMENT)
                                        .executes(OrbsCmd::channel)
                                )
                        )
                )
        );
    }

    private static int clearPlayer(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player != null) {
            OrbManager orbManager = OrbManager.get(player);
            int count = orbManager.getOrbCount();
            orbManager.clearOrbs();
            if (count > 0) {
                ctx.getSource().sendSuccess(() -> Component.literal("Removed " + count + " Orbs from ").append(player.getDisplayName()), false);
            } else {
                ctx.getSource().sendFailure(Component.literal("No Orbs are owned by ").append(player.getDisplayName()));
            }
            return count;
        }
        ctx.getSource().sendFailure(Component.literal("Failed to clear Orbs"));
        return 0;
    }

    private static int clearLivings(CommandContext<CommandSourceStack> ctx) {
        try {
            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "targets");
            AtomicInteger count = new AtomicInteger();
            entities.stream().forEach(entity -> {
                if (entity instanceof LivingEntity living) {
                    OrbManager orbManager = OrbManager.get(living);
                    count.addAndGet(orbManager.getOrbCount());
                    orbManager.clearOrbs();
                }
            });
            if (count.get() > 0) {
                ctx.getSource().sendSuccess(() -> Component.literal("Removed " + count + " Orbs from " + entities.size() + " Entities"), false);
                return count.get();
            } else {
                ctx.getSource().sendFailure(Component.literal("No Orbs are existing among " + entities.size() + " Entities"));
                return 0;
            }
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("Failed to clear Orbs: " + e.getMessage()));
            return 0;
        }
    }

    private static int channel(CommandContext<CommandSourceStack> ctx) {
        try {
            Collection<? extends Entity> entities = EntityArgument.getEntities(ctx, "targets");
            OrbType type = ctx.getArgument("orbType", OrbType.class);
            List<LivingEntity> list = entities.stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).toList();
            if (list.isEmpty()) {
                ctx.getSource().sendFailure(Component.literal("No available entities found"));
                return 0;
            } else {
                list.forEach(living -> type.getChanneler().test(OrbManager.get(living)));
                ctx.getSource().sendSuccess(() -> Component.literal("Channeled " + list.size() + " " + type.toString() + " Orbs"), true);
                return list.size();
            }
        } catch (CommandSyntaxException e) {
            ctx.getSource().sendFailure(Component.literal("Failed to channel Orbs: " + e.getMessage()));
            return 0;
        }
    }

}
