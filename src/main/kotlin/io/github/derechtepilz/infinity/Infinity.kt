package io.github.derechtepilz.infinity

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.derechtepilz.infinity.commands.DevCommand
import io.github.derechtepilz.infinity.commands.InfinityCommand
import io.github.derechtepilz.infinity.events.BlockScanner
import io.github.derechtepilz.infinity.events.GameModeChangeListener
import io.github.derechtepilz.infinity.events.PlayerListener
import io.github.derechtepilz.infinity.items.InfinityAxe
import io.github.derechtepilz.infinity.items.InfinityPickaxe
import io.github.derechtepilz.infinity.util.Rarity
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
    }

    private val lobbyKey = NamespacedKey(NAME, "lobby")
    private val skyKey = NamespacedKey(NAME, "sky")
    private val stoneKey = NamespacedKey(NAME, "stone")
    private val netherKey = NamespacedKey(NAME, "nether")

    private val defaultGamemodeKey = NamespacedKey(NAME, "defaultgamemode")

    var isScannerActive = false

    private val devCommand = DevCommand(this)
    private val blockScanner = BlockScanner(this)

    private val infinityInventories: MutableMap<UUID, MutableList<String>> = mutableMapOf()
    private val minecraftInventories: MutableMap<UUID, MutableList<String>> = mutableMapOf()
    private val infinityAdvancements: MutableMap<UUID, MutableList<String>> = mutableMapOf()
    private val minecraftAdvancements: MutableMap<UUID, MutableList<String>> = mutableMapOf()

    override fun onLoad() {
        val configReader = getConfigReader()
        if (configReader != null) {
            val jsonBuilder = StringBuilder()
            var line: String?
            while (configReader.readLine().also { line = it } != null) {
                jsonBuilder.append(line)
            }
            val jsonObject = JsonParser.parseString(jsonBuilder.toString()).asJsonObject
            val infinityInventoryArray = jsonObject["infinityInventories"].asJsonArray
            val minecraftInventoryArray = jsonObject["minecraftInventories"].asJsonArray
            val infinityAdvancementsArray = jsonObject["infinityAdvancements"].asJsonArray
            val minecraftAdvancementsArray = jsonObject["minecraftAdvancements"].asJsonArray

            for (i in 0 until infinityInventoryArray.size()) {
                val playerDataObject = infinityInventoryArray[i].asJsonObject
                val playerUUID = UUID.fromString(playerDataObject["player"].asString)
                val inventory = playerDataObject["inventory"].asString
                val enderChest = playerDataObject["enderChest"].asString
                val playerInventoryData = mutableListOf(inventory, enderChest)
                infinityInventories[playerUUID] = playerInventoryData
            }

            for (i in 0 until minecraftInventoryArray.size()) {
                val playerDataObject = minecraftInventoryArray[i].asJsonObject
                val playerUUID = UUID.fromString(playerDataObject["player"].asString)
                val inventory = playerDataObject["inventory"].asString
                val enderChest = playerDataObject["enderChest"].asString
                val playerInventoryData = mutableListOf(inventory, enderChest)
                minecraftInventories[playerUUID] = playerInventoryData
            }
            for (i in 0 until infinityAdvancementsArray.size()) {
                val advancementDataObject = infinityAdvancementsArray[i].asJsonObject
                val playerUUID = UUID.fromString(advancementDataObject["player"].asString)
                val advancementData = advancementDataObject["advancements"].asString
                val playerAdvancementData = mutableListOf(advancementData)
                infinityAdvancements[playerUUID] = playerAdvancementData
            }
            for (i in 0 until minecraftAdvancementsArray.size()) {
                val advancementDataObject = minecraftAdvancementsArray[i].asJsonObject
                val playerUUID = UUID.fromString(advancementDataObject["player"].asString)
                val advancementData = advancementDataObject["advancements"].asString
                val playerAdvancementData = mutableListOf(advancementData)
                minecraftAdvancements[playerUUID] = playerAdvancementData
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

        CommandAPI.onEnable()
    }

    override fun onDisable() {
        val configWriter = getConfigWriter()
        val inventoryObject = JsonObject()
        val infinityInventoryArray = JsonArray()
        val minecraftInventoryArray = JsonArray()
        val infinityAdvancementsArray = JsonArray()
        val minecraftAdvancementsArray = JsonArray()
        for (uuid in infinityInventories.keys) {
            val infinityInventory = JsonObject()
            val playerData = infinityInventories[uuid]!!
            val playerUUID = uuid.toString()
            val inventoryData = playerData[0]
            val enderChestData = playerData[1]
            infinityInventory.addProperty("player", playerUUID)
            infinityInventory.addProperty("inventory", inventoryData)
            infinityInventory.addProperty("enderChest", enderChestData)
            infinityInventoryArray.add(infinityInventory)
        }
        for (uuid in minecraftInventories.keys) {
            val minecraftInventory = JsonObject()
            val playerData = minecraftInventories[uuid]!!
            val playerUUID = uuid.toString()
            val inventoryData = playerData[0]
            val enderChestData = playerData[1]
            minecraftInventory.addProperty("player", playerUUID)
            minecraftInventory.addProperty("inventory", inventoryData)
            minecraftInventory.addProperty("enderChest", enderChestData)
            minecraftInventoryArray.add(minecraftInventory)
        }
        for (uuid in infinityAdvancements.keys) {
            val infinityAdvancement = JsonObject()
            val advancementData = infinityAdvancements[uuid]!![0]
            val playerUUID = uuid.toString()
            infinityAdvancement.addProperty("player", playerUUID)
            infinityAdvancement.addProperty("advancements", advancementData)
            infinityAdvancementsArray.add(infinityAdvancement)
        }
        for (uuid in minecraftAdvancements.keys) {
            val minecraftAdvancement = JsonObject()
            val advancementData = minecraftAdvancements[uuid]!![0]
            val playerUUID = uuid.toString()
            minecraftAdvancement.addProperty("player", playerUUID)
            minecraftAdvancement.addProperty("advancements", advancementData)
            minecraftAdvancementsArray.add(minecraftAdvancement)
        }
        inventoryObject.add("infinityInventories", infinityInventoryArray)
        inventoryObject.add("minecraftInventories", minecraftInventoryArray)
        inventoryObject.add("infinityAdvancements", infinityAdvancementsArray)
        inventoryObject.add("minecraftAdvancements", minecraftAdvancementsArray)

        val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(inventoryObject)
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
    
    fun getInfinityInventories(): MutableMap<UUID, MutableList<String>> {
        return infinityInventories
    }

    fun getMinecraftInventories(): MutableMap<UUID, MutableList<String>> {
        return minecraftInventories
    }

    fun getInfinityAdvancements(): MutableMap<UUID, MutableList<String>> {
        return infinityAdvancements
    }

    fun getMinecraftAdvancements(): MutableMap<UUID, MutableList<String>> {
        return minecraftAdvancements
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
