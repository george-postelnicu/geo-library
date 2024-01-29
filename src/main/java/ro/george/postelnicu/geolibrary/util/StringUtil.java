package ro.george.postelnicu.geolibrary.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringUtil {

    public static final String SPACE = " ";
    public static final String WILDCARD = "*";

    public static String splitCapitalizeAndJoin(String text) {
        return Arrays.stream(text.split(SPACE))
                .map(StringUtil::capitalizeFirstLetter)
                .collect(Collectors.joining(SPACE));
    }

    private static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.toLowerCase().substring(1);
    }

    private static boolean isWrongWildcard(String inputString) {
        if (inputString.contains(WILDCARD)) {
            return WILDCARD.equals(inputString) ||
                    (inputString.chars().filter(ch -> ch == WILDCARD.charAt(0)).count() > 2) ||
                    (inputString.length() < 4);
        } else return false;
    }

    public static boolean isBlank(String inputString) {
        return Objects.isNull(inputString) || inputString.trim().isBlank();
    }

    public static boolean isBlankOrWrongWildcard(String inputString) {
        return isBlank(inputString) || isWrongWildcard(inputString);
    }
}
