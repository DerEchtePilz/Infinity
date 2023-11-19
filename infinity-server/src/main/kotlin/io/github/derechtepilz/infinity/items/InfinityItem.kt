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

package io.github.derechtepilz.infinity.items

import io.github.derechtepilz.infinity.Infinity0
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Suppress("LeakingThis")
abstract class InfinityItem @JvmOverloads constructor(material: Material, rarity: Rarity, variationId: Int = -1) : ItemStack(material) {

	companion object {
		val ITEM_ID_KEY: NamespacedKey = NamespacedKey(Infinity0.NAME, "item_id")
		val RARITY_KEY: NamespacedKey = NamespacedKey(Infinity0.NAME, "rarity")
		val VARIATION_ID: NamespacedKey = NamespacedKey(Infinity0.NAME, "variation")
	}

	var rarity: Rarity
	var variationId: Int

	init {
		this.rarity = rarity
		this.variationId = variationId
		applyCommonMeta()
		applyMeta()
	}

	private fun applyCommonMeta() {
		val meta = itemMeta
		meta.displayName(displayName())
		meta.lore(lore())
		meta.persistentDataContainer.set(ITEM_ID_KEY, PersistentDataType.STRING, getId())
		meta.persistentDataContainer.set(RARITY_KEY, PersistentDataType.STRING, rarity.rarityId())
		meta.persistentDataContainer.set(VARIATION_ID, PersistentDataType.INTEGER, variationId)
		itemMeta = meta
	}

	fun updateRarityTo(newRarity: Rarity): InfinityItem {
		rarity = newRarity
		applyType()
		applyCommonMeta()
		applyMeta()
		return this
	}

	fun upgradeItem(currentRarity: Rarity): InfinityItem {
		rarity = currentRarity.nextRarity()
		applyType()
		applyCommonMeta()
		applyMeta()
		return this
	}

	fun downgradeItem(currentRarity: Rarity): InfinityItem {
		rarity = currentRarity.previousRarity()
		applyType()
		applyCommonMeta()
		applyMeta()
		return this
	}

	abstract override fun displayName(): Component

	abstract fun applyMeta()

	abstract override fun lore(): List<Component>

	abstract fun getId(): String

	/**
	 * Only used for updating the item
	 *
	 * May also be used to apply different types for items with variation id's greater than 0
	 */
	abstract fun applyType()

	abstract fun isUpgradable(): Boolean

	abstract override fun clone(): InfinityItem

}