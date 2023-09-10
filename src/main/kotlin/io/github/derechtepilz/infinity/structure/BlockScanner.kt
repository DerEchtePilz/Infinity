package io.github.derechtepilz.infinity.structure

import com.google.gson.JsonObject
import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
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

fun Block.getAsJson(): JsonObject {
    val blockLocation = JsonObject()
    blockLocation.addProperty("locX", this.x)
    blockLocation.addProperty("locY", this.y)
    blockLocation.addProperty("locZ", this.z)
    blockLocation.addProperty("materialType", this.type.name)
    blockLocation.addProperty("directional", false)
    blockLocation.addProperty("shape", false)
    blockLocation.addProperty("bisected", false)
    blockLocation.addProperty("slab", false)
    // Add block metadata
    val blockData = this.blockData
    if (blockData is Directional) {
        blockLocation.addProperty("directional", true)
        blockLocation.addProperty("direction", blockData.facing.name)
    }
    if (blockData is Stairs) {
        blockLocation.addProperty("shape", true)
        blockLocation.addProperty("shapeType", blockData.shape.name)
    }
    if (blockData is Bisected) {
        blockLocation.addProperty("bisected", true)
        blockLocation.addProperty("bisectedHalf", blockData.half.name)
    }
    if (blockData is Slab) {
        blockLocation.addProperty("slab", true)
        blockLocation.addProperty("slabHalf", blockData.type.name)
    }
    return blockLocation
}