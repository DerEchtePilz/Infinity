package io.github.derechtepilz.infinity.gamemode.worldmovement

import io.github.derechtepilz.infinity.gamemode.GameClass
import io.github.derechtepilz.infinity.items.minecraft.ItemBuilder
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.util.TriState
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class EnderChestInventory(private val player: Player) {

	private val mm: MiniMessage = MiniMessage.miniMessage()
	private val enderChestTitle = mm.deserialize("<gradient:#04750b:#0b41bd>Ender Chest</gradient>")

	private val fillerItem = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(Component.empty()).build()

	private val lobbyTeleport = ItemBuilder(Material.NETHER_STAR).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.LOBBY.get())
		.build()
	).build()

	private val classOneTeleport = ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.SKY.get())
		.build()
	).setLore(listOf(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build()

	private val classTwoTeleport = ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.STONE.get())
		.build()
	).setLore(listOf(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build()

	private val classThreeTeleport = ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
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