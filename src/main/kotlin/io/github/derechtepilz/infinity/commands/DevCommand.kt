package io.github.derechtepilz.infinity.commands

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentException
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.LocationType
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.infinity.Infinity
import io.github.derechtepilz.infinity.events.BlockScanner
import io.github.derechtepilz.infinity.util.BlockTracer
import io.github.derechtepilz.infinity.world.StructureEditor
import io.github.derechtepilz.infinity.world.StructureLoader
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.Bisected
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Named

class DevCommand(private val infinity: Infinity) {

    val placedBlocks: MutableList<JsonObject> = mutableListOf()

    fun register() {
        var taskId: Int = Int.MIN_VALUE
        commandTree("dev") {
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
            literalArgument("structure") {
                literalArgument("start") {
                    playerExecutor { player, _ ->
                        infinity.isScannerActive = true
                        player.sendMessage(Component.text("You can now place blocks to define your structure. When generating the structure each block will be placed at the location you place it at now.")
                            .color(NamedTextColor.YELLOW)
                            .append(Component.text("The first seven Bedrock blocks you place will define the locations for villagers when playing this gamemode").color(NamedTextColor.YELLOW))

                        )
                    }
                }
                literalArgument("confirm") {
                    stringArgument("structureName") {
                        playerExecutor { player, args ->
                            // Generate Json here
                            val worldName = player.world.key.toString()
                            val structureName: String = args.getUnchecked("structureName")!!

                            player.sendMessage(Component.text("Registering structure ")
                                .color(NamedTextColor.YELLOW)
                                .append(Component.text(structureName).color(NamedTextColor.GREEN))
                                .append(Component.text(" for world ").color(NamedTextColor.YELLOW))
                                .append(Component.text(worldName).color(NamedTextColor.GREEN))
                                .append(Component.text("...").color(NamedTextColor.YELLOW))
                            )
                            val structureFileDirectory = File("./infinity/structures/${determineWorldName(NamespacedKey(worldName.split(":")[0], worldName.split(":")[1]))}")
                            if (!structureFileDirectory.exists()) {
                                structureFileDirectory.mkdirs()
                            }
                            val structureFile = File(structureFileDirectory, "$structureName.json")
                            if (structureFile.exists()) {
                                player.sendMessage(Component.text("A structure with the name ")
                                    .color(NamedTextColor.RED)
                                    .append(Component.text(structureName).color(NamedTextColor.GREEN))
                                    .append(Component.text(" for world ").color(NamedTextColor.RED))
                                    .append(Component.text(worldName).color(NamedTextColor.GREEN))
                                    .append(Component.text(" does already exist! This existing structure will be overwritten!").color(NamedTextColor.RED))
                                )
                                structureFile.delete()
                            }
                            structureFile.createNewFile()

                            val bufferedWriter = BufferedWriter(FileWriter(structureFile))
                            val jsonArray = JsonArray()

                            BlockScanner.PLACED_LOCATIONS.forEach { location: Location ->
                                jsonArray.add(location.world.getBlockAt(location).getAsJson())
                            }
                            BlockScanner.PLACED_LOCATIONS.clear()
                            val gson = GsonBuilder().setPrettyPrinting().create()
                            val jsonArrayString = gson.toJson(jsonArray)

                            bufferedWriter.write(jsonArrayString)
                            bufferedWriter.close()

                            player.sendMessage(Component.text("Registered structure ")
                                .color(NamedTextColor.YELLOW)
                                .append(Component.text(structureName).color(NamedTextColor.GREEN))
                                .append(Component.text(" for world ").color(NamedTextColor.YELLOW))
                                .append(Component.text(worldName).color(NamedTextColor.GREEN))
                                .append(Component.text(" successfully!").color(NamedTextColor.YELLOW))
                            )

                            infinity.isScannerActive = false
                        }
                    }
                }
                literalArgument("load") {
                    textArgument("structure") {
                        replaceSuggestions(getStructureIdentifiers())
                        playerExecutor { player, args ->
                            val input = args["structure"] as String
                            val worldName = input.split("/")[0]
                            val worldFile = File("./infinity/structures/$worldName")
                            val structureFileName = input.split("/")[1] + ".json"
                            val structureFile = File(worldFile, structureFileName)
                            StructureLoader(worldName, structureFile)
                            player.sendMessage(Component.text("Loading structure " + structureFile.name + "...").color(NamedTextColor.GREEN))
                        }
                    }
                }
                literalArgument("edit") {
                    textArgument("structure") {
                        replaceSuggestions(getStructureIdentifiers())
                        playerExecutor { player, args ->
                            val input = args["structure"] as String
                            val worldName = input.split("/")[0]
                            val worldFile = File("./infinity/structures/$worldName")
                            val structureFileName = input.split("/")[1] + ".json"
                            val structureFile = File(worldFile, structureFileName)
                            val structureBlockLocations = StructureEditor(structureFile).deserializeStructureToLocations()
                            BlockScanner.PLACED_LOCATIONS.addAll(structureBlockLocations)
                            infinity.isScannerActive = true
                            player.sendMessage(Component.text("You can now edit the structure ")
                                .color(NamedTextColor.GREEN)
                                .append(Component.text(structureFile.name).color(NamedTextColor.YELLOW))
                                .append(Component.text("! Type ").color(NamedTextColor.GREEN))
                                .append(Component.text("/dev structure confirm ${structureFileName.replace(".json", "")}").color(NamedTextColor.YELLOW))
                                .append(Component.text(" when you're done!").color(NamedTextColor.GREEN))
                            )
                        }
                    }
                }
            }
        }
    }

