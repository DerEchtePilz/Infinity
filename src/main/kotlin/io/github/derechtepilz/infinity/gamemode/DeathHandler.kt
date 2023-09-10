package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent

class DeathHandler : Listener {

	@EventHandler
	fun onDeath(event: PlayerDeathEvent) {
		val player = event.player
		val world = player.world
		val gamemode = Gamemode.getFromKey(world.key)
		if (gamemode == Gamemode.INFINITY) {

		}
	}

	// Handle spawn points

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		Infinity.INSTANCE.logger.info(this::class.java.simpleName)
		val player = event.player

	}

}