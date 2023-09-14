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

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.gameclass.GameClass
import io.github.derechtepilz.infinity.gamemode.separation.InventorySeparator
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.capitalize
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType
import java.lang.IllegalArgumentException
import java.util.*

/***********************
 *      GAME MODE      *
 ***********************/

fun Player.getGamemode(): Gamemode {
	val playerWorld = this.world
	return Gamemode.getFromKey(playerWorld.key)
	/*return when (playerWorld.key.namespace) {
		"infinity" -> Gamemode.INFINITY
		"minecraft" -> Gamemode.MINECRAFT
		else -> Gamemode.UNKNOWN
	}*/
}

fun Player.hasDefaultGamemode(): Boolean {
	return this.persistentDataContainer.has(Keys.DEFAULT_GAMEMODE.get())
}

/************************************
 *      SWITCH GAME MODE WORLD      *
 ************************************/

fun Player.switchGamemode(cause: PlayerTeleportEvent.TeleportCause) {
	val newWorldKey = when (this.getGamemode()) {
		Gamemode.MINECRAFT -> NamespacedKey.fromString(
			if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING))
				this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING)!!
			else "${Infinity.NAME}:lobby", null
		)!!

		Gamemode.INFINITY -> NamespacedKey.fromString(
			if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING))
				this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING)!!
			else "minecraft:overworld", null
		)!!

		Gamemode.UNKNOWN -> Gamemode.UNKNOWN.getWorld().key
	}

	this.updateLastLocationAndSwitch(cause, SwitchInfo(null, newWorldKey, null))
}

fun Player.switchGamemode(cause: PlayerTeleportEvent.TeleportCause, targetWorld: World, targetLocation: Location? = null, force: ForceInfo? = null) {
	val currentGamemode = this.getGamemode()
	val targetGamemode = Gamemode.getFromKey(targetWorld.key)
	if (currentGamemode == targetGamemode && force == null) {
		throw IllegalArgumentException("The targetWorld can't be in the same game mode!")
	}
	val newWorldKey = targetWorld.key

	this.updateLastLocationAndSwitch(cause, SwitchInfo(force, newWorldKey, targetLocation))
}

private fun Player.updateLastLocationAndSwitch(cause: PlayerTeleportEvent.TeleportCause, switchInfo: SwitchInfo) {
	// Save the player location and orientation
	val currentLocationX = if (switchInfo.force != null) switchInfo.force.x else this.location.x
	val currentLocationY = if (switchInfo.force != null) switchInfo.force.y else this.location.y
	val currentLocationZ = if (switchInfo.force != null) switchInfo.force.z else this.location.z
	val currentYaw = if (switchInfo.force != null) switchInfo.force.yaw else this.location.yaw
	val currentPitch = if (switchInfo.force != null) switchInfo.force.pitch else this.location.pitch
	val currentWorldKey = if (switchInfo.force != null) switchInfo.force.previousWorld else this.world.key

	// Load the new location and orientation
	var newPosX = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE)!! else 0.5
	var newPosY = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE)!! else 101.0
	var newPosZ = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE)!! else 0.5
	var newYaw = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT)!! else 0.0F
	var newPitch = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT)!! else 0.0F

	// Overwrite coordinates and rotation if SwitchInfo contains a non-null Location
	newPosX = if (switchInfo.targetLocation != null) switchInfo.targetLocation.x else newPosX
	newPosY = if (switchInfo.targetLocation != null) switchInfo.targetLocation.y else newPosY
	newPosZ = if (switchInfo.targetLocation != null) switchInfo.targetLocation.z else newPosZ
	newYaw = if (switchInfo.targetLocation != null) switchInfo.targetLocation.yaw else newYaw
	newPitch = if (switchInfo.targetLocation != null) switchInfo.targetLocation.pitch else newPitch

	// Overwrite coordinates and rotation if target world is infinity:lobby
	newPosX = if (switchInfo.targetWorld == Keys.WORLD_LOBBY.get()) 0.5 else newPosX
	newPosY = if (switchInfo.targetWorld == Keys.WORLD_LOBBY.get()) 101.0 else newPosY
	newPosZ = if (switchInfo.targetWorld == Keys.WORLD_LOBBY.get()) 0.5 else newPosZ
	newYaw = if (switchInfo.targetWorld == Keys.WORLD_LOBBY.get()) 0.0f else newYaw
	newPitch = if (switchInfo.targetWorld == Keys.WORLD_LOBBY.get()) 0.0f else newPitch

	// Update the persistent data container
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, currentWorldKey.asString())
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE, currentLocationX)
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE, currentLocationY)
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE, currentLocationZ)
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT, currentYaw)
	this.persistentDataContainer.set(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT, currentPitch)

	val newLocation = Location(Bukkit.getWorld(switchInfo.targetWorld)!!, newPosX, newPosY, newPosZ, newYaw, newPitch)

	// Teleport the player to the newLocation
	this.teleport(newLocation, cause)

	// Update inventory, experience, health and hunger
	this.updateInventory(Infinity.INSTANCE.getInventoryData())
	this.updateExperience(Infinity.INSTANCE.getExperienceData())
	this.updateHealthHunger(Infinity.INSTANCE.getHealthHungerData())
	this.updatePotionEffects(Infinity.INSTANCE.getPotionEffectData())
}

