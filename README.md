# GlobalStarfunGames

Ce dépôt contient un canevas de prompt destiné à Codex pour générer en **une seule itération** un projet Maven complet rassemblant quatre plugins Spigot 1.9.4 (Rush, HikaBrain, SkyWars, FreeCube) plus un module `core-api`. Le prompt met l'accent sur la compatibilité multi‑versions via l'API Spigot, BKCommonLib et les plugins Via* (ViaVersion, ViaBackwards, ViaRewind).

## Principes généraux à inclure dans le prompt
- **Cible Spigot 1.9.4** : l'API utilisée doit être celle de Spigot 1.9.4. Les clients d'autres versions se connecteront grâce aux plugins de compatibilité.
- **Dépendances réseau** : rappeler dans la documentation d'installation la nécessité d'installer ViaVersion, ViaBackwards et ViaRewind. Mentionner que ces plugins traduisent le protocole réseau mais ne fournissent aucune logique de jeu.
- **Abstraction NMS** : imposer l'utilisation d'une bibliothèque comme **BKCommonLib** pour éviter tout accès direct à `net.minecraft.server` ou `org.bukkit.craftbukkit`. L'API Spigot et BKCommonLib suffisent pour les particules, paquets, sons, etc.
- **Gestion des variations de version** : prévoir dans `core-api` des adaptateurs (par exemple `VersionUtils` et `MaterialAdapter`) capables de retourner les matériaux/sons compatibles selon la version détectée (`Bukkit.getServer().getVersion()`). Pattern Strategy recommandé.
- **Qualité du code** : noms explicites, séparation logique métier/présentation, enums pour les états, gestion des erreurs avec messages clairs pour les administrateurs. Éventuellement ajouter quelques tests unitaires simples pour les utilitaires.

## Structure Maven attendue
- Projet parent multi-modules Maven (Java 8) avec les modules :
  - `core-api` (utilitaires communs : équipes, scoreboard unifié, base de mini-jeu, adaptateurs de version).
  - `rush-plugin`
  - `hikabrain-plugin`
  - `skywars-plugin`
  - `freecube-plugin`
- Chaque module plugin doit produire un `.jar` autonome avec son `plugin.yml` (api-version: `1.9.4`, dépendance BKCommonLib, main unique, permissions éventuelles).
- Dépendances communes définies dans le parent : Spigot 1.9.4, BKCommonLib (compatible 1.8+), JUnit (facultatif pour tests).

## Détails à inclure pour chaque mini‑jeu
### Rush
- Concept : deux équipes avec un lit chacune, objectif détruire le lit adverse et éliminer les adversaires.
- Gestion : génération/réinitialisation de l'arène, deux équipes, lits, générateurs de blocs, coffres initiaux, points de réapparition, fin de partie.
- Évènements : `PlayerJoinEvent`, `BlockBreakEvent` (destruction de lit), `PlayerRespawnEvent`, `PlayerDeathEvent`, `EntityDamageByEntityEvent`, `GameStateChangeEvent`.
- Classes suggérées : `RushPlugin`, `RushArena`, `RushTeam`, `RushGameManager`, `RushListener`, `RushCommand`.

### HikaBrain (MLGRush)
- Concept : duel, chaque joueur protège son lit et marque un point en atteignant la plateforme adverse.
- Gestion : construction auto des plateformes, détection des points, réinitialisation après chaque point, score.
- Évènements : `PlayerMoveEvent` (ligne de but), `BlockBreakEvent`, `PlayerDeathEvent`, `PlayerRespawnEvent`.
- Classes suggérées : `HikaBrainPlugin`, `HikaArena`, `HikaTeam`, `HikaGameManager`, `HikaListener`, `HikaCommand`.

### SkyWars
- Concept : joueurs sur îles séparées, dernier survivant gagne.
- Gestion : génération des îles, coffres aléatoires équilibrés, centre plus puissant, limites de temps, détection du dernier survivant, spectateur éventuel.
- Évènements : `PlayerJoinEvent`, `PlayerInteractEvent` (ouverture coffres), `PlayerDeathEvent`, `PlayerRespawnEvent`.
- Classes suggérées : `SkyWarsPlugin`, `SkyWarsArena`, `SkyWarsChestManager`, `SkyWarsGameManager`, `SkyWarsListener`, `SkyWarsCommand`.

