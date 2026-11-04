package me.noci.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.trafficlight.LightStatus;
import me.noci.challenges.challenge.modifiers.trafficlight.TrafficLightModifier;
import me.noci.challenges.command.arguments.LightStatusArgument;
import me.noci.challenges.paper.QuickChallenge;
import me.noci.challenges.settings.Option;
import me.noci.challenges.utils.TimerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public class CommandTrafficLight {

    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("trafficlight")
                .then(
                        Commands.literal("phase")
                                .requires(
                                        source -> source.getSender().hasPermission("quickchallenge.trafficlight.phase"))
                                .then(
                                        Commands.argument("phase", new LightStatusArgument())
                                                .executes(context -> {
                                                    LightStatus light = context.getArgument("phase", LightStatus.class);
                                                    return phase(context, light);
                                                })
                                )
                                .then(
                                        Commands.literal("next").executes(CommandTrafficLight::next)
                                )
                                .executes(CommandTrafficLight::next)
                )
                .requires(source -> source.getSender().hasPermission("quickchallenge.trafficlight"))
                .then(Commands.literal("info").executes(CommandTrafficLight::info))
                .executes(CommandTrafficLight::info)
                .build();
    }


    private static int phase(CommandContext<CommandSourceStack> context, LightStatus status) {
        return needModifier(context, modifier -> {
            modifier.setPhase(status, Bukkit.getOnlinePlayers());
            Component message = Option.Command.TrafficLight.NEW_PHASE.resolve(
                    Placeholder.unparsed("phase", status.name())
            );

            context.getSource().getSender().sendMessage(message);
        });
    }

    private static int next(CommandContext<CommandSourceStack> context) {
        return needModifier(context, modifier -> {
            modifier.nextPhase(Bukkit.getOnlinePlayers());
            Component message = Option.Command.TrafficLight.NEW_PHASE.resolve(
                    Placeholder.unparsed("phase", modifier.lightStatus().name())
            );

            context.getSource().getSender().sendMessage(message);
        });
    }

    private static int info(CommandContext<CommandSourceStack> context) {
        return needModifier(context, modifier -> {
            LightStatus currentPhase = modifier.lightStatus();
            String timeUntilNext = TimerUtils.convertTicksToText(modifier.nextActionTimer());
            Component message = Option.Command.TrafficLight.INFO.resolve(
                    Placeholder.unparsed("phase", currentPhase.name()),
                    Placeholder.unparsed("time", timeUntilNext)
            );

            context.getSource().getSender().sendMessage(message);
        });
    }

    private static int needModifier(CommandContext<CommandSourceStack> context,
                                    Consumer<TrafficLightModifier> modifier) {
        CommandSender sender = context.getSource().getSender();
        ChallengeController controller = QuickChallenge.instance().challengeController();

        Challenge challenge = controller.getCurrentChallenge();
        if (challenge == null) {
            sender.sendMessage(Option.Command.Challenge.NOT_CREATED.get());
            return Command.SINGLE_SUCCESS;
        }

        challenge.modifier(TrafficLightModifier.class)
                .ifPresentOrElse(
                        modifier,
                        () -> sender.sendMessage(Option.Command.TrafficLight.NOT_ENABLED.get())
                );
        return Command.SINGLE_SUCCESS;

    }
}
