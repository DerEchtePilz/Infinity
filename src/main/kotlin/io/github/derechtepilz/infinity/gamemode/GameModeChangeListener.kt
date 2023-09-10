package io.github.derechtepilz.infinity.gamemode

import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.event.GameModeChangeEvent
import io.github.derechtepilz.infinity.util.AdvancementSerializer
import io.github.derechtepilz.infinity.util.InventorySerializer
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
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
    fun onWorldChange(event: PlayerChangedWorldEvent) {
        Bukkit.getPluginManager().callEvent(GameModeChangeEvent(event.player, Gamemode.getFromKey(event.from.key), Gamemode.getFromKey(event.player.world.key)))
    }

    @EventHandler
    fun onGamemodeChange(event: GameModeChangeEvent) {
        val previousGamemode = event.previousGamemode
        val currentGamemode = event.newGamemode
        if (previousGamemode == currentGamemode) {
            // Do nothing, the player was changing worlds in the same gamemode
            return
        }
        val player = event.player
        updatePlayerInventory(
            player,
            if (currentGamemode == Gamemode.INFINITY) plugin.getMinecraftInventories() else plugin.getInfinityInventories(),
            if (currentGamemode == Gamemode.INFINITY) plugin.getInfinityInventories() else plugin.getMinecraftInventories()
        )
        val world = player.world
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        updatePlayerAdvancements(
            player,
            if (currentGamemode == Gamemode.INFINITY) plugin.getMinecraftAdvancements() else plugin.getInfinityAdvancements(),
            if (currentGamemode == Gamemode.INFINITY) plugin.getInfinityAdvancements() else plugin.getMinecraftAdvancements()
        )
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
        sendTabListFooter(player, player.getGamemode())
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

        // Remove the inventories from the map for this player
        // On server shutdown, one UUID is not supposed to appear in both maps
        loadFrom.remove(player.uniqueId)
    }

    @Suppress("UNCHECKED_CAST")
    private fun updatePlayerAdvancements(player: Player, saveTo: MutableMap<UUID, MutableList<String>>, loadFrom: MutableMap<UUID, MutableList<String>>) {
        // Serialize the advancements
        val advancementData = AdvancementSerializer.serializeAdvancements(player)

        // Save the advancements
        saveTo[player.uniqueId] = mutableListOf(advancementData)

        // Clear the player's advancements
        player.clearAdvancements()

        // Load the advancements
        val playerData = (if (loadFrom.containsKey(player.uniqueId)) loadFrom[player.uniqueId] else null) ?: return

        // Deserialize advancements
        val advancementPlayerData = AdvancementSerializer.deserializeAdvancements(playerData[0])

        // Extract advancement information
        val advancementsDone: MutableList<NamespacedKey> = advancementPlayerData[0] as MutableList<NamespacedKey>
        val criteriaDone: MutableMap<NamespacedKey, MutableList<String>> = advancementPlayerData[1] as MutableMap<NamespacedKey, MutableList<String>>

        // Grant player advancements
        player.grantAdvancements(advancementsDone, criteriaDone)

        // Remove the advancements from the map for this player
        // On server shutdown, one UUID is not supposed to appear in both maps
        loadFrom.remove(player.uniqueId)
    }

    private fun Player.clearAdvancements() {
        val advancementIterator = Bukkit.advancementIterator()
        while (advancementIterator.hasNext()) {
            val advancement = advancementIterator.next()
            val progress = this.getAdvancementProgress(advancement)
            for (criteria in progress.awardedCriteria) {
                progress.revokeCriteria(criteria)
            }
        }
    }

    private fun Player.grantAdvancements(advancementsDone: MutableList<NamespacedKey>, criteriaDone: MutableMap<NamespacedKey, MutableList<String>>) {
        for (key in advancementsDone) {
            val advancement = Bukkit.getAdvancement(key)!!
            for (criteria in advancement.criteria) {
                this.getAdvancementProgress(advancement).awardCriteria(criteria)
            }
        }
        for (key in criteriaDone.keys) {
            val advancement = Bukkit.getAdvancement(key)!!
            for (criteria in criteriaDone[key]!!) {
                this.getAdvancementProgress(advancement).awardCriteria(criteria)
            }
        }
    }

}