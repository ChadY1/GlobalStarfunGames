package com.fancraft.hikabrain;

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

    public HikaCommand(HikaGameManager manager) {
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
            sender.sendMessage(ChatColor.YELLOW + "/hikabrain start|stop|join");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            manager.getArenas().stream().findFirst().ifPresent(manager::start);
            sender.sendMessage(ChatColor.GREEN + "Duel lancé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.end(arena, "Arrêt"));
            sender.sendMessage(ChatColor.RED + "Duel stoppé.");
            return true;
        }
        if (args[0].equalsIgnoreCase("join")) {
            manager.getArenas().stream().findFirst().ifPresent(arena -> manager.joinPlayer(arena, player));
            sender.sendMessage(ChatColor.AQUA + "Rejoint HikaBrain.");
            return true;
        }
        return false;
    }
}
