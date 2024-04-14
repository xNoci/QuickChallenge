package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.gui.GuiChallengeOverview;
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

    @SubCommand(path = "overview")
    public void overview(Player player) {
        new GuiChallengeOverview(challengeController).provide(player);
    }

    @SubCommand(path = "create")
    @CommandPermission("quickchallenge.create")
    public void create(Player player) {
        new GuiChallengeCreate(challengeController).provide(player);
    }

    @FallbackCommand
    public void fallback(CommandSender sender) {
    }

}
