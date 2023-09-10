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
	fun saveMap(parent: JsonObject, key: String, map: MutableMap<UUID, MutableList<String>>) {
		val dataArray = JsonArray()
		for (uuid in map.keys) {
			val jsonObject = JsonObject()
			var savedProperties = 0
			jsonObject.addProperty("0", uuid.toString())
			savedProperties += 1
			val dataList = map[uuid]!!
			for (s in dataList) {
				jsonObject.addProperty(savedProperties.toString(), s)
				savedProperties += 1
			}
			dataArray.add(jsonObject)
		}
		parent.add(key, dataArray)
	}

	@JvmStatic
	fun <T> loadMap(jsonArray: JsonArray, firstObjectValueType: (String) -> T): SaveUtil<T, MutableList<String>> {
		val loadedMap: MutableMap<T, MutableList<String>> = mutableMapOf()
		for (i in 0 until jsonArray.size()) {
			val dataObject = getObject(i, jsonArray)
			var readValues = 0
			var mapKey: T? = null
			val mapValues: MutableList<String> = mutableListOf()
			for (key in dataObject.keySet()) {
				if (readValues == 0) {
					mapKey = firstObjectValueType.invoke(dataObject[key].asString)
					readValues += 1
					continue
				}
				mapValues.add(dataObject[key].asString)
			}
			loadedMap[mapKey!!] = mapValues
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