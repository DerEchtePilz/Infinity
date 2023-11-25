package io.github.derechtepilz.infinity.gamemode.states;

import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public enum GamemodeState {

	INFINITY,
	MINECRAFT;

	public Location loadFor(Player player) {
		if (player.getPersistentDataContainer().has(Keys.GAMEMODE_SWITCH_ENABLED.get(), PersistentDataType.BOOLEAN)) {
			Infinity.getInstance().getLogger().info(PlainTextComponentSerializer.plainText().serialize(player.name()) + " tried to switch gamemodes while that is currently disabled for this player!");
			Infinity.getInstance().getLogger().info("Logging, so server admins can give that feedback to players if needed.");
			return player.getLocation();
		}
		switch (this) {
			case INFINITY -> {
				// 1. Check player gamemode using their current world
				if (PlayerUtil.getGamemode(player) == Gamemode.INFINITY) {
					return player.getLocation();
				}

				// 2. Teleport the player to their last location in Infinity
				Location location = getLastLocation(player);
				if (location.getWorld().equals(Gamemode.INFINITY.getWorld())) {
					location = setSpawnLocation(location);
				}
				return switchGamemode(player, location);
			}
			case MINECRAFT -> {
				// 1. Check player gamemode using their current world
				if (PlayerUtil.getGamemode(player) == Gamemode.MINECRAFT) {
					return player.getLocation();
				}

				// 2. Teleport the player to their last location in Minecraft
				Location location = getLastLocation(player);
				return switchGamemode(player, location);
			}
		}
        return null;
    }

	private Location switchGamemode(Player player, Location location) {
		setLastLocation(player);
		player.teleport(location);

		// 3. Load inventories, levels, potion effects, health and hunger
		PlayerUtil.updateInventory(player, Infinity.getInstance().getInventoryData());
		PlayerUtil.updateExperience(player, Infinity.getInstance().getExperienceData());
		PlayerUtil.updateHealthHunger(player, Infinity.getInstance().getHealthHungerData());
		PlayerUtil.updatePotionEffects(player, Infinity.getInstance().getPotionEffectData());
		return location;
	}

	private Location getLastLocation(Player player) {
		String newWorldKey = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, PlayerUtil.getGamemode(player).getOpposite().getWorld().getKey().asString());
		double newPosX = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE, 0.5);
		double newPosY = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE, 101.0);
		double newPosZ = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE, 0.5);
		float newYaw = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT, 0.0F);
		float newPitch = player.getPersistentDataContainer().getOrDefault(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT, 0.0F);
		return new Location(Bukkit.getWorld(NamespacedKey.fromString(newWorldKey)), newPosX, newPosY, newPosZ, newYaw, newPitch);
	}

	private void setLastLocation(Player player) {
		double currentLocationX = player.getLocation().x();
		double currentLocationY = player.getLocation().y();
		double currentLocationZ = player.getLocation().z();
		float currentYaw = player.getLocation().getYaw();
		float currentPitch = player.getLocation().getPitch();
		NamespacedKey currentWorldKey = player.getWorld().getKey();

		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, currentWorldKey.asString());
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE, currentLocationX);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE, currentLocationY);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE, currentLocationZ);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT, currentYaw);
		player.getPersistentDataContainer().set(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT, currentPitch);
	}

	private Location setSpawnLocation(Location original) {
		double newPosX = 0.5;
		double newPosY = 101.0;
		double newPosZ = 0.5;
		float newYaw = 0.0f;
		float newPitch = 0.0f;
		return new Location(original.getWorld(), newPosX, newPosY, newPosZ, newYaw, newPitch);
	}
}
