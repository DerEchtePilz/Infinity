package io.github.derechtepilz.infinity.world

import com.google.gson.JsonParser
import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Bisected.Half
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Slab.Type
import org.bukkit.block.data.type.Stairs
import org.bukkit.block.data.type.Stairs.Shape
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class StructureLoader(private val world: String, private val structureFile: File) {

    init {
        generateStructure()
    }

    private fun generateStructure() {
        val world = Bukkit.getWorld(NamespacedKey(Infinity.NAME, world))!!
        val structureReader = BufferedReader(FileReader(structureFile))
        val builder = java.lang.StringBuilder()
        var line: String?
        while (structureReader.readLine().also { line = it } != null) {
            builder.append(line)
        }
        val jsonArray = JsonParser.parseString(builder.toString()).asJsonArray
        for (i in 0 until jsonArray.size()) {
            val blockInformationJson = jsonArray[i].asJsonObject
            val locX = blockInformationJson.get("locX").asInt
            val locY = blockInformationJson.get("locY").asInt
            val locZ = blockInformationJson.get("locZ").asInt
            val material = Material.matchMaterial(blockInformationJson.get("materialType").asString)!!
            world.setType(locX, locY, locZ, material)
            // Change BlockData
            val placedBlock = world.getBlockAt(locX, locY, locZ)
            val placedBlockState = placedBlock.state
            var placedBlockData = placedBlockState.blockData

            val isDirectional = blockInformationJson.get("directional").asBoolean
            if (isDirectional) {
                val blockFace = BlockFace.valueOf(blockInformationJson.get("direction").asString)
                val directional: Directional = placedBlockData as Directional
                directional.facing = blockFace
                placedBlockData = directional
            }

            val isShape = blockInformationJson.get("shape").asBoolean
            if (isShape) {
                val shape = Shape.valueOf(blockInformationJson.get("shapeType").asString)
                val shaped: Stairs = placedBlockData as Stairs
                shaped.shape = shape
                placedBlockData = shaped
            }

            val isBisected = blockInformationJson.get("bisected").asBoolean
            if (isBisected) {
                val half = Half.valueOf(blockInformationJson.get("bisectedHalf").asString)
                val bisected: Bisected = placedBlockData as Bisected
                bisected.half = half
                placedBlockData = bisected
            }

            val isSlab = blockInformationJson.get("slab").asBoolean
            if (isSlab) {
                val half: Type = Type.valueOf(blockInformationJson.get("slabHalf").asString)
                val slab: Slab = placedBlockData as Slab
                slab.type = half
                placedBlockData = slab
            }

            placedBlockState.blockData = placedBlockData
            placedBlockState.update()
        }
    }

}