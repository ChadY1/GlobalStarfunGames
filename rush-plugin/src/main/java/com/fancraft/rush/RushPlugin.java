package com.fancraft.rush;

import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import com.fancraft.core.config.ConfigHelper;
import org.bukkit.ChatColor;
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
        saveDefaultConfig();
        try {
            loadDefaultArena();
        } catch (IllegalArgumentException ex) {
            getLogger().severe("Configuration Rush invalide: " + ex.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        RushListener listener = new RushListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getCommand("rush").setExecutor(new RushCommand(gameManager));
        getLogger().info("Rush plugin chargé avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(RushArena::reset);
    }

    private void loadDefaultArena() {
        String arenaId = ConfigHelper.requireSection(this, "arenas").getKeys(false).stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Aucune arène Rush définie"));
        RushArena arena = new RushArena(arenaId);
        String basePath = "arenas." + arenaId + ".teams";
        RushTeam red = arena.getTeams().get(0);
        RushTeam blue = arena.getTeams().get(1);
        red.setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.spawn")));
        red.setBedLocation(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.bed")));
        blue.setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.spawn")));
        blue.setBedLocation(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.bed")));
        arena.setLobbySpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, "arenas." + arenaId + ".lobby")));
        red.resetBed();
        blue.resetBed();
        if (arena.getTeams().stream().anyMatch(team -> team.getSpawn() == null || team.getBedLocation() == null)) {
            throw new IllegalArgumentException("Spawns ou lits non configurés pour l'arène Rush");
        }
        gameManager.registerArena(arena);
        getLogger().info(ChatColor.GREEN + "Arène Rush chargée: " + arena.getId());
    }
}
