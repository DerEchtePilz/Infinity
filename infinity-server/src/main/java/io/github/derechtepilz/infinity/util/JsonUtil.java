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

package io.github.derechtepilz.infinity.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.function.Function;

public class JsonUtil {

	private JsonUtil() {
	}

	public static JsonArray getArray(String name, JsonObject parent) {
		return parent.get(name).getAsJsonArray();
	}

	public static JsonObject getObject(int index, JsonArray parent) {
		return parent.get(index).getAsJsonObject();
	}

	@SuppressWarnings("ClassEscapesDefinedScope")
	public static <T> JsonObjectSaveUtil<T, List<String>> loadMap(JsonObject jsonObject, String mapKey, Function<String, T> mapKeyType, List<String> mapValues) {
		T key = mapKeyType.apply(jsonObject.get(mapKey).getAsString());
		List<String> otherValues = new ArrayList<>();
		for (String s : mapValues) {
			otherValues.add(jsonObject.get(s).getAsString());
		}
		return new JsonObjectSaveUtil<>(key, otherValues);
	}

	public static void saveMap(JsonObject parent, String key, Map<UUID, String> map) {
		JsonArray dataArray = new JsonArray();
		for (UUID uuid : map.keySet()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("0", uuid.toString());
			jsonObject.addProperty("1", map.get(uuid));
			dataArray.add(jsonObject);
		}
		parent.add(key, dataArray);
	}

	public static <T> SaveUtil<T, String> loadMap(JsonArray jsonArray, Function<String, T> firstObjectValueType) {
		Map<T, String> loadedMap = new HashMap<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject dataObject = getObject(i, jsonArray);
			int readValues = 0;
			T mapKey = null;
			String mapValues = null;
			for (String key : dataObject.keySet()) {
				if (readValues == 0) {
					mapKey = firstObjectValueType.apply(dataObject.get(key).getAsString());
					readValues += 1;
					continue;
				}
				mapValues = dataObject.get(key).getAsString();
			}
			loadedMap.put(mapKey, mapValues);
		}
		return new SaveUtil<>(loadedMap);
	}

	record JsonObjectSaveUtil<K, V>(K key, V values) {
	}

	public static class SaveUtil<K, V> {

		private final Map<K, V> loadedMap;

		public SaveUtil(Map<K, V> loadedMap) {
			this.loadedMap = loadedMap;
		}

		public void saveTo(Map<K, V> dataMap) {
			for (K key : loadedMap.keySet()) {
				dataMap.put(key, loadedMap.get(key));
			}
		}

	}

}
