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

package io.github.derechtepilz.infinity.gamemode.gameclass

import io.github.derechtepilz.infinity.gamemode.switching.switchGamemode
import io.github.derechtepilz.infinity.util.Keys0
import io.github.derechtepilz.infinity.world.WorldCarver
import net.kyori.adventure.text.Component
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class SignListener : Listener {

	val homeDimension: MutableMap<UUID, SignState.HomeDimensionState> = mutableMapOf()
	val classSelection: MutableMap<UUID, SignState.ClassSelectionState> = mutableMapOf()
	val switchClassSelection: MutableMap<UUID, SignState.ClassSwitchingState> = mutableMapOf()

	companion object {
		lateinit var INSTANCE: SignListener
	}

	init {
		INSTANCE = this
	}

	@EventHandler
	fun onSignClick(event: PlayerInteractEvent) {
		val clickedBlock = event.clickedBlock ?: return
		if (clickedBlock.state !is Sign) {
			return
		}
		val sign = clickedBlock.state as Sign
		val player = event.player
		val action = event.action
		homeDimension[player.uniqueId] = SignState.HomeDimensionState.UNSET
		if (sign.persistentDataContainer.has(Keys0.SIGN_TAG_MINECRAFT_TELEPORT.get(), PersistentDataType.STRING)) {
			// Do stuff on left and right click
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
				player.switchGamemode(PlayerTeleportEvent.TeleportCause.PLUGIN)
			}
		}
		if (sign.persistentDataContainer.has(Keys0.SIGN_TAG_HOME_DIMENSION_TELEPORT.get(), PersistentDataType.STRING)) {
			// Do stuff on left click and right click
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Teleporting to home dimension..."))
			}
			return
		}
		if (sign.persistentDataContainer.has(Keys0.SIGN_TAG_SELECT_CLASS.get(), PersistentDataType.STRING)) {
			// Do stuff
			// Cycle through classes on right click
			// Select the displayed class on left click
			if (action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Selecting class..."))
				player.sendMessage(classSelection.getOrDefault(player.uniqueId, SignState.ClassSelectionState.NO_CLASS_SELECTED).asString())
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				val testClass = if (classSelection.containsKey(player.uniqueId)) {
					classSelection[player.uniqueId]!!.getNext()
				} else {
					SignState.ClassSelectionState.AIRBORN
				}
				testClass.loadFor(player)
				classSelection[player.uniqueId] = SignState.ClassSelectionState.valueOf(testClass.asString().content().uppercase().replace(" ", "_"))
			}
			return
		}
		if (sign.persistentDataContainer.has(Keys0.SIGN_TAG_SWITCH_CLASS.get(), PersistentDataType.STRING)) {
			// Do stuff
			// Cycle through classes on right click
			// Select the displayed class on left click but open a confirmation inventory
			if (action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Switching class..."))
				player.sendMessage(switchClassSelection.getOrDefault(player.uniqueId, SignState.ClassSelectionState.NO_CLASS_SELECTED).asString())
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				val testClass = if (switchClassSelection.containsKey(player.uniqueId)) {
					switchClassSelection[player.uniqueId]!!.getNext()
				} else {
					SignState.ClassSwitchingState.AIRBORN
				}
				testClass.loadFor(player)
				switchClassSelection[player.uniqueId] = SignState.ClassSwitchingState.valueOf(testClass.asString().content().uppercase().replace(" ", "_"))
			}
			return
		}
	}

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val player = event.player
		// Load sign states from player
		val homeDimensionState = SignState.HomeDimensionState.getByValue(player.persistentDataContainer.getOrDefault(Keys0.SIGN_STATE_HOME_DIMENSION.get(), PersistentDataType.STRING, "01"))
		val classSelectionState = SignState.ClassSelectionState.getByValue(player.persistentDataContainer.getOrDefault(Keys0.SIGN_STATE_SELECT_CLASS.get(), PersistentDataType.STRING, "01"))
		val classSwitchingState = SignState.ClassSwitchingState.getByValue(player.persistentDataContainer.getOrDefault(Keys0.SIGN_STATE_SWITCH_CLASS.get(), PersistentDataType.STRING, "01"))

		// Remove keys from player
		player.persistentDataContainer.remove(Keys0.SIGN_STATE_HOME_DIMENSION.get())
		player.persistentDataContainer.remove(Keys0.SIGN_STATE_SELECT_CLASS.get())
		player.persistentDataContainer.remove(Keys0.SIGN_STATE_SWITCH_CLASS.get())

		homeDimension[player.uniqueId] = homeDimensionState
		classSelection[player.uniqueId] = classSelectionState
		switchClassSelection[player.uniqueId] = classSwitchingState

		// Load signs for the player
		WorldCarver.LobbyCarver.setupPlayerSignsWithDelay(player)
	}

	@EventHandler
	fun onQuit(event: PlayerQuitEvent) {
		val player = event.player
		// Load sign states for the player
		saveSignStatesFor(player)
	}

	fun saveSignStatesFor(player: Player) {
		val homeDimensionState = homeDimension[player.uniqueId]!!
		val classSelectionState = classSelection[player.uniqueId]!!
		val classSwitchingState = switchClassSelection[player.uniqueId]!!

		// Save values to player
		player.persistentDataContainer.set(Keys0.SIGN_STATE_HOME_DIMENSION.get(), PersistentDataType.STRING, homeDimensionState.value)
		player.persistentDataContainer.set(Keys0.SIGN_STATE_SELECT_CLASS.get(), PersistentDataType.STRING, classSelectionState.value)
		player.persistentDataContainer.set(Keys0.SIGN_STATE_SWITCH_CLASS.get(), PersistentDataType.STRING, classSwitchingState.value)

		// Clear maps
		homeDimension.remove(player.uniqueId)
		classSelection.remove(player.uniqueId)
		switchClassSelection.remove(player.uniqueId)
	}

}