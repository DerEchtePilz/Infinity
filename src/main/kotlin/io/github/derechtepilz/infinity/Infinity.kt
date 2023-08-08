package io.github.derechtepilz.infinity

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.derechtepilz.infinity.commands.DevCommand
import io.github.derechtepilz.infinity.commands.InfinityCommand
import io.github.derechtepilz.infinity.events.BlockScanner
import io.github.derechtepilz.infinity.events.GameModeChangeListener
import io.github.derechtepilz.infinity.events.PlayerListener
import io.github.derechtepilz.infinity.inventory.ChooseGamemodeInventory
import io.github.derechtepilz.infinity.items.InfinityAxe
import io.github.derechtepilz.infinity.items.InfinityPickaxe
import io.github.derechtepilz.infinity.util.Rarity
import io.github.derechtepilz.infinity.world.WorldManager
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
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

    var isScannerActive = false

    private val devCommand = DevCommand(this)
    private val blockScanner = BlockScanner(this)
    private val gamemodeInventory: ChooseGamemodeInventory = ChooseGamemodeInventory(this)

    private val infinityInventories: MutableMap<UUID, MutableList<String>> = mutableMapOf()
    private val minecraftInventories: MutableMap<UUID, MutableList<String>> = mutableMapOf()

    override fun onLoad() {
        val dispatcherFileDirectory = File("./infinity/config")
        if (!dispatcherFileDirectory.exists()) {
            dispatcherFileDirectory.mkdirs()
        }
        val dispatcherFile = File(dispatcherFileDirectory, "dispatcher.json")
        if (!dispatcherFile.exists()) {
            dispatcherFile.createNewFile()
        }
        CommandAPI.onLoad(CommandAPIBukkitConfig(this)
            .missingExecutorImplementationMessage("You cannot execute this command!")
            .dispatcherFile(dispatcherFile)
        )

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
        // Plugin shutdown logic
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

    fun getDevCommand(): DevCommand {
        return devCommand
    }
    
    fun getBlockScanner(): BlockScanner {
        return blockScanner
    }

    fun getBlockScanner(): BlockScanner {
        return blockScanner
    }

    fun getGamemodeInventory(): ChooseGamemodeInventory {
        return gamemodeInventory
    }

    fun getGamemodeInventory(): ChooseGamemodeInventory {
        return gamemodeInventory
    }

    fun getInfinityInventories(): MutableMap<UUID, MutableList<String>> {
        return infinityInventories
    }

    fun getMinecraftInventories(): MutableMap<UUID, MutableList<String>> {
        return minecraftInventories
    }

}
