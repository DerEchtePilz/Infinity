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

package io.github.derechtepilz.infinity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.InfinityAPI;
import io.github.derechtepilz.InfinityAPIServer;
import io.github.derechtepilz.events.WorldCreateLoadEvent;
import io.github.derechtepilz.infinity.backup.PlayerDataHandler;
import io.github.derechtepilz.infinity.commands.DevUtilCommand;
import io.github.derechtepilz.infinity.commands.InfinityCommand;
import io.github.derechtepilz.infinity.commonevents.JoinEventListener;
import io.github.derechtepilz.infinity.commonevents.QuitEventListener;
import io.github.derechtepilz.infinity.gamemode.gameclass.SignListener;
import io.github.derechtepilz.infinity.gamemode.modification.*;
import io.github.derechtepilz.infinity.gamemode.story.introduction.PlayerQuitInStoryHandler;
import io.github.derechtepilz.infinity.gamemode.switching.GamemodeSwitchHandler;
import io.github.derechtepilz.infinity.gamemode.worldmovement.ChestListener;
import io.github.derechtepilz.infinity.gamemode.worldmovement.EnderChestHandler;
import io.github.derechtepilz.infinity.items.InfinityAxe;
import io.github.derechtepilz.infinity.items.InfinityPickaxe;
import io.github.derechtepilz.infinity.items.Rarity;
import io.github.derechtepilz.infinity.util.JsonUtil;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.world.WorldCarver;
import io.github.derechtepilz.infinity.world.WorldManager;
import io.github.derechtepilz.separation.GamemodeSeparator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class Infinity extends JavaPlugin implements InfinityAPIServer {

	public static final String NAME = "infinity";
	private static Infinity instance;
	private boolean canLoad = true;

	{
		try {
			Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
			Class.forName("net.kyori.adventure.text.Component");
		} catch (ClassNotFoundException e) {
			getLogger().severe("You do not seem to run a Paper server. This plugin heavily relies on API provided by Paper that Spigot does not have natively.");
			getLogger().severe("Please upgrade to Paper here to use this plugin: https://papermc.io/downloads/paper");
			canLoad = false;
		}
		if (canLoad) {
			// Register items
			Registry.Item.register(InfinityPickaxe.ITEM_ID, new InfinityPickaxe(Rarity.UNCOMMON));
			for (int i = 0; i < InfinityAxe.VARIATIONS; i++) {
				Registry.Item.register(InfinityAxe.ITEM_ID, new InfinityAxe(Rarity.UNCOMMON, i));
			}
		}
	}

	private final MiniMessage mm = MiniMessage.miniMessage();
	private final Component infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18f0>Minecraft Infinity</gradient>");

	private final Map<UUID, Integer> startStoryTask = new HashMap<>();
	private final Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

	private final List<UUID> infinityPlayerList = new ArrayList<>();
	private final List<UUID> minecraftPlayerList = new ArrayList<>();

	private final Map<UUID, String> inventoryData = new HashMap<>();
	private final Map<UUID, String> experienceData = new HashMap<>();
	private final Map<UUID, String> healthHungerData = new HashMap<>();
	private final Map<UUID, String> potionEffectData = new HashMap<>();

	private final PlayerDataHandler playerDataHandler = new PlayerDataHandler();

	@Override
	public void onLoad() {
		if (!canLoad) return;

		// Check server version, disable on 1.19.4 and lower

		instance = this;
		InfinityAPI.setServer(this);

		// Load the plugin
		try {
			BufferedReader configReader = getConfigReader();
			if (configReader != null) {
				StringBuilder jsonBuilder = new StringBuilder();
				String line;
				while ((line = configReader.readLine()) != null) {
					jsonBuilder.append(line);
				}
				JsonObject jsonObject = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();

				JsonArray inventoryDataArray = JsonUtil.getArray("inventoryData", jsonObject);
				JsonArray experienceDataArray = JsonUtil.getArray("experienceData", jsonObject);
				JsonArray healthHungerDataArray = JsonUtil.getArray("healthHungerData", jsonObject);
				JsonArray potionEffectDataArray = JsonUtil.getArray("potionEffectData", jsonObject);

				JsonUtil.loadMap(inventoryDataArray, UUID::fromString).saveTo(inventoryData);
				JsonUtil.loadMap(experienceDataArray, UUID::fromString).saveTo(experienceData);
				JsonUtil.loadMap(healthHungerDataArray, UUID::fromString).saveTo(healthHungerData);
				JsonUtil.loadMap(potionEffectDataArray, UUID::fromString).saveTo(potionEffectData);
			}
		} catch (IOException e) {
			getLogger().severe("There was a problem while reading player data. It is possible that data has been lost upon restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}

		InfinityCommand.register();
		DevUtilCommand.register();
	}

	@Override
	public void onEnable() {
		if (!canLoad) {
			getLogger().warning("Enabling sequence not called. Please upgrade to Paper.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		new WorldCreateLoadEvent().callEvent();
		World lobby = Bukkit.createWorld(new WorldCreator("infinity/lobby", Keys.WORLD_LOBBY.get())
			.generator(new WorldManager.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(new WorldManager.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		);
		World sky = Bukkit.createWorld(new WorldCreator("infinity/sky", Keys.WORLD_SKY.get())
			.generator(new WorldManager.ChunkGenerators.EmptyChunkGenerator())
			.biomeProvider(new WorldManager.BiomeProviders.EmptyBiomeProvider())
			.seed(0L)
		);
		World stone = Bukkit.createWorld(new WorldCreator("infinity/stone", Keys.WORLD_STONE.get())
			.generator(new WorldManager.ChunkGenerators.StoneChunkGenerator())
			.biomeProvider(new WorldManager.BiomeProviders.StoneBiomeProvider())
			.seed(0L)
		);
		World nether = Bukkit.createWorld(new WorldCreator("infinity/nether", Keys.WORLD_NETHER.get())
			.generator(new WorldManager.ChunkGenerators.NetherChunkGenerator())
			.environment(World.Environment.NETHER)
			.biomeProvider(new WorldManager.BiomeProviders.NetherBiomeProvider())
			.seed(0L)
		);

		assert lobby != null;
		assert sky != null;
		assert stone != null;
		assert nether != null;

		lobby.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		lobby.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		lobby.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		lobby.setGameRule(GameRule.KEEP_INVENTORY, true);
		lobby.setTime(6000);

		sky.setGameRule(GameRule.KEEP_INVENTORY, true);
		stone.setGameRule(GameRule.KEEP_INVENTORY, true);
		nether.setGameRule(GameRule.KEEP_INVENTORY, true);

		sky.setDifficulty(Difficulty.HARD);
		stone.setDifficulty(Difficulty.HARD);
		nether.setDifficulty(Difficulty.HARD);

		sky.setViewDistance(16);
		stone.setViewDistance(16);
		nether.setViewDistance(16);

		new WorldCarver.LobbyCarver(lobby);
		new WorldCarver.SkyCarver(sky);
		new WorldCarver.StoneCarver(stone);
		new WorldCarver.NetherCarver(nether);

		Bukkit.getPluginManager().registerEvents(new JoinEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new QuitEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new GamemodeSwitchHandler(), this);
		Bukkit.getPluginManager().registerEvents(new AdvancementDisableHandler(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatHandler(), this);
		Bukkit.getPluginManager().registerEvents(new ChestListener(), this);
		Bukkit.getPluginManager().registerEvents(new DeathHandler(), this);
		Bukkit.getPluginManager().registerEvents(new EnderChestHandler(), this);
		Bukkit.getPluginManager().registerEvents(new PortalDisableHandler(), this);
		Bukkit.getPluginManager().registerEvents(new MobSpawnPreventionHandler(), this);
		Bukkit.getPluginManager().registerEvents(new TablistHandler(), this);

		Bukkit.getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", (channel, player, message) -> {
			String messageString = new String(message).substring(1);
			String firstCharacter = messageString.substring(0, 1);
			messageString = messageString.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
			getLogger().info(player.getName() + " just logged in using " + messageString);
		});
	}

	@Override
	public void onDisable() {
		if (!canLoad) {
			// Safeguard so potentially saved player data is not deleted
			return;
		}
		// Save player data
		try {
			BufferedWriter configWriter = getConfigWriter();
			assert configWriter != null;
			JsonObject playerDataObject = new JsonObject();

			JsonUtil.saveMap(playerDataObject, "inventoryData", inventoryData);
			JsonUtil.saveMap(playerDataObject, "experienceData", experienceData);
			JsonUtil.saveMap(playerDataObject, "healthHungerData", healthHungerData);
			JsonUtil.saveMap(playerDataObject, "potionEffectData", potionEffectData);

			String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject);
			configWriter.write(jsonString);
			configWriter.close();
		} catch (IOException e) {
			getLogger().severe("There was a problem while writing player data. It is possible that data has been lost when restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}

		// If PaperMC/Paper #9679 gets merged, this is redundant
		for (Player player : Bukkit.getOnlinePlayers()) {
			SignListener.getInstance().saveSignStatesFor(player);
			DeathHandler.getInstance().saveSpawnPointsFor(player);
			PlayerQuitInStoryHandler.resetIntroduction(player);
		}
	}

	public static Infinity getInstance() {
		return instance;
	}

	public Component getInfinityComponent() {
		return infinityComponent;
	}

	public Map<UUID, Integer> getStartStoryTask() {
		return startStoryTask;
	}

	public Map<UUID, PermissionAttachment> getPlayerPermissions() {
		return playerPermissions;
	}

	public List<UUID> getInfinityPlayerList() {
		return infinityPlayerList;
	}

	public List<UUID> getMinecraftPlayerList() {
		return minecraftPlayerList;
	}

	public Map<UUID, String> getInventoryData() {
		return inventoryData;
	}

	public Map<UUID, String> getExperienceData() {
		return experienceData;
	}

	public Map<UUID, String> getHealthHungerData() {
		return healthHungerData;
	}

	public Map<UUID, String> getPotionEffectData() {
		return potionEffectData;
	}

	public PlayerDataHandler getPlayerDataHandler() {
		return playerDataHandler;
	}

	private BufferedReader getConfigReader() {
		try {
			File configDirectory = new File("./infinity/config");
			if (!configDirectory.exists()) {
				return null;
			}
			File configFile = new File(configDirectory, "player-data-json");
			if (!configFile.exists()) {
				return null;
			}
			return new BufferedReader(new FileReader(configFile));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private BufferedWriter getConfigWriter() {
		try {
			File configDirectory = new File("./infinity/config");
			if (!configDirectory.exists()) {
				configDirectory.mkdirs();
			}
			File configFile = new File(configDirectory, "player-data-json");
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			return new BufferedWriter(new FileWriter(configFile));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public GamemodeSeparator getGamemodeSeparator() {
		return playerDataHandler;
	}
}
