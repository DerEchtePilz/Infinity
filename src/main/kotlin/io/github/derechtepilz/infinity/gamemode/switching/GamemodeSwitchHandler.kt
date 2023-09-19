package io.github.derechtepilz.infinity.gamemode.switching

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.gamemode.ForceInfo
import io.github.derechtepilz.infinity.gamemode.Gamemode
import io.github.derechtepilz.infinity.gamemode.SwitchInfo
import io.github.derechtepilz.infinity.gamemode.getGamemode
import io.github.derechtepilz.infinity.gamemode.updateExperience
import io.github.derechtepilz.infinity.gamemode.updateHealthHunger
import io.github.derechtepilz.infinity.gamemode.updateInventory
import io.github.derechtepilz.infinity.gamemode.updatePotionEffects
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.sendTabListFooter
import io.github.derechtepilz.infinity.world.WorldCarver
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.persistence.PersistentDataType
import java.lang.IllegalArgumentException

class GamemodeSwitchHandler : Listener {

	/**
	 * The `PlayerTeleportEvent` is called **before** the player is actually teleported.
	 *
	 * `event.player.world` will be equal to `event.from.world`
	 */
	@EventHandler
	fun onTeleport(event: PlayerTeleportEvent) {
		val player = event.player
		val from = event.from

		val previousGamemode = Gamemode.getFromKey(from.world.key)
		val currentGamemode = Gamemode.getFromKey(event.to.world.key)

		// Update sign regardless of gamemode but only if worlds have changed
		if (from.world.key == event.to.world.key) {
			return
		}
		WorldCarver.LobbyCarver.setupPlayerSignsWithDelay(player)
		if (previousGamemode == currentGamemode) {
			return
		}

		player.gameMode = if (event.to.world.key == Keys.WORLD_LOBBY.get()) GameMode.ADVENTURE else GameMode.SURVIVAL
		sendTabListFooter(player, player.getGamemode())

		if (event.cause != PlayerTeleportEvent.TeleportCause.COMMAND) {
			// Safeguard, so teleporting with /execute in <dimension> run teleport x y z doesn't cause a StackOverflowError
			// since Player#teleport() calls a PlayerTeleportEvent
			// For this to work, this plugin always needs to teleport with the PLUGIN cause, NOT the COMMAND cause
			return
		}

		player.switchGamemode(PlayerTeleportEvent.TeleportCause.UNKNOWN, player.world, player.location, ForceInfo(from.world.key, from.x, from.y, from.z, from.yaw, from.pitch))
	}

}

fun Player.switchGamemode(cause: PlayerTeleportEvent.TeleportCause) {
	val newWorldKey = when (this.getGamemode()) {
		Gamemode.MINECRAFT -> NamespacedKey.fromString(getLastWorldKey(Gamemode.INFINITY))!!
		Gamemode.INFINITY -> NamespacedKey.fromString(getLastWorldKey(Gamemode.MINECRAFT))!!
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

private fun Player.getLastWorldKey(gamemode: Gamemode): String {
	return this.persistentDataContainer.getOrDefault(Keys.SWITCH_GAMEMODE_LAST_WORLD.get(), PersistentDataType.STRING, gamemode.getWorld().key.asString())
}