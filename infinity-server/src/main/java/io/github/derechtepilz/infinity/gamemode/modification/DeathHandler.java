package io.github.derechtepilz.infinity.gamemode.modification;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.util.PlayerUtil;
import io.github.derechtepilz.infinity.world.WorldCarver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathHandler implements Listener {

	private final Map<UUID, Location> infinityRespawns = new HashMap<>();
	private final Map<UUID, Location> minecraftRespawns = new HashMap<>();

	public static DeathHandler INSTANCE;

	public DeathHandler() {
		INSTANCE = this;
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		World world = player.getWorld();
		Gamemode gamemode = Gamemode.getFromKey(world.getKey());
		if (gamemode == Gamemode.INFINITY) {
			player.setBedSpawnLocation(infinityRespawns.get(player.getUniqueId()));
			Location spawnLocation = player.getBedSpawnLocation();
			if (spawnLocation == null || Gamemode.getFromKey(spawnLocation.getWorld().getKey()) != Gamemode.INFINITY) {
				player.setBedSpawnLocation(new Location(Gamemode.INFINITY.getWorld(), 0.0, 101.0, 0.0), true);
			}
			return;
		}
		if (gamemode == Gamemode.MINECRAFT) {
			player.setBedSpawnLocation(minecraftRespawns.get(player.getUniqueId()));
			Location spawnLocation = player.getBedSpawnLocation();
			if (spawnLocation == null || Gamemode.getFromKey(spawnLocation.getWorld().getKey()) != Gamemode.MINECRAFT) {
				player.setBedSpawnLocation(new Location(Gamemode.MINECRAFT.getWorld(), 0.0, Gamemode.MINECRAFT.getWorld().getHighestBlockYAt(0, 0) + 1.0, 0.0), true);
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		WorldCarver.LobbyCarver.setupPlayerSignWithDelay(player);
	}

	@EventHandler
	public void onSetSpawn(PlayerSetSpawnEvent event) {
		Player player = event.getPlayer();
		switch (event.getCause()) {
			case BED, RESPAWN_ANCHOR -> {
				switch (PlayerUtil.getGamemode(player)) {
					case INFINITY -> {
						if (!infinityRespawns.containsKey(player.getUniqueId()) || !infinityRespawns.get(player.getUniqueId()).equals(event.getLocation())) {
							event.setNotification(Component.text().content("Set spawn for ").append(Infinity.getInstance().getInfinityComponent()).build());
						}
						infinityRespawns.put(player.getUniqueId(), event.getLocation());
					}
					case MINECRAFT -> {
						if (!minecraftRespawns.containsKey(player.getUniqueId()) || !minecraftRespawns.get(player.getUniqueId()).equals(event.getLocation())) {
							event.setNotification(Component.text().content("Set spawn for ").append(Component.text().content("Minecraft").color(NamedTextColor.GREEN)).build());
						}
						minecraftRespawns.put(player.getUniqueId(), event.getLocation());
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Load Minecraft spawn point
		World minecraftSpawnWorld = Bukkit.getWorld(player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_MC_WORLD.get(), PersistentDataType.STRING, "world"));
		double minecraftSpawnX = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_MC_POS_X.get(), PersistentDataType.DOUBLE, 0.0);
		double minecraftSpawnY = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_MC_POS_Y.get(), PersistentDataType.DOUBLE, (double) Gamemode.MINECRAFT.getWorld().getHighestBlockYAt(0, 0));
		double minecraftSpawnZ = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_MC_POS_Z.get(), PersistentDataType.DOUBLE, 0.0);

		// Load Infinity spawn point
		World infinitySpawnWorld = Bukkit.getWorld(player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_INFINITY_WORLD.get(), PersistentDataType.STRING, "infinity/lobby"));
		double infinitySpawnX = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_X.get(), PersistentDataType.DOUBLE, 0.0);
		double infinitySpawnY = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get(), PersistentDataType.DOUBLE, 101.0);
		double infinitySpawnZ = player.getPersistentDataContainer().getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get(), PersistentDataType.DOUBLE, 0.0);

		Location minecraftSpawnLocation = new Location(minecraftSpawnWorld, minecraftSpawnX, minecraftSpawnY, minecraftSpawnZ);
		Location infinitySpawnLocation = new Location(infinitySpawnWorld, infinitySpawnX, infinitySpawnY, infinitySpawnZ);

		// Remove keys
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_MC_WORLD.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_MC_POS_X.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_MC_POS_Y.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_MC_POS_Z.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_INFINITY_WORLD.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_INFINITY_POS_X.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get());
		player.getPersistentDataContainer().remove(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get());

		minecraftRespawns.put(player.getUniqueId(), minecraftSpawnLocation);
		infinityRespawns.put(player.getUniqueId(), infinitySpawnLocation);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		saveSpawnPointsFor(player);
	}

	public void saveSpawnPointsFor(Player player) {
		Location minecraftSpawnLocation = minecraftRespawns.get(player.getUniqueId());
		Location infinitySpawnLocation = infinityRespawns.get(player.getUniqueId());

		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_MC_WORLD.get(), PersistentDataType.STRING, minecraftSpawnLocation.getWorld().getName());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_MC_POS_X.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.x());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_MC_POS_Y.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.y());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_MC_POS_Z.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.z());

		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_INFINITY_WORLD.get(), PersistentDataType.STRING, infinitySpawnLocation.getWorld().getName());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_INFINITY_POS_X.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.x());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.y());
		player.getPersistentDataContainer().set(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.z());

		minecraftRespawns.remove(player.getUniqueId());
		infinityRespawns.remove(player.getUniqueId());
	}

}
