package io.github.derechtepilz.infinity.util;

import org.bukkit.NamespacedKey;

public record ForceInfo(NamespacedKey previousWorld, double x, double y, double z, float yaw, float pitch) {
}
