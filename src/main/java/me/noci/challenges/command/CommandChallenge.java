package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.quickcommand.QuickCommand;
import me.noci.quickutilities.quickcommand.annotation.CommandPermission;
import me.noci.quickutilities.quickcommand.annotation.FallbackCommand;
import me.noci.quickutilities.quickcommand.annotation.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandChallenge extends QuickCommand {

    private final ChallengeController challengeController;

    public CommandChallenge(JavaPlugin plugin, ChallengeController challengeController) {
        super(plugin, "challenge");
        autoRegister();
        this.challengeController = challengeController;
    }

    @SubCommand(path = "create")
    @CommandPermission("quickchallenge.create")
    public void create(Player player) {
        if (challengeController.isChallengeCreated()) {
            player.sendMessage(Option.Command.Challenge.ALREADY_CREATED.get());
            return;
        }
        new GuiChallengeCreate(challengeController).provide(player);
    }

    @SubCommand(path = "delete")
    @CommandPermission("quickchallenge.create")
    public void delete(CommandSender sender) {
        if (!challengeController.isChallengeCreated()) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return;
        }

        if (challengeController.delete()) {
            sender.sendMessage(Option.Command.Challenge.SUCCESSFULLY_DELETED.get());
            return;
        }
        sender.sendMessage(Option.Command.Challenge.FAILED_DELETION.get());
    }


    @FallbackCommand
    public void fallback(CommandSender sender) {
    }

}
