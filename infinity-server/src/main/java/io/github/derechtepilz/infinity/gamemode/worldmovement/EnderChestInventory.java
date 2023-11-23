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

package io.github.derechtepilz.infinity.gamemode.worldmovement;

import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass;
import io.github.derechtepilz.infinity.items.minecraft.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EnderChestInventory {

	private final Player player;
	private static EnderChestInventory enderChestInventory;
	private final Component enderChestTitle = MiniMessage.miniMessage().deserialize("<gradient:#04750b:#0b41bd>Ender Chest</gradient>");

	private final ItemStack fillerItem = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(Component.empty()).build();

	private final ItemStack lobbyTeleport = new ItemBuilder(Material.NETHER_STAR).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.LOBBY.get())
		.build()
	).build();

	private final ItemStack classOneTeleport = new ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.SKY.get())
		.build()
	).setLore(List.of(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build();

	private final ItemStack classTwoTeleport = new ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.STONE.get())
		.build()
	).setLore(List.of(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build();

	private final ItemStack classThreeTeleport = new ItemBuilder(Material.ENDER_PEARL).setName(Component.text()
		.content("Teleport to: ")
		.color(NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, false)
		.append(GameClass.Dimension.NETHER.get())
		.build()
	).setLore(List.of(
		Component.text().content("Left-click  to teleport").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build(),
		Component.text().content("Right-click to select").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false).build()
	)).build();

	public EnderChestInventory(Player player) {
		this.player = player;
		enderChestInventory = this;
		openInventory();
	}

	private void openInventory() {
		Inventory inventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, enderChestTitle);
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, fillerItem);
			inventory.setItem(21, classOneTeleport);
			inventory.setItem(23, lobbyTeleport);
		}
		player.openInventory(inventory);
	}

	public static EnderChestInventory getInstance() {
		return enderChestInventory;
	}

	public Component getEnderChestTitle() {
		return enderChestTitle;
	}

	public ItemStack getLobbyTeleport() {
		return lobbyTeleport;
	}

	public ItemStack getClassOneTeleport() {
		return classOneTeleport;
	}

	public ItemStack getClassTwoTeleport() {
		return classTwoTeleport;
	}

	public ItemStack getClassThreeTeleport() {
		return classThreeTeleport;
	}
}
