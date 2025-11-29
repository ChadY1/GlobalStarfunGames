package com.fancraft.core.version;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Adapter for legacy API names (1.9.4 baseline).
 */
public class LegacyVersionAdapter implements VersionAdapter {

    @Override
    public Material getColoredWool(DyeColor color) {
        return Material.WOOL;
    }

    @Override
    public Sound getGoalSound() {
        try {
            return Sound.valueOf("BLOCK_NOTE_PLING");
        } catch (IllegalArgumentException ex) {
            return Sound.valueOf("NOTE_PLING");
        }
    }

    @Override
    public Particle getCloudParticle() {
        return Particle.CLOUD;
    }

    @Override
    public String getVersionTag() {
        return "legacy";
    }
}
