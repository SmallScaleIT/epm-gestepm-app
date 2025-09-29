package com.epm.gestepm.emailapi.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmailUtils {

    public static String transform(final LocalDateTime offsetDateTime, final String format) {
        return offsetDateTime != null
                ? offsetDateTime.format(DateTimeFormatter.ofPattern(format))
                : "(NULL)";
    }
}
