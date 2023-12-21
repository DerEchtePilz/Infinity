package io.github.derechtepilz.infinity.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.util.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Data {

	final Map<UUID, String> inventoryData;
	final Map<UUID, String> experienceData;
	final Map<UUID, String> healthHungerData;
	final Map<UUID, String> potionEffectData;

	Data() {
		this.inventoryData = new HashMap<>();
		this.experienceData = new HashMap<>();
		this.healthHungerData = new HashMap<>();
		this.potionEffectData = new HashMap<>();
	}

	public void saveData(BufferedWriter writer) {
		try {
			JsonObject playerDataObject = new JsonObject();

			JsonUtil.saveMap(playerDataObject, "inventoryData", inventoryData);
			JsonUtil.saveMap(playerDataObject, "experienceData", experienceData);
			JsonUtil.saveMap(playerDataObject, "healthHungerData", healthHungerData);
			JsonUtil.saveMap(playerDataObject, "potionEffectData", potionEffectData);

			String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject);

			writer.write(jsonString);
			writer.close();
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while writing player data. It is possible that data has been lost when restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}
	}

	public void loadData(BufferedReader reader) {
		try {
			if (reader == null) {
				return;
			}
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
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

			reader.close();
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while reading player data. It is possible that data has been lost upon restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}
	}

	public BufferedWriter getWriter(String directorySuffix, String dataFileName) {
		try {
			File dataDirectory = new File("./infinity/" + directorySuffix);
			if (!dataDirectory.exists()) {
				dataDirectory.mkdirs();
			}
			File dataFile = new File(dataDirectory, dataFileName + ".json");
			if (!dataFile.exists()) {
				dataFile.createNewFile();
			}
			return new BufferedWriter(new FileWriter(dataFile));
		} catch (IOException e) {
			return null;
		}
	}

	public BufferedReader getReader(String directorySuffix, String dataFileName) {
		try {
			File dataDirectory = new File("./infinity/" + directorySuffix);
			if (!dataDirectory.exists()) {
				return null;
			}
			File dataFile = new File(dataDirectory, dataFileName + ".json");
			if (!dataFile.exists()) {
				return null;
			}
			return new BufferedReader(new FileReader(dataFile));
		} catch (IOException e) {
			return null;
		}
	}

	public abstract BufferedWriter getWriter();

	public abstract BufferedReader getReader();

}
