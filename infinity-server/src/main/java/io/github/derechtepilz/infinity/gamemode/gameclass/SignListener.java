package io.github.derechtepilz.infinity.gamemode.gameclass;

import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.world.WorldCarver;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
