package com.fancraft.core.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility helpers to read locations and required values from plugin configs.
 */
public final class ConfigHelper {

    private ConfigHelper() {
    }

    public static Location readLocation(ConfigurationSection section) {
        if (section == null) {
            throw new IllegalArgumentException("Section manquante pour une localisation");
        }
        String worldName = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw", 0.0);
        float pitch = (float) section.getDouble("pitch", 0.0);
        if (worldName == null) {
            throw new IllegalArgumentException("Nom de monde manquant dans une localisation");
        }
        if (Bukkit.getWorld(worldName) == null) {
            Bukkit.getLogger().warning("Monde absent dans la configuration, création: " + worldName);
            Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
        }
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    public static ConfigurationSection requireSection(JavaPlugin plugin, String path) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null) {
            throw new IllegalStateException("Section manquante dans config: " + path);
        }
        return section;
    }
}
