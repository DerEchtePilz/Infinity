package io.github.derechtepilz.infinity.gamemode.world

import com.google.gson.JsonParser
import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class MobSpawnPreventionHandler : Listener {

	private val loadedSpawnWorld = mutableMapOf(
		Keys.WORLD_SKY.get() to false,
		Keys.WORLD_STONE.get() to false,
		Keys.WORLD_NETHER.get() to false
	)
	private val preventedSpawnLocations: MutableMap<NamespacedKey, MutableList<Location>> = mutableMapOf()

	@EventHandler
	fun onMobSpawn(event: EntitySpawnEvent) {
		val world = event.location.world
		if (Gamemode.getFromKey(world.key) != Gamemode.INFINITY) {
			return
		}
		// If Infinity should spawn mobs, catch this here if possible
		val preventedLocations = when (world.key) {
			Keys.WORLD_SKY.get() -> {
				if (!loadedSpawnWorld[Keys.WORLD_SKY.get()]!!) {
					loadedSpawnWorld[Keys.WORLD_SKY.get()] = true
					preventedSpawnLocations[Keys.WORLD_SKY.get()] = preventLocations(deserializeStructureToLocations(Keys.WORLD_SKY, Infinity.INSTANCE.getResource("sky/sky_spawn.json")!!), false)
				}
				preventedSpawnLocations[Keys.WORLD_SKY.get()]!!
			}
			Keys.WORLD_STONE.get() -> {
				if (!loadedSpawnWorld[Keys.WORLD_STONE.get()]!!) {
					loadedSpawnWorld[Keys.WORLD_STONE.get()] = true
					preventedSpawnLocations[Keys.WORLD_STONE.get()] = preventLocations(deserializeStructureToLocations(Keys.WORLD_STONE, Infinity.INSTANCE.getResource("stone/stone_spawn.json")!!), true)
				}
				preventedSpawnLocations[Keys.WORLD_STONE.get()]!!
			}
			Keys.WORLD_NETHER.get() -> {
				if (!loadedSpawnWorld[Keys.WORLD_NETHER.get()]!!) {
					loadedSpawnWorld[Keys.WORLD_NETHER.get()] = true
					preventedSpawnLocations[Keys.WORLD_NETHER.get()] = preventLocations(deserializeStructureToLocations(Keys.WORLD_NETHER, Infinity.INSTANCE.getResource("nether/nether_spawn.json")!!), false)
				}
				preventedSpawnLocations[Keys.WORLD_NETHER.get()]!!
			}
			else -> mutableListOf()
		}
		if (preventedLocations.isEmpty()) {
			return
		}
		val spawnLocation = event.location
		preventedLocations.forEach { location ->
			if (location.isSimilar(spawnLocation)) {
				event.isCancelled = true
			}
		}
	}

	private fun deserializeStructureToLocations(worldKey: Keys, structure: InputStream): List<Location> {
		val structureLocations: MutableList<Location> = mutableListOf()
		val world = Bukkit.getWorld(worldKey.get())!!

		val reader = BufferedReader(InputStreamReader(structure))
		val builder = StringBuilder()
		var line: String?
		while (reader.readLine().also { line = it } != null) {
			builder.append(line)
		}
		reader.close()
		val structureArray = JsonParser.parseString(builder.toString()).asJsonArray
		for (i in 0 until structureArray.size()) {
			val blockLocationObject = structureArray[i].asJsonObject
			val locX = blockLocationObject.get("locX").asInt
			val locY = blockLocationObject.get("locY").asInt
			val locZ = blockLocationObject.get("locZ").asInt
			val blockLocation = Location(world, locX.toDouble(), locY.toDouble(), locZ.toDouble())
			structureLocations.add(blockLocation)
		}
		return structureLocations.toList()
	}

	private fun preventLocations(structureLocations: List<Location>, isStone: Boolean): MutableList<Location> {
		val locationIterator = structureLocations.iterator()
		val preventedLocations: MutableList<Location> = mutableListOf()
		while (locationIterator.hasNext()) {
			val currentLocation = locationIterator.next().clone()
			if (isStone) {
				if (currentLocation.blockY != 101) continue
				preventedLocations.add(currentLocation)
				continue
			}
			if (currentLocation.blockY != 100) continue
			currentLocation.y = 101.0
			preventedLocations.add(currentLocation)
		}
		return preventedLocations
	}

	/**
	 * In this context, a [Location] is similar if `x` and `z` are equal to each other and the `y` coordinate of the `other` [Location] is greater or equal to this [Location]
	 */
	private infix fun Location.isSimilar(other: Location): Boolean {
		return this.blockX == other.blockX && this.blockZ == other.blockZ && this.blockY <= other.blockY
	}

}