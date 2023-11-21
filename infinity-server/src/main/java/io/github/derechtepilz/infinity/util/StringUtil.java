package io.github.derechtepilz.infinity.util;

public class StringUtil {

    private StringUtil() {}

    public static String capitalize(String toCapitalize) {
        String firstCharacter = toCapitalize.substring(0, 1);
        toCapitalize = toCapitalize.replaceFirst(firstCharacter, firstCharacter.toUpperCase());
        return toCapitalize;
    }

}
