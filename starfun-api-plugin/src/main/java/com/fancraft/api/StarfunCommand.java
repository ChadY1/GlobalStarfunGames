package com.fancraft.api;

import com.fancraft.core.api.StarfunService;
import com.fancraft.core.npc.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StarfunCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final NpcManager npcManager;

    public StarfunCommand(JavaPlugin plugin, NpcManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("starfun.admin")) {
            sender.sendMessage(ChatColor.RED + "Permission manquante: starfun.admin");
            return true;
        }
        if (args.length == 0) {
            printHelp(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
            printHelp(sender);
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            getServices().forEach(service -> service.reloadConfig(sender));
            sender.sendMessage(ChatColor.GREEN + "Tous les mini-jeux Starfun ont été rechargés.");
            return true;
        }
        if (args[0].equalsIgnoreCase("npc")) {
            return handleNpc(sender, args);
        }
        Optional<StarfunService> targetService = getServices().stream()
                .filter(service -> service.getGameKey().equalsIgnoreCase(args[0]))
                .findFirst();
        if (!targetService.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Mini-jeu inconnu. Utilisez /starfun help.");
            return true;
        }
        String[] forwarded = new String[Math.max(0, args.length - 1)];
        if (args.length > 1) {
            System.arraycopy(args, 1, forwarded, 0, args.length - 1);
        }
        if (!targetService.get().handleAdminCommand(sender, forwarded)) {
            targetService.get().sendHelp(sender);
        }
        return true;
    }

    private boolean handleNpc(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Commande réservée aux joueurs.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 3 || !args[1].equalsIgnoreCase("spawn")) {
            sender.sendMessage(ChatColor.YELLOW + "/starfun npc spawn <arenaId> [sword|bow]");
            return true;
        }
        String arenaId = args[2];
        ItemStack weapon = new ItemStack(args.length > 3 && args[3].equalsIgnoreCase("bow") ? Material.BOW : Material.IRON_SWORD);
        npcManager.spawnFighter(player.getLocation(), arenaId, weapon, ChatColor.AQUA);
        sender.sendMessage(ChatColor.GREEN + "PNJ combattant créé pour l'arène " + arenaId + ".");
        return true;
    }

    private void printHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- Aide Starfun ---");
        sender.sendMessage(ChatColor.YELLOW + "/starfun help" + ChatColor.WHITE + " : affiche ce menu.");
        sender.sendMessage(ChatColor.YELLOW + "/starfun reload" + ChatColor.WHITE + " : recharge toutes les configs.");
        sender.sendMessage(ChatColor.YELLOW + "/starfun npc spawn <arenaId> [sword|bow]" + ChatColor.WHITE + " : crée un bot PVP de test.");
        getServices().forEach(service -> service.sendHelp(sender));
    }

    private List<StarfunService> getServices() {
        return Bukkit.getServicesManager().getRegistrations(StarfunService.class)
                .stream()
                .map(org.bukkit.plugin.RegisteredServiceProvider::getProvider)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> base = new ArrayList<>();
            base.add("help");
            base.add("reload");
            base.add("npc");
            base.addAll(getServices().stream().map(StarfunService::getGameKey).collect(Collectors.toList()));
            return base.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("npc")) {
            return java.util.Arrays.asList("spawn");
        }
        return java.util.Collections.emptyList();
    }
}
