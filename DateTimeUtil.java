package org.ifzen.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateTimeUtil {

    public static String YYYYMMDD_0 = "yyyy-MM-dd";
    public static String YYYYMMDD_1 = "yyyyMMdd";
    public static String YYYYMMDDHHMmmss = "yyyyMMddHHmmss";
    public static String YYYYMMDD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static int MAX_ERR_MSG_INTERVAL_IN_MINUTES = 60;

    public static Date today_000000() {
        return DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    }

    /**
     * possible pattern: yyyy-MM-dd, yyyyMMdd
     */
    public static String today_000000(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String todayPlusOffset(long offset, String pattern) {
        return LocalDate.now().plusDays(offset).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static Date todayPlusOffset(long offset) {
        return Date.from(LocalDate.now().plusDays(offset).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String dayPlusOffset(Date date, long offset, String pattern) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(offset).format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatByDefaultTimezone(Date date, String pattern) {
        return date.toInstant().atZone(ZoneId.of("Asia/Tokyo")).toLocalDate().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static Date dayPlusOffset(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);
        return calendar.getTime();
    }

    public static Date getOffsetFixedDateTime(Date date, int offset, Integer field, Integer hour, Integer minute, Integer second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (null != field) calendar.add(field, offset);
        if (null != hour) calendar.set(Calendar.HOUR_OF_DAY, hour);
        if (null != minute) calendar.set(Calendar.MINUTE, minute);
        if (null != second) calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    public static Date getOffsetDay(Date date, int offset, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, offset);
        return calendar.getTime();
    }

    public static String formatDefault(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(YYYYMMDD_0);
        return df.format(date);
    }

    public static String format(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return DateUtils.parseDate(dateStr, pattern);
        } catch (ParseException e) {
            log.info("parse exception", e);
        }
        return null;
    }

    public static Date parse_yyyyMMdd(String dateStr) {
        return parse(dateStr, YYYYMMDD_0);
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static int daysBetween(Date date1, Date date2) {
        LocalDate start = DateTimeUtil.toLocalDate(date1);
        LocalDate end = DateTimeUtil.toLocalDate(date2);
        int daysBetween = Period.between(start, end).getDays();
        assert(daysBetween >= 0);
        return daysBetween;
    }
}
