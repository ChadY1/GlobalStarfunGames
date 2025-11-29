# Starfun Mini-Jeux (Spigot 1.9.4)

Ce dépôt fournit un projet Maven multi-module prêt à compiler quatre plugins Spigot (Rush, HikaBrain, SkyWars, FreeCube) appuyés sur un module `core-api` partagé. Le code cible l'API Spigot 1.9.4, évite tout accès NMS et s'appuie sur **BKCommonLib** pour absorber les différences entre versions. Les JAR finaux sont pré-intégrés (shade) avec `core-api` pour éviter les erreurs de classe manquante.

## Arborescence des modules
- `core-api` : utilitaires communs (gestion d'équipes, scoreboards, base d'arène/manager, adaptateurs de version via pattern Strategy, PNJ de test via `NpcManager`). Il inclut désormais un `plugin.yml` minimal (`StarfunCoreAPI`) pour ceux qui souhaitent le déployer séparément, bien que chaque mini‑jeu reste autonome grâce au shading.
- `starfun-api-plugin` : plugin d'orchestration qui expose `/starfun` pour l'aide globale, le rechargement centralisé et le spawn de PNJ pilotes pour tester les arènes.
- `rush-plugin` : lit à protéger/détruire, deux équipes, détections de lits et éliminations. Plugin: **StarfunRush**.
- `hikabrain-plugin` : duel MLGRush, points lorsqu'un joueur atteint la plateforme adverse, remise à zéro après chaque point. Plugin: **StarfunHikaBrain**.
- `skywars-plugin` : îles flottantes, coffres aléatoires, détection du dernier survivant et mode spectateur. Plugin: **StarfunSkyWars**.
- `freecube-plugin` : monde créatif téléportable avec option de protection anti-grief et retour hub. Plugin: **StarfunFreeCube**.

Chaque plugin expose une commande dédiée (`/rush`, `/hikabrain`, `/skywars`, `/freecube`) pour rejoindre et contrôler les parties. Tous les `plugin.yml` déclarent `api-version: 1.9.4` et la dépendance douce à BKCommonLib.

## Compilation
```bash
mvn clean package
```
Les JAR sont générés dans `*/target/` pour chaque module et le workflow CI GitHub publie tous les artefacts (y compris `core-api` et `starfun-api-plugin`) pour téléchargement rapide. Java 8 est requis. Chaque plugin final inclut automatiquement `core-api`; aucun JAR supplémentaire n'est nécessaire sur le serveur.

## Configuration complète
- **core-api** : ne requiert pas de configuration; il sert de socle partagé et peut être chargé comme plugin `StarfunCoreAPI` si vous voulez des logs dédiés, mais n'est pas nécessaire sur le serveur car déjà inclus dans chaque JAR de mini‑jeu.
- **rush-plugin** : éditez `rush-plugin/src/main/resources/config.yml` pour définir les coordonnées du lobby, les spawns et lits des équipes Rouge/Bleu. Copiez le fichier dans `plugins/StarfunRush/config.yml` et adaptez les mondes.
- **hikabrain-plugin** : configurez l'unique arène (lobby, spawn rouge/bleu) dans `hikabrain-plugin/src/main/resources/config.yml` puis déployez-le avec le JAR.
- **skywars-plugin** : renseignez les points de spawn, coffres îles et coffres centre dans `skywars-plugin/src/main/resources/config.yml`.
- **freecube-plugin** : ajustez le monde créatif, la position du hub et l'option `protect` dans `freecube-plugin/src/main/resources/config.yml`.

## Guide d'installation par mode
1. **Dépendances communes** : placez les cinq JAR Starfun (dont `StarfunAPI`) dans `plugins/` avec **BKCommonLib** et les plugins réseau **ViaVersion**, **ViaBackwards**, **ViaRewind**.
2. **Rush** (StarfunRush) : copiez `config.yml`, vérifiez les coordonnées des lits/spawns, puis utilisez `/rush start|stop|join` (permission admin `fancraft.rush.admin`).
3. **HikaBrain** (StarfunHikaBrain) : copiez `config.yml`, validez le lobby et les spawns, commande `/hikabrain start|stop|join` (admin `fancraft.hikabrain.admin`).
4. **SkyWars** (StarfunSkyWars) : copiez `config.yml`, vérifiez les points d'apparition et coffres; démarrez via `/skywars start|stop|join` (admin `fancraft.skywars.admin`).
5. **FreeCube** (StarfunFreeCube) : copiez `config.yml`, ajustez le monde créatif/hub; `/freecube go` envoie dans le monde créatif, `/freecube hub` (admin `fancraft.freecube.admin`) renvoie au hub.
6. **Starfun API** : `/starfun help` liste toutes les commandes disponibles, `/starfun reload` recharge tous les mini-jeux, `/starfun npc spawn <areneId>` crée un PNJ combattant pour vos tests PVP.

## Installation sur serveur
1. Copiez chaque JAR plugin Starfun dans le dossier `plugins/` d'un serveur Spigot 1.9.4.
2. Installez **BKCommonLib** (dépendance requise) dans le même dossier.
3. Ajoutez les plugins de compatibilité réseau **ViaVersion**, **ViaBackwards** et **ViaRewind** pour permettre aux clients de versions différentes de se connecter. Maintenez-les à jour et soyez conscients que certaines fonctionnalités peuvent varier selon la version du client.
4. Démarrez le serveur et vérifiez les commandes `/rush`, `/hikabrain`, `/skywars`, `/freecube`.

## Notes de compatibilité
- Aucun import NMS ou CraftBukkit n'est utilisé. Les adaptateurs de `core-api` (sons, matériaux, particules) détectent la version du serveur via `VersionUtils` et utilisent des APIs sûres.
- Si une fonctionnalité n'existe pas sur une version, les managers désactivent poliment l'action ou préviennent l'administrateur via les logs.

Testez sur plusieurs versions de client avec Via* pour vérifier le rendu des sons/particules et adaptez les configurations (spawns, coffres, protections) selon vos maps.
