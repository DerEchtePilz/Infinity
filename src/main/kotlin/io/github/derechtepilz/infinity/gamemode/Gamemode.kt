package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
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
	INFINITY(Bukkit.getWorld(NamespacedKey(Infinity.NAME, "lobby"))!!),

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