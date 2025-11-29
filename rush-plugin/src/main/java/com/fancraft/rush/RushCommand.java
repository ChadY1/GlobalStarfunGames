package com.fancraft.rush;

import com.fancraft.core.game.GameState;
import com.fancraft.core.proxy.ProxyBridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Simple command entry point for Rush mini-game.
 */
public class RushCommand implements CommandExecutor {

    private final RushGameManager gameManager;
    private final RushPlugin plugin;
    private final ProxyBridge proxyBridge;
    private final boolean bungeeEnabled;
    private final String bungeeServer;

    public RushCommand(RushPlugin plugin, RushGameManager gameManager, ProxyBridge proxyBridge, boolean bungeeEnabled, String bungeeServer) {
        this.plugin = plugin;
        this.gameManager = gameManager;
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
            if (!sender.hasPermission("fancraft.rush.admin")) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de démarrer une partie.");
                return true;
            }
            gameManager.getArenas().stream().findFirst().ifPresent(gameManager::start);
            sender.sendMessage(ChatColor.GREEN + "Partie Rush démarrée.");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("fancraft.rush.admin")) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'arrêter une partie.");
                return true;
            }
            gameManager.getArenas().stream().findFirst().ifPresent(arena -> gameManager.end(arena, "Arrêt manuel"));
            sender.sendMessage(ChatColor.RED + "Partie Rush stoppée.");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (bungeeEnabled && bungeeServer != null && !bungeeServer.isEmpty()) {
                proxyBridge.connect(player, bungeeServer);
                sender.sendMessage(ChatColor.AQUA + "Connexion au serveur Rush " + bungeeServer + "...");
            } else {
                gameManager.getArenas().stream().findFirst().ifPresent(arena -> gameManager.joinPlayer(arena, player));
                sender.sendMessage(ChatColor.AQUA + "Rejoint la partie Rush.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("state")) {
            gameManager.getArenas().stream().findFirst().ifPresent(arena -> sender.sendMessage("Etat: " + arena.getState()));
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
