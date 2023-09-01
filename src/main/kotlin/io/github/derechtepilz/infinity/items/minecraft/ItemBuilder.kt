package io.github.derechtepilz.infinity.items.minecraft

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

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

	fun build(): ItemStack {
		itemStack.itemMeta = meta
		return itemStack
	}

}