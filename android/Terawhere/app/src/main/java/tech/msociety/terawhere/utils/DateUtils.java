package tech.msociety.terawhere.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    
    private static SimpleDateFormat getLocalizedFormatter(SimpleDateFormat simpleDateFormat) {
        simpleDateFormat = (SimpleDateFormat) simpleDateFormat.clone();
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        return simpleDateFormat;
    }
    
    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, LOCALE);
        return simpleDateFormat.format(date);
    }
    
    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    
    public static String toFriendlyTimeString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FRIENDLY_TIME_FORMAT, LOCALE);
        return getLocalizedFormatter(simpleDateFormat).format(date);
    }
    
    public static Date fromFriendlyTimeString(String timeString) {
        Date date = null;
    
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FRIENDLY_TIME_FORMAT, LOCALE);
        
        try {
            date = simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return date;
    }
    
    public static Date mysqlDateTimeStringToDate(String mysqlDateTimeString) {
        // Assume this function only used to convert backend date to local date

        if (mysqlDateTimeString == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MYSQL_DATE_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Date date = null;
        
        try {
            date = simpleDateFormat.parse(mysqlDateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }
    
    public static Date getDateFromDates(Date dateWithDateComponent, Date dateWithTimeComponent) {
        Calendar calendarWithDateComponent = Calendar.getInstance();
        calendarWithDateComponent.setTime(dateWithDateComponent);
        
        Calendar calendarWithTimeComponent = Calendar.getInstance();
        calendarWithTimeComponent.setTime(dateWithTimeComponent);
        
        Calendar calendar = Calendar.getInstance();
        
        calendar.set(Calendar.YEAR, calendarWithDateComponent.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendarWithDateComponent.get(Calendar.MONTH));
        calendar.set(Calendar.DATE, calendarWithDateComponent.get(Calendar.DATE));
        
        calendar.set(Calendar.HOUR_OF_DAY, calendarWithTimeComponent.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendarWithTimeComponent.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendarWithTimeComponent.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendarWithTimeComponent.get(Calendar.MILLISECOND));
        
        return calendar.getTime();
    }
}
