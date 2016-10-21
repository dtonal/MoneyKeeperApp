package de.dtonal.moneykeeperapp.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Simple util to handle messy Date class in POST-LocalDate (Java 8) times.
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

    public static int getMonth(Date date)
    {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) +1;
    }

    public static Date firstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }


    public static Date lastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date addMonth(int i, Date startDate) {
        Calendar calendar = Calendar.getInstance(Locale.GERMANY);
        calendar.setTime(startDate);
        calendar.add(Calendar.MONTH, i);
        return calendar.getTime();
    }
}
