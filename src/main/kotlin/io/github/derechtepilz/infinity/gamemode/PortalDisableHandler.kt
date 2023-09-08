package io.github.derechtepilz.infinity.gamemode

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.PortalCreateEvent

class PortalDisableHandler : Listener {

	@EventHandler
	fun onCreatePortal(event: PortalCreateEvent) {
		if (Gamemode.getFromKey(event.world.key) == Gamemode.INFINITY) {
			event.isCancelled = true
		}
	}

}