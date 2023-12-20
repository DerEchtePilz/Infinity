package io.github.derechtepilz.infinity.backup;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.data.Data;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerDataHandler {

	private final Set<UUID> offlinePlayers = new HashSet<>();
	private final Map<UUID, String> lastGamemodeBackup = new HashMap<>();
	private final Map<UUID, String> lastInventoryBackup = new HashMap<>();
	private final Map<UUID, String> lastPotionEffectsBackup = new HashMap<>();
	private final Map<UUID, String> lastHealthHungerBackup = new HashMap<>();
	private final Map<UUID, String> lastExperienceBackup = new HashMap<>();

	public PlayerDataHandler() {}

	public void updateInventory(Player player, Data inventoryData) {
		toggleInventory(player, inventoryData);
	}

	public void updateExperience(Player player, Data experienceData) {
		toggleExperience(player, experienceData);
	}

	public void updateHealthHunger(Player player, Data healthHungerData) {
		toggleHealthHunger(player, healthHungerData);
	}

	public void updatePotionEffects(Player player, Data potionEffectData) {
		togglePotionEffects(player, potionEffectData);
	}


	private void toggleInventory(Player player, Data inventoryData) {
		// Serialize the enderchest and inventory of the player
		String inventory = InventorySerializer.serialize(player);

		// Load the player inventory and enderchest
		String playerData = inventoryData.getInventoryData().get(player.getUniqueId());

		// Deserialize the enderchest and inventory of the player
		List<ItemStack[]> playerInventoryData = InventorySerializer.deserialize(playerData);
		ItemStack[] inventoryContents = playerInventoryData.get(0);
		ItemStack[] enderChestContents = playerInventoryData.get(1);

		// Save the player inventory and enderchest
		inventoryData.getOtherGamemodeData().setInventoryData(player.getUniqueId(), inventory);

		// Clear the enderchest and inventory of the player
		player.getInventory().clear();
		player.getEnderChest().clear();

		// Place the items in the enderchest and inventory of the player
		player.getInventory().setContents(inventoryContents);
		player.getEnderChest().setContents(enderChestContents);
	}

	private void toggleExperience(Player player, Data experienceData) {
		// Serialize the experience of the player
		String experience = ExperienceSerializer.serialize(player);

		// Load the player's experience
		String playerData = experienceData.getExperienceData().get(player.getUniqueId());

		// Deserialize the player's experience
		List<? extends Number> playerExperienceData = ExperienceSerializer.deserialize(playerData);

		// Save the player's experience
		experienceData.getOtherGamemodeData().setExperienceData(player.getUniqueId(), experience);

		// Reset the player's experience
		player.setLevel(0);
		player.setExp(0.0f);

		// Update the player's experience
		player.setLevel((int) playerExperienceData.get(0));
		player.setExp((float) playerExperienceData.get(1));
	}

	private void toggleHealthHunger(Player player, Data healthHungerData) {
		// Serialize the health and hunger of the player
		String healthHunger = HealthHungerSerializer.serialize(player);

		// Load the player's health and hunger
		String playerData = healthHungerData.getHealthHungerData().get(player.getUniqueId());

		// Deserialize the player's health and hunger
		List<? extends Number> healthHungerList = HealthHungerSerializer.deserialize(playerData);

		// Save the player's health and hunger
		healthHungerData.getOtherGamemodeData().setHealthHungerData(player.getUniqueId(), healthHunger);

		// Reset the player's health and hunger
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20.0f);

		// Update the player's health and hunger
		player.setHealth((double) healthHungerList.get(0));
		player.setFoodLevel((int) healthHungerList.get(1));
		player.setSaturation((float) healthHungerList.get(2));
	}

	private void togglePotionEffects(Player player, Data potionEffectData) {
		// Serialize the potion effects of the player
		String potionEffect = EffectSerializer.serialize(player);

		// Load the player's potion effects
		String playerData = potionEffectData.getPotionEffectData().get(player.getUniqueId());

		// Deserialize the player's potion effects
		List<PotionEffect> potionEffectList = EffectSerializer.deserialize(playerData);

		// Save the player's potion effects
		potionEffectData.getOtherGamemodeData().setPotionEffectData(player.getUniqueId(), potionEffect);

		// Reset the player's potion effects
		player.clearActivePotionEffects();

		// Update the player's potion effects
		player.addPotionEffects(potionEffectList);
	}

	public void createBackup() {
		Infinity.getInstance().getLogger().info("Backing up player data...");
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
		jsonObject.addProperty("time", System.currentTimeMillis());
		for (UUID uuid : backedUpPlayers) {
			JsonObject playerData = new JsonObject();
			playerData.addProperty("gamemode", gamemodeBackupData.get(uuid));
			playerData.addProperty("inventory", inventoryBackupData.get(uuid));
			playerData.addProperty("potionEffects", potionEffectBackupData.get(uuid));
			playerData.addProperty("experience", experienceBackupData.get(uuid));
			playerData.addProperty("healthHunger", healthHungerBackupData.get(uuid));
			jsonObject.add(uuid.toString(), playerData);
		}
		// Don't save a "backup" with zero saved players
		if (backedUpPlayers.isEmpty()) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(Infinity.getInstance(), () -> {
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
			Infinity.getInstance().getLogger().info("Finished backing up player data!");
		});
	}

	public void loadBackup() {

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
