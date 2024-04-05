package me.noci.challenges;

import io.papermc.lib.PaperLib;
import me.noci.challenges.challenge.ChallengeController;
import me.noci.challenges.listeners.PlayerJoinListener;
import me.noci.challenges.listeners.PlayerQuitListener;
import me.noci.challenges.listeners.ResourcePackStatusListener;
import me.noci.challenges.worlds.WorldController;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickChallenge extends JavaPlugin {

    private WorldController worldController;
    private ChallengeController challengeController;

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        this.worldController = new WorldController();
        this.challengeController = new ChallengeController(worldController);
        this.challengeController.loadChallenges();
        registerListener();
    }

    @Override
    public void onDisable() {
        this.challengeController.save();
        worldController.deleteWorlds();
    }

    private void registerListener() {
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new ResourcePackStatusListener(), this);
        pluginManager.registerEvents(new PlayerPortalListener(worldController), this);
    }

}
