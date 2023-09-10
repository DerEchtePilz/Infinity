/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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