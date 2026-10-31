package me.noci.challenges.paper;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.command.CommandAllItems;
import me.noci.challenges.command.CommandChallenge;
import me.noci.challenges.command.CommandTimer;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class QuickChallengeBootstrap implements PluginBootstrap {


    private ChallengeController controller;

    @Override
    public void bootstrap(BootstrapContext context) {
        Path challengeFile = context.getDataDirectory().resolve("challenge.qcc");
        this.controller = new ChallengeController(challengeFile);

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar()
                    .register(CommandAllItems.createCommand(controller), "Command to control all items challenge");
            event.registrar().register(CommandChallenge.createCommand(controller), "Command to control the challenge");
            event.registrar().register(CommandTimer.createCommand(controller), "Command to control the timer");
        });
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new QuickChallenge(controller);
    }
}
