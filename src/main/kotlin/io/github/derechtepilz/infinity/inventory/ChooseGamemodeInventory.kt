package io.github.derechtepilz.infinity.inventory

import io.github.derechtepilz.infinity.items.minecraft.ItemBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ChooseGamemodeInventory(private val plugin: Plugin) {

    private var inventory: Inventory = Bukkit.createInventory(null, 3 * 9, Component.text("Choose gamemode..."))
    private val fillerItem: ItemStack = ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(Component.empty()).build()
    private val minecraftItem: ItemStack = ItemBuilder(Material.GRASS_BLOCK).setName(Component.text("Minecraft").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).build()
    private val infinityItem: ItemStack = ItemBuilder(Material.DRAGON_EGG).setName(Component.text("Infinity").color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false)).build()

    fun openInventory(player: Player) {
        inventory = Bukkit.createInventory(null, 3 * 9, Component.text("Choose gamemode..."))
        placeItems()
        registerEvents()
        player.openInventory(inventory)
    }

    private fun placeItems() {
        for (i in 0 until 27) {
            inventory.setItem(i, fillerItem)
            inventory.setItem(11, minecraftItem)
            inventory.setItem(15, infinityItem)
        }
    }

    private fun registerEvents() {
        val inventoryClickEvent = object : Listener {
            @EventHandler
            fun onClick(event: InventoryClickEvent) {
                event.isCancelled = true
                val player = event.whoClicked as Player
                when (event.currentItem) {
                    minecraftItem -> {
                        val blockY = Bukkit.getWorld("world")!!.getHighestBlockYAt(0, 0) + 1
                        Bukkit.dispatchCommand(player, "infinity teleport minecraft:overworld 0 $blockY 0")
                        event.inventory.close()
                    }
                    infinityItem -> {
                        Bukkit.dispatchCommand(player, "infinity teleport infinity:lobby 0 101 0")
                        event.inventory.close()
                    }
                }
            }
        }
        Bukkit.getPluginManager().registerEvents(inventoryClickEvent, plugin)
    }

}