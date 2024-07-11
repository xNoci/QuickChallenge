package me.noci.challenges.listeners;

import me.noci.challenges.ResourcePack;
import me.noci.challenges.headcomponent.HeadComponent;
import me.noci.challenges.settings.Config;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;


public class ResourcePackStatusListener implements Listener {

    private final Config config;

    public ResourcePackStatusListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (!ResourcePack.DEFAULT.isSame(event.getID())) return;

        switch (event.getStatus()) {
            case DECLINED:
                event.getPlayer().sendMessage(config.get(Option.ResourcePack.WARNING));
            case SUCCESSFULLY_LOADED: {
                Player player = event.getPlayer();
                TagResolver resolver = TagResolver.builder()
                        .resolvers(
                                Placeholder.component("player_head", HeadComponent.create(player.getUniqueId()).build()),
                                Placeholder.component("player_name", player.name())
                        )
                        .build();

                Bukkit.broadcast(config.get(Option.Settings.PLAYER_JOIN, resolver));
            }
        }

    }

}
