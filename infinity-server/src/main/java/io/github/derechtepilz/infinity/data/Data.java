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
import java.util.Collections;
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

	public Map<UUID, String> getInventoryData() {
		return Collections.unmodifiableMap(inventoryData);
	}

	public Map<UUID, String> getExperienceData() {
		return Collections.unmodifiableMap(experienceData);
	}

	public Map<UUID, String> getHealthHungerData() {
		return Collections.unmodifiableMap(healthHungerData);
	}

	public Map<UUID, String> getPotionEffectData() {
		return Collections.unmodifiableMap(potionEffectData);
	}

	public void saveData() {
		try {
			JsonObject playerDataObject = new JsonObject();

			JsonUtil.saveMap(playerDataObject, "inventoryData", inventoryData);
			JsonUtil.saveMap(playerDataObject, "experienceData", experienceData);
			JsonUtil.saveMap(playerDataObject, "healthHungerData", healthHungerData);
			JsonUtil.saveMap(playerDataObject, "potionEffectData", potionEffectData);

			String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(playerDataObject);

			getWriter().write(jsonString);
			getWriter().close();
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while writing player data. It is possible that data has been lost when restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}
	}

	public void loadData() {
		try {
			if (getReader() == null) {
				return;
			}
			StringBuilder jsonBuilder = new StringBuilder();
			String line;
			while ((line = getReader().readLine()) != null) {
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
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while reading player data. It is possible that data has been lost upon restarting. This is NOT a plugin issue! Please DO NOT report this!");
		}
	}

	protected BufferedWriter getWriter(String dataFileName) {
		try {
			File dataDirectory = new File("./infinity/data");
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

	protected BufferedReader getReader(String dataFileName) {
		try {
			File dataDirectory = new File("./infinity/data");
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

	public void setInventoryData(UUID player, String data) {
		inventoryData.put(player, data);
	}

	public void setExperienceData(UUID player, String data) {
		experienceData.put(player, data);
	}

	public void setHealthHungerData(UUID player, String data) {
		healthHungerData.put(player, data);
	}

	public void setPotionEffectData(UUID player, String data) {
		potionEffectData.put(player, data);
	}

	public abstract Data getOtherGamemodeData();

	public abstract BufferedWriter getWriter();

	public abstract BufferedReader getReader();

}
