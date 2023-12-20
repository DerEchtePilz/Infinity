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

public class HealthHungerSerializer extends Serializer {

	private static HealthHungerSerializer instance;

	private HealthHungerSerializer() {}

	public static String serialize(Player player) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitOutputStream = new BukkitObjectOutputStream(outputStream);
			bukkitOutputStream.writeDouble(player.getHealth());
			bukkitOutputStream.writeInt(player.getFoodLevel());
			bukkitOutputStream.writeFloat(player.getSaturation());
			bukkitOutputStream.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while serializing health and hunger values. This might be a bug. Please report this!");
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
			double health = bukkitInputStream.readDouble();
			int foodLevel = bukkitInputStream.readInt();
			float saturation = bukkitInputStream.readFloat();
			return new ArrayList<>(List.of(health, foodLevel, saturation));
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was an error while deserializing health and hunger values. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	public static String createBackup(Player player) {
		String currentGamemodeData = serialize(player);
		String minecraftData = Infinity.getInstance().getMinecraftData().getHealthHungerData().getOrDefault(player.getUniqueId(), null);
		String infinityData = Infinity.getInstance().getInfinityData().getHealthHungerData().getOrDefault(player.getUniqueId(), null);
		return getInstance().buildBackupString(currentGamemodeData, minecraftData, infinityData);
	}

	public static HealthHungerSerializer getInstance() {
		if (instance == null) {
			instance = new HealthHungerSerializer();
		}
		return instance;
	}

}
