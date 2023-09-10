package io.github.derechtepilz.infinity.util

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

private val lastWorldKey = NamespacedKey(Infinity.NAME, "lastworld")
private val lastPosX = NamespacedKey(Infinity.NAME, "lastposx")
private val lastPosY = NamespacedKey(Infinity.NAME, "lastposy")
private val lastPosZ = NamespacedKey(Infinity.NAME, "lastposz")
private val lastYaw = NamespacedKey(Infinity.NAME, "lastyaw")
private val lastPitch = NamespacedKey(Infinity.NAME, "lastpitch")

fun Player.switchGamemode(cause: TeleportCause): Boolean {
    val currentLocationX = this.location.x
    val currentLocationY = this.location.y
    val currentLocationZ = this.location.z
    val currentYaw = this.location.yaw
    val currentPitch = this.location.pitch
    val worldKey = this.world.key

    val newPosX = if (this.persistentDataContainer.has(lastPosX, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastPosX, PersistentDataType.DOUBLE)!! else 0.5
    val newPosY = if (this.persistentDataContainer.has(lastPosY, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastPosY, PersistentDataType.DOUBLE)!! else 101.0
    val newPosZ = if (this.persistentDataContainer.has(lastPosZ, PersistentDataType.DOUBLE)) this.persistentDataContainer.get(lastPosZ, PersistentDataType.DOUBLE)!! else 0.5
    val newYaw = if (this.persistentDataContainer.has(lastYaw, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastYaw, PersistentDataType.FLOAT)!! else 0.0F
    val newPitch = if (this.persistentDataContainer.has(lastPitch, PersistentDataType.FLOAT)) this.persistentDataContainer.get(lastPitch, PersistentDataType.FLOAT)!! else 0.0F

    val newLocation = when (this.getGamemode()) {
        Gamemode.MINECRAFT -> {
            val newWorldKey = NamespacedKey.fromString(
                if (this.persistentDataContainer.has(lastWorldKey, PersistentDataType.STRING))
                    this.persistentDataContainer.get(lastWorldKey, PersistentDataType.STRING)!!
                else "${Infinity.NAME}:lobby", null
            )!!
            Location(Bukkit.getWorld(newWorldKey)!!, newPosX, newPosY, newPosZ, newYaw, newPitch)
        }
        Gamemode.INFINITY -> {
            val newWorldKey = NamespacedKey.fromString(
                if (this.persistentDataContainer.has(lastWorldKey, PersistentDataType.STRING))
                    this.persistentDataContainer.get(lastWorldKey, PersistentDataType.STRING)!!
                else "minecraft:overworld", null
            )!!
            Location(Bukkit.getWorld(newWorldKey)!!, newPosX, newPosY, newPosZ, newYaw, newPitch)
        }
        Gamemode.UNKNOWN -> {
            null
        }
    } ?: return false

    // Update the persistent data container
    this.persistentDataContainer.set(lastWorldKey, PersistentDataType.STRING, worldKey.asString())
    this.persistentDataContainer.set(lastPosX, PersistentDataType.DOUBLE, currentLocationX)
    this.persistentDataContainer.set(lastPosY, PersistentDataType.DOUBLE, currentLocationY)
    this.persistentDataContainer.set(lastPosZ, PersistentDataType.DOUBLE, currentLocationZ)
    this.persistentDataContainer.set(lastYaw, PersistentDataType.FLOAT, currentYaw)
    this.persistentDataContainer.set(lastPitch, PersistentDataType.FLOAT, currentPitch)

    // Teleport the player to the newLocation
    return this.teleport(newLocation, cause)
}