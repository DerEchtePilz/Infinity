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
