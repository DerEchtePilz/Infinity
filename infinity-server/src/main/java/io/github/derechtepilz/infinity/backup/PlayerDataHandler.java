package io.github.derechtepilz.infinity.backup;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.separation.GamemodeSeparator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PlayerDataHandler implements GamemodeSeparator {

	private final Set<UUID> offlinePlayers = new HashSet<>();
	private final Map<UUID, String> lastGamemodeBackup = new HashMap<>();
	private final Map<UUID, String> lastInventoryBackup = new HashMap<>();
	private final Map<UUID, String> lastPotionEffectsBackup = new HashMap<>();
	private final Map<UUID, String> lastHealthHungerBackup = new HashMap<>();
	private final Map<UUID, String> lastExperienceBackup = new HashMap<>();

	public PlayerDataHandler() {}

	@Override
	public void updateInventory(Player player, Map<UUID, String> inventories) {
		toggleInventory(player, inventories);
	}

	@Override
	public void updatePotionEffects(Player player, Map<UUID, String> potionEffects) {
		togglePotionEffects(player, potionEffects);
	}

	@Override
	public void updateHealthHunger(Player player, Map<UUID, String> healthHunger) {
		toggleHealthHunger(player, healthHunger);
	}

	@Override
	public void updateExperience(Player player, Map<UUID, String> experience) {
		toggleExperience(player, experience);
	}

	private void toggleInventory(Player player, Map<UUID, String> inventories) {
		// Serialize the enderchest and inventory of the player
		String inventoryData = InventorySerializer.serialize(player);

		// Load the player inventory and enderchest
		String playerData = inventories.getOrDefault(player.getUniqueId(), null);

		if (playerData == null) {
			// Save the player inventory and enderchest
			inventories.put(player.getUniqueId(), inventoryData);

			// Clear the enderchest and inventory of the player
			player.getInventory().clear();
			player.getEnderChest().clear();
			return;
		}

		// Deserialize the enderchest and inventory of the player
		List<ItemStack[]> playerInventoryData = InventorySerializer.deserialize(playerData);
		ItemStack[] inventoryContents = playerInventoryData.get(0);
		ItemStack[] enderChestContents = playerInventoryData.get(1);

		// Save the player inventory and enderchest
		inventories.put(player.getUniqueId(), inventoryData);

		// Clear the enderchest and inventory of the player
		player.getInventory().clear();
		player.getEnderChest().clear();

		// Place the items in the enderchest and inventory of the player
		player.getInventory().setContents(inventoryContents);
		player.getEnderChest().setContents(enderChestContents);
	}

	private void togglePotionEffects(Player player, Map<UUID, String> potionEffects) {
		// Serialize the potion effects of the player
		String potionEffectData = EffectSerializer.serialize(player);

		// Load the player's potion effects
		String playerData = potionEffects.getOrDefault(player.getUniqueId(), null);

		if (playerData == null) {
			// For each player, player is only reached when switching gamemodes the first time
			// Save the player's potion effects
			potionEffects.put(player.getUniqueId(), potionEffectData);

			// Reset the player's potion effects
			player.clearActivePotionEffects();
			return;
		}

		// Deserialize the player's potion effects
		List<PotionEffect> potionEffectList = EffectSerializer.deserialize(playerData);

		// Save the player's potion effects
		potionEffects.put(player.getUniqueId(), potionEffectData);

		// Reset the player's potion effects
		player.clearActivePotionEffects();

		// Update the player's potion effects
		player.addPotionEffects(potionEffectList);
	}

	private void toggleHealthHunger(Player player, Map<UUID, String> healthHunger) {
		// Serialize the health and hunger of the player
		String healthHungerData = HealthHungerSerializer.serialize(player);

		// Load the player's health and hunger
		String playerData = healthHunger.getOrDefault(player.getUniqueId(), null);

		if (playerData == null) {
			// For each player, player is only reached when switching gamemodes the first time
			// Save the player's health and hunger
			healthHunger.put(player.getUniqueId(), healthHungerData);

			// Reset the player's health and hunger
			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.setSaturation(20.0f);
			return;
		}

		// Deserialize the player's health and hunger
		List<? extends Number> healthHungerList = HealthHungerSerializer.deserialize(playerData);

		// Save the player's health and hunger
		healthHunger.put(player.getUniqueId(), healthHungerData);

		// Reset the player's health and hunger
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20.0f);

		// Update the player's health and hunger
		player.setHealth((double) healthHungerList.get(0));
		player.setFoodLevel((int) healthHungerList.get(1));
		player.setSaturation((float) healthHungerList.get(2));
	}

	private void toggleExperience(Player player, Map<UUID, String> experience) {
		// Serialize the experience of the player
		String experienceData = ExperienceSerializer.serialize(player);

		// Load the player's experience
		String playerData = experience.getOrDefault(player.getUniqueId(), null);

		if (playerData == null) {
			// For each player, player is only reached when switching gamemodes the first time
			// Save the player's experience
			experience.put(player.getUniqueId(), experienceData);

			// Reset the player's experience
			player.setLevel(0);
			player.setExp(0.0f);
			return;
		}

		// Deserialize the player's experience
		List<? extends Number> playerExperienceData = ExperienceSerializer.deserialize(playerData);

		// Save the player's experience
		experience.put(player.getUniqueId(), experienceData);

		// Reset the player's experience
		player.setLevel(0);
		player.setExp(0.0f);

		// Update the player's experience
		player.setLevel((int) playerExperienceData.get(0));
		player.setExp((float) playerExperienceData.get(1));
	}

	public void createBackup() {
		List<UUID> backedUpPlayers = new ArrayList<>();
		Map<UUID, String> gamemodeBackupData = new HashMap<>();
		Map<UUID, String> inventoryBackupData = new HashMap<>();
		Map<UUID, String> potionEffectBackupData = new HashMap<>();
		Map<UUID, String> experienceBackupData = new HashMap<>();
		Map<UUID, String> healthHungerBackupData = new HashMap<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			backedUpPlayers.add(player.getUniqueId());
			String inventoryBackup = InventorySerializer.createBackup(player);
			String potionEffectBackup = EffectSerializer.createBackup(player);
			String experienceBackup = ExperienceSerializer.createBackup(player);
			String healthHungerBackup = HealthHungerSerializer.createBackup(player);

			gamemodeBackupData.put(player.getUniqueId(), PlayerUtil.getGamemode(player).name());
			inventoryBackupData.put(player.getUniqueId(), inventoryBackup);
			potionEffectBackupData.put(player.getUniqueId(), potionEffectBackup);
			experienceBackupData.put(player.getUniqueId(), experienceBackup);
			healthHungerBackupData.put(player.getUniqueId(), healthHungerBackup);
		}
		for (UUID uuid : offlinePlayers) {
			backedUpPlayers.add(uuid);
			gamemodeBackupData.put(uuid, lastGamemodeBackup.get(uuid));
			inventoryBackupData.put(uuid, lastInventoryBackup.get(uuid));
			potionEffectBackupData.put(uuid, lastPotionEffectsBackup.get(uuid));
			healthHungerBackupData.put(uuid, lastHealthHungerBackup.get(uuid));
			experienceBackupData.put(uuid, lastExperienceBackup.get(uuid));
		}
		JsonObject jsonObject = new JsonObject();
		for (UUID uuid : backedUpPlayers) {
			JsonObject playerData = new JsonObject();
			playerData.addProperty("gamemode", gamemodeBackupData.get(uuid));
			playerData.addProperty("inventory", inventoryBackupData.get(uuid));
			playerData.addProperty("potionEffects", potionEffectBackupData.get(uuid));
			playerData.addProperty("experience", experienceBackupData.get(uuid));
			playerData.addProperty("healthHunger", healthHungerBackupData.get(uuid));
			jsonObject.add(uuid.toString(), playerData);
		}
		File backupFile = new File("infinity/config", "backup-data.json");
		if (backupFile.exists()) {
			backupFile.delete();
		}
		String backupString = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
		try {
			backupFile.createNewFile();
			BufferedWriter fileWriter = new BufferedWriter(new FileWriter(backupFile));
			fileWriter.write(backupString);
			fileWriter.close();
		} catch (IOException e) {
			Infinity.getInstance().getLogger().warning("Failed to write backup data to file. This might be a bug. Please report this!");
		}
	}

	public void removeFromOfflinePlayers(Player player) {
		offlinePlayers.remove(player.getUniqueId());
		lastGamemodeBackup.remove(player.getUniqueId());
		lastInventoryBackup.remove(player.getUniqueId());
		lastPotionEffectsBackup.remove(player.getUniqueId());
		lastHealthHungerBackup.remove(player.getUniqueId());
		lastExperienceBackup.remove(player.getUniqueId());
	}

	public void backupActivePlayerData(Player player) {
		offlinePlayers.add(player.getUniqueId());
		lastGamemodeBackup.put(player.getUniqueId(), PlayerUtil.getGamemode(player).name());
		lastInventoryBackup.put(player.getUniqueId(), InventorySerializer.createBackup(player));
		lastPotionEffectsBackup.put(player.getUniqueId(), EffectSerializer.createBackup(player));
		lastHealthHungerBackup.put(player.getUniqueId(), HealthHungerSerializer.createBackup(player));
		lastExperienceBackup.put(player.getUniqueId(), ExperienceSerializer.createBackup(player));
	}

}
