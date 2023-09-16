package io.github.derechtepilz.infinity.gamemode.switching

import io.github.derechtepilz.infinity.gamemode.ForceInfo
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.github.derechtepilz.infinity.gamemode.switchGamemode
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.sendTabListFooter
import io.github.derechtepilz.infinity.world.WorldCarver
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

class GamemodeSwitchHandler : Listener {

	@EventHandler
	fun onTeleport(event: PlayerTeleportEvent) {
		val player = event.player
		val from = event.from

		val previousGamemode = Gamemode.getFromKey(from.world.key)
		val currentGamemode = Gamemode.getFromKey(event.to.world.key)

		// Update sign regardless of gamemode
		WorldCarver.LobbyCarver.setupPlayerSignsWithDelay(player)
		if (previousGamemode == currentGamemode) {
			return
		}

		player.gameMode = if (event.to.world.key == Keys.WORLD_LOBBY.get()) GameMode.ADVENTURE else GameMode.SURVIVAL
		sendTabListFooter(player, player.getGamemode())

		if (event.cause != PlayerTeleportEvent.TeleportCause.COMMAND) {
			// Safeguard, so teleporting with /execute in <dimension> run teleport x y z doesn't cause a StackOverflowError
			// since Player#teleport() calls a PlayerTeleportEvent
			// For this to work, this plugin always needs to teleport with the PLUGIN cause, NOT the COMMAND cause
			return
		}

		player.switchGamemode(PlayerTeleportEvent.TeleportCause.UNKNOWN, player.world, player.location, ForceInfo(from.world.key, from.x, from.y, from.z, from.yaw, from.pitch))
	}

}

fun Player.switchGamemode() {

}