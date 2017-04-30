package tech.msociety.terawhere.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RandomUtils {
    public static int numberBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static Date date() {
        int year = RandomUtils.numberBetween(1970, 2018);
        int month = RandomUtils.numberBetween(0, 11);
        int hour = RandomUtils.numberBetween(9, 22);
        int min = RandomUtils.numberBetween(0, 59);
        int sec = RandomUtils.numberBetween(0, 59);

        GregorianCalendar gregorianCalendar = new GregorianCalendar(year, month, 1);
        int day = RandomUtils.numberBetween(1, gregorianCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, min, sec);

        return calendar.getTime();
    }
}
