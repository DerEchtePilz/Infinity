/*
 *  Infinity - a Minecraft story-game for Paper servers
 *  Copyright (C) 2023  DerEchtePilz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.derechtepilz.infinity

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import io.github.derechtepilz.events.WorldCreateLoadEvent
import io.github.derechtepilz.infinity.commands.DevUtilCommand
import io.github.derechtepilz.infinity.commands.InfinityCommand
import io.github.derechtepilz.infinity.gamemode.PlayerJoinServerListener
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener
import io.github.derechtepilz.infinity.gamemode.modification.AdvancementDisableHandler
import io.github.derechtepilz.infinity.gamemode.modification.ChatHandler
import io.github.derechtepilz.infinity.gamemode.modification.DeathHandler
import io.github.derechtepilz.infinity.gamemode.modification.MobSpawnPreventionHandler
import io.github.derechtepilz.infinity.gamemode.modification.PortalDisableHandler
import io.github.derechtepilz.infinity.gamemode.modification.TablistHandler
import io.github.derechtepilz.infinity.gamemode.story.introduction.PlayerQuitInStoryListener
import io.github.derechtepilz.infinity.gamemode.switching.GamemodeSwitchHandler
import io.github.derechtepilz.infinity.gamemode.worldmovement.ChestListener
import io.github.derechtepilz.infinity.gamemode.worldmovement.EnderChestHandler
import io.github.derechtepilz.infinity.items.InfinityAxe0
import io.github.derechtepilz.infinity.items.InfinityPickaxe0
import io.github.derechtepilz.infinity.items.InfinityPickaxe1
import io.github.derechtepilz.infinity.items.Rarity
import io.github.derechtepilz.infinity.util.JsonUtil0
import io.github.derechtepilz.infinity.util.Keys0
import io.github.derechtepilz.infinity.util.capitalize
import io.github.derechtepilz.infinity.world.WorldCarver
import io.github.derechtepilz.infinity.world.WorldManager0
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.*

class Infinity0 : JavaPlugin() {

	init {
		try {
			Class.forName("io.papermc.paper.event.player.AsyncChatEvent")
			Class.forName("net.kyori.adventure.text.Component")
		} catch (e: ClassNotFoundException) {
			logger.severe("You do not seem to run a Paper server. This plugin heavily relies on API provided by Paper that Spigot does not have natively.")
			logger.severe("Please upgrade to Paper here to use this plugin: https://papermc.io/downloads/paper")
			canLoad = false
		}
		if (canLoad) {
			// Register items
			loadItems()
		}
	}

	companion object {
		const val NAME = "infinity"
		lateinit var INSTANCE: Infinity0
		var canLoad = true

		fun loadItems() {
			// Dummy method for tests so Infinity items can be used in tests
			Registry0.Item.register(InfinityPickaxe1., InfinityPickaxe0(Rarity.UNCOMMON))
			for (i in 0 until InfinityAxe0.VARIATIONS) {
				Registry0.Item.register(InfinityAxe0.ITEM_ID, InfinityAxe0(Rarity.UNCOMMON, i))
			}
		}
	}

	private val mm: MiniMessage = MiniMessage.miniMessage()
	val infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>")

	val startStoryTask: MutableMap<UUID, Int> = mutableMapOf()
	val playerPermissions: MutableMap<UUID, PermissionAttachment> = mutableMapOf()

	val infinityPlayerList: MutableList<UUID> = mutableListOf()
	val minecraftPlayerList: MutableList<UUID> = mutableListOf()

	private val inventoryData: MutableMap<UUID, String> = mutableMapOf()
	private val experienceData: MutableMap<UUID, String> = mutableMapOf()
	private val healthHungerData: MutableMap<UUID, String> = mutableMapOf()
	private val potionEffectData: MutableMap<UUID, String> = mutableMapOf()

	override fun onLoad() {
		if (!canLoad) {
			return
		}
		// Check server version, disable on 1.19.4 and lower

		INSTANCE = this

		// Load the plugin
		val configReader = getConfigReader()
		if (configReader != null) {
			val jsonBuilder = StringBuilder()
			var line: String?
			while (configReader.readLine().also { line = it } != null) {
				jsonBuilder.append(line)
			}
			val jsonObject = JsonParser.parseString(jsonBuilder.toString()).asJsonObject
			val inventoryDataArray = JsonUtil0.getArray0("inventoryData", jsonObject)
			val experienceDataArray = JsonUtil0.getArray0("experienceData", jsonObject)
			val healthHungerDataArray = JsonUtil0.getArray0("healthHungerData", jsonObject)
			val potionEffectDataArray = JsonUtil0.getArray0("potionEffectData", jsonObject)

			JsonUtil0.loadMap(inventoryDataArray, UUID::fromString).saveTo(inventoryData)
			JsonUtil0.loadMap(experienceDataArray, UUID::fromString).saveTo(experienceData)
			JsonUtil0.loadMap(healthHungerDataArray, UUID::fromString).saveTo(healthHungerData)
			JsonUtil0.loadMap(potionEffectDataArray, UUID::fromString).saveTo(potionEffectData)
		}
		CommandAPI.onLoad(CommandAPIBukkitConfig(this).missingExecutorImplementationMessage("You cannot execute this command!"))

		InfinityCommand.register()
		DevUtilCommand().register()
	}

	override fun onEnable() {
		if (!canLoad) {
			logger.warning("Enabling sequence not called. Please upgrade to Paper.")
			Bukkit.getPluginManager().disablePlugin(this)
			return
		}
		WorldCreateLoadEvent().callEvent()
		val lobby = Bukkit.createWorld(WorldCreator("infinity/lobby", Keys0.WORLD_LOBBY.get())
			.generator(WorldManager0.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(WorldManager0.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		)!!
		val sky = Bukkit.createWorld(WorldCreator("infinity/sky", Keys0.WORLD_SKY.get())
			.generator(WorldManager0.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(WorldManager0.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		)!!
		val stone = Bukkit.createWorld(WorldCreator("infinity/stone", Keys0.WORLD_STONE.get())
			.generator(WorldManager0.ChunkGenerators.StoneChunkGenerator())
			.biomeProvider(WorldManager0.BiomeProviders.StoneBiomeProvider())
			.seed(0L)
		)!!
		val nether = Bukkit.createWorld(WorldCreator("infinity/nether", Keys0.WORLD_NETHER.get())
			.generator(WorldManager0.ChunkGenerators.NetherChunkGenerator())
			.environment(World.Environment.NETHER)
			.biomeProvider(WorldManager0.BiomeProviders.NetherBiomeProvider())
			.seed(0L)
		)!!

		lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
		lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
		lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false)
		lobby.setGameRule(GameRule.KEEP_INVENTORY, true)
		lobby.time = 6000

		sky.setGameRule(GameRule.KEEP_INVENTORY, true)
		stone.setGameRule(GameRule.KEEP_INVENTORY, true)
		nether.setGameRule(GameRule.KEEP_INVENTORY, true)

		sky.difficulty = Difficulty.HARD
		stone.difficulty = Difficulty.HARD
		nether.difficulty = Difficulty.HARD

		sky.viewDistance = 16
		stone.viewDistance = 16
		nether.viewDistance = 16

		WorldCarver.LobbyCarver(lobby)
		WorldCarver.SkyCarver(sky)
		WorldCarver.StoneCarver(stone)
		WorldCarver.NetherCarver(nether)

		Bukkit.getPluginManager().registerEvents(PlayerJoinServerListener(), this)
		Bukkit.getPluginManager().registerEvents(GamemodeSwitchHandler(), this)
		Bukkit.getPluginManager().registerEvents(AdvancementDisableHandler(), this)
		Bukkit.getPluginManager().registerEvents(SignListener(), this)
		Bukkit.getPluginManager().registerEvents(ChatHandler(), this)
		Bukkit.getPluginManager().registerEvents(ChestListener(), this)
		Bukkit.getPluginManager().registerEvents(DeathHandler(), this)
		Bukkit.getPluginManager().registerEvents(EnderChestHandler(), this)
		Bukkit.getPluginManager().registerEvents(PortalDisableHandler(), this)
		Bukkit.getPluginManager().registerEvents(MobSpawnPreventionHandler(), this)
		Bukkit.getPluginManager().registerEvents(TablistHandler(), this)
		Bukkit.getPluginManager().registerEvents(PlayerQuitInStoryListener(), this)

		Bukkit.getMessenger().registerIncomingPluginChannel(this, "minecraft:brand") { channel, player, message ->
			logger.info("${player.name} just logged in using ${String(message).substring(1).capitalize()}")
		}

		CommandAPI.onEnable()
	}

	override fun onDisable() {
		if (!canLoad) {
			// Safeguard so potentially saved player data is not deleted
			return
		}
		// Save player data
		val configWriter = getConfigWriter()
		val playerDataObject = JsonObject()

		JsonUtil0.saveMap0(playerDataObject, "inventoryData", inventoryData)
		JsonUtil0.saveMap0(playerDataObject, "experienceData", experienceData)
		JsonUtil0.saveMap0(playerDataObject, "healthHungerData", healthHungerData)
		JsonUtil0.saveMap0(playerDataObject, "potionEffectData", potionEffectData)

		val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject)
		configWriter.write(jsonString)
		configWriter.close()

		CommandAPI.onDisable()

		// If PaperMC/Paper #9679 gets merged, this is redundant
		for (player in Bukkit.getOnlinePlayers()) {
			SignListener.INSTANCE.saveSignStatesFor(player)
			DeathHandler.INSTANCE.saveSpawnPointsFor(player)
			PlayerQuitInStoryListener.INSTANCE.resetIntroduction(player)
		}
	}

	fun getInventoryData(): MutableMap<UUID, String> {
		return inventoryData
	}

	fun getExperienceData(): MutableMap<UUID, String> {
		return experienceData
	}

	fun getHealthHungerData(): MutableMap<UUID, String> {
		return healthHungerData
	}

	fun getPotionEffectData(): MutableMap<UUID, String> {
		return potionEffectData
	}

	private fun getConfigReader(): BufferedReader? {
		val configDirectory = File("./infinity/config")
		if (!configDirectory.exists()) {
			return null
		}
		val configFile = File(configDirectory, "player-data.json")
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
		val configFile = File(configDirectory, "player-data.json")
		if (!configFile.exists()) {
			configFile.createNewFile()
		}
		return BufferedWriter(FileWriter(configFile))
	}

}
