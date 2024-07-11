package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Config;
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
    private final Config config;

    public CommandTimer(JavaPlugin plugin, ChallengeController challengeController, Config config) {
        super(plugin, "timer");
        autoRegister();
        this.challengeController = challengeController;
        this.config = config;
    }

    @SubCommand(path = "start")
    @CommandPermission("quickchallenge.timer.start")
    public void start(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (challenge.started()) {
                                sender.sendMessage(config.get(Option.Command.Timer.ALREADY_STARTED));
                                return;
                            }
                            challengeController.startChallenge();
                            sender.sendMessage(config.get(Option.Command.Timer.SUCCESSFULLY_STARTED));
                        },
                        () -> sender.sendMessage(config.get(Option.Command.Challenge.NOT_CREATED))
                );
    }

    @SubCommand(path = "stop")
    @CommandPermission("quickchallenge.timer.stop")
    public void stop(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started()) {
                                sender.sendMessage(config.get(Option.Command.Timer.NOT_RUNNING));
                                return;
                            }
                            challengeController.stopChallenge();
                            sender.sendMessage(config.get(Option.Command.Timer.SUCCESSFULLY_STOPPED));
                        },
                        () -> sender.sendMessage(config.get(Option.Command.Challenge.NOT_CREATED))
                );
    }

    @SubCommand(path = "pause")
    @CommandPermission("quickchallenge.timer.pause")
    public void pause(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started() || challenge.paused()) {
                                sender.sendMessage(config.get(Option.Command.Timer.NOT_RUNNING));
                                return;
                            }
                            challenge.paused(true);
                            sender.sendMessage(config.get(Option.Command.Timer.SUCCESSFULLY_PAUSED));
                        },
                        () -> sender.sendMessage(config.get(Option.Command.Challenge.NOT_CREATED))
                );
    }

    @SubCommand(path = "resume")
    @CommandPermission("quickchallenge.timer.resume")
    public void resume(CommandSender sender) {
        challengeController.challenge()
                .ifPresentOrElse(
                        challenge -> {
                            if (!challenge.started() || !challenge.paused()) {
                                sender.sendMessage(config.get(Option.Command.Timer.NOT_PAUSED));
                                return;
                            }
                            challenge.paused(false);
                            sender.sendMessage(config.get(Option.Command.Timer.SUCCESSFULLY_RESUMED));
                        },
                        () -> sender.sendMessage(config.get(Option.Command.Challenge.NOT_CREATED))
                );
    }

    @FallbackCommand
    public void fallback(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.get(Option.Command.ONLY_FOR_PLAYERS));
            return;
        }

        if (sender.hasPermission("quickchallenge.timer.start") || sender.hasPermission("quickchallenge.timer.stop") || sender.hasPermission("quickchallenge.timer.resume") || sender.hasPermission("quickchallenge.timer.pause")) {
            sender.sendMessage(config.get(Option.Command.Timer.HELP));
            return;
        }

        sender.sendMessage(config.get(Option.Command.NO_PERMISSION));
    }

}
