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

import java.util.Map;

public class SaveUtil<K, V> {

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
