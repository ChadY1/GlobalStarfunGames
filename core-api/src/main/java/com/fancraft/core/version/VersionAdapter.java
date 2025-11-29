package com.fancraft.core.version;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Strategy for handling version differences without using NMS.
 */
public interface VersionAdapter {

    Material getColoredWool(DyeColor color);

    Sound getGoalSound();

    Particle getCloudParticle();

    String getVersionTag();
}
