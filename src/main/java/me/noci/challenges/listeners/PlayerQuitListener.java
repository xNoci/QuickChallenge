package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.colors.Colors;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final ChallengeController challengeController;

    public PlayerQuitListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Component component = Component.text()
                .append(Component.text("« ", Colors.JOIN_INDICATOR_QUIT))
                .append(Component.text(player.getName(), Colors.JOIN_PLAYER_NAME))
                .append(Component.text(" left the server", Colors.CHAT_COLOR))
                .asComponent();

        event.quitMessage(component);

        challengeController.fromEntity(player).ifPresent(challenge -> challenge.setLastKnownLocation(player));
    }

}
