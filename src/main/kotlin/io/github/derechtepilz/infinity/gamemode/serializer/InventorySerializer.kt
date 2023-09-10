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

package io.github.derechtepilz.infinity.gamemode.serializer

import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object InventorySerializer {

	fun serializePlayerInventory(playerInventory: PlayerInventory): String {
		// This contains contents, armor and offhand (contents are indexes 0 - 35, armor 36 - 39, offhand - 40)
		val items = playerInventory.contents
		val outputStream = ByteArrayOutputStream()
		val dataOutput = BukkitObjectOutputStream(outputStream)
		dataOutput.writeInt(items.size)
		for (item in items) {
			if (item != null) {
				dataOutput.writeObject(item.serializeAsBytes())
			} else {
				dataOutput.writeObject(null)
			}
		}
		dataOutput.close()
		return Base64Coder.encodeLines(outputStream.toByteArray())
	}

	fun serializeInventory(inventory: Inventory): String {
		val outputStream = ByteArrayOutputStream()
		val dataOutput = BukkitObjectOutputStream(outputStream)

		// Write the size of the inventory
		dataOutput.writeInt(inventory.size)

		// Save every element in the list
		for (i in 0 until inventory.size) {
			dataOutput.writeObject(inventory.getItem(i)?.serializeAsBytes())
		}

		// Serialize that array
		dataOutput.close()
		return Base64Coder.encodeLines(outputStream.toByteArray())
	}

	fun deserializeToInventory(data: String): Array<ItemStack?> {
		val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
		val dataInput = BukkitObjectInputStream(inputStream)
		val items = arrayOfNulls<ItemStack>(dataInput.readInt())
		for (i in items.indices) {
			val stack = dataInput.readObject() as ByteArray?
			if (stack != null) {
				items[i] = ItemStack.deserializeBytes(stack)
			} else {
				items[i] = ItemStack(Material.AIR)
			}
		}
		dataInput.close()
		return items
	}

}