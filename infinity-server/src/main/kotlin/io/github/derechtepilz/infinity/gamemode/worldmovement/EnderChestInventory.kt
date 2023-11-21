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

package io.github.derechtepilz.infinity.gamemode.worldmovement

import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass
import io.github.derechtepilz.infinity.items.minecraft.ItemBuilder0
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class EnderChestInventory(private val player: Player) {

	private val mm: MiniMessage = MiniMessage.miniMessage()
	private val enderChestTitle = mm.deserialize("<gradient:#04750b:#0b41bd>Ender Chest</gradient>")

	private val fillerItem = ItemBuilder0(Material.BLACK_STAINED_GLASS_PANE).setName(Component.empty()).build()

	private val lobbyTeleport = ItemBuilder0(Material.NETHER_STAR).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.LOBBY.get())
		.build()
	).build()

	private val classOneTeleport = ItemBuilder0(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.SKY.get())
		.build()
	).setLore(listOf(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build()

	private val classTwoTeleport = ItemBuilder0(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.STONE.get())
		.build()
	).setLore(listOf(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build()

	private val classThreeTeleport = ItemBuilder0(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.NETHER.get())
		.build()
	).setLore(listOf(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build()

	init {
		INSTANCE = this
		openInventory()
	}

	companion object {
		lateinit var INSTANCE: EnderChestInventory
	}

	private fun openInventory() {
		val inventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, enderChestTitle)
		for (i in 0 until inventory.size) {
			inventory.setItem(i, fillerItem)
			inventory.setItem(21, classOneTeleport)
			inventory.setItem(23, lobbyTeleport)
		}
		player.openInventory(inventory)
	}

	fun enderChestTitle(): Component {
		return enderChestTitle
	}

	fun lobbyTeleport(): ItemStack {
		return lobbyTeleport.clone()
	}

	fun classOneTeleport(): ItemStack {
		return classOneTeleport.clone()
	}

	fun classTwoTeleport(): ItemStack {
		return classTwoTeleport.clone()
	}

	fun classThreeTeleport(): ItemStack {
		return classThreeTeleport.clone()
	}

}