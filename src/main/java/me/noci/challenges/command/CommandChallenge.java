package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.settings.Config;
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
    private final Config config;

    public CommandChallenge(JavaPlugin plugin, ChallengeController challengeController, Config config) {
        super(plugin, "challenge");
        autoRegister();
        this.challengeController = challengeController;
        this.config = config;
    }

    @SubCommand(path = "create")
    @CommandPermission("quickchallenge.create")
    public void create(Player player) {
        if (challengeController.isChallengeCreated()) {
            player.sendMessage(config.get(Option.Command.Challenge.ALREADY_CREATED));
            return;
        }
        new GuiChallengeCreate(challengeController).provide(player);
    }

    @SubCommand(path = "delete")
    @CommandPermission("quickchallenge.create")
    public void delete(CommandSender sender) {
        if (!challengeController.isChallengeCreated()) {
            sender.sendMessage(config.get(Option.Command.Challenge.NOT_CREATED));
            return;
        }

        if (challengeController.delete()) {
            sender.sendMessage(config.get(Option.Command.Challenge.SUCCESSFULLY_DELETED));
            return;
        }
        sender.sendMessage(config.get(Option.Command.Challenge.FAILED_DELETION));
    }


    @FallbackCommand
    public void fallback(CommandSender sender) {
    }

}
