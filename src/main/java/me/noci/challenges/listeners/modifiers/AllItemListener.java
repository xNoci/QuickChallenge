package me.noci.challenges.listeners.modifiers;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.allitem.AllItemModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class AllItemListener implements Listener {

    private final ChallengeController challengeController;

    public AllItemListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void handleInventoryClick(InventoryClickEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        if (event.isCancelled()) return;
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() == InventoryType.CREATIVE)
            return;
        if (event.getCurrentItem() == null) return;

        AllItemModifier modifier = challenge.modifier(AllItemModifier.class).orElse(null);
        if (modifier == null) return;


        if (!modifier.currentItem().matches(event.getCurrentItem())) return;
        modifier.collectCurrentItem(event.getWhoClicked());
    }

    @EventHandler
    public void handleSlotChange(PlayerInventorySlotChangeEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        AllItemModifier modifier = challenge.modifier(AllItemModifier.class).orElse(null);
        if (modifier == null) return;
        if (!modifier.currentItem().matches(event.getNewItemStack())) return;

        modifier.collectCurrentItem(event.getPlayer());

    }

}
