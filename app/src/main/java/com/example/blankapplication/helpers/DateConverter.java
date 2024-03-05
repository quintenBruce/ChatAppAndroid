package com.example.blankapplication.helpers;
import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {
    private static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @TypeConverter
    public static Date fromIsoString(String value) {
        try {
            if (value != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
                return sdf.parse(value);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String toIsoString(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_DATE_FORMAT, Locale.getDefault());
            return sdf.format(date);
        }
        return null;
    }
}
