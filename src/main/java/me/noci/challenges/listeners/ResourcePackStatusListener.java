package me.noci.challenges.listeners;

import me.noci.challenges.ResourcePack;
import me.noci.challenges.settings.Option;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;


public class ResourcePackStatusListener implements Listener {

    @EventHandler
    public void handleResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (!ResourcePack.DEFAULT.isSame(event.getID())) return;

        switch (event.getStatus()) {
            case DECLINED:
                event.getPlayer().sendMessage(Option.ResourcePack.WARNING.get());
            case SUCCESSFULLY_LOADED: {
                Player player = event.getPlayer();

                Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<head:" + player.getUniqueId() + ":true>"));
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<sprite:blocks:block/stone>"));
                Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<sprite:minecraft:items:item/porkchop>"));

                Bukkit.broadcast(Option.Settings.PLAYER_JOIN.resolve(
                        Placeholder.parsed("player_head", "<head:" + player.getUniqueId() + ":true>"),
                        Placeholder.component("player_name", player.name())
                ));
            }
        }

    }

}
