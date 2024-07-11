package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerTickListener implements Listener {

    private final ChallengeController challengeController;
    private final Config config;

    public ServerTickListener(ChallengeController challengeController, Config config) {
        this.challengeController = challengeController;
        this.config = config;
    }

    @EventHandler
    public void handleServerTick(ServerTickStartEvent event) {
        challengeController.challenge().ifPresentOrElse(challenge -> {
                    if (!challenge.started()) {
                        Component message = config.get(Option.Settings.ActionBar.CHALLENGE_NOT_STARTED);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
                        return;
                    }
                    challenge.tickModifiers();
                },
                () -> {
                    Component message = config.get(Option.Settings.ActionBar.NO_CHALLENGE_CREATED);
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
                }
        );
    }

}
