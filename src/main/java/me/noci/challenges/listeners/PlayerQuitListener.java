package me.noci.challenges.listeners;

import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.headcomponent.HeadComponent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final ChallengeController challengeController;
    private final Config config;

    public PlayerQuitListener(ChallengeController challengeController, Config config) {
        this.challengeController = challengeController;
        this.config = config;
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        TagResolver resolver = TagResolver.builder()
                .resolvers(
                        Placeholder.component("player_head", HeadComponent.create(player.getUniqueId()).build()),
                        Placeholder.component("player_name", player.name())
                )
                .build();

        event.quitMessage(config.get(Option.Settings.PLAYER_QUIT, resolver));

        challengeController.challenge().ifPresent(challenge -> {
            if (Bukkit.getOnlinePlayers().size() != 1) return;
            if (challenge.paused()) return;
            challenge.paused(true);
        });

    }

}
