package com.fancraft.api;

import com.fancraft.core.api.StarfunService;
import com.fancraft.core.npc.NpcManager;
import com.fancraft.core.proxy.ProxyBridge;
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
    private final ProxyBridge proxyBridge;
    private final boolean bungeeEnabled;
    private final java.util.Map<String, String> routes;

    public StarfunCommand(JavaPlugin plugin, NpcManager npcManager, ProxyBridge proxyBridge, boolean bungeeEnabled, java.util.Map<String, String> routes) {
        this.plugin = plugin;
        this.npcManager = npcManager;
        this.proxyBridge = proxyBridge;
        this.bungeeEnabled = bungeeEnabled;
        this.routes = routes;
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
        if (args[0].equalsIgnoreCase("send")) {
            return handleSend(sender, args);
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
        sender.sendMessage(ChatColor.YELLOW + "/starfun send <jeu> [joueur]" + ChatColor.WHITE + " : envoie via BungeeCord vers le serveur du mini-jeu.");
        getServices().forEach(service -> service.sendHelp(sender));
    }

    private boolean handleSend(CommandSender sender, String[] args) {
        if (!bungeeEnabled) {
            sender.sendMessage(ChatColor.RED + "BungeeCord désactivé dans starfun-api-plugin/config.yml.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /starfun send <jeu> [variant] [joueur]");
            return true;
        }
        String gameKey = args[1].toLowerCase();
        String variant = null;
        Player target = null;
        if (args.length == 3) {
            Player possiblePlayer = Bukkit.getPlayer(args[2]);
            if (possiblePlayer != null) {
                target = possiblePlayer;
            } else {
                variant = args[2].toLowerCase();
            }
        } else if (args.length >= 4) {
            variant = args[2].toLowerCase();
            target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable: " + args[3]);
                return true;
            }
        }
        String routeKey = variant == null ? gameKey : gameKey + "-" + variant;
        String server = routes.getOrDefault(routeKey, routes.getOrDefault(gameKey, gameKey));
        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Commande réservée aux joueurs ou précisez un joueur.");
                return true;
            }
            target = (Player) sender;
        }
        proxyBridge.connect(target, server);
        sender.sendMessage(ChatColor.GREEN + "Transfert de " + target.getName() + " vers le serveur " + server + ".");
        return true;
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
            base.add("send");
            base.addAll(getServices().stream().map(StarfunService::getGameKey).collect(Collectors.toList()));
            return base.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("npc")) {
            return java.util.Arrays.asList("spawn");
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("send")) {
            if (args.length == 2) {
                return routes.keySet().stream()
                        .map(key -> key.contains("-") ? key.substring(0, key.indexOf('-')) : key)
                        .distinct()
                        .collect(Collectors.toList());
            }
            if (args.length == 3) {
                String gameKey = args[1].toLowerCase();
                return routes.keySet().stream()
                        .filter(k -> k.startsWith(gameKey + "-"))
                        .map(k -> k.substring(gameKey.length() + 1))
                        .collect(Collectors.toList());
            }
        }
        return java.util.Collections.emptyList();
    }
}
