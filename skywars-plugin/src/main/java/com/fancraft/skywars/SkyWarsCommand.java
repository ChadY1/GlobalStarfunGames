package com.fancraft.skywars;

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

    public SkyWarsCommand(SkyWarsGameManager manager) {
        this.manager = manager;
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
            manager.getArenas().stream().findFirst().ifPresent(manager::start);
            sender.sendMessage(ChatColor.GREEN + "SkyWars lancé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.end(arena, "Arrêt"));
            sender.sendMessage(ChatColor.RED + "SkyWars stoppé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.joinPlayer(arena, player));
            sender.sendMessage(ChatColor.AQUA + "Rejoint SkyWars.");
            return true;
        }
        return false;
    }
}
