package com.fancraft.skywars;

import com.fancraft.core.game.BaseArena;
import com.fancraft.core.game.GameState;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Arena describing island spawns and chest manager.
 */
public class SkyWarsArena extends BaseArena {

    private final List<Location> spawnPoints = new ArrayList<>();
    private final List<Location> centerChests = new ArrayList<>();
    private final List<Location> islandChests = new ArrayList<>();
    private SkyWarsChestManager chestManager;

    public SkyWarsArena(String id) {
        super(id);
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public List<Location> getCenterChests() {
        return centerChests;
    }

    public List<Location> getIslandChests() {
        return islandChests;
    }

    public SkyWarsChestManager getChestManager() {
        return chestManager;
    }

    public void setChestManager(SkyWarsChestManager chestManager) {
        this.chestManager = chestManager;
    }

    @Override
    public void reset() {
        setState(GameState.RESETTING);
        getPlayers().clear();
        setState(GameState.WAITING);
    }

    public boolean isCenterChest(Location location) {
        return centerChests.stream().anyMatch(loc -> loc.getBlock().equals(location.getBlock()));
    }
}
