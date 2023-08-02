package io.github.derechtepilz.infinity.events

import com.destroystokyo.paper.MaterialTags
import com.google.gson.JsonObject
import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.metadata.Metadatable

class BlockScanner(private val infinity: Infinity) : Listener {

    private var placedVillagerLocations = 0

    private val placedLocations: MutableList<Location> = mutableListOf()

    @EventHandler
    fun onPlaceStructure(event: BlockPlaceEvent) {
        if (infinity.isScannerActive) {
            val placedBlock = event.blockPlaced
            placedLocations.add(placedBlock.location)
            val blockLocation = JsonObject()
            blockLocation.addProperty("locX", placedBlock.x)
            blockLocation.addProperty("locY", placedBlock.y)
            blockLocation.addProperty("LocZ", placedBlock.z)
            blockLocation.addProperty("materialType", placedBlock.type.name)
            blockLocation.addProperty("villagerLocation", false)
            blockLocation.addProperty("directional", false)
            blockLocation.addProperty("shape", false)
            blockLocation.addProperty("bisected", false)
            // Add block metadata
            val blockData = placedBlock.state
            println(blockData)
            if (blockData is Directional) {
                blockLocation.addProperty("directional", true)
                blockLocation.addProperty("direction", (blockData as Directional).facing.name)
            }
            if (blockData is Stairs) {
                blockLocation.addProperty("shape", true)
                blockLocation.addProperty("shapeType", (blockData as Stairs).shape.name)
            }
            if (blockData is Bisected) {
                blockLocation.addProperty("bisected", true)
                blockLocation.addProperty("bisectedHalf", (blockData as Bisected).half.name)

            }
            if (placedBlock.type == Material.BEDROCK && placedVillagerLocations >= Infinity.MAX_VILLAGERS) {
                placedVillagerLocations++
                blockLocation.addProperty("villagerLocation", true)
                blockLocation.addProperty("villager", placedVillagerLocations)
            }
            infinity.getDevCommand().placedBlocks.add(blockLocation)
        }
    }

    @EventHandler
    fun onDestroy(event: BlockBreakEvent) {
        if (infinity.isScannerActive) {
            val blockBroken = event.block
            val brokenLocation = blockBroken.location
            val locationIndex = placedLocations.indexOf(brokenLocation) - 1
            if (locationIndex <= -1) {
                return
            }
            placedLocations.remove(brokenLocation)
            infinity.getDevCommand().placedBlocks.removeAt(locationIndex)
        }
    }

}