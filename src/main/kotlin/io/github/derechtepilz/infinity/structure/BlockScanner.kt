package io.github.derechtepilz.infinity.structure

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent

class BlockScanner(private val infinity: Infinity) : Listener {

	companion object {
		val PLACED_LOCATIONS: MutableList<Location> = mutableListOf()
	}

	@EventHandler
	fun onPlaceStructure(event: BlockPlaceEvent) {
		if (infinity.isScannerActive) {
			val placedBlock = event.blockPlaced
			PLACED_LOCATIONS.add(placedBlock.location)
		}
	}

	@EventHandler
	fun onDestroy(event: BlockBreakEvent) {
		if (infinity.isScannerActive) {
			val blockBroken = event.block
			PLACED_LOCATIONS.remove(blockBroken.location)
		}
	}

}