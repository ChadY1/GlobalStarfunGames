package com.fancraft.freecube;

import com.fancraft.core.proxy.ProxyBridge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command for FreeCube navigation.
 */
public class FreeCubeCommand implements CommandExecutor {

    private final FreeCubeWorldManager worldManager;
    private final ProxyBridge proxyBridge;
    private final boolean bungeeEnabled;
    private final String bungeeServer;

    public FreeCubeCommand(FreeCubeWorldManager worldManager, ProxyBridge proxyBridge, boolean bungeeEnabled, String bungeeServer) {
        this.worldManager = worldManager;
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
        if (!sender.hasPermission("fancraft.freecube.use")) {
            sender.sendMessage(ChatColor.RED + "Permission manquante pour accéder à FreeCube.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("go")) {
            if (bungeeEnabled && bungeeServer != null && !bungeeServer.isEmpty()) {
                proxyBridge.connect(player, bungeeServer);
                player.sendMessage(ChatColor.AQUA + "Connexion au serveur FreeCube " + bungeeServer + "...");
            } else {
                worldManager.sendToCreative(player);
                player.sendMessage(ChatColor.GREEN + "Téléporté vers FreeCube.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("hub")) {
            if (!sender.hasPermission("fancraft.freecube.admin")) {
                sender.sendMessage(ChatColor.RED + "Permission manquante.");
                return true;
            }
            worldManager.sendToHub(player);
            player.sendMessage(ChatColor.YELLOW + "Retour au hub.");
            return true;
        }
        return false;
    }
}
