package com.jdapplications.gcgaming.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by danielhartwich on 1/2/15.
 */
public class DateFormatter {
    public static String formatJSONISO8601Date(String inputJSONDate) {
        String output = "-";
        if (inputJSONDate != "null") {
//            DateTimeZone timeZone = DateTimeZone.forID("Europe/Berlin");
            // Usually better to specify a time zone than rely on default.
            DateTimeFormatter formatter = DateTimeFormat.forPattern("EEE dd.MMMMM yyyy HH:mm");
            // Parse string into a DateTime. Passing to constructor conveniently uses the built-in ISO 8601 parser built into DateTime class.
            DateTime dateTime = new DateTime(inputJSONDate, DateTimeZone.UTC);
            // Render date-time as a string in a particular format.
            output = formatter.print(dateTime);
        }
        return output+ " Uhr";
    }
}
