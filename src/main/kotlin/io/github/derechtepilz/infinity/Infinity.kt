package io.github.derechtepilz.infinity

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.derechtepilz.infinity.commands.DevCommand
import io.github.derechtepilz.infinity.commands.InfinityCommand
import io.github.derechtepilz.infinity.structure.BlockScanner
import io.github.derechtepilz.infinity.gamemode.GameModeChangeListener
import io.github.derechtepilz.infinity.gamemode.PlayerListener
import io.github.derechtepilz.infinity.gamemode.advancement.AdvancementListener
import io.github.derechtepilz.infinity.items.InfinityAxe
import io.github.derechtepilz.infinity.items.InfinityPickaxe
import io.github.derechtepilz.infinity.items.Rarity
import io.github.derechtepilz.infinity.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.lang.StringBuilder
import java.util.UUID

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
        lateinit var INSTANCE: Infinity
    }

    private val lobbyKey = NamespacedKey(NAME, "lobby")
    private val skyKey = NamespacedKey(NAME, "sky")
    private val stoneKey = NamespacedKey(NAME, "stone")
    private val netherKey = NamespacedKey(NAME, "nether")

    private val defaultGamemodeKey = NamespacedKey(NAME, "defaultgamemode")

    var isScannerActive = false

    private val devCommand = DevCommand(this)
    private val blockScanner = BlockScanner(this)

    private val inventoryData: MutableMap<UUID, MutableList<String>> = mutableMapOf()
    private val experienceData: MutableMap<UUID, MutableList<String>> = mutableMapOf()

    override fun onLoad() {
        INSTANCE = this
        val configReader = getConfigReader()
        if (configReader != null) {
            val jsonBuilder = StringBuilder()
            var line: String?
            while (configReader.readLine().also { line = it } != null) {
                jsonBuilder.append(line)
            }
            val jsonObject = JsonParser.parseString(jsonBuilder.toString()).asJsonObject
            val inventoryDataArray = jsonObject["inventoryData"].asJsonArray
            val experienceDataArray = jsonObject["experienceData"].asJsonArray

            for (i in 0 until inventoryDataArray.size()) {
                val playerDataObject = inventoryDataArray[i].asJsonObject
                val playerUUID = UUID.fromString(playerDataObject["player"].asString)
                val inventory = playerDataObject["inventory"].asString
                val enderChest = playerDataObject["enderChest"].asString
                val playerInventoryData = mutableListOf(inventory, enderChest)
                inventoryData[playerUUID] = playerInventoryData
            }
            for (i in 0 until experienceDataArray.size()) {
                val experienceDataObject = experienceDataArray[i].asJsonObject
                val playerUUID = UUID.fromString(experienceDataObject["player"].asString)
                val experienceLevel = experienceDataObject["level"].asString
                val experienceProgress = experienceDataObject["progress"].asString
                experienceData[playerUUID] = mutableListOf(experienceLevel, experienceProgress)
            }
        }
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).missingExecutorImplementationMessage("You cannot execute this command!"))

        InfinityCommand.register(this)
        devCommand.register()
    }

    override fun onEnable() {
        val lobby = Bukkit.createWorld(WorldCreator("infinity/lobby", lobbyKey)
            .generator(WorldManager.ChunkGenerators.EmptyChunkGenerator())
            .biomeProvider(WorldManager.BiomeProviders.EmptyBiomeProvider())
            .seed(0L)
        )
        val sky = Bukkit.createWorld(WorldCreator("infinity/sky", skyKey)
            .generator(WorldManager.ChunkGenerators.EmptyChunkGenerator())
            .biomeProvider(WorldManager.BiomeProviders.EmptyBiomeProvider())
            .seed(0L)
        )
        val stone = Bukkit.createWorld(WorldCreator("infinity/stone", stoneKey)
            .generator(WorldManager.ChunkGenerators.StoneChunkGenerator())
            .biomeProvider(WorldManager.BiomeProviders.StoneBiomeProvider())
            .seed(0L)
        )
        val nether = Bukkit.createWorld(WorldCreator("infinity/nether", netherKey)
            .generator(WorldManager.ChunkGenerators.NetherChunkGenerator())
            .environment(World.Environment.NETHER)
            .biomeProvider(WorldManager.BiomeProviders.NetherBiomeProvider())
            .seed(0L)
        )

        Bukkit.getPluginManager().registerEvents(blockScanner, this)
        Bukkit.getPluginManager().registerEvents(PlayerListener(this), this)
        Bukkit.getPluginManager().registerEvents(GameModeChangeListener(this), this)
        Bukkit.getPluginManager().registerEvents(AdvancementListener(), this)

        CommandAPI.onEnable()
    }

    override fun onDisable() {
        val configWriter = getConfigWriter()
        val playerDataObject = JsonObject()
        val inventoryDataArray = JsonArray()
        val experienceDataArray = JsonArray()
        for (uuid in inventoryData.keys) {
            val inventoryData = JsonObject()
            val playerData = this.inventoryData[uuid]!!
            val playerUUID = uuid.toString()
            val playerInventoryData = playerData[0]
            val enderChestData = playerData[1]
            inventoryData.addProperty("player", playerUUID)
            inventoryData.addProperty("inventory", playerInventoryData)
            inventoryData.addProperty("enderChest", enderChestData)
            inventoryDataArray.add(inventoryData)
        }
        for (uuid in experienceData.keys) {
            val experienceData = JsonObject()
            val playerExperienceData = this.experienceData[uuid]!!
            val playerUuid = uuid.toString()
            experienceData.addProperty("player", playerUuid)
            experienceData.addProperty("level", playerExperienceData[0])
            experienceData.addProperty("progress", playerExperienceData[1])
            experienceDataArray.add(experienceData)
        }

        playerDataObject.add("inventoryData", inventoryDataArray)
        playerDataObject.add("experienceData", experienceDataArray)

        val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject)
        configWriter.write(jsonString)
        configWriter.close()
    }

    fun getLobbyKey(): NamespacedKey {
        return lobbyKey
    }

    fun getSkyKey(): NamespacedKey {
        return skyKey
    }

    fun getStoneKey(): NamespacedKey {
        return stoneKey
    }

    fun getNetherKey(): NamespacedKey {
        return netherKey
    }

    fun getDefaultGamemode(): NamespacedKey {
        return defaultGamemodeKey
    }

    fun getDevCommand(): DevCommand {
        return devCommand
    }
    
    fun getBlockScanner(): BlockScanner {
        return blockScanner
    }
    
    fun getInventoryData(): MutableMap<UUID, MutableList<String>> {
        return inventoryData
    }

    fun getExperienceData(): MutableMap<UUID, MutableList<String>> {
        return experienceData
    }

    private fun getConfigReader(): BufferedReader? {
        val configDirectory = File("./infinity/config")
        if (!configDirectory.exists()) {
            return null
        }
        val configFile = File(configDirectory, "config.json")
        if (!configFile.exists()) {
            return null
        }
        return BufferedReader(FileReader(configFile))
    }

    private fun getConfigWriter(): BufferedWriter {
        val configDirectory = File("./infinity/config")
        if (!configDirectory.exists()) {
            configDirectory.mkdirs()
        }
        val configFile = File(configDirectory, "config.json")
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        return BufferedWriter(FileWriter(configFile))
    }

}
