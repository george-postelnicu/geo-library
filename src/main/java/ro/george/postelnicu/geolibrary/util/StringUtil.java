package ro.george.postelnicu.geolibrary.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class StringUtil {

    public static final String SPACE = " ";

    public static String splitCapitalizeAndJoin(String text) {
        return Arrays.stream(text.split(SPACE))
                .map(StringUtil::capitalizeFirstLetter)
                .collect(Collectors.joining(SPACE));
    }

    public static Set<String> splitCapitalizeAndJoins(Set<String> texts) {
        return texts.stream()
                .map(StringUtil::splitCapitalizeAndJoin)
                .collect(Collectors.toSet());
    }

    private static String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.toLowerCase().substring(1);
    }

    private static Set<String> capitalizeFirstLetters(Set<String> texts) {
        return texts.stream()
                .map(StringUtil::capitalizeFirstLetter)
                .collect(Collectors.toSet());
    }
}
