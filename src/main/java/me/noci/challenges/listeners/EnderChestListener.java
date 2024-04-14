package me.noci.challenges.listeners;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnderChestListener implements Listener {

    private final ChallengeController challengeController;

    public EnderChestListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    private void handleEnderChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.ENDER_CHEST) return;

        Player player = event.getPlayer();
        if (player.isSneaking()) return;

        event.setCancelled(true);

        challengeController.fromEntity(player)
                .ifPresent(challenge -> {
                    List<ItemStack> content = challenge.enderChest(player);

                    EnderChestHolder chestHolder = new EnderChestHolder(challenge);
                    Inventory enderChest = Bukkit.createInventory(chestHolder, InventoryType.ENDER_CHEST);
                    enderChest.setContents(content.toArray(ItemStack[]::new));
                    chestHolder.inventory(enderChest);

                    player.openInventory(enderChest);
                });
    }

    @EventHandler
    public void handleEnderClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof EnderChestHolder chestHolder)) return;
        chestHolder.challenge().saveEnderChest((Player) event.getPlayer(), Lists.newArrayList(event.getInventory().getContents()));
    }

    private static class EnderChestHolder implements InventoryHolder {

        @Getter private final Challenge challenge;
        @Setter private Inventory inventory;

        private EnderChestHolder(Challenge challenge) {
            this.challenge = challenge;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

}
