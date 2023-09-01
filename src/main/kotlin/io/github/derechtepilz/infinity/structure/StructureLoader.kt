package io.github.derechtepilz.infinity.structure

import com.google.gson.JsonParser
import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Bisected.Half
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Leaves
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Slab.Type
import org.bukkit.block.data.type.Stairs
import org.bukkit.block.data.type.Stairs.Shape
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader

class StructureLoader(private val world: String, private val structureFile: InputStream) {

	init {
		generateStructure()
	}

	private fun generateStructure() {
		val world = Bukkit.getWorld(NamespacedKey(Infinity.NAME, world))!!
		val structureReader = BufferedReader(InputStreamReader(structureFile))
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

			if (blockInformationJson.has("direction")) {
				val directional: Directional = placedBlockData as Directional
				directional.facing = BlockFace.valueOf(blockInformationJson.get("direction").asString)
				placedBlockData = directional
			}

			if (blockInformationJson.has("shapeType")) {
				val shaped: Stairs = placedBlockData as Stairs
				shaped.shape = Shape.valueOf(blockInformationJson.get("shapeType").asString)
				placedBlockData = shaped
			}

			if (blockInformationJson.has("bisectedHalf")) {
				val bisected: Bisected = placedBlockData as Bisected
				bisected.half = Half.valueOf(blockInformationJson.get("bisectedHalf").asString)
				placedBlockData = bisected
			}

			if (blockInformationJson.has("slabHalf")) {
				val slab: Slab = placedBlockData as Slab
				slab.type = Type.valueOf(blockInformationJson.get("slabHalf").asString)
				placedBlockData = slab
			}

			if (blockInformationJson.has("persistent")) {
				val leaves: Leaves = placedBlockData as Leaves
				leaves.isPersistent = blockInformationJson.get("persistent").asBoolean
				placedBlockData = leaves
			}

			placedBlockState.blockData = placedBlockData
			placedBlockState.update()
		}
	}

}