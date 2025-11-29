package com.fancraft.hikabrain;

import com.fancraft.core.config.ConfigHelper;
import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for HikaBrain plugin.
 */
public class HikaBrainPlugin extends JavaPlugin {

    private HikaGameManager gameManager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.gameManager = new HikaGameManager(getLogger(), adapter, 5);
        this.gameManager.setScoreboardService(scoreboardService);
        saveDefaultConfig();
        try {
            loadArena();
        } catch (IllegalArgumentException ex) {
            getLogger().severe("Configuration HikaBrain invalide: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        HikaListener listener = new HikaListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("hikabrain").setExecutor(new HikaCommand(gameManager));
        getLogger().info("HikaBrain prêt avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(HikaArena::reset);
    }

    private void loadArena() {
        String arenaId = ConfigHelper.requireSection(this, "arenas").getKeys(false).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune arène HikaBrain définie"));
        HikaArena arena = new HikaArena(arenaId);
        String basePath = "arenas." + arenaId + ".teams";
        arena.getTeams().get(0).setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.spawn")));
        arena.getTeams().get(1).setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.spawn")));
        arena.setLobbySpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, "arenas." + arenaId + ".lobby")));
        gameManager.registerArena(arena);
        getLogger().info(ChatColor.GREEN + "Arène HikaBrain chargée: " + arena.getId());
    }
}
