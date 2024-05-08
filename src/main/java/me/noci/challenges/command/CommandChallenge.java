package me.noci.challenges.command;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.quickutilities.quickcommand.QuickCommand;
import me.noci.quickutilities.quickcommand.annotation.CommandPermission;
import me.noci.quickutilities.quickcommand.annotation.FallbackCommand;
import me.noci.quickutilities.quickcommand.annotation.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        new GuiChallengeCreate(challengeController).provide(player);
    }

    @SubCommand(path = "delete")
    @CommandPermission("quickchallenge.create")
    public void delete(CommandSender sender) {
        if (challengeController.delete()) {
            sender.sendMessage(Component.text("Challenge wurde gelöscht.", NamedTextColor.GREEN));
            return;
        }
        sender.sendMessage(Component.text("Challenge konnte nicht gelöscht werden.", NamedTextColor.RED));
    }


    @FallbackCommand
    public void fallback(CommandSender sender) {
    }

}
