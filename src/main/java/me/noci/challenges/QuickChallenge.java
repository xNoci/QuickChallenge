package me.noci.challenges;

import io.papermc.lib.PaperLib;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickChallenge extends JavaPlugin {

    @Override
    public void onEnable() {
        PaperLib.suggestPaper(this);

        registerListener();
    }

    @Override
    public void onDisable() {

    }

    private void registerListener() {
    }

}
