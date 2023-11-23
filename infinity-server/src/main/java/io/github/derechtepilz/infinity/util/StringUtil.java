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

public class StringUtil {

	private StringUtil() {
	}

	public static String capitalize(String toCapitalize) {
		String firstCharacter = toCapitalize.substring(0, 1);
		toCapitalize = toCapitalize.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
		return toCapitalize;
	}

	public static String normalize(Enum<?> value) {
		String[] wordArray = value.name().toLowerCase().split("_");
		StringBuilder normalizedValue = new StringBuilder();
		for (int i = 0; i < wordArray.length; i++) {
			normalizedValue.append((i == 0) ? capitalize(wordArray[i]) : wordArray[i]);
		}
		return normalizedValue.toString().strip();
	}

}
