package io.github.derechtepilz.infinity

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.infinity.items.InfinityAxe
import io.github.derechtepilz.infinity.items.InfinityItem
import io.github.derechtepilz.infinity.items.InfinityPickaxe
import io.github.derechtepilz.infinity.util.BlockTracer
import io.github.derechtepilz.infinity.util.Rarity
import io.github.derechtepilz.infinity.world.WorldManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

/*
 * Current required setup:
 * 1. Setup and start the server normally
 * 2. Delete all world folders (world setup will be done by the plugin)
 * 3. bukkit.yml -> Add worlds-section and define generator plugin
 * 4. (Optional) bukkit.yml -> disable end - this is optional because chunks are generated by the plugin resulting in no structures being present
 */
class Infinity : JavaPlugin() {

    init {
        // Register items
        Registry.Item.register(InfinityPickaxe.ITEM_ID, InfinityPickaxe(Rarity.UNCOMMON))
        for (i in 0 until InfinityAxe.VARIATIONS) {
            Registry.Item.register(InfinityAxe.ITEM_ID, InfinityAxe(Rarity.UNCOMMON, i))
        }
    }

    companion object {
        const val NAME = "infinity"
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).missingExecutorImplementationMessage("You cannot execute this command!"))

        var taskId: Int = Int.MIN_VALUE
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
            literalArgument("dev") {
                withRequirement { sender: CommandSender -> PlainTextComponentSerializer.plainText().serialize((sender as Player).displayName()) == "DerEchtePilz" }
                literalArgument("tracer") {
                    literalArgument("start") {
                        playerExecutor { player, _ ->
                            taskId = startTracer(player)
                            player.sendMessage(Component.text("BlockTracer started!").color(NamedTextColor.GREEN))
                        }
                    }
                    literalArgument("stop") {
                        playerExecutor { _, _ ->
                            Bukkit.getScheduler().cancelTask(taskId)
                            if (BlockTracer.lastBlock == null) {
                                return@playerExecutor
                            }
                            BlockTracer.lastBlock?.type = BlockTracer.replacedBlocks[BlockTracer.lastBlock?.location]!!
                            BlockTracer.replacedBlocks.clear()
                            BlockTracer.lastBlock = null
                        }
                    }
                }
            }
        }
    }

    override fun onEnable() {
        CommandAPI.onEnable()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    // TODO: Generate the Lobby as "world" and create other overworld worlds in the onEnable()
    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator {
        return when (worldName) {
            "world" -> {
                logger.info("Generating world: world[Minecraft=minecraft:overworld | Bukkit=world | Infinity:infinity:lobby]")
                WorldManager.Lobby()
            }
            "world_nether" -> {
                logger.info("Generating world: world_nether[Minecraft=minecraft:the_nether | Bukkit=world_nether | Infinity:infinity:nether]")
                WorldManager.Nether()
            }
            else -> WorldManager.Lobby()
        }
    }

    private fun startTracer(player: Player): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(this, BlockTracer(player), 0, 1)
    }

    private fun isInfinityItem(item: ItemStack): Boolean {
        return item.hasItemMeta() && item.itemMeta.persistentDataContainer.has(InfinityItem.ITEM_ID_KEY, PersistentDataType.STRING)
    }

}
