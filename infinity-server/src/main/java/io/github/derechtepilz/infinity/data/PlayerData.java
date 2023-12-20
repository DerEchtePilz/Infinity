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
	public void saveData() {
		try {
			JsonObject playerGamemode = new JsonObject();

			for (UUID uuid : Infinity.getInstance().getPlayerGamemode().keySet()) {
				playerGamemode.addProperty(uuid.toString(), Infinity.getInstance().getPlayerGamemode().get(uuid).name().toLowerCase());
			}

			String jsonString = new GsonBuilder().setPrettyPrinting().create().toJson(playerGamemode);

			getWriter().write(jsonString);
			getWriter().close();
		} catch (IOException e) {
			// TODO: Add an error message
		}
	}

	@Override
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

			for (String key : jsonObject.keySet()) {
				Infinity.getInstance().getPlayerGamemode().put(UUID.fromString(key), Gamemode.valueOf(jsonObject.get(key).getAsString().toUpperCase()));
			}
			getReader().close();
		} catch (IOException e) {
			// TODO: Add an error message
		}
	}

	@Override
	public BufferedWriter getWriter() {
		return super.getWriter("player-data");
	}

	@Override
	public BufferedReader getReader() {
		return super.getReader("player-data");
	}

}
