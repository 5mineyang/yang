package com.mineyang.yang.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateUtils {

    public enum DateStyle {
        NONE(""),

        YYYY_MM("yyyy-MM"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss.SSS"),
        MM_DD("MM-dd"),
        MM_DD_HH_MM("MM-dd HH:mm"),
        MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),

        YYYY_MM_EN("yyyy/MM"),
        YYYY_MM_DD_EN("yyyy/MM/dd"),
        YYYY_MM_DD_HH_MM_EN("yyyy/MM/dd HH:mm"),
        YYYY_MM_DD_HH_MM_SS_EN("yyyy/MM/dd HH:mm:ss"),
        MM_DD_EN("MM/dd"),
        MM_DD_HH_MM_EN("MM/dd HH:mm"),
        MM_DD_HH_MM_SS_EN("MM/dd HH:mm:ss"),

        YYYY_MM_CN("yyyy年MM月"),
        YYYY_MM_DD_CN("yyyy年MM月dd日"),
        YYYY_MM_DD_HH_MM_CN("yyyy年MM月dd日 HH:mm"),
        YYYY_MM_DD_HH_MM_SS_CN("yyyy年MM月dd日 HH:mm:ss"),
        MM_DD_CN("MM月dd日"),
        MM_DD_HH_MM_CN("MM月dd日 HH:mm"),
        MM_DD_HH_MM_SS_CN("MM月dd日 HH:mm:ss"),

        HH_MM("HH:mm"),
        HH_MM_SS("HH:mm:ss");

        private String value;

        DateStyle(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static enum Week {

        MONDAY("星期一", "Monday", "Mon.", 1),
        TUESDAY("星期二", "Tuesday", "Tues.", 2),
        WEDNESDAY("星期三", "Wednesday", "Wed.", 3),
        THURSDAY("星期四", "Thursday", "Thur.", 4),
        FRIDAY("星期五", "Friday", "Fri.", 5),
        SATURDAY("星期六", "Saturday", "Sat.", 6),
        SUNDAY("星期日", "Sunday", "Sun.", 7);

        String name_cn;
        String name_en;
        String name_enShort;
        int number;

        Week(String name_cn, String name_en, String name_enShort, int number) {
            this.name_cn = name_cn;
            this.name_en = name_en;
            this.name_enShort = name_enShort;
            this.number = number;
        }

        public String getChineseName() {
            return name_cn;
        }

        public String getName() {
            return name_en;
        }

        public String getShortName() {
            return name_enShort;
        }

        public int getNumber() {
            return number;
        }
    }

    /**
     * 获取SimpleDateFormat
     *
     * @param template 日期格式
     * @return SimpleDateFormat对象
     * @throws RuntimeException 异常：非法日期格式
     */
    private static SimpleDateFormat getDateFormat(String template) throws RuntimeException {
        return new SimpleDateFormat(template, Locale.getDefault());
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式
     * @return 数值
     */
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期字符串
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期字符串
     */
    private static String addInteger(String date, int dateType, int amount) {
        String dateString = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            myDate = addInteger(myDate, dateType, amount);
            dateString = DateToString(myDate, dateStyle);
        }
        return dateString;
    }

    /**
     * 增加日期中某类型的某数值。如增加日期
     *
     * @param date     日期
     * @param dateType 类型
     * @param amount   数值
     * @return 计算后日期
     */
    private static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(dateType, amount);
            myDate = calendar.getTime();
        }
        return myDate;
    }

    /**
     * 获取精确的日期
     *
     * @param timestamps 时间long集合
     * @return 日期
     */
    private static Date getAccurateDate(List<Long> timestamps) {
        Date date = null;
        long timestamp = 0;
        Map<Long, long[]> map = new HashMap<>();
        List<Long> absoluteValues = new ArrayList<>();

        if (timestamps != null && timestamps.size() > 0) {
            if (timestamps.size() > 1) {
                for (int i = 0; i < timestamps.size(); i++) {
                    for (int j = i + 1; j < timestamps.size(); j++) {
                        long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
                        absoluteValues.add(absoluteValue);
                        long[] timestampTmp = {timestamps.get(i), timestamps.get(j)};
                        map.put(absoluteValue, timestampTmp);
                    }
                }

                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的
                long minAbsoluteValue = -1;
                if (!absoluteValues.isEmpty()) {
                    // 如果timestamps的size为2，这是差值只有一个，因此要给默认值
                    minAbsoluteValue = absoluteValues.get(0);
                }
                for (int i = 0; i < absoluteValues.size(); i++) {
                    for (int j = i + 1; j < absoluteValues.size(); j++) {
                        if (absoluteValues.get(i) > absoluteValues.get(j)) {
                            minAbsoluteValue = absoluteValues.get(j);
                        } else {
                            minAbsoluteValue = absoluteValues.get(i);
                        }
                    }
                }

                if (minAbsoluteValue != -1) {
                    long[] timestampsLastTmp = map.get(minAbsoluteValue);
                    if (absoluteValues.size() > 1) {
                        timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                    } else if (absoluteValues.size() == 1) {
                        // 当timestamps的size为2，需要与当前时间作为参照
                        long dateOne = timestampsLastTmp[0];
                        long dateTwo = timestampsLastTmp[1];
                        if ((Math.abs(dateOne - dateTwo)) < 100000000000L) {
                            timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                        } else {
                            long now = new Date().getTime();
                            if (Math.abs(dateOne - now) <= Math.abs(dateTwo - now)) {
                                timestamp = dateOne;
                            } else {
                                timestamp = dateTwo;
                            }
                        }
                    }
                }
            } else {
                timestamp = timestamps.get(0);
            }
        }

        if (timestamp != 0) {
            date = new Date(timestamp);
        }
        return date;
    }

    /**
     * 判断字符串是否为日期字符串
     *
     * @param date 日期字符串
     * @return true or false
     */
    public static boolean isDate(String date) {
        boolean isDate = false;
        if (date != null) {
            if (StringToDate(date) != null) {
                isDate = true;
            }
        }
        return isDate;
    }

    /**
     * 获取日期字符串的日期风格。失敗返回null。
     *
     * @param date 日期字符串
     * @return 日期风格
     */
    public static DateStyle getDateStyle(String date) {
        DateStyle dateStyle;
        Map<Long, DateStyle> map = new HashMap<>();
        List<Long> timestamps = new ArrayList<>();
        for (DateStyle style : DateStyle.values()) {
            Date dateTmp = StringToDate(date, style.getValue());
            if (dateTmp != null) {
                timestamps.add(dateTmp.getTime());
                map.put(dateTmp.getTime(), style);
            }
        }
        dateStyle = map.get(getAccurateDate(timestamps).getTime());
        return dateStyle;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static Date StringToDate(String date) {
        return StringToDate(date, DateStyle.NONE);
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date     日期字符串
     * @param template 日期格式
     * @return 日期
     */
    public static Date StringToDate(String date, String template) {
        return StringToDate(date, TimeZone.getDefault(), template);
    }

    public static Date StringToDate(String date, TimeZone timeZone, String template) {
        Date myDate = null;
        if (date != null) {
            try {
                SimpleDateFormat sf = getDateFormat(template);
                sf.setTimeZone(timeZone);
                myDate = sf.parse(date);
            } catch (Exception e) {
                Logger.e(DateUtils.class, e);
            }
        }
        return myDate;
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date      日期字符串
     * @param dateStyle 日期风格
     * @return 日期
     */
    public static Date StringToDate(String date, DateStyle dateStyle) {
        return StringToDate(date, TimeZone.getDefault(), dateStyle);
    }

    /**
     * 将日期字符串转化为日期。失败返回null。
     *
     * @param date      日期字符串
     * @param timeZone  时区
     * @param dateStyle 日期风格
     * @return 日期
     */
    public static Date StringToDate(String date, TimeZone timeZone, DateStyle dateStyle) {
        Date myDate;
        if (dateStyle == null || dateStyle == DateStyle.NONE) {
            List<Long> timestamps = new ArrayList<>();
            for (DateStyle style : DateStyle.values()) {
                Date dateTmp = StringToDate(date, style.getValue());
                if (dateTmp != null) {
                    timestamps.add(dateTmp.getTime());
                }
            }
            myDate = getAccurateDate(timestamps);
        } else {
            myDate = StringToDate(date, timeZone, dateStyle.getValue());
        }
        return myDate;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date     日期
     * @param template 日期格式
     * @return 日期字符串
     */
    public static String DateToString(Date date, String template) {
        return DateToString(date, TimeZone.getDefault(), template);
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date     日期
     * @param timeZone 时区
     * @param template 日期格式
     * @return 日期字符串
     */
    public static String DateToString(Date date, TimeZone timeZone, String template) {
        String dateString = null;
        if (date != null) {
            try {
                SimpleDateFormat sf = getDateFormat(template);
                sf.setTimeZone(timeZone);
                dateString = sf.format(date);
            } catch (Exception e) {
            }
        }
        return dateString;
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date      日期
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String DateToString(Date date, DateStyle dateStyle) {
        return DateToString(date, TimeZone.getDefault(), dateStyle);
    }

    /**
     * 将日期转化为日期字符串。失败返回null。
     *
     * @param date      日期
     * @param timeZone  时区
     * @param dateStyle 日期风格
     * @return 日期字符串
     */
    public static String DateToString(Date date, TimeZone timeZone, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {
            dateString = DateToString(date, timeZone, dateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date     旧日期字符串
     * @param template 新日期格式
     * @return 新日期字符串
     */
    public static String StringToString(String date, String template) {
        return StringToString(date, null, template);
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date      旧日期字符串
     * @param dateStyle 新日期风格
     * @return 新日期字符串
     */
    public static String StringToString(String date, DateStyle dateStyle) {
        return StringToString(date, null, dateStyle);
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date        旧日期字符串
     * @param oldTemplate 旧日期格式
     * @param newTemplate 新日期格式
     * @return 新日期字符串
     */
    public static String StringToString(String date, String oldTemplate, String newTemplate) {
        String dateString = null;
        if (oldTemplate == null) {
            DateStyle style = getDateStyle(date);
            if (style != null) {
                Date myDate = StringToDate(date, style.getValue());
                dateString = DateToString(myDate, newTemplate);
            }
        } else {
            Date myDate = StringToDate(date, oldTemplate);
            dateString = DateToString(myDate, newTemplate);
        }
        return dateString;
    }

    /**
     * 将日期字符串转化为另一日期字符串。失败返回null。
     *
     * @param date         旧日期字符串
     * @param oldDteStyle  旧日期风格
     * @param newDateStyle 新日期风格
     * @return 新日期字符串
     */
    public static String StringToString(String date, DateStyle oldDteStyle, DateStyle newDateStyle) {
        String dateString = null;
        if (oldDteStyle == null) {
            DateStyle style = getDateStyle(date);
            dateString = StringToString(date, style.getValue(), newDateStyle.getValue());
        } else {
            dateString = StringToString(date, oldDteStyle.getValue(), newDateStyle.getValue());
        }
        return dateString;
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期字符串
     */
    public static String addYear(String date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }

    /**
     * 增加日期的年份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static Date addYear(Date date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加月份后的日期字符串
     */
    public static String addMonth(String date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }

    /**
     * 增加日期的月份。失败返回null。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static Date addMonth(Date date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期字符串
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期字符串
     */
    public static String addDay(String date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }

    /**
     * 增加日期的天数。失败返回null。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期字符串
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的日期字符串
     */
    public static String addHour(String date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }

    /**
     * 增加日期的小时。失败返回null。
     *
     * @param date       日期
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的日期
     */
    public static Date addHour(Date date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date         日期字符串
     * @param minuteAmount 增加数量。可为负数
     * @return 增加分钟后的日期字符串
     */
    public static String addMinute(String date, int minuteAmount) {
        return addInteger(date, Calendar.MINUTE, minuteAmount);
    }

    /**
     * 增加日期的分钟。失败返回null。
     *
     * @param date         日期
     * @param minuteAmount 增加数量。可为负数
     * @return 增加分钟后的日期
     */
    public static Date addMinute(Date date, int minuteAmount) {
        return addInteger(date, Calendar.MINUTE, minuteAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date         日期字符串
     * @param secondAmount 增加数量。可为负数
     * @return 增加秒钟后的日期字符串
     */
    public static String addSecond(String date, int secondAmount) {
        return addInteger(date, Calendar.SECOND, secondAmount);
    }

    /**
     * 增加日期的秒钟。失败返回null。
     *
     * @param date         日期
     * @param secondAmount 增加数量。可为负数
     * @return 增加秒钟后的日期
     */
    public static Date addSecond(Date date, int secondAmount) {
        return addInteger(date, Calendar.SECOND, secondAmount);
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期字符串
     * @return 年份
     */
    public static int getYear(String date) {
        return getYear(StringToDate(date));
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期字符串
     * @return 月份
     */
    public static int getMonth(String date) {
        return getMonth(StringToDate(date));
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期字符串
     * @return 天
     */
    public static int getDay(String date) {
        return getDay(StringToDate(date));
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    public static int getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期字符串
     * @return 小时
     */
    public static int getHour(String date) {
        return getHour(StringToDate(date));
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期
     * @return 小时
     */
    public static int getHour(Date date) {
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期字符串
     * @return 分钟
     */
    public static int getMinute(String date) {
        return getMinute(StringToDate(date));
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期
     * @return 分钟
     */
    public static int getMinute(Date date) {
        return getInteger(date, Calendar.MINUTE);
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期字符串
     * @return 秒钟
     */
    public static int getSecond(String date) {
        return getSecond(StringToDate(date));
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期
     * @return 秒钟
     */
    public static int getSecond(Date date) {
        return getInteger(date, Calendar.SECOND);
    }

    /**
     * 获取日期 。默认yyyy-MM-dd格式。失败返回null。
     *
     * @param date 日期字符串
     * @return 日期
     */
    public static String getDate(String date) {
        return StringToString(date, DateStyle.YYYY_MM_DD);
    }

    /**
     * 获取日期。默认yyyy-MM-dd格式。失败返回null。
     *
     * @param date 日期
     * @return 日期
     */
    public static String getDate(Date date) {
        return DateToString(date, DateStyle.YYYY_MM_DD);
    }

    /**
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
     *
     * @param date 日期字符串
     * @return 时间
     */
    public static String getTime(String date) {
        return StringToString(date, DateStyle.HH_MM_SS);
    }

    /**
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。
     *
     * @param date 日期
     * @return 时间
     */
    public static String getTime(Date date) {
        return DateToString(date, DateStyle.HH_MM_SS);
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期字符串
     * @return 星期
     */
    public static Week getWeek(String date) {
        Week week = null;
        DateStyle dateStyle = getDateStyle(date);
        if (dateStyle != null) {
            Date myDate = StringToDate(date, dateStyle);
            week = getWeek(myDate);
        }
        return week;
    }

    /**
     * 获取日期的星期。失败返回null。
     *
     * @param date 日期
     * @return 星期
     */
    public static Week getWeek(Date date) {
        Week week = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        switch (weekNumber) {
            case 0:
                week = Week.SUNDAY;
                break;
            case 1:
                week = Week.MONDAY;
                break;
            case 2:
                week = Week.TUESDAY;
                break;
            case 3:
                week = Week.WEDNESDAY;
                break;
            case 4:
                week = Week.THURSDAY;
                break;
            case 5:
                week = Week.FRIDAY;
                break;
            case 6:
                week = Week.SATURDAY;
                break;
        }
        return week;
    }

    /**
     * 获取日期 时间为00:00:00
     *
     * @param date date
     * @return date
     */
    public static Date dateWithoutTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param date      日期字符串
     * @param otherDate 另一个日期字符串
     * @return 相差天数
     */
    public static int getIntervalDays(String date, String otherDate) {
        return getIntervalDays(StringToDate(date), StringToDate(otherDate));
    }

    /**
     * @param dateEarlier 日期
     * @param dateLater   另一个日期
     * @return 相差天数 -1 昨天  0 今天  1明天
     */
    public static int getIntervalDays(Date dateEarlier, Date dateLater) {
        Date date1 = dateWithoutTime(dateEarlier);
        Date date2 = dateWithoutTime(dateLater);
        long time = date2.getTime() - date1.getTime();
        return (int) time / (24 * 60 * 60 * 1000);
    }

    /**
     * 返回当前时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     *
     * @return 时间戳字符串
     */
    public static String getTimestamp() {
        return DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
    }

    /**
     * UTC 时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     *
     * @return 时间戳字符串
     */
    public static String getUTCTimestamp() {
        return getUTCTimestamp(new Date());
    }

    /**
     * UTC 时间转时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     *
     * @param utcDate UTC时间
     * @return 时间戳字符串
     */
    public static String getUTCTimestamp(Date utcDate) {
        return DateToString(utcDate, TimeZone.getTimeZone("UTC"), DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
    }

    /**
     * UTC 时间戳 转Date
     *
     * @param utcTimestamp UTC 时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     * @return UTC 时间
     */
    public static Date UTCTimestampToDate(String utcTimestamp) {
        return StringToDate(utcTimestamp, TimeZone.getTimeZone("UTC"), DateStyle.YYYY_MM_DD_HH_MM_SS_SSS);
    }

    /**
     * UTC 时间戳 转本地时间
     *
     * @param utcTimestamp UTC 时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     * @param template     时间日期格式
     * @return 本地时间字符串
     */
    public static String UTCTimestampToLocalString(String utcTimestamp, String template) {
        return DateToString(UTCTimestampToDate(utcTimestamp), template);
    }

    /**
     * UTC 时间戳 转本地时间
     *
     * @param utcTimestamp UTC 时间戳 格式：YYYY-MM-dd HH:mm:ss.SSS
     * @param style        DateStyle
     * @return 本地之间字符串
     */
    public static String UTCTimestampToLocalString(String utcTimestamp, DateStyle style) {
        return DateToString(UTCTimestampToDate(utcTimestamp), style);
    }
}