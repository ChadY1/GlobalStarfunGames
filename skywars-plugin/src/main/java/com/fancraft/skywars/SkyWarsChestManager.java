package com.fancraft.skywars;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Supplies randomized loot for SkyWars chests.
 */
public class SkyWarsChestManager {

    private final Random random = new Random();
    private final List<ItemStack> baseLoot = Arrays.asList(
            new ItemStack(Material.STONE_SWORD),
            new ItemStack(Material.SNOW_BALL, 16),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.WOOD),
            new ItemStack(Material.GOLDEN_APPLE)
    );

    public void fill(Inventory inventory, boolean centerChest) {
        int rolls = centerChest ? 6 : 4;
        for (int i = 0; i < rolls; i++) {
            ItemStack item = baseLoot.get(random.nextInt(baseLoot.size())).clone();
            inventory.setItem(random.nextInt(inventory.getSize()), item);
        }
    }
}