### FreeCube
- Concept : mode créatif libre dans un monde plat ou généré.
- Gestion : téléportation vers le monde, protection anti-grief optionnelle, système de claims/parcelles si souhaité, commandes hub.
- Évènements : `PlayerTeleportEvent`, `PlayerJoinEvent`, `BlockPlaceEvent`, `BlockBreakEvent` (si protection active).
- Classes suggérées : `FreeCubePlugin`, `FreeCubeWorldManager`, `FreeCubeListener`, `FreeCubeCommand`.

## Compatibilité multi‑versions et migrations
- Interdire le NMS et privilégier BKCommonLib pour les fonctionnalités avancées.
- Encapsuler les différences de matériaux/sons via des adaptateurs et initialiser les implémentations adaptées à `onEnable`.
- Si une fonctionnalité manque sur une version, désactiver proprement ou avertir les joueurs.
- Prévenir dans la documentation que malgré Via*, certaines fonctionnalités peuvent dysfonctionner selon la version du client.

## Gabarit de prompt pour Codex
```
Tu es un assistant qui doit générer un projet Maven multi-module complet pour Spigot 1.9.4. Objectif : produire en une seule réponse l'arborescence et le code de cinq modules :
- core-api (utilitaires communs, adaptateurs de version sans NMS, gestion équipes/scoreboard/mini-jeu).
- rush-plugin, hikabrain-plugin, skywars-plugin, freecube-plugin (un plugin Spigot chacun).

Contraintes générales :
- Utiliser uniquement l'API Spigot 1.9.4 et BKCommonLib ; aucune importation NMS ou CraftBukkit.
- Fournir pour chaque module un plugin.yml correct (api-version: 1.9.4, dépendance BKCommonLib, classe main unique, commandes, permissions).
- Implémenter des adaptateurs de version dans core-api (ex. VersionUtils, MaterialAdapter) avec pattern Strategy pour sons/particules/matériaux.
- Séparer la logique métier de la présentation (scoreboards/messages) et commenter brièvement en Javadoc.
- Inclure un README général expliquant compilation (`mvn clean package`) et installation, en rappelant d'installer ViaVersion, ViaBackwards, ViaRewind.

Mini-jeux à coder :
- Rush : deux équipes, lits à détruire, générateurs de ressources, coffres initiaux, réapparition, gestion des états et victoire. Écouter PlayerJoinEvent, BlockBreakEvent (lit), PlayerRespawnEvent, PlayerDeathEvent, EntityDamageByEntityEvent, GameStateChangeEvent. Classes : RushPlugin, RushArena, RushTeam, RushGameManager, RushListener, RushCommand.
- HikaBrain (MLGRush) : duel, plateforme/point, reset après point, score. Écouter PlayerMoveEvent, BlockBreakEvent, PlayerDeathEvent, PlayerRespawnEvent. Classes : HikaBrainPlugin, HikaArena, HikaTeam, HikaGameManager, HikaListener, HikaCommand.
- SkyWars : îles flottantes, coffres aléatoires (centre plus riche), limite de temps, dernier survivant, mode spectateur. Écouter PlayerJoinEvent, PlayerInteractEvent (coffres), PlayerDeathEvent, PlayerRespawnEvent. Classes : SkyWarsPlugin, SkyWarsArena, SkyWarsChestManager, SkyWarsGameManager, SkyWarsListener, SkyWarsCommand.
- FreeCube : monde créatif téléportable, protection anti-grief/claims optionnelle, commandes hub. Écouter PlayerTeleportEvent, PlayerJoinEvent, BlockPlaceEvent/BlockBreakEvent si protection. Classes : FreeCubePlugin, FreeCubeWorldManager, FreeCubeListener, FreeCubeCommand.

Exigences supplémentaires :
- Parent Maven Java 8 ; dépendances communes Spigot 1.9.4, BKCommonLib (compatible 1.8+), JUnit facultatif.
- Chaque plugin expose des commandes (/rush, /hikabrain, /skywars, /freecube) pour démarrer/arrêter les parties, rejoindre une arène, consulter un classement.
- Utiliser un gestionnaire de commandes simple ou celui de BKCommonLib ; pas de doublons de main class.
- Prévoir des messages/erreurs clairs pour les administrateurs et désactiver proprement les fonctionnalités manquantes sur certaines versions.
- Fournir un paragraphe final rappelant d'installer/mettre à jour ViaVersion, ViaBackwards, ViaRewind pour la compatibilité réseau et de tester sur différentes versions.
```

Ce gabarit peut être collé tel quel dans Codex pour générer le projet conforme aux attentes FanCraft.
