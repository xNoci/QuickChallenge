package me.noci.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.SneakyThrows;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import me.noci.challenges.challenge.modifiers.timer.TimerModifier;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.paper.QuickChallenge;
import me.noci.challenges.settings.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CommandChallenge {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("challenge")
                .then(
                        Commands.literal("create")
                                .requires(source -> source.getSender() instanceof Player)
                                .requires(source -> source.getSender().hasPermission("quickchallenge.create"))
                                .executes(CommandChallenge::create)
                )
                .then(
                        Commands.literal("delete")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.delete"))
                                .executes(CommandChallenge::delete)
                )
                .build();
    }

    @SneakyThrows
    private static int create(CommandContext<CommandSourceStack> context) {
        Player player = context.getSource().getPlayerOrThrow();
        ChallengeController controller = QuickChallenge.instance().challengeController();

        if (controller.isChallengeCreated()) {
            player.sendMessage(Option.Command.Challenge.ALREADY_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        new GuiChallengeCreate().openGui(player);

        return Command.SINGLE_SUCCESS;
    }


    private static int delete(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        ChallengeController controller = QuickChallenge.instance().challengeController();

        if (!controller.isChallengeCreated()) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        if (controller.delete()) {
            sender.sendMessage(Option.Command.Challenge.SUCCESSFULLY_DELETED.get());
            return Command.SINGLE_SUCCESS;
        }

        sender.sendMessage(Option.Command.Challenge.FAILED_DELETION.get());
        return Command.SINGLE_SUCCESS;
    }

}
