package com.fancraft.rush;

import com.fancraft.core.game.GameState;
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

    public RushCommand(RushGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/rush start|stop|join");
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
            gameManager.getArenas().stream().findFirst().ifPresent(arena -> gameManager.joinPlayer(arena, player));
            sender.sendMessage(ChatColor.AQUA + "Rejoint la partie Rush.");
            return true;
        }
        if (args[0].equalsIgnoreCase("state")) {
            gameManager.getArenas().stream().findFirst().ifPresent(arena -> sender.sendMessage("Etat: " + arena.getState()));
            return true;
        }
        return false;
    }
}