    private fun Argument<*>.structureArgument(nodeName: String, block: Argument<*>.() -> Unit = {}) = then(CustomArgument(GreedyStringArgument(nodeName)) { info ->
        val input = info.input
        if (input.contains(" ")) {
            throw CustomArgumentException.fromAdventureComponent(Component.text("Wrong argument provided! Cannot contain spaces!"))
        }
        val worldName = input.split("/")[0]
        val worldFile = File("./infinity/structures/$worldName")
        val structureFileName = input.split("/")[1] + ".json"
        return@CustomArgument File(worldFile, structureFileName)
    }.replaceSuggestions(getStructureIdentifiers())).apply(block)

    private fun getStructureIdentifiers(): ArgumentSuggestions<CommandSender> {
        return ArgumentSuggestions.strings { info ->
            val structures = mutableListOf<String>()
            val lobbyDirectory = File("./infinity/structures")
            if (!lobbyDirectory.exists()) {
                return@strings structures.toTypedArray()
            }
            val worldStructureFiles = lobbyDirectory.listFiles()!!
            for (worldStructureFile in worldStructureFiles) {
                var structureSuggestion = worldStructureFile.name
                val worldStructures = worldStructureFile.listFiles()!!
                for (worldStructure in worldStructures) {
                    structureSuggestion = "$structureSuggestion/${worldStructure.nameWithoutExtension}"
                    structures.add(structureSuggestion)
                }
            }

            return@strings structures.toTypedArray()
        }
    }

    private fun startTracer(player: Player): Int {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(infinity, BlockTracer(player), 0, 1)
    }

    private fun determineWorldName(worldKey: NamespacedKey): String {
        return when (worldKey) {
            infinity.getLobbyKey() -> "lobby"
            infinity.getSkyKey() -> "sky"
            infinity.getStoneKey() -> "stone"
            else -> "nether"
        }
    }

    private fun Block.getAsJson(): JsonObject {
        val blockLocation = JsonObject()
        blockLocation.addProperty("locX", this.x)
        blockLocation.addProperty("locY", this.y)
        blockLocation.addProperty("locZ", this.z)
        blockLocation.addProperty("materialType", this.type.name)
        blockLocation.addProperty("villagerLocation", false) // Figure out when and how to access this
        blockLocation.addProperty("directional", false)
        blockLocation.addProperty("shape", false)
        blockLocation.addProperty("bisected", false)
        blockLocation.addProperty("slab", false)
        // Add block metadata
        val blockData = this.blockData
        if (blockData is Directional) {
            blockLocation.addProperty("directional", true)
            blockLocation.addProperty("direction", blockData.facing.name)
        }
        if (blockData is Stairs) {
            blockLocation.addProperty("shape", true)
            blockLocation.addProperty("shapeType", blockData.shape.name)
        }
        if (blockData is Bisected) {
            blockLocation.addProperty("bisected", true)
            blockLocation.addProperty("bisectedHalf", blockData.half.name)
        }
        if (blockData is Slab) {
            blockLocation.addProperty("slab", true)
            blockLocation.addProperty("slabHalf", blockData.type.name)
        }
        if (this.type == Material.BEDROCK && infinity.getBlockScanner().placedVillagerLocations < Infinity.MAX_VILLAGERS) {
            // TODO: Villager locations are not handled yet
            infinity.getBlockScanner().placedVillagerLocations++
            blockLocation.addProperty("villagerLocation", true)
            blockLocation.addProperty("villager", infinity.getBlockScanner().placedVillagerLocations)
        }
        return blockLocation
    }

}