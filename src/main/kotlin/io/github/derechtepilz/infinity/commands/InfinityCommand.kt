package io.github.derechtepilz.infinity.commands

import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.Registry
import io.github.derechtepilz.infinity.items.InfinityItem
import io.github.derechtepilz.infinity.util.BlockTracer
import io.github.derechtepilz.infinity.util.Rarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object InfinityCommand {

    fun register() {
        commandTree("infinity", { commandSender: CommandSender -> commandSender.isOp }) {
            literalArgument("give") {
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
        }
    }

    private fun isInfinityItem(item: ItemStack): Boolean {
        return item.hasItemMeta() && item.itemMeta.persistentDataContainer.has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)
    }

}