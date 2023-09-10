package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.experience.ExperienceSerializer
import io.github.derechtepilz.infinity.gamemode.inventory.InventorySerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType
import java.util.*

fun Player.getGamemode(): Gamemode {
    val playerWorld = this.world
    return when (playerWorld.key.namespace) {
        "infinity" -> Gamemode.INFINITY
        "minecraft" -> Gamemode.MINECRAFT
        else -> Gamemode.UNKNOWN
    }
}

private val lastMinecraftWorldKey = NamespacedKey(Infinity.NAME, "lastminecraftworld")
private val lastInfinityWorldKey = NamespacedKey(Infinity.NAME, "lastinfinityworld")
private val lastMinecraftPosX = NamespacedKey(Infinity.NAME, "lastminecraftposx")
private val lastMinecraftPosY = NamespacedKey(Infinity.NAME, "lastminecraftposy")
private val lastMinecraftPosZ = NamespacedKey(Infinity.NAME, "lastminecraftposz")
private val lastInfinityPosX = NamespacedKey(Infinity.NAME, "lastinfinityposx")
private val lastInfinityPosY = NamespacedKey(Infinity.NAME, "lastinfinityposy")
private val lastInfinityPosZ = NamespacedKey(Infinity.NAME, "lastinfinityposz")
private val lastMinecraftYaw = NamespacedKey(Infinity.NAME, "lastminecraftyaw")
private val lastMinecraftPitch = NamespacedKey(Infinity.NAME, "lastminecraftpitch")
private val lastInfinityYaw = NamespacedKey(Infinity.NAME, "lastinfinityyaw")
private val lastInfinityPitch = NamespacedKey(Infinity.NAME, "lastinfinitypitch")

fun Player.switchGamemode(cause: PlayerTeleportEvent.TeleportCause) {
    val currentLocationX = this.location.x
    val currentLocationY = this.location.y
    val currentLocationZ = this.location.z
    val currentYaw = this.location.yaw
    val currentPitch = this.location.pitch
    val worldKey = this.world.key

    val newLocation = when (this.getGamemode()) {
        Gamemode.MINECRAFT -> {
            val newWorldKey = NamespacedKey.fromString(
                if (this.persistentDataContainer.has(lastInfinityWorldKey, PersistentDataType.STRING))
                    this.persistentDataContainer.get(lastInfinityWorldKey, PersistentDataType.STRING)!!
                else "${Infinity.NAME}:lobby", null
            )!!

            val newPosX = if (this.persistentDataContainer.has(lastInfinityPosX, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastInfinityPosX, PersistentDataType.DOUBLE)!! else 0.5
            val newPosY = if (this.persistentDataContainer.has(lastInfinityPosY, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastInfinityPosY, PersistentDataType.DOUBLE)!! else 101.0
            val newPosZ = if (this.persistentDataContainer.has(lastInfinityPosZ, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastInfinityPosZ, PersistentDataType.DOUBLE)!! else 0.5
            val newYaw = if (this.persistentDataContainer.has(lastInfinityYaw, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastInfinityYaw, PersistentDataType.FLOAT)!! else 0.0F
            val newPitch = if (this.persistentDataContainer.has(lastInfinityPitch, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastInfinityPitch, PersistentDataType.FLOAT)!! else 0.0F

            // Update the persistent data container
            this.persistentDataContainer.set(lastMinecraftWorldKey, PersistentDataType.STRING, worldKey.asString())
            this.persistentDataContainer.set(lastMinecraftPosX, PersistentDataType.DOUBLE, currentLocationX)
            this.persistentDataContainer.set(lastMinecraftPosY, PersistentDataType.DOUBLE, currentLocationY)
            this.persistentDataContainer.set(lastMinecraftPosZ, PersistentDataType.DOUBLE, currentLocationZ)
            this.persistentDataContainer.set(lastMinecraftYaw, PersistentDataType.FLOAT, currentYaw)
            this.persistentDataContainer.set(lastMinecraftPitch, PersistentDataType.FLOAT, currentPitch)

            Location(Bukkit.getWorld(newWorldKey)!!, newPosX, newPosY, newPosZ, newYaw, newPitch)
        }
        Gamemode.INFINITY -> {
            val newWorldKey = NamespacedKey.fromString(
                if (this.persistentDataContainer.has(lastMinecraftWorldKey, PersistentDataType.STRING))
                    this.persistentDataContainer.get(lastMinecraftWorldKey, PersistentDataType.STRING)!!
                else "minecraft:overworld", null
            )!!

            val newPosX = if (this.persistentDataContainer.has(lastMinecraftPosX, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastMinecraftPosX, PersistentDataType.DOUBLE)!! else 0.5
            val newPosY = if (this.persistentDataContainer.has(lastMinecraftPosY, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastMinecraftPosY, PersistentDataType.DOUBLE)!! else 101.0
            val newPosZ = if (this.persistentDataContainer.has(lastMinecraftPosZ, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastMinecraftPosZ, PersistentDataType.DOUBLE)!! else 0.5
            val newYaw = if (this.persistentDataContainer.has(lastMinecraftYaw, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastMinecraftYaw, PersistentDataType.FLOAT)!! else 0.0F
            val newPitch = if (this.persistentDataContainer.has(lastMinecraftPitch, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastMinecraftPitch, PersistentDataType.FLOAT)!! else 0.0F

            // Update the persistent data container
            this.persistentDataContainer.set(lastInfinityWorldKey, PersistentDataType.STRING, worldKey.asString())
            this.persistentDataContainer.set(lastInfinityPosX, PersistentDataType.DOUBLE, currentLocationX)
            this.persistentDataContainer.set(lastInfinityPosY, PersistentDataType.DOUBLE, currentLocationY)
            this.persistentDataContainer.set(lastInfinityPosZ, PersistentDataType.DOUBLE, currentLocationZ)
            this.persistentDataContainer.set(lastInfinityYaw, PersistentDataType.FLOAT, currentYaw)
            this.persistentDataContainer.set(lastInfinityPitch, PersistentDataType.FLOAT, currentPitch)

            Location(Bukkit.getWorld(newWorldKey)!!, newPosX, newPosY, newPosZ, newYaw, newPitch)
        }
        Gamemode.UNKNOWN -> {
            null
        }
    } ?: return

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

fun Player.hasDefaultGamemode(infinity: Infinity): Boolean {
    return this.persistentDataContainer.has(infinity.getDefaultGamemode())
}