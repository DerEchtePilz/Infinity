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

package io.github.derechtepilz.infinity.gamemode.gameclass;

import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.world.WorldCarver;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignListener implements Listener {

	public static SignListener INSTANCE;

	private final Map<UUID, SignState.HomeDimensionState> homeDimension = new HashMap<>();
	private final Map<UUID, SignState.ClassSelectionState> classSelection = new HashMap<>();
	private final Map<UUID, SignState.ClassSwitchingState> switchClassSelection = new HashMap<>();

	public SignListener() {
		INSTANCE = this;
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock == null) return;
		if (!(clickedBlock.getState() instanceof Sign)) {
			return;
		}
		Sign sign = (Sign) clickedBlock.getState();
		Player player = event.getPlayer();
		Action action = event.getAction();
		homeDimension.put(player.getUniqueId(), SignState.HomeDimensionState.UNSET);
		if (sign.getPersistentDataContainer().has(Keys.SIGN_TAG_MINECRAFT_TELEPORT.get(), PersistentDataType.STRING)) {
			// Do stuff on left and right click
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
				PlayerUtil.switchGamemode(player, PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}
		if (sign.getPersistentDataContainer().has(Keys.SIGN_TAG_HOME_DIMENSION_TELEPORT.get(), PersistentDataType.STRING)) {
			// Do stuff on left click and right click
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Teleporting to home dimension..."));
			}
			return;
		}
		if (sign.getPersistentDataContainer().has(Keys.SIGN_TAG_SELECT_CLASS.get(), PersistentDataType.STRING)) {
			// Do stuff
			// Cycle through classes on right click
			// Select the displayed class on left click
			if (action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Selecting class..."));
				player.sendMessage(classSelection.getOrDefault(player.getUniqueId(), SignState.ClassSelectionState.NO_CLASS_SELECTED).asString());
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				SignState.ClassSelectionState testClass = (classSelection.containsKey(player.getUniqueId()))
					? classSelection.get(player.getUniqueId()).getNext()
					: SignState.ClassSelectionState.AIRBORN;
				testClass.loadFor(player);
				classSelection.put(player.getUniqueId(), SignState.ClassSelectionState.valueOf(testClass.asString().content().toUpperCase().replace(" ", "_")));
			}
			return;
		}
		if (sign.getPersistentDataContainer().has(Keys.SIGN_TAG_SWITCH_CLASS.get(), PersistentDataType.STRING)) {
			// Do stuff
			// Cycle through classes on right click
			// Select the displayed class on left click but open a confirmation inventory
			if (action == Action.LEFT_CLICK_BLOCK) {
				player.sendMessage(Component.text().content("Switching class..."));
				player.sendMessage(switchClassSelection.getOrDefault(player.getUniqueId(), SignState.ClassSwitchingState.NO_CLASS_SELECTED).asString());
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				SignState.ClassSwitchingState testClass = (switchClassSelection.containsKey(player.getUniqueId()))
					? switchClassSelection.get(player.getUniqueId()).getNext()
					: SignState.ClassSwitchingState.AIRBORN;
				testClass.loadFor(player);
				switchClassSelection.put(player.getUniqueId(), SignState.ClassSwitchingState.valueOf(testClass.asString().content().toUpperCase().replace(" ", "_")));
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		// Load sign states from player
		SignState.HomeDimensionState homeDimensionState = SignState.HomeDimensionState.getByValue(player.getPersistentDataContainer().getOrDefault(Keys.SIGN_STATE_HOME_DIMENSION.get(), PersistentDataType.STRING, "01"));
		SignState.ClassSelectionState classSelectionState = SignState.ClassSelectionState.getByValue(player.getPersistentDataContainer().getOrDefault(Keys.SIGN_STATE_SELECT_CLASS.get(), PersistentDataType.STRING, "01"));
		SignState.ClassSwitchingState classSwitchingState = SignState.ClassSwitchingState.getByValue(player.getPersistentDataContainer().getOrDefault(Keys.SIGN_STATE_SWITCH_CLASS.get(), PersistentDataType.STRING, "01"));

		// Remove keys from player
		player.getPersistentDataContainer().remove(Keys.SIGN_STATE_HOME_DIMENSION.get());
		player.getPersistentDataContainer().remove(Keys.SIGN_STATE_SELECT_CLASS.get());
		player.getPersistentDataContainer().remove(Keys.SIGN_STATE_SWITCH_CLASS.get());

		homeDimension.put(player.getUniqueId(), homeDimensionState);
		classSelection.put(player.getUniqueId(), classSelectionState);
		switchClassSelection.put(player.getUniqueId(), classSwitchingState);

		// Load signs for the player
		WorldCarver.LobbyCarver.setupPlayerSignWithDelay(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		saveSignStatesFor(player);
	}

	public void saveSignStatesFor(Player player) {
		SignState.HomeDimensionState homeDimensionState = homeDimension.get(player.getUniqueId());
		SignState.ClassSelectionState classSelectionState = classSelection.get(player.getUniqueId());
		SignState.ClassSwitchingState classSwitchingState = switchClassSelection.get(player.getUniqueId());

		// Save values to the player
		player.getPersistentDataContainer().set(Keys.SIGN_STATE_HOME_DIMENSION.get(), PersistentDataType.STRING, homeDimensionState.getValue());
		player.getPersistentDataContainer().set(Keys.SIGN_STATE_SELECT_CLASS.get(), PersistentDataType.STRING, classSelectionState.getValue());
		player.getPersistentDataContainer().set(Keys.SIGN_STATE_SWITCH_CLASS.get(), PersistentDataType.STRING, classSwitchingState.getValue());

		// Clear maps
		homeDimension.remove(player.getUniqueId());
		classSelection.remove(player.getUniqueId());
		switchClassSelection.remove(player.getUniqueId());
	}

	public Map<UUID, SignState.HomeDimensionState> getHomeDimension() {
		return homeDimension;
	}

	public Map<UUID, SignState.ClassSelectionState> getClassSelection() {
		return classSelection;
	}

	public Map<UUID, SignState.ClassSwitchingState> getSwitchClassSelection() {
		return switchClassSelection;
	}

}
