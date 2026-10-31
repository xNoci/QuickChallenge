package me.noci.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.SneakyThrows;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.gui.GuiAllItemOverview;
import me.noci.challenges.settings.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.IntStream;

public class CommandAllItems {

    public static LiteralCommandNode<CommandSourceStack> createCommand(ChallengeController controller) {
        return Commands.literal("allitems")
                .then(
                        Commands.literal("skip")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.allitems.skip"))
                                .then(
                                        Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(context -> {
                                                    int amount = IntegerArgumentType.getInteger(context, "amount");
                                                    return skip(context, controller, amount);
                                                })
                                )
                                .executes(context -> skip(context, controller, 1))
                )
                .then(
                        Commands.literal("reset")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.allitems.reset"))
                                .executes(context -> reset(context, controller))
                )
                .requires(source -> source.getExecutor() instanceof Player) // Should send custom message here
                .executes(context -> overview(context, controller))
                .build();
    }


    @SneakyThrows
    private static int overview(CommandContext<CommandSourceStack> context, ChallengeController challengeController) {
        Player player = context.getSource().getPlayerOrThrow();

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            player.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> new GuiAllItemOverview(allItemModifier).provide(player),
                        () -> player.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );

        return Command.SINGLE_SUCCESS;
    }

    private static int skip(CommandContext<CommandSourceStack> context, ChallengeController challengeController,
                            int amount) {
        CommandSender sender = context.getSource().getSender();

        if (!Option.Settings.DEBUG.get()) {
            sender.sendMessage(Option.Command.DEBUG_COMMAND.get());
            return Command.SINGLE_SUCCESS;
        }

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> IntStream.range(0, amount)
                                .forEach(i -> allItemModifier.skip(challenge, sender)),
                        () -> sender.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );

        return Command.SINGLE_SUCCESS;
    }

    private static int reset(CommandContext<CommandSourceStack> context, ChallengeController challengeController) {
        CommandSender sender = context.getSource().getSender();

        if (!Option.Settings.DEBUG.get()) {
            sender.sendMessage(Option.Command.DEBUG_COMMAND.get());
            return Command.SINGLE_SUCCESS;
        }

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        AllItemModifier::reset,
                        () -> sender.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );

        return Command.SINGLE_SUCCESS;
    }

}
