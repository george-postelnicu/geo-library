package ro.george.postelnicu.geolibrary.util;

import java.util.Arrays;
import java.util.stream.Collectors;

public class StringUtil {
    public static final String WILDCARD = "*";
    public static final String LIKE = "%";
    public static final String SPACE = " ";

    public static String splitCapitalizeAndJoin(String text) {
        return Arrays.stream(text.split(SPACE))
                .map(StringUtil::capitalizeFirstLetter)
                .collect(Collectors.joining(SPACE));
    }

    private static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.toLowerCase().substring(1);
    }

}
