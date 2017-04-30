package tech.msociety.terawhere.utils.faker;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class Faker {
    private Random random;

    public Faker() {
        this.random = new Random();
    }

    public Object randomElement(Object[] objects) {
        return objects[(new Random().nextInt(objects.length))];
    }

    public long numberBetween(long start, long end) {
        return start + Math.round(Math.random() * (end - start));
    }

    public long number(int numDigits) {
        StringBuilder stringBuilder = new StringBuilder(numDigits);

        for (int i = 0; i < numDigits; i++) {
            stringBuilder.append((char) ('0' + random.nextInt(10)));
        }

        return Long.parseLong(stringBuilder.toString());
    }

    public String words(int numWords) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < numWords; i++) {
            stringBuilder.append(randomElement(FakerLoremIpsum.LOREM_IPSUM_WORDS));
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    public boolean randomBoolean() {
        return random.nextBoolean();
    }

    public Date dateBetween(Date startDate, Date endDate) {
        long newDateAsLong = numberBetween(startDate.getTime(), endDate.getTime());
        return new Date(newDateAsLong);
    }

    public Date dateBeforeToday() {
        return dateBetween(new Date(0), new Date());
    }

    public Date dateFromToday(int calendarField, int offset) {
        Date startDate = new Date();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(calendarField, endCalendar.get(calendarField) + offset);

        return dateBetween(startDate, endCalendar.getTime());
    }

    public Date dateWithOffset(Date anchorDate, int calendarField, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(anchorDate);

        calendar.add(calendarField, offset);

        return calendar.getTime();
    }

    public String name() {
        return (String) randomElement(FakerName.NAMES);
    }

    public String phoneNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(numberBetween(8, 9));
        stringBuilder.append(number(7));
        return stringBuilder.toString();
    }
}
