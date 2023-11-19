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

package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity0
import io.github.derechtepilz.infinity.gamemode.Gamemode.*
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World

/**
 * An enum representation of the two game modes [MINECRAFT] and [INFINITY] plus [UNKNOWN] as a fallback game mode!
 */
enum class Gamemode(private val defaultWorld: World) {

	/**
	 * Represents normal Minecraft.
	 *
	 * Contains the known Minecraft worlds.
	 */
	MINECRAFT(Bukkit.getWorld("world")!!),

	/**
	 * Represents the Infinity game mode.
	 *
	 * Contains three worlds to play and a lobby that is the central point of the story.
	 */
	INFINITY(Bukkit.getWorld(NamespacedKey(Infinity0.NAME, "lobby"))!!),

	/**
	 * A fallback value for compatibility with other custom created worlds.
	 */
	UNKNOWN(Bukkit.getWorld("world")!!);

	fun getWorld(): World {
		return this.defaultWorld
	}

	companion object {
		@JvmStatic
		fun getFromKey(key: NamespacedKey): Gamemode {
			return valueOf(key.namespace().uppercase())
		}
	}

}