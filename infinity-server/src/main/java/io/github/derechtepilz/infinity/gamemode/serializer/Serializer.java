package io.github.derechtepilz.infinity.gamemode.serializer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Serializer {

	protected String buildBackupString(String currentGamemodeData, String minecraftData, String infinityData) {
		try {
			JsonObject backupObject = new JsonObject();
			backupObject.addProperty("currentGamemodeData", currentGamemodeData);
			backupObject.addProperty("minecraftData", minecraftData);
			backupObject.addProperty("infinityData", infinityData);
			String backupJsonString = new GsonBuilder().create().toJson(backupObject);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);

			bukkitOutputStream.write(backupJsonString.getBytes());
			bukkitOutputStream.close();

			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while creating backup. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
		}
		return null;
	}

}
