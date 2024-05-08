package me.noci.challenges.command;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.quickutilities.quickcommand.QuickCommand;
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

    @SubCommand(path = "skip")
    @CommandPermission("quickchallenge.allitems.skip")
    public void skip(Player player) {
        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            player.sendMessage(Component.text("Es wurde noch keine Challenge erstellt.", NamedTextColor.RED));
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> allItemModifier.skip(challenge, player),
                        () -> player.sendMessage(Component.text("Deine aktuelle Challenge besitzt kein AllItems Modifier.", NamedTextColor.RED))
                );
    }

    @SubCommand(path = "skip")
    @CommandPermission("quickchallenge.allitems.skip")
    public void skip(Player player, int amount) {
        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            player.sendMessage(Component.text("Es wurde noch keine Challenge erstellt.", NamedTextColor.RED));
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        allItemModifier -> IntStream.range(0, amount).forEach(i -> allItemModifier.skip(challenge, player)),
                        () -> player.sendMessage(Component.text("Deine aktuelle Challenge besitzt kein AllItems Modifier.", NamedTextColor.RED))
                );
    }

    @SubCommand(path = "reset")
    @CommandPermission("quickchallenge.allitems.reset")
    public void reset(Player player) {
        Challenge challenge = challengeController.challenge().orElse(null);
        if (challenge == null) {
            player.sendMessage(Component.text("Es wurde noch keine Challenge erstellt.", NamedTextColor.RED));
            return;
        }

        challenge.modifier(AllItemModifier.class)
                .ifPresentOrElse(
                        AllItemModifier::reset,
                        () -> player.sendMessage(Component.text("Deine aktuelle Challenge besitzt kein AllItems Modifier.", NamedTextColor.RED))
                );
    }


    @FallbackCommand
    public void fallback(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only players can execute this command."));
            return;
        }

        if (sender.hasPermission("quickchallenge.allitems.skip") || sender.hasPermission("quickchallenge.allitems.skip")) {
            sender.sendMessage(Component.text("Benutze: /allitems [skip/reset]", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text("Du hast keine Rechte diesen Befehl zu benutzen. ", NamedTextColor.RED));
    }

}
