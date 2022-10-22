package io.github.fisher2911.kingdoms.util;

import java.util.Locale;

public class StringUtils {

    public static String capitalize(String s) {
        final String[] parts = s.split(" ");
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String part : parts) {
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT)).append(part.substring(1));
            if (index < parts.length - 1) builder.append(" ");
        }
        return builder.toString();
    }

}
