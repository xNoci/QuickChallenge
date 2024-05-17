package me.noci.challenges.listeners;

import me.noci.challenges.ResourcePack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.joinMessage(null);

        ResourcePack.DEFAULT.apply(player);
        ResourcePack.HIGH_RES_OVERLAY.apply(player);
    }


}
