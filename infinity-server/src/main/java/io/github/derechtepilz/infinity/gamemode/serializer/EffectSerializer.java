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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.derechtepilz.infinity.Infinity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EffectSerializer extends Serializer {

	private static EffectSerializer instance;

	private EffectSerializer() {
	}

	public static String serialize(Player player) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream);
			JsonArray jsonArray = new JsonArray();
			for (PotionEffect effect : player.getActivePotionEffects()) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", effect.getType().getName());
				jsonObject.addProperty("duration", effect.getDuration());
				jsonObject.addProperty("amplifier", effect.getAmplifier());
				jsonObject.addProperty("ambient", effect.isAmbient());
				jsonObject.addProperty("infinite", effect.isInfinite());
				jsonObject.addProperty("particle", effect.hasParticles());
				jsonObject.addProperty("icon", effect.hasIcon());
				jsonArray.add(jsonObject);
			}
			String potionJsonString = new GsonBuilder().create().toJson(jsonArray);
			bukkitObjectOutputStream.write(potionJsonString.getBytes());
			bukkitObjectOutputStream.close();
			return Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while serializing potion effects. This might be a bug! Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	public static List<PotionEffect> deserialize(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
			BukkitObjectInputStream bukkitInputStream = new BukkitObjectInputStream(inputStream);
			String jsonString = new String(bukkitInputStream.readAllBytes());
			JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
			List<PotionEffect> potionEffects = new ArrayList<>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
				PotionEffectType type = PotionEffectType.getByName(jsonObject.get("name").getAsString());
				int duration = jsonObject.get("duration").getAsInt();
				int amplifier = jsonObject.get("amplifier").getAsInt();
				boolean ambient = jsonObject.get("ambient").getAsBoolean();
				boolean infinite = jsonObject.get("infinite").getAsBoolean();
				boolean particle = jsonObject.get("particle").getAsBoolean();
				boolean icon = jsonObject.get("icon").getAsBoolean();
				PotionEffect potionEffect = new PotionEffect(type, (infinite) ? PotionEffect.INFINITE_DURATION : duration, amplifier, ambient, particle, icon);
				potionEffects.add(potionEffect);
			}
			return potionEffects;
		} catch (IOException e) {
			Infinity.getInstance().getLogger().severe("There was a problem while deserializing potion effects. This might be a bug. Please report this!");
			for (StackTraceElement stackTraceElement : e.getStackTrace()) {
				Infinity.getInstance().getLogger().severe(stackTraceElement.toString());
			}
			return null;
		}
	}

	public static String createBackup(Player player) {
		String currentGamemodeData = serialize(player);
		String savedGamemodeData = Infinity.getInstance().getPotionEffectData().getOrDefault(player.getUniqueId(), null);
		return getInstance().buildBackupString(currentGamemodeData, savedGamemodeData);
	}

	public static EffectSerializer getInstance() {
		if (instance == null) {
			instance = new EffectSerializer();
		}
		return instance;
	}

}
