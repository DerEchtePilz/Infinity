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