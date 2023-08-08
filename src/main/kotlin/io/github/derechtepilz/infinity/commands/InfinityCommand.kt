package io.github.derechtepilz.infinity.commands

import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.Registry
import io.github.derechtepilz.infinity.items.InfinityItem
import io.github.derechtepilz.infinity.util.InventorySerializer
import io.github.derechtepilz.infinity.util.Rarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@Suppress("UNCHECKED_CAST")
object InfinityCommand {

    fun register(plugin: Infinity) {
        commandTree("infinity") {
            literalArgument("give") {
                withRequirement { sender: CommandSender -> sender.isOp }
                literalArgument("*") {
                    playerExecutor { player, _ ->
                        player.inventory.addItem(*Registry.Item.getAllItems())
                    }
                }
                multiLiteralArgument(nodeName = "itemId", *Registry.Item.getItemIds()) {
                    integerArgument("variation", 0, 100, true) {
                        playerExecutor { player, args ->
                            val itemId = args.getUnchecked<String>("itemId")!!
                            val variationId = args.getUnchecked<Int>("variation")!!
                            val item = Registry.Item.getItem(itemId, args.getOrDefault("variation", -1) as Int)
                            if (item == null) {
                                player.sendMessage(Component.text("The ")
                                    .color(NamedTextColor.RED)
                                    .append(Component.translatable("infinity.$itemId").color(NamedTextColor.RED).hoverEvent(Registry.Item.getItem(itemId, 0)!!))
                                    .append(Component.text(" does not have a variation id $variationId"))
                                )
                                return@playerExecutor
                            }
                            player.inventory.addItem(item)
                        }
                    }
                }
            }
            literalArgument("changerarity") {
                withRequirement { sender: CommandSender -> sender.isOp }
                multiLiteralArgument(nodeName = "rarity", *Rarity.values().map { rarity -> rarity.name.lowercase() }.toTypedArray()) {
                    playerExecutor { player, args ->
                        val rarity = Rarity.valueOf((args["rarity"] as String).uppercase())
                        val heldItem = player.inventory.itemInMainHand
                        val selectedSlot = player.inventory.heldItemSlot
                        if (!isInfinityItem(heldItem)) {
                            player.sendMessage(Component.text("Cannot change rarity for item ")
                                .color(NamedTextColor.RED)
                                .append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
                                .append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
                            )
                            return@playerExecutor
                        }
                        val itemId = heldItem.itemMeta.persistentDataContainer[InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING]!!
                        val variation = heldItem.itemMeta.persistentDataContainer[InfinityItem.VARIATION_ID, PersistentDataType.INTEGER]!!
                        val item = Registry.Item.getItem(itemId, variation)
                        player.inventory.setItem(selectedSlot, item!!.updateRarityTo(rarity))
                    }
                }
            }
            literalArgument("upgrade") {
                withRequirement { sender: CommandSender -> sender.isOp }
                playerExecutor { player, _ ->
                    // Check if there's an InfinityItem in the main hand
                    val heldItem = player.inventory.itemInMainHand
                    val selectedSlot = player.inventory.heldItemSlot
                    if (!isInfinityItem(heldItem)) {
                        player.sendMessage(Component.text("Cannot upgrade rarity for item ")
                            .color(NamedTextColor.RED)
                            .append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
                            .append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
                        )
                        return@playerExecutor
                    }
                    // Upgrade the item
                    val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
                    val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
                    val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

                    val basicItem = Registry.Item.getItem(itemId, variationId)!!
                    val upgradedItem = basicItem.upgradeItem(Rarity.valueOf(currentRarity.uppercase()))
                    player.inventory.setItem(selectedSlot, upgradedItem)
                    player.sendMessage(Component.text("Your ")
                        .color(NamedTextColor.GREEN)
                        .append(heldItem.displayName().hoverEvent(heldItem))
                        .append(Component.text(" has been upgraded to an ").color(NamedTextColor.GREEN))
                        .append(Component.text("[").color(NamedTextColor.WHITE)
                            .hoverEvent(upgradedItem)
                            .append(upgradedItem.displayName())
                            .append(Component.text("]").color(NamedTextColor.WHITE))
                        )
                        .append(Component.text("!").color(NamedTextColor.GREEN))
                    )
                }
            }
            literalArgument("downgrade") {
                withRequirement { sender: CommandSender -> sender.isOp }
                playerExecutor { player, _ ->
                    // Check if there's an InfinityItem in the main hand
                    val heldItem = player.inventory.itemInMainHand
                    val selectedSlot = player.inventory.heldItemSlot
                    if (!isInfinityItem(heldItem)) {
                        player.sendMessage(Component.text("Cannot downgrade rarity for item ")
                            .color(NamedTextColor.RED)
                            .append(Component.translatable(heldItem).color(NamedTextColor.RED).hoverEvent(heldItem))
                            .append(Component.text(" because it isn't an InfinityItem!").color(NamedTextColor.RED))
                        )
                        return@playerExecutor
                    }
                    // Downgrade the item
                    val itemId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)!!
                    val currentRarity = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.RARITY_KEY, PersistentDataType.STRING)!!
                    val variationId = heldItem.itemMeta.persistentDataContainer.get(InfinityItem.VARIATION_ID, PersistentDataType.INTEGER)!!

                    val basicItem = Registry.Item.getItem(itemId, variationId)!!
                    val downgradedItem = basicItem.downgradeItem(Rarity.valueOf(currentRarity.uppercase()))
                    player.inventory.setItem(selectedSlot, downgradedItem)
                    player.sendMessage(Component.text("Your ")
                        .color(NamedTextColor.RED)
                        .append(heldItem.displayName().hoverEvent(heldItem))
                        .append(Component.text(" has been downgraded to an ").color(NamedTextColor.RED))
                        .append(Component.text("[").color(NamedTextColor.WHITE)
                            .hoverEvent(downgradedItem)
                            .append(downgradedItem.displayName())
                            .append(Component.text("]").color(NamedTextColor.WHITE))
                        )
                        .append(Component.text("!").color(NamedTextColor.RED))
                    )
                }
            }
            literalArgument("teleport") {
                withRequirement { sender: CommandSender -> sender.isOp }
                worldArgument("world") {
                    playerExecutor { player, args ->
                        val targetWorld = args["world"] as World
                        player.teleport(Location(targetWorld, player.location.x, player.location.y, player.location.z))
                    }
                    locationArgument("location") {
                        playerExecutor { player, args ->
                            val targetWorld = args["world"] as World
                            val targetLocation = args["location"] as Location
                            player.teleport(Location(targetWorld, targetLocation.x, targetLocation.y, targetLocation.z))
                        }
                    }
                }
            }
            literalArgument("gamemode") {
                multiLiteralArgument(nodeName = "gamemode", "infinity", "minecraft", "test", "recover") {
                    playerExecutor { player, args ->
                        val blockY = Bukkit.getWorld("world")!!.getHighestBlockYAt(0, 0) + 1
                        when (args["gamemode"] as String) {
                            "infinity" -> player.teleport(Location(Bukkit.getWorld(plugin.getLobbyKey())!!, 0.5, 101.0, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN)
                            "minecraft" -> player.teleport(Location(Bukkit.getWorld("world")!!, 0.5, blockY.toDouble(), 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN)
                            "test" -> {
                                // Serialize player inventory
                                val playerInventory = InventorySerializer.serializePlayerInventory(player.inventory)
                                println("PlayerInventory: $playerInventory")

                                // Serialize player enderchest
                                val playerEnderChest = InventorySerializer.serializeInventory(player.enderChest)
                                println("PlayerEnderChest: $playerEnderChest")

                                // Save inventory
                                plugin.getInfinityInventories()[player.uniqueId] = mutableListOf(playerInventory, playerEnderChest)
                                player.inventory.clear()
                                player.enderChest.clear()
                            }
                            "recover" -> {
                                val playerItemInformation = plugin.getInfinityInventories()[player.uniqueId]!!

                                val playerInventoryData = playerItemInformation[0] as String
                                val playerEnderChestData = playerItemInformation[1] as String

                                val playerInventoryContents = InventorySerializer.deserializeToInventory(playerInventoryData)
                                val playerEnderChest = InventorySerializer.deserializeToInventory(playerEnderChestData)

                                player.inventory.contents = playerInventoryContents
                                player.enderChest.contents = playerEnderChest
                            }
                        }
                    }
                }
            }
            literalArgument("defaultgamemode") {
                multiLiteralArgument(nodeName = "gamemode", "infinity", "minecraft") {
                    playerExecutor { player, args ->
                        TODO("Store the chosen option in the player and use that option when the player joins the server to make them join the correct gamemode")
                    }
                }
            }
        }
    }

    private fun isInfinityItem(item: ItemStack): Boolean {
        return item.hasItemMeta() && item.itemMeta.persistentDataContainer.has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)
    }

}