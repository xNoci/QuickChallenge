package me.noci.challenges.listeners.modifiers;

import me.noci.challenges.challenge.Challenge;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.challenge.modifiers.randomizer.LootTableRandomizerModifier;
import me.noci.challenges.team.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;

import java.util.Collection;
import java.util.Random;

public class LootRandomizeListener implements Listener {

    private static final Random RANDOM = new Random();
    private final ChallengeController challengeController;

    public LootRandomizeListener(ChallengeController challengeController) {
        this.challengeController = challengeController;
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        Challenge challenge = challengeController.getCurrentChallenge();
        if (challenge == null || challenge.paused()) return;

        LootTableRandomizerModifier modifier = challenge.modifier(LootTableRandomizerModifier.class).orElse(null);
        if (modifier == null) return;

        if (event.getEntity() == null) return;
        event.getLoot().clear();

        Team team = new Team(event.getEntity().getUniqueId());
        LootTable lootTable = modifier.randomize(team, event.getLootTable().getKey());
        
        Collection<ItemStack> items = lootTable.populateLoot(RANDOM, event.getLootContext());
        event.setLoot(items);
    }

}
