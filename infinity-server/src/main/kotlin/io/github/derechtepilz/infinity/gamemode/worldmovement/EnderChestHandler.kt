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

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import java.util.*

class EnderChestHandler : Listener {

	private val page: MutableMap<UUID, Int> = mutableMapOf()

	@EventHandler
	fun onEnderChestClick(event: InventoryClickEvent) {
		if (event.clickedInventory == null) return
		if (event.clickedInventory!! != event.view.topInventory) return
		if (event.view.title() != EnderChestInventory.INSTANCE.enderChestTitle()) return
		event.isCancelled = true

		val player = event.whoClicked as Player
		val item = if (event.currentItem != null) event.currentItem!! else return
		val inventory = event.clickedInventory!!
		when (item) {
			EnderChestInventory.INSTANCE.classOneTeleport() -> {
				if (event.isRightClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f))
					inventory.setItem(21, EnderChestInventory.INSTANCE.classTwoTeleport())
					return
				}
				if (event.isLeftClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f))
					Bukkit.getScheduler().runTaskLater(Infinity.INSTANCE, Runnable {
						player.teleport(Location(Bukkit.getWorld(Keys.WORLD_SKY.get())!!, 0.5, 101.0, 0.5), TeleportCause.PLUGIN)
					}, 20)
				}
			}
			EnderChestInventory.INSTANCE.classTwoTeleport() -> {
				if (event.isRightClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f))
					inventory.setItem(21, EnderChestInventory.INSTANCE.classThreeTeleport())
					return
				}
				if (event.isLeftClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f))
					Bukkit.getScheduler().runTaskLater(Infinity.INSTANCE, Runnable {
						player.teleport(Location(Bukkit.getWorld(Keys.WORLD_STONE.get())!!, 0.5, 101.0, 0.5), TeleportCause.PLUGIN)
					}, 20)
				}
			}
			EnderChestInventory.INSTANCE.classThreeTeleport() -> {
				if (event.isRightClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 1.0f))
					inventory.setItem(21, EnderChestInventory.INSTANCE.classOneTeleport())
					return
				}
				if (event.isLeftClick) {
					player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f))
					Bukkit.getScheduler().runTaskLater(Infinity.INSTANCE, Runnable {
						player.teleport(Location(Bukkit.getWorld(Keys.WORLD_NETHER.get())!!, 0.5, 101.0, 0.5), TeleportCause.PLUGIN)
					}, 20)
				}
			}
			EnderChestInventory.INSTANCE.lobbyTeleport() -> {
				player.playSound(Sound.sound(Key.key("minecraft:block.note_block.bell"), Sound.Source.PLAYER, 1.0f, 0.5f))
				Bukkit.getScheduler().runTaskLater(Infinity.INSTANCE, Runnable {
					player.teleport(Location(Bukkit.getWorld(Keys.WORLD_LOBBY.get())!!, 0.5, 101.0, 0.5), TeleportCause.PLUGIN)
				}, 20)
			}
		}
	}

}