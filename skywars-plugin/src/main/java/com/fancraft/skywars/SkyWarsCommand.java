package com.fancraft.skywars;

import com.fancraft.core.proxy.ProxyBridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command entry for /skywars.
 */
public class SkyWarsCommand implements CommandExecutor {

    private final SkyWarsGameManager manager;
    private final ProxyBridge proxyBridge;
    private final boolean bungeeEnabled;
    private final String bungeeServer;

    public SkyWarsCommand(SkyWarsGameManager manager, ProxyBridge proxyBridge, boolean bungeeEnabled, String bungeeServer) {
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
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/skywars start|stop|join");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!sender.hasPermission("fancraft.skywars.admin")) {
                sender.sendMessage(ChatColor.RED + "Permission requise pour démarrer.");
                return true;
            }
            manager.getArenas().stream().findFirst().ifPresent(manager::start);
            sender.sendMessage(ChatColor.GREEN + "SkyWars lancé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("fancraft.skywars.admin")) {
                sender.sendMessage(ChatColor.RED + "Permission requise pour arrêter.");
                return true;
            }
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.end(arena, "Arrêt"));
            sender.sendMessage(ChatColor.RED + "SkyWars stoppé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (bungeeEnabled && bungeeServer != null && !bungeeServer.isEmpty()) {
                proxyBridge.connect(player, bungeeServer);
                sender.sendMessage(ChatColor.AQUA + "Connexion au serveur SkyWars " + bungeeServer + "...");
            } else {
                manager.getArenas().stream().findFirst().ifPresent(arena -> manager.joinPlayer(arena, player));
                sender.sendMessage(ChatColor.AQUA + "Rejoint SkyWars.");
            }
            return true;
        }
        return false;
    }
}
