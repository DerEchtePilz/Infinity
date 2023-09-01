package io.github.derechtepilz.infinity.structure

import com.google.gson.JsonObject
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Leaves
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs

fun Block.getAsJson(): JsonObject {
	val blockLocation = JsonObject()
	blockLocation.addProperty("locX", this.x)
	blockLocation.addProperty("locY", this.y)
	blockLocation.addProperty("locZ", this.z)
	blockLocation.addProperty("materialType", this.type.name)
	// Add block metadata
	val blockData = this.blockData
	if (blockData is Directional) {
		blockLocation.addProperty("direction", blockData.facing.name)
	}
	if (blockData is Stairs) {
		blockLocation.addProperty("shapeType", blockData.shape.name)
	}
	if (blockData is Bisected) {
		blockLocation.addProperty("bisectedHalf", blockData.half.name)
	}
	if (blockData is Slab) {
		blockLocation.addProperty("slabHalf", blockData.type.name)
	}
	if (blockData is Leaves) {
		blockLocation.addProperty("persistent", blockData.isPersistent)
	}
	return blockLocation
}