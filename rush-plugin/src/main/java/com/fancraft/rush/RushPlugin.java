package com.fancraft.rush;

import com.fancraft.core.api.StarfunService;
import com.fancraft.core.config.ConfigHelper;
import com.fancraft.core.proxy.ProxyBridge;
import com.fancraft.core.scoreboard.ScoreboardService;
import com.fancraft.core.version.VersionAdapter;
import com.fancraft.core.version.VersionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main entry point for Rush plugin.
 */
public class RushPlugin extends JavaPlugin implements StarfunService {

    private RushGameManager gameManager;
    private ProxyBridge proxyBridge;
    private boolean bungeeEnabled;
    private String bungeeServer;

    @Override
    public void onEnable() {
        VersionAdapter adapter = VersionUtils.getAdapter();
        ScoreboardService scoreboardService = new ScoreboardService();
        this.gameManager = new RushGameManager(getLogger(), adapter);
        this.gameManager.setScoreboardService(scoreboardService);
        saveDefaultConfig();
        this.proxyBridge = new ProxyBridge(this);
        this.bungeeEnabled = ConfigHelper.getBooleanOrDefault(this, "bungeecord.enabled", false);
        this.bungeeServer = ConfigHelper.getStringOrDefault(this, "bungeecord.server", "");
        loadArenas();
        RushListener listener = new RushListener(gameManager, adapter);
        Bukkit.getPluginManager().registerEvents(listener, this);
        getServer().getServicesManager().register(com.fancraft.core.api.StarfunService.class, this, this, org.bukkit.plugin.ServicePriority.Normal);
        getCommand("rush").setExecutor(new RushCommand(this, gameManager, proxyBridge, bungeeEnabled, bungeeServer));
        getLogger().info("Rush plugin chargé avec l'adaptateur " + adapter.getVersionTag());
    }

    @Override
    public void onDisable() {
        gameManager.getArenas().forEach(RushArena::reset);
    }

    private void loadArenas() {
        gameManager.clearArenas();
        if (getConfig().getConfigurationSection("arenas") == null) {
            getLogger().warning("Aucune arène Rush définie dans la configuration.");
            return;
        }
        for (String arenaId : getConfig().getConfigurationSection("arenas").getKeys(false)) {
            try {
                RushArena arena = new RushArena(arenaId);
                String basePath = "arenas." + arenaId + ".teams";
                RushTeam red = arena.getTeams().get(0);
                RushTeam blue = arena.getTeams().get(1);
                red.setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.spawn")));
                red.setBedLocation(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".red.bed")));
                blue.setSpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.spawn")));
                blue.setBedLocation(ConfigHelper.readLocation(ConfigHelper.requireSection(this, basePath + ".blue.bed")));
                arena.setLobbySpawn(ConfigHelper.readLocation(ConfigHelper.requireSection(this, "arenas." + arenaId + ".lobby")));
                gameManager.registerArena(arena);
                getLogger().info(ChatColor.GREEN + "Arène Rush chargée: " + arena.getId());
            } catch (Exception ex) {
                getLogger().severe("Impossible de charger l'arène Rush " + arenaId + " : " + ex.getMessage());
            }
        }
    }

    @Override
    public String getGameKey() {
        return "rush";
    }

    @Override
    public String getDisplayName() {
        return "StarfunRush";
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[Rush] /rush join|start|stop|state");
        sender.sendMessage(ChatColor.GRAY + "Config en jeu: /rush set <arene> lobby|redspawn|redbed|bluespawn|bluebed");
    }

    @Override
    public void reloadConfig(CommandSender sender) {
        reloadConfig();
        this.bungeeEnabled = ConfigHelper.getBooleanOrDefault(this, "bungeecord.enabled", false);
        this.bungeeServer = ConfigHelper.getStringOrDefault(this, "bungeecord.server", "");
        loadArenas();
        sender.sendMessage(ChatColor.GREEN + "Configuration Rush rechargée.");
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
                sender.sendMessage(ChatColor.YELLOW + "Usage: /rush set <arene> lobby|redspawn|redbed|bluespawn|bluebed");
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
                case "redbed":
                    ConfigHelper.writeLocation(getConfig(), base + ".teams.red.bed", player.getLocation());
                    break;
                case "bluespawn":
                    ConfigHelper.writeLocation(getConfig(), base + ".teams.blue.spawn", player.getLocation());
                    break;
                case "bluebed":
                    ConfigHelper.writeLocation(getConfig(), base + ".teams.blue.bed", player.getLocation());
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
