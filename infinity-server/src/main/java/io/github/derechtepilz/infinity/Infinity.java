package io.github.derechtepilz.infinity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.events.WorldCreateLoadEvent;
import io.github.derechtepilz.infinity.util.JsonUtil;
import io.github.derechtepilz.infinity.util.Keys;
import io.github.derechtepilz.infinity.world.WorldManager;
import io.github.derechtepilz.infinity.world.WorldManager0;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class Infinity extends JavaPlugin {

    public static final String NAME = "infinity";
    public static Infinity instance;
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
            // TODO: Convert to Java before registering
        }
    }

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Component infinityComponent = mm.deserialize("<gradient:#18e1f0:#de18e1f0>Minecraft Infinity</gradient>");

    private final Map<UUID, Integer> startStoryTask = new HashMap<>();
    private final Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private final List<UUID> infinityPlayerList = new ArrayList<>();
    private final List<UUID> minecraftPlayerList = new ArrayList<>();

    private final Map<UUID, String> inventoryData = new HashMap<>();
    private final Map<UUID, String> experienceData = new HashMap<>();
    private final Map<UUID, String> healthHungerData = new HashMap<>();
    private final Map<UUID, String> potionEffectData = new HashMap<>();

    @Override
    public void onLoad() {
        if (!canLoad) return;

        // Check server version, disable on 1.19.4 and lower

        instance = this;

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

        // TODO: Register commands
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

        // TODO: Create structures

        // TODO: Register events

        Bukkit.getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", (channel, player, message) -> {
            String messageString = new String(message).substring(1);
            String firstCharacter = messageString.substring(0, 1);
            messageString = messageString.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
            getLogger().info("${player.name} just logged in using " + messageString);
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

        // TODO: Save additional data, done in listeners, comes later
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

}
