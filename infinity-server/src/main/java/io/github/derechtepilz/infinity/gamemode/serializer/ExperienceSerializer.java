package io.github.derechtepilz.infinity.gamemode.serializer;

import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ExperienceSerializer {

	private ExperienceSerializer() {}

	public static String serialize(Player player) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
			bukkitOutputStream.writeInt(player.getLevel());
			bukkitOutputStream.writeFloat(player.getExp());
			bukkitOutputStream.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while serializing experience. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	public static List<? extends Number> deserialize(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
			BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
			int level = bukkitInputStream.readInt();
			float progress = bukkitInputStream.readFloat();
			bukkitInputStream.close();
			return new ArrayList<>(List.of(level, progress));
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while deserializing experience. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

}
