package com.example.maola.dtourmap.Utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtils {
    public static String getDate(long milliSeconds, boolean time) {
        // Create a DateFormatter object for displaying date in specified format.
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Date date = new Date(milliSeconds);
        if(time){
           return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date);
        } else {
            return DateFormat.getDateInstance(DateFormat.FULL).format(date);
        }
    }
}
