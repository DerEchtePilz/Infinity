package io.github.derechtepilz.infinity.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import io.github.derechtepilz.infinity.gamemode.Gamemode;
import io.github.derechtepilz.infinity.util.JsonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

public class PlayerData extends Data {

	@Override
	public void saveData(BufferedWriter writer) {
		try {
			JsonObject playerGamemode = new JsonObject();

			for (UUID uuid : Infinity.getInstance().getPlayerGamemode().keySet()) {
				playerGamemode.addProperty(uuid.toString(), Infinity.getInstance().getPlayerGamemode().get(uuid).name().toLowerCase());
			}

			String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(playerGamemode);

			writer.write(jsonString);
			writer.close();
		} catch (IOException e) {
			// TODO: Add an error message
		}
	}

	@Override
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

			for (String key : jsonObject.keySet()) {
				Infinity.getInstance().getPlayerGamemode().put(UUID.fromString(key), Gamemode.valueOf(jsonObject.get(key).getAsString().toUpperCase()));
			}
			reader.close();
		} catch (IOException e) {
			// TODO: Add an error message
		}
	}

	@Override
	public BufferedWriter getWriter() {
		return super.getWriter("data", "player-data");
	}

	@Override
	public BufferedReader getReader() {
		return super.getReader("data", "player-data");
	}

	public BufferedWriter getWriter(String directorySuffix) {
		return super.getWriter(directorySuffix, "player-data");
	}

	public BufferedReader getReader(String directorySuffix) {
		return super.getReader(directorySuffix, "player-data");
	}

}
