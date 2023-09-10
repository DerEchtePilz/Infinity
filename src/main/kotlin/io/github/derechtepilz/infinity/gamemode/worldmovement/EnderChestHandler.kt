package io.github.derechtepilz.infinity.gamemode.worldmovement

import io.github.derechtepilz.infinity.util.Keys
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import java.util.*

class EnderChestHandler : Listener {

	private val page: MutableMap<UUID, Int> = mutableMapOf()

	@EventHandler
	fun onEnderChestClick(event: InventoryClickEvent) {
		if (event.clickedInventory == null) return
		if (event.clickedInventory!! != event.view.topInventory) return
		if (event.view.title() != EnderChestInventory.INSTANCE.enderChestTitle()) return
		event.isCancelled = true

		val player = event.whoClicked as Player
		val item = if (event.currentItem != null) event.currentItem!! else return
		val inventory = event.clickedInventory!!
		when (item) {
			EnderChestInventory.INSTANCE.classOneTeleport() -> {
				if (event.isRightClick) {
					inventory.setItem(21, EnderChestInventory.INSTANCE.classTwoTeleport())
					return
				}
				if (event.isLeftClick) {
					player.teleport(Location(Bukkit.getWorld(Keys.WORLD_SKY.get())!!, 0.0, 101.0, 0.0), TeleportCause.PLUGIN)
				}
			}
			EnderChestInventory.INSTANCE.classTwoTeleport() -> {
				if (event.isRightClick) {
					inventory.setItem(21, EnderChestInventory.INSTANCE.classThreeTeleport())
					return
				}
				if (event.isLeftClick) {
					player.teleport(Location(Bukkit.getWorld(Keys.WORLD_STONE.get())!!, 0.0, 101.0, 0.0), TeleportCause.PLUGIN)
				}
			}
			EnderChestInventory.INSTANCE.classThreeTeleport() -> {
				if (event.isRightClick) {
					inventory.setItem(21, EnderChestInventory.INSTANCE.classOneTeleport())
					return
				}
				if (event.isLeftClick) {
					player.teleport(Location(Bukkit.getWorld(Keys.WORLD_NETHER.get())!!, 0.0, 101.0, 0.0), TeleportCause.PLUGIN)
				}
			}
			EnderChestInventory.INSTANCE.lobbyTeleport() -> {
				player.teleport(Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get())!!, 0.0, 101.0, 0.0), TeleportCause.PLUGIN)
			}
		}
	}

}