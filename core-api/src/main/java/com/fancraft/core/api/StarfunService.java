package com.fancraft.core.api;

import org.bukkit.command.CommandSender;

/**
 * Service exposé par chaque plugin Starfun afin que le plugin API centralise
 * les commandes d'administration et l'aide utilisateur.
 */
public interface StarfunService {

    /**
     * Identifiant court du mini-jeu (rush, hikabrain, skywars, freecube).
     */
    String getGameKey();

    /**
     * Nom complet affiché dans les menus d'aide.
     */
    String getDisplayName();

    /**
     * Affiche une aide détaillée à l'expéditeur.
     */
    void sendHelp(CommandSender sender);

    /**
     * Permet de recharger la configuration et les arènes.
     */
    void reloadConfig(CommandSender sender);

    /**
     * Laisse le service gérer une sous-commande spécifique (ex: configuration d'arène).
     *
     * @param sender émetteur de la commande
     * @param args   arguments restants après le nom du mini-jeu
     * @return true si la commande a été gérée
     */
    boolean handleAdminCommand(CommandSender sender, String[] args);
}
