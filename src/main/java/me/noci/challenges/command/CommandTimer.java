package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.quickcommand.QuickCommand;
import me.noci.quickutilities.quickcommand.annotation.CommandPermission;
import me.noci.quickutilities.quickcommand.annotation.FallbackCommand;
import me.noci.quickutilities.quickcommand.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandTimer extends QuickCommand {

    private final ChallengeController challengeController;

    public CommandTimer(JavaPlugin plugin, ChallengeController challengeController) {
        super(plugin, "timer");
        autoRegister();
        this.challengeController = challengeController;
    }

    @SubCommand(path = "start")
    @CommandPermission("quickchallenge.timer.start")
    public void start(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (challenge.started()) {
                                sender.sendMessage(Option.Command.Timer.ALREADY_STARTED.get());
                                return;
                            }
                            challengeController.startChallenge();
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_STARTED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );
    }

    @SubCommand(path = "stop")
    @CommandPermission("quickchallenge.timer.stop")
    public void stop(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started()) {
                                sender.sendMessage(Option.Command.Timer.NOT_RUNNING.get());
                                return;
                            }
                            challengeController.stopChallenge();
                            sender.sendMessage(Option.Command.Timer.SUCCESSFULLY_STOPPED.get());
                        },
                        () -> sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get())
                );
    }

    @SubCommand(path = "pause")
    @CommandPermission("quickchallenge.timer.pause")
    public void pause(CommandSender sender) {
        challengeController.challenge()
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
    }

    @SubCommand(path = "resume")
    @CommandPermission("quickchallenge.timer.resume")
    public void resume(CommandSender sender) {
        challengeController.challenge()
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
    }

    @FallbackCommand
    public void fallback(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Option.Command.ONLY_FOR_PLAYERS.get());
            return;
        }

        if (sender.hasPermission("quickchallenge.timer.start") || sender.hasPermission("quickchallenge.timer.stop") || sender.hasPermission("quickchallenge.timer.resume") || sender.hasPermission("quickchallenge.timer.pause")) {
            sender.sendMessage(Option.Command.Timer.HELP.get());
            return;
        }

        sender.sendMessage(Option.Command.NO_PERMISSION.get());
    }

}
