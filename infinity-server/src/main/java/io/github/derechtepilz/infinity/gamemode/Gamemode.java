/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

	public Gamemode getOpposite() {
		return switch (this) {
			case MINECRAFT -> INFINITY;
			case INFINITY -> MINECRAFT;
		};
	}

	public static Gamemode getFromKey(NamespacedKey key) {
		return valueOf(key.namespace().toUpperCase());
	}

}
