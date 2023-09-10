package io.github.derechtepilz.infinity.gamemode.worldmovement

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.*

class EnderChestHandler : Listener {

	private val page: MutableMap<UUID, Int> = mutableMapOf()

	@EventHandler
	fun onEnderChestClick(event: InventoryClickEvent) {

	}

}