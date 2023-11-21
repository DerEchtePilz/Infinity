package io.github.derechtepilz.infinity.gamemode;

import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public enum Gamemode {

	MINECRAFT(Bukkit.getWorld("world")),
	INFINITY(Bukkit.getWorld(new NamespacedKey(Infinity.NAME, "lobby")));

	private final World world;

	Gamemode(World world) {
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public static Gamemode getFromKey(NamespacedKey key) {
		return valueOf(key.namespace().toUpperCase());
	}

}
