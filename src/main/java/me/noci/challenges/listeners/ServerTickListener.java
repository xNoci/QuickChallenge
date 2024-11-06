package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerTickListener implements Listener {

    private final ChallengeController challengeController;

    public ServerTickListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleServerTick(ServerTickStartEvent event) {
        challengeController.challenge().ifPresentOrElse(challenge -> {
                    if (!challenge.started()) {
                        Component message = Option.Settings.ActionBar.CHALLENGE_NOT_STARTED.get();
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
                        return;
                    }
                    challenge.tickModifiers();
                },
                () -> {
                    Component message = Option.Settings.ActionBar.NO_CHALLENGE_CREATED.get();
                    Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(message));
                }
        );
    }

}
