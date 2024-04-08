package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.quickutilities.quickcommand.QuickCommand;
import me.noci.quickutilities.quickcommand.annotation.CommandPermission;
import me.noci.quickutilities.quickcommand.annotation.FallbackCommand;
import me.noci.quickutilities.quickcommand.annotation.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public void start(Player player) {
        challengeController.fromEntity(player)
                .ifPresentOrElse(
                        challenge -> {
                            challengeController.startChallenge(challenge);
                            player.sendMessage(Component.text("Die Challenge wurde gestartet.", NamedTextColor.GREEN));
                        },
                        () -> player.sendMessage(Component.text("Du bist in keiner Challenge die gestartet werden kann.", NamedTextColor.RED))
                );
    }

    @SubCommand(path = "pause")
    @CommandPermission("quickchallenge.timer.pause")
    public void pause(Player player) {
        challengeController.fromEntity(player)
                .ifPresentOrElse(
                        challenge -> {
                            challenge.paused(true);
                            player.sendMessage(Component.text("Die Challenge wurde pausiert.", NamedTextColor.GREEN));
                        },
                        () -> player.sendMessage(Component.text("Du bist in keiner Challenge die pausiert werden kann.", NamedTextColor.RED))
                );
    }

    @SubCommand(path = "resume")
    @CommandPermission("quickchallenge.timer.resume")
    public void resume(Player player) {
        challengeController.fromEntity(player)
                .ifPresentOrElse(
                        challenge -> {
                            challenge.paused(false);
                            player.sendMessage(Component.text("Die Challenge wurde fortgesetzt.", NamedTextColor.GREEN));
                        },
                        () -> player.sendMessage(Component.text("Du bist in keiner Challenge die fortgesetzt werden kann.", NamedTextColor.RED))
                );
    }

    @FallbackCommand
    public void fallback(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can execute this command."));
            return;
        }

        if (sender.hasPermission("quickchallenge.timer.start") || sender.hasPermission("quickchallenge.timer.resume") || sender.hasPermission("quickchallenge.timer.pause")) {
            sender.sendMessage(Component.text("Benutze: /timer [start/pause/resume]", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text("Du hast keine Rechte diesen Befehl zu benutzen. ", NamedTextColor.RED));
    }

}
