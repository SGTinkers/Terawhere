package tech.msociety.terawhere.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static final String FRIENDLY_DATE_FORMAT = "dd MMM yyyy";
    private static final String FRIENDLY_TIME_FORMAT = "KK:mm a";
    private static final String FRIENDLY_DATE_TIME_FORMAT = "dd MMM yyyy, KK:mm:ss a";

    private static final SimpleDateFormat friendlyDateFormatter = new SimpleDateFormat(FRIENDLY_DATE_FORMAT, Locale.UK);
    private static final SimpleDateFormat friendlyTimeFormatter = new SimpleDateFormat(FRIENDLY_TIME_FORMAT, Locale.UK);
    private static final SimpleDateFormat friendlyDateTimeFormatter = new SimpleDateFormat(FRIENDLY_DATE_TIME_FORMAT, Locale.UK);

    private static SimpleDateFormat getLocalizedFormatter(SimpleDateFormat simpleDateFormat) {
        simpleDateFormat = (SimpleDateFormat) simpleDateFormat.clone();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat;
    }

    public static Date twentyFourHoursBefore(Date date) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static Date twentyFourHoursAfter(Date date) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static Date nextWeek(Date date) {
        Calendar calendar = toCalendar(date);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        return calendar.getTime();
    }

    public static int numberOfDays(Date startDateTime, Date endDateTime) throws ParseException {
        Date start = fromFriendlyDateString(toFriendlyDateString(startDateTime));
        Date end = fromFriendlyDateString(toFriendlyDateString(endDateTime));

        long duration = start.getTime() - end.getTime();

        return (int) TimeUnit.DAYS.convert(duration, TimeUnit.MILLISECONDS);
    }

    public static String nextFriendlyDateString(String date) throws ParseException {
        Calendar calendar = toCalendar(fromFriendlyDateString(date));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return toFriendlyDateString(calendar.getTime());
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static Date dateBetween(Date lower, Date upper) {
        return null;
//        return (new Faker()).date().between(lower, upper);
    }

    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String toFriendlyDateString(Date date) {
        return friendlyDateFormatter.format(date);
    }

    public static Date fromFriendlyDateString(String dateString) throws ParseException {
        return friendlyDateFormatter.parse(dateString);
    }

    public static String toFriendlyTimeString(Date date) {
        return getLocalizedFormatter(friendlyTimeFormatter).format(date);
    }

    public static Date fromFriendlyTimeString(String timeString) throws ParseException {
        return friendlyTimeFormatter.parse(timeString);
    }

    public static String toFriendlyDateTimeString(Date date) {
        return getLocalizedFormatter(friendlyDateTimeFormatter).format(date);
    }

//    public static Date fromFriendlyDateTimeString(String timeString) throws ParseException {
//        return friendlyDateTimeFormatter.parse(timeString);
//    }

    public static boolean dateIsInThePast(Date date) {
        return (date.getTime() < (new Date()).getTime());
    }
}
