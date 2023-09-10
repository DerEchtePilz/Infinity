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

package io.github.derechtepilz.infinity.gamemode

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent
import io.github.derechtepilz.infinity.util.Keys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

class DeathHandler : Listener {

	private val infinityRespawns: MutableMap<UUID, Location> = mutableMapOf()
	private val minecraftRespawns: MutableMap<UUID, Location> = mutableMapOf()

	init {
		INSTANCE = this
	}

	companion object {
		lateinit var INSTANCE: DeathHandler
	}

	@EventHandler
	fun onDeath(event: PlayerDeathEvent) {
		val player = event.player
		val world = player.world
		val gamemode = Gamemode.getFromKey(world.key)
		if (gamemode == Gamemode.INFINITY) {
			player.bedSpawnLocation = infinityRespawns[player.uniqueId]
			val spawnLocation = player.bedSpawnLocation
			if (spawnLocation == null || Gamemode.getFromKey(spawnLocation.world.key) != Gamemode.INFINITY) {
				player.setBedSpawnLocation(Location(Gamemode.INFINITY.getWorld(), 0.0, 101.0, 0.0), true)
			}
			return
		}
		if (gamemode == Gamemode.MINECRAFT) {
			player.bedSpawnLocation = minecraftRespawns[player.uniqueId]
			val spawnLocation = player.bedSpawnLocation
			if (spawnLocation == null || Gamemode.getFromKey(spawnLocation.world.key) != Gamemode.MINECRAFT) {
				player.setBedSpawnLocation(Location(Gamemode.MINECRAFT.getWorld(), 0.0, Gamemode.MINECRAFT.getWorld().getHighestBlockYAt(0, 0).toDouble() + 1.0, 0.0), true)
			}
			return
		}
	}

	@EventHandler
	fun onSetSpawn(event: PlayerSetSpawnEvent) {
		val player = event.player
		when (event.cause) {
			PlayerSetSpawnEvent.Cause.BED, PlayerSetSpawnEvent.Cause.RESPAWN_ANCHOR -> {
				when (player.getGamemode()) {
					Gamemode.INFINITY -> {
						if (!infinityRespawns.containsKey(player.uniqueId) || infinityRespawns[player.uniqueId] != event.location!!) {
							event.notification = Component.text().content("Set spawn for ").append(MiniMessage.miniMessage().deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")).build()
						}
						infinityRespawns[player.uniqueId] = event.location!!
					}

					Gamemode.MINECRAFT -> {
						if (!minecraftRespawns.containsKey(player.uniqueId) || minecraftRespawns[player.uniqueId] != event.location!!) {
							event.notification = Component.text().content("Set spawn for ").append(Component.text().content("Minecraft").color(NamedTextColor.GREEN)).build()
						}
						minecraftRespawns[player.uniqueId] = event.location!!
					}

					Gamemode.UNKNOWN -> return
				}
			}
			else -> return
		}
		return
	}

	// Handle spawn points

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val player = event.player
		// Load Minecraft spawn point
		val minecraftSpawnWorld = Bukkit.getWorld(player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_MC_WORLD.get(), PersistentDataType.STRING, "world"))!!
		val minecraftSpawnX = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_MC_POS_X.get(), PersistentDataType.DOUBLE, 0.0)
		val minecraftSpawnY = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_MC_POS_Y.get(), PersistentDataType.DOUBLE, Gamemode.MINECRAFT.getWorld().getHighestBlockYAt(0, 0).toDouble())
		val minecraftSpawnZ = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_MC_POS_Z.get(), PersistentDataType.DOUBLE, 0.0)

		// Load Infinity spawn point
		val infinitySpawnWorld = Bukkit.getWorld(player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_INFINITY_WORLD.get(), PersistentDataType.STRING, "infinity/lobby"))!!
		val infinitySpawnX = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_X.get(), PersistentDataType.DOUBLE, 0.0)
		val infinitySpawnY = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get(), PersistentDataType.DOUBLE, 101.0)
		val infinitySpawnZ = player.persistentDataContainer.getOrDefault(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get(), PersistentDataType.DOUBLE, 0.0)

		val minecraftSpawnLocation = Location(minecraftSpawnWorld, minecraftSpawnX, minecraftSpawnY, minecraftSpawnZ)
		val infinitySpawnLocation = Location(infinitySpawnWorld, infinitySpawnX, infinitySpawnY, infinitySpawnZ)

		// Remove keys
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_MC_WORLD.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_MC_POS_X.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_MC_POS_Y.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_MC_POS_Z.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_INFINITY_WORLD.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_INFINITY_POS_X.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get())
		player.persistentDataContainer.remove(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get())

		minecraftRespawns[player.uniqueId] = minecraftSpawnLocation
		infinityRespawns[player.uniqueId] = infinitySpawnLocation
	}

	fun saveSpawnPointsFor(player: Player) {
		val minecraftSpawnLocation = minecraftRespawns[player.uniqueId]!!
		val infinitySpawnLocation = infinityRespawns[player.uniqueId]!!

		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_MC_WORLD.get(), PersistentDataType.STRING, minecraftSpawnLocation.world.name)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_MC_POS_X.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.x)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_MC_POS_Y.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.y)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_MC_POS_Z.get(), PersistentDataType.DOUBLE, minecraftSpawnLocation.z)

		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_INFINITY_WORLD.get(), PersistentDataType.STRING, infinitySpawnLocation.world.name)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_INFINITY_POS_X.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.x)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_INFINITY_POS_Y.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.y)
		player.persistentDataContainer.set(Keys.DEATH_RESPAWN_INFINITY_POS_Z.get(), PersistentDataType.DOUBLE, infinitySpawnLocation.z)

		minecraftRespawns.remove(player.uniqueId)
		infinityRespawns.remove(player.uniqueId)
	}

}