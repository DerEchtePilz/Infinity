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

package io.github.derechtepilz.infinity.gamemode.modification;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MobSpawnPreventionHandler implements Listener {

	private final Map<NamespacedKey, Boolean> loadedSpawnWorld = new HashMap<>();
	private final Map<NamespacedKey, List<Location>> preventedSpawnLocations = new HashMap<>();

	@EventHandler
	public void onMobSpawn(EntitySpawnEvent event) {
		World world = event.getLocation().getWorld();
		if (Gamemode.getFromKey(world.getKey()) != Gamemode.INFINITY) {
			return;
		}
		List<Location> preventedLocations;
		if (world.getKey().equals(Keys.WORLD_SKY.get())) {
			if (!loadedSpawnWorld.get(Keys.WORLD_SKY.get())) {
				loadedSpawnWorld.put(Keys.WORLD_SKY.get(), true);
				preventedSpawnLocations.put(Keys.WORLD_SKY.get(), preventLocations(deserializeStructureToLocations(Keys.WORLD_SKY, Infinity.getInstance().getResource("sky/sky_spawn.json")), false));
			}
			preventedLocations = preventedSpawnLocations.get(Keys.WORLD_SKY.get());
		} else if (world.getKey().equals(Keys.WORLD_STONE.get())) {
			if (!loadedSpawnWorld.get(Keys.WORLD_STONE.get())) {
				loadedSpawnWorld.put(Keys.WORLD_STONE.get(), true);
				preventedSpawnLocations.put(Keys.WORLD_STONE.get(), preventLocations(deserializeStructureToLocations(Keys.WORLD_STONE, Infinity.getInstance().getResource("stone/stone_spawn.json")), false));
			}
			preventedLocations = preventedSpawnLocations.get(Keys.WORLD_STONE.get());
		} else if (world.getKey().equals(Keys.WORLD_NETHER.get())) {
			if (!loadedSpawnWorld.get(Keys.WORLD_NETHER.get())) {
				loadedSpawnWorld.put(Keys.WORLD_NETHER.get(), true);
				preventedSpawnLocations.put(Keys.WORLD_NETHER.get(), preventLocations(deserializeStructureToLocations(Keys.WORLD_NETHER, Infinity.getInstance().getResource("nether/nether_spawn.json")), false));
			}
			preventedLocations = preventedSpawnLocations.get(Keys.WORLD_NETHER.get());
		} else {
			preventedLocations = new ArrayList<>();
		}
		if (preventedLocations.isEmpty()) {
			return;
		}
		Location spawnLocation = event.getLocation();
		preventedLocations.forEach(location -> {
			if (isSimilar(location, spawnLocation)) {
				event.setCancelled(true);
			}
		});
	}

	private List<Location> deserializeStructureToLocations(Keys worldKey, InputStream structure) {
		try {
			List<Location> structureLocations = new ArrayList<>();
			World world = Bukkit.getWorld(worldKey.get());

			BufferedReader reader = new BufferedReader(new InputStreamReader(structure));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();
			JsonArray structureArray = JsonParser.parseString(builder.toString()).getAsJsonArray();
			for (int i = 0; i < structureArray.size(); i++) {
				JsonObject blockLocationObject = structureArray.get(i).getAsJsonObject();
				int locX = blockLocationObject.get("locX").getAsInt();
				int locY = blockLocationObject.get("locY").getAsInt();
				int locZ = blockLocationObject.get("locZ").getAsInt();
				Location blockLocation = new Location(world, locX, locY, locZ);
				structureLocations.add(blockLocation);
			}
			return structureLocations;
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error reading resources. Mobs might be spawning in areas where they shouldn't (added by Infinity). This affects the spawns of the Infinity worlds. Please report this.");
		}
        return null;
    }

	private List<Location> preventLocations(List<Location> structureLocations, boolean isStone) {
		Iterator<Location> locationIterator = structureLocations.iterator();
		List<Location> preventedLocations = new ArrayList<>();
		while (locationIterator.hasNext()) {
			Location currentLocation = locationIterator.next().clone();
			if (isStone) {
				if (currentLocation.getBlockY() != 101) continue;
				preventedLocations.add(currentLocation);
				continue;
			}
			if (currentLocation.getBlockY() != 100) continue;
			currentLocation.setY(101.0);
			preventedLocations.add(currentLocation);
		}
		return preventedLocations;
	}

	private boolean isSimilar(Location thisLocation, Location otherLocation) {
		return thisLocation.getBlockX() == otherLocation.getBlockX()
			&& thisLocation.getBlockZ() == otherLocation.getBlockZ()
			&& thisLocation.getBlockZ() <= otherLocation.getBlockZ();
	}

}
