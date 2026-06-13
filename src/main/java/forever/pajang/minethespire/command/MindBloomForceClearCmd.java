package forever.pajang.minethespire.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import forever.pajang.minethespire.content.ModEffects;
import forever.pajang.minethespire.content.effect.MindBloomEffect;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public final class MindBloomForceClearCmd {
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(
            Component.translatable("commands.effect.clear.specific.failed")
    );

    public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("clearMindBloom")
                .then(Commands.argument("targets", EntityArgument.entities())
                    .executes(context -> {

                        MindBloomEffect.ON_CMD_CLEAR = true;
                        Collection<? extends Entity> entities = EntityArgument.getEntities(context, "targets");
                        int count = 0;
                        for (Entity entity : entities) {
                            if (entity instanceof LivingEntity living && living.removeEffect(ModEffects.MIND_BLOOM)) {
                                count++;
                            }
                        }
                        MindBloomEffect.ON_CMD_CLEAR = false;

                        if (count == 0) {
                            throw ERROR_CLEAR_SPECIFIC_FAILED.create();
                        } else {
                            CommandSourceStack source = context.getSource();
                            MindBloomEffect effect = ModEffects.MIND_BLOOM.get();
                            if (entities.size() == 1) {
                                source.sendSuccess(() -> Component.translatable("commands.effect.clear.specific.success.single",
                                                effect.getDisplayName(), entities.iterator().next().getDisplayName()), true);
                            } else {
                                source.sendSuccess(() -> Component.translatable("commands.effect.clear.specific.success.multiple",
                                        effect.getDisplayName(), entities.size()), true);
                            }
                            return count;
                        }
                    })
                )
        );

    }
}
