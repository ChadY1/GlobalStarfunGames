package com.fancraft.freecube;

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

    public FreeCubeCommand(FreeCubeWorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("go")) {
            worldManager.sendToCreative(player);
            player.sendMessage(ChatColor.GREEN + "Téléporté vers FreeCube.");
            return true;
        }
        if (args[0].equalsIgnoreCase("hub")) {
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage(ChatColor.YELLOW + "Retour au hub.");
            return true;
        }
        return false;
    }
}
