package com.fancraft.hikabrain;

import com.fancraft.core.api.StarfunService;
import com.fancraft.core.config.ConfigHelper;
import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for HikaBrain plugin.
 */
public class HikaBrainPlugin extends JavaPlugin implements StarfunService {

    private HikaGameManager gameManager;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.gameManager = new HikaGameManager(getLogger(), adapter, 5);
        this.gameManager.setScoreboardService(scoreboardService);
        saveDefaultConfig();
        loadArenas();
        HikaListener listener = new HikaListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getServer().getServicesManager().register(com.fancraft.core.api.StarfunService.class, this, this, org.bukkit.plugin.ServicePriority.Normal);
        getCommand("hikabrain").setExecutor(new HikaCommand(this, gameManager));
        getLogger().info("HikaBrain prêt avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(HikaArena::reset);
    }

    private void loadArenas() {
        gameManager.clearArenas();
        if (getConfig().getConfigurationSection("arenas") == null) {
            getLogger().warning("Aucune arène HikaBrain définie.");
            return;
        }
        for (String arenaId : getConfig().getConfigurationSection("arenas").getKeys(false)) {
            try {
                HikaArena arena = new HikaArena(arenaId);
                String basePath = "arenas." + arenaId + ".teams";
                arena.getTeams().get(0).setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.spawn")));
                arena.getTeams().get(1).setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.spawn")));
                arena.setLobbySpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, "arenas." + arenaId + ".lobby")));
                gameManager.registerArena(arena);
                getLogger().info(ChatColor.GREEN + "Arène HikaBrain chargée: " + arena.getId());
            } catch (Exception ex) {
                getLogger().severe("Arène HikaBrain invalide: " + ex.getMessage());
            }
        }
    }

    @Override
    public String getGameKey() {
        return "hikabrain";
    }

    @Override
    public String getDisplayName() {
        return "StarfunHikaBrain";
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[HikaBrain] /hikabrain join|start|stop|score");
        sender.sendMessage(ChatColor.GRAY + "Config: /hikabrain set <arene> lobby|redspawn|bluespawn");
    }

    @Override
    public void reloadConfig(CommandSender sender) {
        reloadConfig();
        loadArenas();
        sender.sendMessage(ChatColor.GREEN + "Configuration HikaBrain rechargée.");
    }

    @Override
    public boolean handleAdminCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfig(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Commande réservée aux joueurs.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /hikabrain set <arene> lobby|redspawn|bluespawn");
                return true;
            }
            Player player = (Player) sender;
            String arenaId = args[1];
            String target = args[2].toLowerCase();
            String base = "arenas." + arenaId;
            switch (target) {
                case "lobby":
                    ConfigHelper.writeLocation(getConfig(), base + ".lobby", player.getLocation());
                    break;
                case "redspawn":
                    ConfigHelper.writeLocation(getConfig(), base + ".teams.red.spawn", player.getLocation());
                    break;
                case "bluespawn":
                    ConfigHelper.writeLocation(getConfig(), base + ".teams.blue.spawn", player.getLocation());
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Point inconnu.");
                    return true;
            }
            saveConfig();
            reloadConfig(sender);
            sender.sendMessage(ChatColor.GREEN + "Point " + target + " enregistré pour l'arène " + arenaId + ".");
            return true;
        }
        return false;
    }
}
