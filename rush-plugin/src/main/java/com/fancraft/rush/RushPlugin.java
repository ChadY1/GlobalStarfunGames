package com.fancraft.rush;

import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for Rush plugin.
 */
public class RushPlugin extends JavaPlugin {

    private RushGameManager gameManager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.gameManager = new RushGameManager(getLogger(), adapter);
        this.gameManager.setScoreboardService(scoreboardService);
        RushListener listener = new RushListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("rush").setExecutor(new RushCommand(gameManager));
        getLogger().info("Rush plugin chargé avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(RushArena::reset);
    }
}
