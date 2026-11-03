package me.noci.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.SneakyThrows;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.gui.GuiChallengeCreate;
import me.noci.challenges.settings.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChallenge {

    public static LiteralCommandNode<CommandSourceStack> createCommand(ChallengeController controller) {
        return Commands.literal("challenge")
                .then(
                        Commands.literal("create")
                                .requires(source -> source.getSender() instanceof Player)
                                .requires(source -> source.getSender().hasPermission("quickchallenge.create"))
                                .executes(context -> create(context, controller))
                )
                .then(
                        Commands.literal("delete")
                                .requires(source -> source.getSender().hasPermission("quickchallenge.delete"))
                                .executes(context -> delete(context, controller))
                )
                .then(
                        Commands.literal("save")
                                .executes(context -> {
                                    controller.create();
                                    controller.save();
                                    return Command.SINGLE_SUCCESS;
                                })
                ).then(
                        Commands.literal("load")
                                .executes(context -> {
                                    controller.tryLoadChallenge();
                                    return Command.SINGLE_SUCCESS;
                                })
                )
                .build();
    }

    @SneakyThrows
    private static int create(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        Player player = context.getSource().getPlayerOrThrow();

        if (controller.isChallengeCreated()) {
            player.sendMessage(Option.Command.Challenge.ALREADY_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        GuiChallengeCreate.openGui(player, controller);

        return Command.SINGLE_SUCCESS;
    }


    private static int delete(CommandContext<CommandSourceStack> context, ChallengeController controller) {
        CommandSender sender = context.getSource().getSender();

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
