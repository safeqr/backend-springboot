package com.safeqr.app.utils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class DateParsingUtils {

    private DateParsingUtils() {
        // private constructor to hide the implicit public one
    }

    private static final DateTimeFormatter INPUT_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("EEE, ")
            .appendPattern("[ ]") // This makes a single space optional
            .appendPattern("[ ]") // This allows for a second optional space
            .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
            .appendPattern(" MMM yyyy HH:mm:ss Z")
            .toFormatter(Locale.ENGLISH);

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

    public static String parseAndFormatDate(String inputDate) {
        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(inputDate, INPUT_FORMATTER);
            return dateTime.format(OUTPUT_FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing date: " + inputDate, e);
        }
    }

    public static OffsetDateTime parseDate(String inputDate) {
        try {
            return OffsetDateTime.parse(inputDate, INPUT_FORMATTER);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing date: " + inputDate, e);
        }
    }
}