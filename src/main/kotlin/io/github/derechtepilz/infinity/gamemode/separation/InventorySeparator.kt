package io.github.derechtepilz.infinity.gamemode.separation

import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.List

/**
 * Keeps track of which inventories to load.
 */
object InventorySeparator : Serializer<UUID, List<Array<ItemStack?>>> {

	override fun serialize(origin: UUID): String {
		return InventorySerializer.serialize(Bukkit.getPlayer(origin)!!)
	}

	override fun deserialize(data: String): List<Array<ItemStack?>> {
		return InventorySerializer.deserialize(data)
	}

}