package me.noci.challenges.listeners;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChallengeMasterListener implements Listener {

    private final ChallengeController challengeController;

    public ChallengeMasterListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityTarget(EntityTargetEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityTargetLiving(EntityTargetLivingEntityEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityDamage(EntityDamageEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityDamageByEntity(EntityDamageByEntityEvent event) {
        //TODO Check if needed due to EntityDamageEvent
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityDamageByBlock(EntityDamageByBlockEvent event) {
        //TODO Check if needed due to EntityDamageEvent
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleEntityMove(EntityMoveEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleItemPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handleItemDrop(EntityDropItemEvent event) {
        if (event.getEntity() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) return;
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handlePlayerInteract(PlayerInteractEvent event) {
        if(event.getPlayer().getGameMode().isInvulnerable()) return;
        event.setCancelled(challengeController.shouldCancelEvents());
    }

    @EventHandler
    public void handlePlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (event.getPlayer().getGameMode().isInvulnerable()) return;

        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return;
        }

        event.setCancelled(challengeController.shouldCancelEvents());
    }

}
