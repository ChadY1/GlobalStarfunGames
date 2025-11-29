# FanCraft Mini-Jeux (Spigot 1.9.4)

Ce dépôt fournit un projet Maven multi-module prêt à compiler quatre plugins Spigot (Rush, HikaBrain, SkyWars, FreeCube) appuyés sur un module `core-api` partagé. Le code cible l'API Spigot 1.9.4, évite tout accès NMS et s'appuie sur **BKCommonLib** pour absorber les différences entre versions.

## Arborescence des modules
- `core-api` : utilitaires communs (gestion d'équipes, scoreboards, base d'arène/manager, adaptateurs de version via pattern Strategy).
- `rush-plugin` : lit à protéger/détruire, deux équipes, détections de lits et éliminations.
- `hikabrain-plugin` : duel MLGRush, points lorsqu'un joueur atteint la plateforme adverse, remise à zéro après chaque point.
- `skywars-plugin` : îles flottantes, coffres aléatoires, détection du dernier survivant et mode spectateur.
- `freecube-plugin` : monde créatif téléportable avec option de protection anti-grief et retour hub.

Chaque plugin expose une commande dédiée (`/rush`, `/hikabrain`, `/skywars`, `/freecube`) pour rejoindre et contrôler les parties. Tous les `plugin.yml` déclarent `api-version: 1.9.4` et la dépendance douce à BKCommonLib.

## Compilation
```bash
mvn clean package
```
Les JAR sont générés dans `*/target/` pour chaque module. Java 8 est requis.

## Installation sur serveur
1. Copiez chaque JAR plugin dans le dossier `plugins/` d'un serveur Spigot 1.9.4.
2. Installez **BKCommonLib** (dépendance requise) dans le même dossier.
3. Ajoutez les plugins de compatibilité réseau **ViaVersion**, **ViaBackwards** et **ViaRewind** pour permettre aux clients de versions différentes de se connecter. Maintenez-les à jour et soyez conscients que certaines fonctionnalités peuvent varier selon la version du client.
4. Démarrez le serveur et vérifiez les commandes `/rush`, `/hikabrain`, `/skywars`, `/freecube`.

## Notes de compatibilité
- Aucun import NMS ou CraftBukkit n'est utilisé. Les adaptateurs de `core-api` (sons, matériaux, particules) détectent la version du serveur via `VersionUtils` et utilisent des APIs sûres.
- Si une fonctionnalité n'existe pas sur une version, les managers désactivent poliment l'action ou préviennent l'administrateur via les logs.

Testez sur plusieurs versions de client avec Via* pour vérifier le rendu des sons/particules et adaptez les configurations (spawns, coffres, protections) selon vos maps.
