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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

class InfinityPickaxe(rarity: Rarity) : InfinityItem(Material.matchMaterial(rarity.asTool().asPickaxe())!!, rarity) {

	companion object {
		const val ITEM_ID = "infinity_pickaxe"
		const val ITEM_NAME = "Infinity Pickaxe"
	}

	override fun displayName(): Component = Component.text(ITEM_NAME).color(rarity.color()).decoration(TextDecoration.ITALIC, false)

	override fun applyMeta() {
		val meta = itemMeta
		meta.isUnbreakable = true
		itemMeta = meta
	}

	override fun lore(): List<Component> {
		val lore = mutableListOf<Component>()
		lore.add(Component.text("Cobblestone Drops: ")
			.color(NamedTextColor.GRAY)
			.decoration(TextDecoration.ITALIC, false)
			.append(Component.text(rarity.asTool().modifier())
				.color(NamedTextColor.GREEN)
				.decoration(TextDecoration.ITALIC, false)
			)
		)
		lore.add(Component.text("Basalt Drops: ")
			.color(NamedTextColor.GRAY)
			.decoration(TextDecoration.ITALIC, false)
			.append(Component.text(rarity.asTool().modifier())
				.color(NamedTextColor.GREEN)
				.decoration(TextDecoration.ITALIC, false)
			)
		)
		lore.add(Component.empty())
		lore.add(rarity.rarityString())
		return lore.toList()
	}

	override fun getId(): String = ITEM_ID

	override fun applyType() {
		type = Material.matchMaterial(rarity.asTool().asPickaxe())!!
	}

	override fun isUpgradable() = true

	override fun clone(): InfinityPickaxe {
		val infinityPickaxe = InfinityPickaxe(this.rarity)
		infinityPickaxe.itemMeta = this.itemMeta
		return infinityPickaxe
	}

}