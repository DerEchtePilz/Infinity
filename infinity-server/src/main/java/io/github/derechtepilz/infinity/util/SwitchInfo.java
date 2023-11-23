package io.github.derechtepilz.infinity.util;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;

public record SwitchInfo(ForceInfo force, NamespacedKey targetWorld, Location targetLocation) {
}
