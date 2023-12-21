package io.github.derechtepilz.infinity.backup;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.data.GamemodeData;
import io.github.derechtepilz.infinity.data.InfinityData;
import io.github.derechtepilz.infinity.data.MinecraftData;
import io.github.derechtepilz.infinity.data.PlayerData;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.UUID;

public class PlayerDataHandler {

	private final MinecraftData backupMinecraftData = new MinecraftData();
	private final InfinityData backupInfinityData = new InfinityData();
	private final PlayerData backupPlayerData = new PlayerData();

	public PlayerDataHandler() {
		copyData(Infinity.getInstance().getMinecraftData(), backupMinecraftData);
		copyData(Infinity.getInstance().getInfinityData(), backupInfinityData);
	}

	public void updateInventory(Player player, GamemodeData inventoryData) {
		toggleInventory(player, inventoryData);
	}

	public void updateExperience(Player player, GamemodeData experienceData) {
		toggleExperience(player, experienceData);
	}

	public void updateHealthHunger(Player player, GamemodeData healthHungerData) {
		toggleHealthHunger(player, healthHungerData);
	}

	public void updatePotionEffects(Player player, GamemodeData potionEffectData) {
		togglePotionEffects(player, potionEffectData);
	}


	private void toggleInventory(Player player, GamemodeData inventoryData) {
		// Serialize the enderchest and inventory of the player
		String inventory = InventorySerializer.serialize(player);

		// Load the player inventory and enderchest
		String playerData = inventoryData.getInventoryData().get(player.getUniqueId());

		// Deserialize the enderchest and inventory of the player
		List<ItemStack[]> playerInventoryData = InventorySerializer.deserialize(playerData);
		ItemStack[] inventoryContents = playerInventoryData.get(0);
		ItemStack[] enderChestContents = playerInventoryData.get(1);

		// Save the player inventory and enderchest
		inventoryData.getOtherData().setInventoryData(player.getUniqueId(), inventory);

		// Clear the enderchest and inventory of the player
		player.getInventory().clear();
		player.getEnderChest().clear();

		// Place the items in the enderchest and inventory of the player
		player.getInventory().setContents(inventoryContents);
		player.getEnderChest().setContents(enderChestContents);
	}

	private void toggleExperience(Player player, GamemodeData experienceData) {
		// Serialize the experience of the player
		String experience = ExperienceSerializer.serialize(player);

		// Load the player's experience
		String playerData = experienceData.getExperienceData().get(player.getUniqueId());

		// Deserialize the player's experience
		List<? extends Number> playerExperienceData = ExperienceSerializer.deserialize(playerData);

		// Save the player's experience
		experienceData.getOtherData().setExperienceData(player.getUniqueId(), experience);

		// Reset the player's experience
		player.setLevel(0);
		player.setExp(0.0f);

		// Update the player's experience
		player.setLevel((int) playerExperienceData.get(0));
		player.setExp((float) playerExperienceData.get(1));
	}

	private void toggleHealthHunger(Player player, GamemodeData healthHungerData) {
		// Serialize the health and hunger of the player
		String healthHunger = HealthHungerSerializer.serialize(player);

		// Load the player's health and hunger
		String playerData = healthHungerData.getHealthHungerData().get(player.getUniqueId());

		// Deserialize the player's health and hunger
		List<? extends Number> healthHungerList = HealthHungerSerializer.deserialize(playerData);

		// Save the player's health and hunger
		healthHungerData.getOtherData().setHealthHungerData(player.getUniqueId(), healthHunger);

		// Reset the player's health and hunger
		player.setHealth(20.0);
		player.setFoodLevel(20);
		player.setSaturation(20.0f);

		// Update the player's health and hunger
		player.setHealth((double) healthHungerList.get(0));
		player.setFoodLevel((int) healthHungerList.get(1));
		player.setSaturation((float) healthHungerList.get(2));
	}

	private void togglePotionEffects(Player player, GamemodeData potionEffectData) {
		// Serialize the potion effects of the player
		String potionEffect = EffectSerializer.serialize(player);

		// Load the player's potion effects
		String playerData = potionEffectData.getPotionEffectData().get(player.getUniqueId());

		// Deserialize the player's potion effects
		List<PotionEffect> potionEffectList = EffectSerializer.deserialize(playerData);

		// Save the player's potion effects
		potionEffectData.getOtherData().setPotionEffectData(player.getUniqueId(), potionEffect);

		// Reset the player's potion effects
		player.clearActivePotionEffects();

		// Update the player's potion effects
		player.addPotionEffects(potionEffectList);
	}

	public void createBackup() {
		updateGamemodeData(Infinity.getInstance().getMinecraftPlayerList(), backupMinecraftData);
		updateGamemodeData(Infinity.getInstance().getInfinityPlayerList(), backupInfinityData);

		backupMinecraftData.saveData(backupMinecraftData.getWriter("backup"));
		backupInfinityData.saveData(backupInfinityData.getWriter("backup"));
		backupPlayerData.saveData(backupPlayerData.getWriter("backup"));
	}

	private void updateGamemodeData(List<UUID> playerList, GamemodeData backupData) {
		for (UUID uuid : playerList) {
			if (Bukkit.getPlayer(uuid) == null) continue;
			Player player = Bukkit.getPlayer(uuid);
			backupData.setInventoryData(uuid, InventorySerializer.serialize(player));
			backupData.setExperienceData(uuid, ExperienceSerializer.serialize(player));
			backupData.setHealthHungerData(uuid, HealthHungerSerializer.serialize(player));
			backupData.setPotionEffectData(uuid, EffectSerializer.serialize(player));
		}
	}

	private void copyData(GamemodeData data, GamemodeData toData) {
		for (UUID uuid : data.getInventoryData().keySet()) {
			toData.setInventoryData(uuid, data.getInventoryData().get(uuid));
		}
		for (UUID uuid : data.getExperienceData().keySet()) {
			toData.setExperienceData(uuid, data.getExperienceData().get(uuid));
		}
		for (UUID uuid : data.getHealthHungerData().keySet()) {
			toData.setInventoryData(uuid, data.getHealthHungerData().get(uuid));
		}
		for (UUID uuid : data.getPotionEffectData().keySet()) {
			toData.setPotionEffectData(uuid, data.getPotionEffectData().get(uuid));
		}
	}

}
