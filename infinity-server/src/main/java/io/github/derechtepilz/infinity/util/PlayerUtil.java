package io.github.derechtepilz.infinity.util;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass;
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer;
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerUtil {

	private PlayerUtil() {
	}

	private static final NamespacedKey gameClassKey = new NamespacedKey(Infinity.NAME, "gameclass");

	public static Gamemode getGamemode(Player player) {
		World world = player.getWorld();
		return Gamemode.getFromKey(world.getKey());
	}

	public static TextComponent getGameClass(Player player) {
		return GameClass.valueOf(player.getPersistentDataContainer().get(gameClassKey, PersistentDataType.STRING).toUpperCase()).get();
	}

	public static void setGameClass(Player player, GameClass gameClass) {
		player.getPersistentDataContainer().set(gameClassKey, PersistentDataType.STRING, gameClass.name().toLowerCase());
	}

	public static Location switchGamemode(Player player, PlayerTeleportEvent.TeleportCause cause) {
		if (player.getPersistentDataContainer().has(Keys.GAMEMODE_SWITCH_ENABLED.get(), PersistentDataType.BOOLEAN)) {
			Infinity.getInstance().getLogger().info(PlainTextComponentSerializer.plainText().serialize(player.name()) + " tried to switch gamemodes while that is currently disabled for this player!");
			Infinity.getInstance().getLogger().info("Logging, so server admins can give that feedback to players if needed.");
			return player.getLocation();
		}
		NamespacedKey newWorldKey = switch (getGamemode(player)) {
			case MINECRAFT -> NamespacedKey.fromString(getLastWorldKey(player, Gamemode.INFINITY));
			case INFINITY -> NamespacedKey.fromString(getLastWorldKey(player, Gamemode.MINECRAFT));
		};

		return updateLastLocationAndSwitch(player, cause, new SwitchInfo(null, newWorldKey, null));
	}

	public static Location switchGamemode(Player player, PlayerTeleportEvent.TeleportCause cause, World targetWorld) {
		return switchGamemode(player, cause, targetWorld, null);
	}

	public static Location switchGamemode(Player player, PlayerTeleportEvent.TeleportCause cause, World targetWorld, Location targetLocation) {
		return switchGamemode(player, cause, targetWorld, targetLocation, null);
	}

	public static Location switchGamemode(Player player, PlayerTeleportEvent.TeleportCause cause, World targetWorld, Location targetLocation, ForceInfo force) {
		Gamemode currentGamemode = getGamemode(player);
		Gamemode targetGamemode = Gamemode.getFromKey(targetWorld.getKey());
		if (currentGamemode == targetGamemode && force == null) {
			throw new IllegalArgumentException("The targetWorld can't be in the same game mode!");
		}
		NamespacedKey newWorldKey = targetWorld.getKey();

		return updateLastLocationAndSwitch(player, cause, new SwitchInfo(force, newWorldKey, targetLocation));
	}

	private static Location updateLastLocationAndSwitch(Player player, PlayerTeleportEvent.TeleportCause cause, SwitchInfo switchInfo) {
		// Save the player location and orientation
		double currentLocationX = (switchInfo.force() != null) ? switchInfo.force().x() : player.getLocation().x();
		double currentLocationY = (switchInfo.force() != null) ? switchInfo.force().y() : player.getLocation().y();
		double currentLocationZ = (switchInfo.force() != null) ? switchInfo.force().z() : player.getLocation().z();
		float currentYaw = (switchInfo.force() != null) ? switchInfo.force().yaw() : player.getLocation().getYaw();
		float currentPitch = (switchInfo.force() != null) ? switchInfo.force().pitch() : player.getLocation().getPitch();
		NamespacedKey currentWorldKey = (switchInfo.force() != null) ? switchInfo.force().previousWorld() : player.getWorld().getKey();

		// Load the new location and orientation
		double newPosX = (player.getPersistentDataContainer().has(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE)) ? player.getPersistentDataContainer().get(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE) : 0.5;
		double newPosY = (player.getPersistentDataContainer().has(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE)) ? player.getPersistentDataContainer().get(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE) : 101.0;
		double newPosZ = (player.getPersistentDataContainer().has(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE)) ? player.getPersistentDataContainer().get(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE) : 0.5;
		float newYaw = (player.getPersistentDataContainer().has(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT)) ? player.getPersistentDataContainer().get(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT) : 0.0F;
		float newPitch = (player.getPersistentDataContainer().has(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT)) ? player.getPersistentDataContainer().get(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT) : 0.0F;

		// Overwrite coordinates and rotation if SwitchInfo contains a non-null Location
		newPosX = (switchInfo.targetLocation() != null) ? switchInfo.targetLocation().x() : newPosX;
		newPosY = (switchInfo.targetLocation() != null) ? switchInfo.targetLocation().y() : newPosY;
		newPosZ = (switchInfo.targetLocation() != null) ? switchInfo.targetLocation().z() : newPosZ;
		newYaw = (switchInfo.targetLocation() != null) ? switchInfo.targetLocation().getYaw() : newYaw;
		newPitch = (switchInfo.targetLocation() != null) ? switchInfo.targetLocation().getPitch() : newPitch;

		// Overwrite coordinates and rotation if target world is infinity:lobby
		newPosX = (switchInfo.targetWorld() == Keys.WORLD_LOBBY.get()) ? 0.5 : newPosX;
		newPosY = (switchInfo.targetWorld() == Keys.WORLD_LOBBY.get()) ? 101.0 : newPosY;
		newPosZ = (switchInfo.targetWorld() == Keys.WORLD_LOBBY.get()) ? 0.5 : newPosZ;
		newYaw = (switchInfo.targetWorld() == Keys.WORLD_LOBBY.get()) ? 0.0f : newYaw;
		newPitch = (switchInfo.targetWorld() == Keys.WORLD_LOBBY.get()) ? 0.0f : newPitch;

		// Update the persistent data container
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, currentWorldKey.asString());
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE, currentLocationX);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE, currentLocationY);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE, currentLocationZ);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT, currentYaw);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT, currentPitch);

		Location newLocation = new Location(Bukkit.getWorld(switchInfo.targetWorld()), newPosX, newPosY, newPosZ, newYaw, newPitch);

		// Teleport the player to the newLocation
		player.teleport(newLocation, cause);

		// Update inventory, experience, health and hunger
		updateInventory(player, Infinity.getInstance().getInventoryData());
		updateExperience(player, Infinity.getInstance().getExperienceData());
		updateHealthHunger(player, Infinity.getInstance().getHealthHungerData());
		updatePotionEffects(player, Infinity.getInstance().getPotionEffectData());

		return newLocation;
	}

	public static void updateInventory(Player player, Map<UUID, String> inventories) {
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

	public static void updateExperience(Player player, Map<UUID, String> experience) {
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

	public static void updateHealthHunger(Player player, Map<UUID, String> healthHunger) {
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

	public static void updatePotionEffects(Player player, Map<UUID, String> potionEffects) {
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

	public static boolean hasDefaultGamemode(Player player) {
		return player.getPersistentDataContainer().has(Keys.DEFAULT_GAMEMODE.get());
	}

	public static void terminateStoryTitleTask(Player player) {
		Bukkit.getScheduler().cancelTask(Infinity.getInstance().getStartStoryTask().getOrDefault(player.getUniqueId(), -1));
		Infinity.getInstance().getStartStoryTask().remove(player.getUniqueId());
	}

	private static String getLastWorldKey(Player player, Gamemode gamemode) {
		return player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, gamemode.getWorld().getKey().asString());
	}

	public static void sendTabListFooter(Player player, Gamemode gamemode) {
		switch (gamemode) {
			case MINECRAFT -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
				.color(NamedTextColor.GRAY)
				.append(Component.newline())
				.append(Component.text("/infinity gamemode infinity").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
			);

			case INFINITY -> player.sendPlayerListFooter(Component.text("Switch gamemodes by executing")
				.color(NamedTextColor.GRAY)
				.append(Component.newline())
				.append(Component.text("/infinity gamemode minecraft").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Set your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode <gamemode>").color(NamedTextColor.YELLOW))
				.append(Component.newline())
				.append(Component.newline())
				.append(Component.text("Reset your default gamemode by executing").color(NamedTextColor.GRAY))
				.append(Component.newline())
				.append(Component.text("/infinity defaultgamemode reset").color(NamedTextColor.YELLOW))
			);
		}
	}

}
