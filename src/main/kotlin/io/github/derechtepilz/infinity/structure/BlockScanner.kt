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
		if (infinity.isScannerActive && infinity.mode == "place") {
			val placedBlock = event.blockPlaced
			PLACED_LOCATIONS.add(placedBlock.location)
			return
		}
		if (infinity.isScannerActive && infinity.mode == "destroy") {
			val placedBlock = event.blockPlaced
			PLACED_LOCATIONS.remove(placedBlock.location)
			return
		}
	}

	@EventHandler
	fun onDestroy(event: BlockBreakEvent) {
		if (infinity.isScannerActive && infinity.mode == "place") {
			val blockBroken = event.block
			PLACED_LOCATIONS.remove(blockBroken.location)
			return
		}
		if (infinity.isScannerActive && infinity.mode == "destroy") {
			val blockBroken = event.block
			PLACED_LOCATIONS.add(blockBroken.location)
			return
		}
	}

}