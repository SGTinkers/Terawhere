package tech.msociety.terawhere.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static final String MYSQL_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MYSQL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String MYSQL_TIME_FORMAT = "HH:mm:ss";
    public static final String FRIENDLY_DATE_FORMAT = "dd MMM yyyy";
    public static final String FRIENDLY_TIME_FORMAT = "KK:mm a";
    public static final String FRIENDLY_DATE_TIME_FORMAT = "dd MMM yyyy, KK:mm:ss a";
    public static final String DAY_OF_MONTH_FORMAT = "dd";
    public static final String MONTH_ABBREVIATED_FORMAT = "MMMM";
    public static final Locale LOCALE = Locale.UK;
    
    private static final SimpleDateFormat friendlyDateFormatter = new SimpleDateFormat(FRIENDLY_DATE_FORMAT, LOCALE);
    private static final SimpleDateFormat friendlyTimeFormatter = new SimpleDateFormat(FRIENDLY_TIME_FORMAT, LOCALE);
    private static final SimpleDateFormat friendlyDateTimeFormatter = new SimpleDateFormat(FRIENDLY_DATE_TIME_FORMAT, LOCALE);

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
    
    public static String toString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, LOCALE);
        return simpleDateFormat.format(date);
    }
    
    public static Date fromFriendlyTimeString(String timeString) {
        Date date = null;
        
        try {
            date = friendlyTimeFormatter.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
    }

    public static String toFriendlyDateTimeString(Date date) {
        return getLocalizedFormatter(friendlyDateTimeFormatter).format(date);
    }

//    public static Date fromFriendlyDateTimeString(String timeString) {
//        try {
//            return friendlyDateTimeFormatter.parse(timeString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    public static boolean dateIsInThePast(Date date) {
        return (date.getTime() < (new Date()).getTime());
    }
    
    public static Date fromMysqlDateTimeString(String mysqlDateTimeString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MYSQL_DATE_TIME_FORMAT, LOCALE);
        
        Date date = null;
        
        try {
            date = simpleDateFormat.parse(mysqlDateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
    }
}
