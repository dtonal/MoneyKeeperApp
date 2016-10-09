package de.dtonal.moneykeeperapp;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dtonal on 08.10.16.
 */

public class DateUtil {
    public static Date firstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        return calendar.getTime();
    }


    public static Date lastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        return calendar.getTime();
    }


    public static int calenderWeek(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Date addDays(int numberOfDaysToAdd, Date startDate)
    {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, numberOfDaysToAdd);
        return calendar.getTime();
    }

    public static int getYear(Date date)
    {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
