package io.github.derechtepilz.infinity.events

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.util.InventorySerializer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import java.util.*

class GameModeChangeListener(plugin: Infinity) : Listener {

    private val plugin: Infinity

    init {
        this.plugin = plugin
    }

    @EventHandler
    fun onGamemodeChange(event: PlayerChangedWorldEvent) {
        val previousGamemode = event.from.key.namespace
        val currentGamemode = event.player.world.key.namespace
        if (previousGamemode == currentGamemode) {
            // Do nothing, the player was changing worlds in the same gamemode
            return
        }
        val player = event.player
        updatePlayerInventory(
            player,
            if (currentGamemode == "infinity") plugin.getMinecraftInventories() else plugin.getInfinityInventories(),
            if (currentGamemode == "infinity") plugin.getInfinityInventories() else plugin.getMinecraftInventories()
        )
    }

    private fun updatePlayerInventory(player: Player, saveTo: MutableMap<UUID, MutableList<String>>, loadFrom: MutableMap<UUID, MutableList<String>>) {
        // Serialize the enderchest and inventory of the player
        val inventoryData = InventorySerializer.serializePlayerInventory(player.inventory)
        val enderChestData = InventorySerializer.serializeInventory(player.enderChest)

        // Save the player inventory and enderchest
        saveTo[player.uniqueId] = mutableListOf(inventoryData, enderChestData)

        // Clear the enderchest and inventory of the player
        player.inventory.clear()
        player.enderChest.clear()

        // Load the player inventory and enderchest
        val playerData = (if (loadFrom.containsKey(player.uniqueId)) loadFrom[player.uniqueId] else null) ?: return

        // Deserialize the enderchest and inventory of the player
        val inventoryContents = InventorySerializer.deserializeToInventory(playerData[0])
        val enderChestContents = InventorySerializer.deserializeToInventory(playerData[1])

        // Place the items in the enderchest and inventory of the player
        player.inventory.contents = inventoryContents
        player.enderChest.contents = enderChestContents

        // Remove the infinity inventories from the map for this player
        // On server shutdown, one UUID is not supposed to appear in both maps
        loadFrom.remove(player.uniqueId)
    }

    fun changeToInfinity(player: Player) {
        // Serialize the enderchest and inventory of the player
        val minecraftInventoryData = InventorySerializer.serializePlayerInventory(player.inventory)
        val minecraftEnderChestData = InventorySerializer.serializeInventory(player.enderChest)

        // Save the player inventory and enderchest to the Minecraft inventories
        plugin.getMinecraftInventories()[player.uniqueId] = mutableListOf(minecraftInventoryData, minecraftEnderChestData)

        // Clear the enderchest and inventory of the player
        player.inventory.clear()
        player.enderChest.clear()

        // Load the player inventory and enderchest from the Infinity inventories
        val infinityPlayerData = (if (plugin.getInfinityInventories().containsKey(player.uniqueId)) plugin.getInfinityInventories()[player.uniqueId] else null) ?: return

        // Deserialize the enderchest and inventory of the player
        val infinityInventoryContents = InventorySerializer.deserializeToInventory(infinityPlayerData[0])
        val infinityEnderChestContents = InventorySerializer.deserializeToInventory(infinityPlayerData[1])

        // Place the items in the enderchest and inventory of the player
        player.inventory.contents = infinityInventoryContents
        player.enderChest.contents = infinityEnderChestContents

        // Remove the infinity inventories from the map for this player
        // On server shutdown, one UUID is not supposed to appear in both maps
        plugin.getInfinityInventories().remove(player.uniqueId)
    }

    fun changeToMinecraft(player: Player) {
        // Serialize the enderchest and inventory of the player
        val infinityInventoryData = InventorySerializer.serializePlayerInventory(player.inventory)
        val infinityEnderChestData = InventorySerializer.serializeInventory(player.enderChest)

        // Save the player inventory and enderchest to the Infinity inventories
        plugin.getInfinityInventories()[player.uniqueId] = mutableListOf(infinityInventoryData, infinityEnderChestData)

        // Clear the enderchest and inventory of the player
        player.inventory.clear()
        player.enderChest.clear()

        // Load the player inventory and enderchest from the Minecraft inventories
        val minecraftPlayerData = (if (plugin.getMinecraftInventories()[player.uniqueId] != null) plugin.getMinecraftInventories()[player.uniqueId] else null) ?: return

        // Deserialize the enderchest and inventory of the player
        val minecraftInventoryContents = InventorySerializer.deserializeToInventory(minecraftPlayerData[0])
        val minecraftEnderChestContents = InventorySerializer.deserializeToInventory(minecraftPlayerData[1])

        // Place the items in the enderchest and inventory of the player
        player.inventory.contents = minecraftInventoryContents
        player.enderChest.contents = minecraftEnderChestContents

        // Remove the minecraft inventories from the map for this player
        // On server shutdown, one UUID is not supposed to appear in both maps
        plugin.getMinecraftInventories().remove(player.uniqueId)
    }

}