package me.noci.challenges.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.noci.challenges.challenge.ChallengeController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerTickListener implements Listener {
    private static final Component NO_CHALLENGE_CREATED = Component.text("Es wurde noch keine Challenge erstellt.", NamedTextColor.RED);
    private static final Component CHALLENGE_NOT_STARTED = Component.text("Challenge wurde noch nicht gestartet.", NamedTextColor.RED);

    private final ChallengeController challengeController;

    public ServerTickListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleServerTick(ServerTickStartEvent event) {
        challengeController.challenge().ifPresentOrElse(challenge -> {
                    if (!challenge.started()) {
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(CHALLENGE_NOT_STARTED));
                        return;
                    }
                    challenge.tickModifiers();
                },
                () -> Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(NO_CHALLENGE_CREATED))
        );
    }

}
