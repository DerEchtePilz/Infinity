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

package io.github.derechtepilz.infinity.gamemode.serializer

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64

object EffectSerializer {

	@JvmStatic
	fun serialize(player: Player): String {
		val outputStream = ByteArrayOutputStream()
		val bukkitOutputStream = BukkitObjectOutputStream(outputStream)
		val jsonArray = JsonArray()
		for (effect in player.activePotionEffects) {
			val jsonObject = JsonObject()
			jsonObject.addProperty("name", effect.type.name)
			jsonObject.addProperty("duration", effect.duration)
			jsonObject.addProperty("amplifier", effect.amplifier)
			jsonObject.addProperty("ambient", effect.isAmbient)
			jsonObject.addProperty("infinite", effect.isInfinite)
			jsonObject.addProperty("particle", effect.hasParticles())
			jsonObject.addProperty("icon", effect.hasIcon())
			jsonArray.add(jsonObject)
		}
		val potionJsonString = GsonBuilder().create().toJson(jsonArray)
		bukkitOutputStream.write(potionJsonString.toByteArray())
		bukkitOutputStream.close()
		return Base64.getEncoder().encodeToString(outputStream.toByteArray())
	}

	@JvmStatic
	fun deserialize(data: String): MutableList<PotionEffect> {
		val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(data))
		val bukkitInputStream = BukkitObjectInputStream(inputStream)
		val jsonString = String(bukkitInputStream.readAllBytes())
		val jsonArray = JsonParser.parseString(jsonString).asJsonArray
		val potionEffects: MutableList<PotionEffect> = mutableListOf()
		for (i in 0 until jsonArray.size()) {
			val jsonObject = jsonArray[0].asJsonObject
			val type = PotionEffectType.getByName(jsonObject["name"].asString)!!
			val duration = jsonObject["duration"].asInt
			val amplifier = jsonObject["amplifier"].asInt
			val ambient = jsonObject["ambient"].asBoolean
			val infinite = jsonObject["infinite"].asBoolean
			val particle = jsonObject["particle"].asBoolean
			val icon = jsonObject["icon"].asBoolean
			val potionEffect = PotionEffect(type, if (infinite) PotionEffect.INFINITE_DURATION else duration, amplifier, ambient, particle, icon)
			potionEffects.add(potionEffect)
		}
		return potionEffects
	}

}