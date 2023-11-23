package cn.sct.baseflink.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * @author bigdata_dpy
 */
public class TimeUtil {
    public static String timeStamp2SecondTime(long seconds) {
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.CHINA);
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(seconds), ZoneId.systemDefault()));
    }

    public static String timeStamp2MillisecondTime(long seconds) {
        String format = "yyyy-MM-dd HH:mm:ss.SSS";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.CHINA);
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(seconds), ZoneId.systemDefault()));
    }

    public static String timeStamp2NetMillisecondTime(long seconds) {
        String format = "yyyy-MM-dd HH:mm:ss.000";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.CHINA);
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(seconds), ZoneId.systemDefault()));
    }

    public static String timeStamp2Date(long seconds) {
        String format = "yyyy-MM-dd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.CHINA);
        return formatter.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(seconds), ZoneId.systemDefault()));
    }

    public static String time2Date(String date) {
        String inputFormat = "yyyy-MM-dd HH:mm:ss";
        String outputFormat = "yyyy-MM-dd";

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputFormat, Locale.CHINA);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(outputFormat, Locale.CHINA);

        LocalDateTime localDateTime = LocalDateTime.parse(date, inputFormatter);
        return localDateTime.format(outputFormatter);
    }
}