package com.fancraft.freecube;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Handles teleportation and safety for the creative world.
 */
public class FreeCubeWorldManager {

    private final String worldName;

    public FreeCubeWorldManager(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
        }
        return world;
    }

    public void sendToCreative(Player player) {
        World world = getWorld();
        player.teleport(world.getSpawnLocation());
        player.setGameMode(GameMode.CREATIVE);
    }
}
