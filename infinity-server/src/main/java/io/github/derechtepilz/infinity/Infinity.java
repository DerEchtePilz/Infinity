package io.github.derechtepilz.infinity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.events.WorldCreateLoadEvent;
import io.github.derechtepilz.infinity.util.JsonUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
            getLogger().severe("There was a problem reading player data. It is possible that data has been lost upon restarting. This is NOT a plugin issue! Please DO NOT report this!");
        }

        // Register commands
    }

    @Override
    public void onEnable() {
        if (!canLoad) {
            getLogger().warning("Enabling sequence not called. Please upgrade to Paper.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new WorldCreateLoadEvent().callEvent();
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
