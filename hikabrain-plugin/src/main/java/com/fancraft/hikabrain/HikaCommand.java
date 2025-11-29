package com.fancraft.hikabrain;

import com.fancraft.core.proxy.ProxyBridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /hikabrain.
 */
public class HikaCommand implements CommandExecutor {

    private final HikaGameManager manager;
    private final HikaBrainPlugin plugin;
    private final ProxyBridge proxyBridge;
    private final boolean bungeeEnabled;
    private final String bungeeServer;

    public HikaCommand(HikaBrainPlugin plugin, HikaGameManager manager, ProxyBridge proxyBridge, boolean bungeeEnabled, String bungeeServer) {
        this.plugin = plugin;
        this.manager = manager;
        this.proxyBridge = proxyBridge;
        this.bungeeEnabled = bungeeEnabled;
        this.bungeeServer = bungeeServer;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            plugin.sendHelp(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!sender.hasPermission("fancraft.hikabrain.admin")) {
                sender.sendMessage(ChatColor.RED + "Permission manquante pour démarrer un duel.");
                return true;
            }
            manager.getArenas().stream().findFirst().ifPresent(manager::start);
            sender.sendMessage(ChatColor.GREEN + "Duel lancé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("fancraft.hikabrain.admin")) {
                sender.sendMessage(ChatColor.RED + "Permission manquante pour arrêter un duel.");
                return true;
            }
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.end(arena, "Arrêt"));
            sender.sendMessage(ChatColor.RED + "Duel stoppé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (bungeeEnabled && bungeeServer != null && !bungeeServer.isEmpty()) {
                proxyBridge.connect(player, bungeeServer);
                sender.sendMessage(ChatColor.AQUA + "Connexion au serveur HikaBrain " + bungeeServer + "...");
            } else {
                manager.getArenas().stream().findFirst().ifPresent(arena -> manager.joinPlayer(arena, player));
                sender.sendMessage(ChatColor.AQUA + "Rejoint HikaBrain.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("set")) {
            return plugin.handleAdminCommand(sender, args);
        }
        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig(sender);
            return true;
        }
        return false;
    }
}
