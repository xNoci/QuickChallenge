package me.noci.challenges.command;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.gui.GuiAllItemOverview;
import me.noci.challenges.settings.Option;
import me.noci.quickutilities.quickcommand.QuickCommand;
import me.noci.quickutilities.quickcommand.annotation.Command;
import me.noci.quickutilities.quickcommand.annotation.CommandPermission;
import me.noci.quickutilities.quickcommand.annotation.FallbackCommand;
import me.noci.quickutilities.quickcommand.annotation.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.IntStream;

public class CommandAllItems extends QuickCommand {

    private final ChallengeController challengeController;

    public CommandAllItems(JavaPlugin plugin, ChallengeController challengeController) {
        super(plugin, "allitems");
        autoRegister();
        this.challengeController = challengeController;
    }

    @Command
    public void overview(Player player) {
        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            player.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> new GuiAllItemOverview(allItemModifier).provide(player),
                        () -> player.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );

    }

    @SubCommand(path = "skip")
    @CommandPermission("quickchallenge.allitems.skip")
    public void skip(CommandSender sender) {
        if (!Option.Settings.DEBUG.get()) {
            sender.sendMessage(Option.Command.DEBUG_COMMAND.get());
            return;
        }

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            sender.sendMessage(Component.text("", NamedTextColor.RED));
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> allItemModifier.skip(challenge, sender),
                        () -> sender.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );
    }

    @SubCommand(path = "skip")
    @CommandPermission("quickchallenge.allitems.skip")
    public void skip(CommandSender sender, int amount) {
        if (!Option.Settings.DEBUG.get()) {
            sender.sendMessage(Option.Command.DEBUG_COMMAND.get());
            return;
        }

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> IntStream.range(0, amount).forEach(i -> allItemModifier.skip(challenge, sender)),
                        () -> sender.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );
    }

    @SubCommand(path = "reset")
    @CommandPermission("quickchallenge.allitems.reset")
    public void reset(CommandSender sender) {
        if (!Option.Settings.DEBUG.get()) {
            sender.sendMessage(Option.Command.DEBUG_COMMAND.get());
            return;
        }

        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        AllItemModifier::reset,
                        () -> sender.sendMessage(Option.Command.AllItems.NOT_ENABLED.get())
                );
    }


    @FallbackCommand
    public void fallback(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Option.Command.ONLY_FOR_PLAYERS.get());
            return;
        }

        if (sender.hasPermission("quickchallenge.allitems.skip") || sender.hasPermission("quickchallenge.allitems.skip")) {
            sender.sendMessage(Option.Command.AllItems.HELP.get());
            return;
        }

        sender.sendMessage(Option.Command.NO_PERMISSION.get());
    }

}
