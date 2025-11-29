package com.fancraft.core.version;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Adapter for more recent versions while keeping compatibility with 1.9.4 compilation.
 */
public class ModernVersionAdapter implements VersionAdapter {

    @Override
    public Material getColoredWool(DyeColor color) {
        Material modern = Material.matchMaterial(color.name() + "_WOOL");
        return modern != null ? modern : Material.WOOL;
    }

    @Override
    public Sound getGoalSound() {
        try {
            return Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
        } catch (IllegalArgumentException ex) {
            return Sound.valueOf("BLOCK_NOTE_PLING");
        }
    }

    @Override
    public Particle getCloudParticle() {
        return Particle.CLOUD;
    }

    @Override
    public String getVersionTag() {
        return "modern";
    }
}
