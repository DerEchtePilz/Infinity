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
import io.github.derechtepilz.infinity.gamemode.ChatHandler
import io.github.derechtepilz.infinity.commands.InfinityCommand
import io.github.derechtepilz.infinity.gamemode.*
import io.github.derechtepilz.infinity.gamemode.AdvancementListener
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener
import io.github.derechtepilz.infinity.gamemode.world.MobSpawnPreventionHandler
import io.github.derechtepilz.infinity.gamemode.worldmovement.ChestListener
import io.github.derechtepilz.infinity.gamemode.worldmovement.EnderChestHandler
import io.github.derechtepilz.infinity.items.InfinityAxe
import io.github.derechtepilz.infinity.items.InfinityPickaxe
import io.github.derechtepilz.infinity.items.Rarity
import io.github.derechtepilz.infinity.util.JsonUtil
import io.github.derechtepilz.infinity.util.Keys
import io.github.derechtepilz.infinity.util.capitalize
import io.github.derechtepilz.infinity.world.WorldCarver
import io.github.derechtepilz.infinity.world.WorldManager
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin
import java.io.*
import java.util.*

class Infinity : JavaPlugin() {

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
			Registry.Item.register(InfinityPickaxe.ITEM_ID, InfinityPickaxe(Rarity.UNCOMMON))
			for (i in 0 until InfinityAxe.VARIATIONS) {
				Registry.Item.register(InfinityAxe.ITEM_ID, InfinityAxe(Rarity.UNCOMMON, i))
			}
		}
	}

	companion object {
		const val NAME = "infinity"
		lateinit var INSTANCE: Infinity
		var canLoad = true
	}

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
		ConfigHandler.loadConfig(this)

		// Load the plugin
		val configReader = getConfigReader()
		if (configReader != null) {
			val jsonBuilder = StringBuilder()
			var line: String?
			while (configReader.readLine().also { line = it } != null) {
				jsonBuilder.append(line)
			}
			val jsonObject = JsonParser.parseString(jsonBuilder.toString()).asJsonObject
			val inventoryDataArray = JsonUtil.getArray("inventoryData", jsonObject)
			val experienceDataArray = JsonUtil.getArray("experienceData", jsonObject)
			val healthHungerDataArray = JsonUtil.getArray("healthHungerData", jsonObject)
			val potionEffectDataArray = JsonUtil.getArray("potionEffectData", jsonObject)

			JsonUtil.loadMap(inventoryDataArray, UUID::fromString).saveTo(inventoryData)
			JsonUtil.loadMap(experienceDataArray, UUID::fromString).saveTo(experienceData)
			JsonUtil.loadMap(healthHungerDataArray, UUID::fromString).saveTo(healthHungerData)
			JsonUtil.loadMap(potionEffectDataArray, UUID::fromString).saveTo(potionEffectData)
		}
		CommandAPI.onLoad(CommandAPIBukkitConfig(this).missingExecutorImplementationMessage("You cannot execute this command!"))

		InfinityCommand.register()
	}

	override fun onEnable() {
		if (!canLoad) {
			logger.warning("Enabling sequence not called. Please upgrade to Paper.")
			Bukkit.getPluginManager().disablePlugin(this)
			return
		}
		val lobby = Bukkit.createWorld(WorldCreator("infinity/lobby", Keys.WORLD_LOBBY.get())
			.generator(WorldManager.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(WorldManager.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		)!!
		val sky = Bukkit.createWorld(WorldCreator("infinity/sky", Keys.WORLD_SKY.get())
			.generator(WorldManager.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(WorldManager.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		)!!
		val stone = Bukkit.createWorld(WorldCreator("infinity/stone", Keys.WORLD_STONE.get())
			.generator(WorldManager.ChunkGenerators.StoneChunkGenerator())
			.biomeProvider(WorldManager.BiomeProviders.StoneBiomeProvider())
			.seed(0L)
		)!!
		val nether = Bukkit.createWorld(WorldCreator("infinity/nether", Keys.WORLD_NETHER.get())
			.generator(WorldManager.ChunkGenerators.NetherChunkGenerator())
			.environment(World.Environment.NETHER)
			.biomeProvider(WorldManager.BiomeProviders.NetherBiomeProvider())
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

		Bukkit.getPluginManager().registerEvents(PlayerJoinGamemodeListener(), this)
		Bukkit.getPluginManager().registerEvents(GameModeChangeListener(this), this)
		Bukkit.getPluginManager().registerEvents(AdvancementListener(), this)
		Bukkit.getPluginManager().registerEvents(SignListener(), this)
		Bukkit.getPluginManager().registerEvents(ChatHandler(), this)
		Bukkit.getPluginManager().registerEvents(ChestListener(), this)
		Bukkit.getPluginManager().registerEvents(DeathHandler(), this)
		Bukkit.getPluginManager().registerEvents(EnderChestHandler(), this)
		Bukkit.getPluginManager().registerEvents(PortalDisableHandler(), this)
		Bukkit.getPluginManager().registerEvents(MobSpawnPreventionHandler(), this)

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
		ConfigHandler.saveConfig(this)
		// Save player data
		val configWriter = getConfigWriter()
		val playerDataObject = JsonObject()

		JsonUtil.saveMap(playerDataObject, "inventoryData", inventoryData)
		JsonUtil.saveMap(playerDataObject, "experienceData", experienceData)
		JsonUtil.saveMap(playerDataObject, "healthHungerData", healthHungerData)
		JsonUtil.saveMap(playerDataObject, "potionEffectData", potionEffectData)

		val jsonString = GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject)
		configWriter.write(jsonString)
		configWriter.close()

		CommandAPI.onDisable()

		// If PaperMC/Paper #9679 gets merged, this is redundant
		for (player in Bukkit.getOnlinePlayers()) {
			SignListener.INSTANCE.saveSignStatesFor(player)
			DeathHandler.INSTANCE.saveSpawnPointsFor(player)
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
