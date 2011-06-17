package com.moogu.myweb.server.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.moogu.myweb.shared.utils.IlmsFormats;

/**
 * Complement to {@link DateUtils} from jakarta commons lang
 * 
 */
public final class DateHelper {

    public static final long MILLISECS_PER_MINUTE = 60 * 1000;

    public static final long MILLISECS_PER_HOUR = 60 * DateHelper.MILLISECS_PER_MINUTE;

    public static final long MILLISECS_PER_DAY = 24 * DateHelper.MILLISECS_PER_HOUR;

    public static Date parseDateTime(String dateString) {
        try {
            return new SimpleDateFormat(IlmsFormats.DATE_TIME_FORMAT).parse(dateString);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        } catch (final RuntimeException e) {
            throw new RuntimeException("Error while parsing date string '" + dateString
                            + "' with format pattern "
                            + IlmsFormats.DATE_TIME_FORMAT, e);
        }
    }

    public static String formatToDate(Date date) {
        return new SimpleDateFormat(IlmsFormats.DATE_FORMAT).format(date);
    }

    public static String formatToHour(Date date) {
        return new SimpleDateFormat(IlmsFormats.HOUR_FORMAT).format(date);
    }

    public static String formatToDateTime(Date date) {
        return new SimpleDateFormat(IlmsFormats.DATE_TIME_FORMAT).format(date);
    }

    public static String formatToDate(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * @param date string dd/MM/yyyy
     * @return
     */
    public static Date parseToValueDate(String date) {
        Date result = null;
        try {
            result = new SimpleDateFormat(IlmsFormats.DATE_FORMAT).parse(date);
        } catch (final Exception e) {

            throw new IllegalArgumentException(date + " is not parseable with dd/MM/yyyy", e);

        }
        return result;
    }

    public static Date parse(String date, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(date);
        } catch (final Exception e) {
            throw new IllegalArgumentException(date + " is not parseable with dd/MM/yyyy", e);
        }
    }

    public static Date now() {
        return new Date();
    }

    public static Date parseIso8601(String isoDate) {
        try {
            return new SimpleDateFormat(IlmsFormats.ISO8601_FORMAT).parse(isoDate);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(isoDate + " is not parseable");
        }
    }

    public static String formatIso8601(Date isoDate) {
        return new SimpleDateFormat(IlmsFormats.ISO8601_FORMAT).format(isoDate);
    }

    /**
     * Create a date with hour, min sec, millis set to 0
     */
    public static Date createMidnightDate(int year, int month, int dayOfmonth) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, dayOfmonth, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date createMidnightDate(Date date) {
        Date newDate = DateUtils.setHours(date, 0);
        newDate = DateUtils.setMinutes(newDate, 0);
        newDate = DateUtils.setSeconds(newDate, 0);
        newDate = DateUtils.setMilliseconds(newDate, 0);
        return newDate;
    }

    public static Date parse(DateFormat dateFormat, String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(dateString + " is not parseable");
        }
    }

    /**
     * Trim the millis
     * 
     * @param date
     * @return
     */
    public static Date trimMillis(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * (date2-date1)+1 in days<br>
     * we have to add 1 day, because it is not included. so if user has a period 21/07 to 23/07, the difference will
     * return 2 days. but user wants to know how many days we have in the range, and this is 3 days
     */
    public static long calculateDiff(Date startDate, Date endDate) {

        // generate calendars
        final Calendar start = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        start.setTime(startDate);
        end.setTime(endDate);

        //period from today to today should be showed as ONE DAY.
        long daysBetween = 1;

        while (start.before(end)) {
            start.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }
}
