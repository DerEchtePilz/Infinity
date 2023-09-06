package io.github.derechtepilz.infinity.gamemode.worldmovement

import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.getGamemode
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.block.EnderChest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

class ChestListener : Listener {

	private val northChestLocation = Vector(0, 100, -1)
	private val eastChestLocation = Vector(1,100, 0)
	private val southChestLocation = Vector(0, 100, 1)
	private val westChestLocation = Vector(-1, 100, 0)

	@EventHandler
	fun onEnderChestOpen(event: PlayerInteractEvent) {
		val player = event.player
		if (player.getGamemode() != Gamemode.INFINITY) {
			return
		}
		val action = event.action
		if (action != Action.RIGHT_CLICK_BLOCK) {
			return
		}
		val clickedBlock = event.clickedBlock!!
		if (clickedBlock.state !is EnderChest) {
			return
		}
		val clickedLocation = event.clickedBlock!!.location.toVector()
		if (clickedLocation == northChestLocation || clickedLocation == eastChestLocation || clickedLocation == southChestLocation || clickedLocation == westChestLocation) {
			event.isCancelled = true
			EnderChestInventory(player)
		}
	}

	@EventHandler
	fun onBlockBreak(event: BlockBreakEvent) {
		val player = event.player
		if (player.getGamemode() != Gamemode.INFINITY) {
			return
		}
		val blockLocation = event.block.location.toVector()
		if (blockLocation == northChestLocation || blockLocation == eastChestLocation || blockLocation == southChestLocation || blockLocation == westChestLocation) {
			event.isCancelled = true
		}
	}

}