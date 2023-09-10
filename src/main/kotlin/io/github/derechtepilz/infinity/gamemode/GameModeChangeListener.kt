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

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.event.GameModeChangeEvent
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.sendTabListFooter
import io.github.derechtepilz.infinity.world.WorldCarver
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerTeleportEvent

class GameModeChangeListener(plugin: Infinity) : Listener {

	private val plugin: Infinity

	init {
		this.plugin = plugin
		INSTANCE = this
	}

	companion object {
		lateinit var INSTANCE: GameModeChangeListener
	}

	@EventHandler
	fun onTeleport(event: PlayerTeleportEvent) {
		if (event.cause != PlayerTeleportEvent.TeleportCause.COMMAND) {
			return
		}
		val player = event.player
		val from = event.from
		player.switchGamemode(PlayerTeleportEvent.TeleportCause.UNKNOWN, player.world, player.location, ForceInfo(from.world.key, from.x, from.y, from.z, from.yaw, from.pitch))
	}

	@EventHandler
	fun onWorldChange(event: PlayerChangedWorldEvent) {
		val player = event.player
		val from = event.from.key
		val current = player.world.key
		val previousGamemode = Gamemode.getFromKey(from)
		val currentGamemode = Gamemode.getFromKey(current)

		// Update sign regardless of gamemode
		WorldCarver.LobbyCarver.setupPlayerSignsWithDelay(player)
		player.gameMode = if (current == Keys.WORLD_LOBBY.get()) GameMode.ADVENTURE else GameMode.SURVIVAL
		if (previousGamemode == currentGamemode) {
			return
		}
		Bukkit.getPluginManager().callEvent(GameModeChangeEvent(player, previousGamemode, currentGamemode))
	}

	@EventHandler
	fun onGamemodeChange(event: GameModeChangeEvent) {
		val player = event.player
		sendTabListFooter(player, player.getGamemode())
	}

}