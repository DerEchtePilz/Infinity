package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.experience.ExperienceSerializer
import io.github.derechtepilz.infinity.gamemode.inventory.InventorySerializer
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.capitalize
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
	return when (playerWorld.key.namespace) {
		"infinity" -> Gamemode.INFINITY
		"minecraft" -> Gamemode.MINECRAFT
		else -> Gamemode.UNKNOWN
	}
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

	this.updateLastLocationAndSwitch(cause, SwitchInfo(newWorldKey, null))
}

fun Player.switchGamemode(cause: PlayerTeleportEvent.TeleportCause, targetWorld: World, targetLocation: Location? = null) {
	val currentGamemode = this.getGamemode()
	val targetGamemode = Gamemode.getFromKey(targetWorld.key)
	if (currentGamemode == targetGamemode) {
		throw IllegalArgumentException("The targetWorld can't be in the same game mode!")
	}
	val newWorldKey = targetWorld.key

	this.updateLastLocationAndSwitch(cause, SwitchInfo(newWorldKey, targetLocation))
}

private fun Player.updateLastLocationAndSwitch(cause: PlayerTeleportEvent.TeleportCause, switchInfo: SwitchInfo) {
	// Save the player location and orientation
	val currentLocationX = this.location.x
	val currentLocationY = this.location.y
	val currentLocationZ = this.location.z
	val currentYaw = this.location.yaw
	val currentPitch = this.location.pitch
	val currentWorldKey = this.world.key

	// Load the new location and orientation
	var newPosX = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_X.get(), PersistentDataType.DOUBLE)!! else 0.5
	var newPosY = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_Y.get(), PersistentDataType.DOUBLE)!! else 101.0
	var newPosZ = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_Z.get(), PersistentDataType.DOUBLE)!! else 0.5
	val newYaw = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_YAW.get(), PersistentDataType.FLOAT)!! else 0.0F
	val newPitch = if (this.persistentDataContainer.has(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT)) this.persistentDataContainer.get(Keys.SWITCH_GAMEMODE_LAST_PITCH.get(), PersistentDataType.FLOAT)!! else 0.0F

	// Overwrite coordinates if SwitchInfo contains a non-null Location
	newPosX = if (switchInfo.targetLocation != null) switchInfo.targetLocation.x else newPosX
	newPosY = if (switchInfo.targetLocation != null) switchInfo.targetLocation.y else newPosY
	newPosZ = if (switchInfo.targetLocation != null) switchInfo.targetLocation.z else newPosZ

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

	// Update inventory and experience
	this.updateInventory(Infinity.INSTANCE.getInventoryData())
	this.updateExperience(Infinity.INSTANCE.getExperienceData())
}

fun Player.updateInventory(inventories: MutableMap<UUID, MutableList<String>>) {
	// Serialize the enderchest and inventory of the player
	val inventoryData = InventorySerializer.serializePlayerInventory(this.inventory)
	val enderChestData = InventorySerializer.serializeInventory(this.enderChest)

	// Load the player inventory and enderchest
	val playerData = if (inventories.containsKey(this.uniqueId)) inventories[this.uniqueId] else null

	if (playerData == null) {
		// Save the player inventory and enderchest
		inventories[this.uniqueId] = mutableListOf(inventoryData, enderChestData)

		// Clear the enderchest and inventory of the player
		this.inventory.clear()
		this.enderChest.clear()
		return
	}

	// Deserialize the enderchest and inventory of the player
	val inventoryContents = InventorySerializer.deserializeToInventory(playerData[0])
	val enderChestContents = InventorySerializer.deserializeToInventory(playerData[1])

	// Save the player inventory and enderchest
	inventories[this.uniqueId] = mutableListOf(inventoryData, enderChestData)

	// Clear the enderchest and inventory of the player
	this.inventory.clear()
	this.enderChest.clear()

	// Place the items in the enderchest and inventory of the player
	this.inventory.contents = inventoryContents
	this.enderChest.contents = enderChestContents
}

fun Player.updateExperience(experience: MutableMap<UUID, MutableList<String>>) {
	// Serialize the experience of the player
	val experienceLevelData = ExperienceSerializer.serializeLevel(this.level)
	val experienceProgressData = ExperienceSerializer.serializeProgress(this.exp)

	// Load the player's experience
	val playerData = if (experience.containsKey(this.uniqueId)) experience[this.uniqueId] else null

	if (playerData == null) {
		// For each player, this is only reached when switching gamemodes the first time
		// Save the player's experience
		experience[this.uniqueId] = mutableListOf(experienceLevelData, experienceProgressData)

		// Reset the player's experience
		this.level = 0
		this.exp = 0.0f
		return
	}

	// Deserialize the player's experience
	val experienceLevel = ExperienceSerializer.deserialize(playerData[0], Int::class.java) as Int
	val experienceProgress = ExperienceSerializer.deserialize(playerData[1], Float::class.java) as Float

	// Save the player's experience
	experience[this.uniqueId] = mutableListOf(experienceLevelData, experienceProgressData)

	// Reset the player's experience
	this.level = 0
	this.exp = 0.0f

	// Update the player's experience
	this.level = experienceLevel
	this.exp = experienceProgress
}

data class SwitchInfo(val targetWorld: NamespacedKey, val targetLocation: Location?)

/**********************************
 *      GAME CLASS AND SIGNS      *
 **********************************/

private val gameClassKey = NamespacedKey(Infinity.NAME, "gameclass")

fun Player.getClass(): String {
	return GameClass.valueOf(this.persistentDataContainer.get(gameClassKey, PersistentDataType.STRING)!!.uppercase()).name.lowercase().capitalize()
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