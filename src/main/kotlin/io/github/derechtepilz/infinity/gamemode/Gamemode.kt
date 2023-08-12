package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause
import org.bukkit.persistence.PersistentDataType

enum class Gamemode(private val defaultWorld: World) {

    MINECRAFT(Bukkit.getWorld("world")!!),
    INFINITY(Bukkit.getWorld(NamespacedKey(Infinity.NAME, "lobby"))!!),
    UNKNOWN(Bukkit.getWorld("world")!!);

    fun getWorld(): World {
        return this.defaultWorld
    }

}

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

fun Player.switchGamemode(cause: TeleportCause): Boolean {
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
    } ?: return false

    // Teleport the player to the newLocation
    return this.teleport(newLocation, cause)
}