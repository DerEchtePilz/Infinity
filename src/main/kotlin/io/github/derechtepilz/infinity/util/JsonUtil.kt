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

package io.github.derechtepilz.infinity.util

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*

object JsonUtil {

	@JvmStatic
	fun getArray(name: String, parent: JsonObject): JsonArray {
		return parent[name].asJsonArray
	}

	@JvmStatic
	fun getObject(index: Int, parent: JsonArray): JsonObject {
		return parent[index].asJsonObject
	}

	@JvmStatic
	fun <T> loadMap(jsonObject: JsonObject, mapKey: String, mapKeyType: (String) -> T, mapValues: MutableList<String>): JsonObjectSaveUtil<T, MutableList<String>> {
		val key: T = mapKeyType.invoke(jsonObject[mapKey].asString)
		val otherValues: MutableList<String> = mutableListOf()
		for (s in mapValues) {
			otherValues.add(jsonObject[s].asString)
		}
		return JsonObjectSaveUtil(key, otherValues)
	}

	@JvmStatic
	fun saveMap(parent: JsonObject, key: String, map: MutableMap<UUID, String>) {
		val dataArray = JsonArray()
		for (uuid in map.keys) {
			val jsonObject = JsonObject()
			jsonObject.addProperty("0", uuid.toString())
			jsonObject.addProperty("1", map[uuid]!!)
			dataArray.add(jsonObject)
		}
		parent.add(key, dataArray)
	}

	@JvmStatic
	fun <T> loadMap(jsonArray: JsonArray, firstObjectValueType: (String) -> T): SaveUtil<T, String> {
		val loadedMap: MutableMap<T, String> = mutableMapOf()
		for (i in 0 until jsonArray.size()) {
			val dataObject = getObject(i, jsonArray)
			var readValues = 0
			var mapKey: T? = null
			var mapValues: String? = null
			for (key in dataObject.keySet()) {
				if (readValues == 0) {
					mapKey = firstObjectValueType.invoke(dataObject[key].asString)
					readValues += 1
					continue
				}
				mapValues = dataObject[key].asString
			}
			loadedMap[mapKey!!] = mapValues!!
		}
		return SaveUtil(loadedMap)
	}

	class SaveUtil<K, V>(private val loadedMap: MutableMap<K, V>) {

		fun saveTo(dataMap: MutableMap<K, V>) {
			for (key in loadedMap.keys) {
				dataMap[key] = loadedMap[key]!!
			}
		}

	}

	data class JsonObjectSaveUtil<K, V>(val key: K, val values: V)

}