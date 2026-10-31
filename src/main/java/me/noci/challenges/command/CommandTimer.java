package me.noci.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Option;
import org.bukkit.command.CommandSender;

public class CommandTimer {

    public static LiteralCommandNode<CommandSourceStack> createCommand(ChallengeController controller) {
        return Commands.literal("timer")
                .then(
                        Commands.literal("start")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.timer.start"))
                                .executes(context -> start(context, controller))
                )
                .then(
                        Commands.literal("stop")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.timer.stop"))
                                .executes(context -> stop(context, controller))
                )
                .then(
                        Commands.literal("pause")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.timer.pause"))
                                .executes(context -> stop(context, controller))
                )
                .then(
                        Commands.literal("resume")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.timer.resume"))
                                .executes(context -> resume(context, controller))
                )
                .build();
    }


    private static int start(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        CommandSender sender = context.getSource().getSender();
        controller.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (challenge.started()) {
                                sender.sendMessage(Option.Command.Timer.ALREADY_STARTED.get());
                                return;
                            }
                            controller.startChallenge();
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_STARTED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );
        return Command.SINGLE_SUCCESS;
    }

    private static int stop(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        CommandSender sender = context.getSource().getSender();
        controller.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started()) {
                                sender.sendMessage(Option.Command.Timer.NOT_RUNNING.get());
                                return;
                            }
                            controller.stopChallenge();
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_STOPPED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );
        return Command.SINGLE_SUCCESS;
    }

    private static int pause(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        CommandSender sender = context.getSource().getSender();
        controller.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started() || challenge.paused()) {
                                sender.sendMessage(Option.Command.Timer.NOT_RUNNING.get());
                                return;
                            }
                            challenge.paused(true);
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_PAUSED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );

        return Command.SINGLE_SUCCESS;
    }

    private static int resume(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        CommandSender sender = context.getSource().getSender();
        controller.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started() || !challenge.paused()) {
                                sender.sendMessage(Option.Command.Timer.NOT_PAUSED.get());
                                return;
                            }
                            challenge.paused(false);
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_RESUMED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );

        return Command.SINGLE_SUCCESS;
    }

}