fun Player.updateInventory(inventories: MutableMap<UUID, String>) {
	// Serialize the enderchest and inventory of the player
	val inventoryData = InventorySeparator.serialize(this.uniqueId)

	// Load the player inventory and enderchest
	val playerData = if (inventories.containsKey(this.uniqueId)) inventories[this.uniqueId] else null

	if (playerData == null) {
		// Save the player inventory and enderchest
		inventories[this.uniqueId] = inventoryData

		// Clear the enderchest and inventory of the player
		this.inventory.clear()
		this.enderChest.clear()
		return
	}

	// Deserialize the enderchest and inventory of the player
	val playerInventoryData = InventorySeparator.deserialize(playerData)
	val inventoryContents = playerInventoryData[0]
	val enderChestContents = playerInventoryData[1]

	// Save the player inventory and enderchest
	inventories[this.uniqueId] = inventoryData

	// Clear the enderchest and inventory of the player
	this.inventory.clear()
	this.enderChest.clear()

	// Place the items in the enderchest and inventory of the player
	this.inventory.contents = inventoryContents
	this.enderChest.contents = enderChestContents
}

fun Player.updateExperience(experience: MutableMap<UUID, String>) {
	// Serialize the experience of the player
	val experienceData = ExperienceSerializer.serialize(this)

	// Load the player's experience
	val playerData = if (experience.containsKey(this.uniqueId)) experience[this.uniqueId] else null

	if (playerData == null) {
		// For each player, this is only reached when switching gamemodes the first time
		// Save the player's experience
		experience[this.uniqueId] = experienceData

		// Reset the player's experience
		this.level = 0
		this.exp = 0.0f
		return
	}

	// Deserialize the player's experience
	val playerExperienceData = ExperienceSerializer.deserialize(playerData)

	// Save the player's experience
	experience[this.uniqueId] = experienceData

	// Reset the player's experience
	this.level = 0
	this.exp = 0.0f

	// Update the player's experience
	this.level = playerExperienceData[0] as Int
	this.exp = playerExperienceData[1] as Float
}

fun Player.updateHealthHunger(healthHunger: MutableMap<UUID, String>) {
	// Serialize the health and hunger of the player
	val healthHungerData = HealthHungerSerializer.serialize(this)

	// Load the player's health and hunger
	val playerData = if (healthHunger.containsKey(this.uniqueId)) healthHunger[this.uniqueId] else null

	if (playerData == null) {
		// For each player, this is only reached when switching gamemodes the first time
		// Save the player's health and hunger
		healthHunger[this.uniqueId] = healthHungerData

		// Reset the player's health and hunger
		this.health = 20.0
		this.foodLevel = 20
		this.saturation = 20.0f
		return
	}

	// Deserialize the player's health and hunger
	val healthHungerList = HealthHungerSerializer.deserialize(playerData)

	// Save the player's health and hunger
	healthHunger[this.uniqueId] = healthHungerData

	// Reset the player's health and hunger
	this.health = 20.0
	this.foodLevel = 20
	this.saturation = 20.0f

	// Update the player's health and hunger
	this.health = healthHungerList[0] as Double
	this.foodLevel = healthHungerList[1] as Int
	this.saturation = healthHungerList[2] as Float
}

fun Player.updatePotionEffects(potionEffects: MutableMap<UUID, String>) {
	// Serialize the potion effects of the player
	val potionEffectData = EffectSerializer.serialize(this)

	// Load the player's potion effects
	val playerData = if (potionEffects.containsKey(this.uniqueId)) potionEffects[this.uniqueId] else null

	if (playerData == null) {
		// For each player, this is only reached when switching gamemodes the first time
		// Save the player's potion effects
		potionEffects[this.uniqueId] = potionEffectData

		// Reset the player's potion effects
		this.clearActivePotionEffects()
		return
	}

	// Deserialize the player's potion effects
	val potionEffectList = EffectSerializer.deserialize(playerData)

	// Save the player's potion effects
	potionEffects[this.uniqueId] = potionEffectData

	// Reset the player's potion effects
	this.clearActivePotionEffects()

	// Update the player's potion effects
	this.addPotionEffects(potionEffectList)
}

data class SwitchInfo(val force: ForceInfo?, val targetWorld: NamespacedKey, val targetLocation: Location?)
data class ForceInfo(val previousWorld: NamespacedKey, val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float)

/**********************************
 *      GAME CLASS AND SIGNS      *
 **********************************/

private val gameClassKey = NamespacedKey(Infinity.NAME, "gameclass")

fun Player.getClass(): TextComponent {
	return GameClass.valueOf(this.persistentDataContainer.get(gameClassKey, PersistentDataType.STRING)!!.uppercase()).get()
}

fun Player.setClass(gameClass: GameClass) {
	this.persistentDataContainer.set(gameClassKey, PersistentDataType.STRING, gameClass.name.lowercase())
}

@Suppress("ReplaceManualRangeWithIndicesCalls")
fun Enum<*>.normalize(): String {
	val wordArray = this.name.lowercase().split("_")
	var normalizedValue = ""
	for (i in 0 until wordArray.size) {
		normalizedValue = "$normalizedValue ${if (i == 0) wordArray[i].capitalize() else wordArray[i]}"
	}
	return normalizedValue.trim()
}