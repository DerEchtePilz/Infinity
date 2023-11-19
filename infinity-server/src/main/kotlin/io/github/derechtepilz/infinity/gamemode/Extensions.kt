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
import io.github.derechtepilz.infinity.gamemode.serializer.EffectSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.ExperienceSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.HealthHungerSerializer
import io.github.derechtepilz.infinity.gamemode.serializer.InventorySerializer
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.capitalize
import net.kyori.adventure.text.TextComponent
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
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

fun Player.updateInventory(inventories: MutableMap<UUID, String>) {
	// Serialize the enderchest and inventory of the player
	val inventoryData = InventorySerializer.serialize(this)

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
	val playerInventoryData = InventorySerializer.deserialize(playerData)
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