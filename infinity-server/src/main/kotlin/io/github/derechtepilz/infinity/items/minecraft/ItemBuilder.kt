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

package io.github.derechtepilz.infinity.items.minecraft

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class ItemBuilder(material: Material) {

	private val material: Material
	private val itemStack: ItemStack
	private val meta: ItemMeta

	init {
		this.material = material
		itemStack = ItemStack(material)
		meta = itemStack.itemMeta
	}

	fun setName(displayName: Component): ItemBuilder {
		meta.displayName(displayName)
		return this
	}

	fun setLore(lore: List<Component>): ItemBuilder {
		meta.lore(lore)
		return this
	}

	fun <T : Any> setTag(key: NamespacedKey, value: T, type: PersistentDataType<T, T>): ItemBuilder {
		meta.persistentDataContainer.set(key, type, value)
		return this
	}

	fun build(): ItemStack {
		itemStack.itemMeta = meta
		return itemStack
	}

}