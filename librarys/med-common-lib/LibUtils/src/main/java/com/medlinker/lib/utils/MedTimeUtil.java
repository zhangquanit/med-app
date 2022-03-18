package com.medlinker.lib.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.medlinker.lib.log.LogUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Terry on 15/10/28.
 */
public class MedTimeUtil {


    /*
     * 将时间戳转换成 类似 19:34这样的格式
     * 参数单位：毫秒
     * */
    public static String milliToDate(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /*
     * 将时间戳转换成 2015.09.23这样的格式
     * */
    public static String milliToDateOne(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /*
     * 将时间戳转换成 09-20 15:20 这样的格式
     * */
    public static String milliToDateTwo(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm ");
        String date = format.format(new Date(milliSecond));
        return date;

    }

    public static String noticeTimeFormater(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm ");
        String date = format.format(new Date(milliSecond));
        return date;

    }


    /*
     * 将时间戳转化成 15.09.12 14:20 这样的格式
     * */
    public static String milliToDateThree(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm ");
        String date = format.format(new Date(milliSecond));
        return date;

    }

    /*
     * 将时间戳转换成 2015.09.23这样的格式
     * */
    public static String milliToDateFive(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /*
     * 将时间戳转换成 2015.09.23这样的格式
     * */
    public static String milliToDateSix(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /*
     * 将时间戳转换成 2015/09/23 00:00:00
     * */
    public static String milliToDateSix2(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /**
     * 将时间戳转换为 2016.02这样的格式
     *
     * @param milliSecond
     * @return
     */
    public static String milliToDateSeven(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        String date = format.format(new Date(milliSecond));
        return date;
    }


    /*
     * 将时间戳转换成 09.20 15:20 这样的格式
     * */
    public static String milliToDateEight(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd HH:mm ");
        String date = format.format(new Date(milliSecond));
        return date;

    }


    /**
     * 将时间转为 09:25:16 这样的格式
     *
     * @param milliSecond
     * @return
     */
    public static String milliToDateNine(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        // UTC时间，避免拿到的时间受时区影响
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = format.format(new Date(milliSecond));
        return date;
    }


    /*
     * 将时间戳转换成 09.23这样的格式
     * */
    public static String milliToDateTen(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("MM.dd");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    /**
     * @param milliSecond
     * @return
     */
    public static String milliToDateEleven(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(milliSecond));
    }

    /**
     * 2015-09-12
     *
     * @param milliSecond
     * @return
     */
    public static String milliToDateFour(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date(milliSecond));
        return date;
    }

    public static String formaterTime(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String date = format.format(new Date(milliSecond * 1000L));
        return date;
    }

    /**
     * 09.12
     */
    public static String milliToDateDay(long milliSecond) {
        return new SimpleDateFormat("MM.dd", Locale.getDefault()).format(new Date(milliSecond));
    }

    /*
     *智能时间显示格式
     * 规则：当天则显示为 09:43 昨天显示：昨天  否则：2015.09.20
     * */
    public static String smartMilliToDate(long milliSecond) {
        Calendar calendar = Calendar.getInstance();

        long oneDayMilli = 24 * 60 * 60 * 1000;
        long hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
        long minute = calendar.get(Calendar.MINUTE) * 60 * 1000;
        long second = calendar.get(Calendar.SECOND) * 1000;
        long milli = calendar.get(Calendar.MILLISECOND);

        long milliBeyondZeroClock = hour + minute + second + milli;//当前时间距离当天凌晨的毫秒数

        long difference = System.currentTimeMillis() - milliSecond;

        if (difference < milliBeyondZeroClock) {
            //当天
            return milliToDate(milliSecond);
        } else if (difference > milliBeyondZeroClock && difference < milliBeyondZeroClock + oneDayMilli) {
            //昨天
            return "昨天";
        } else {
            //昨天以前
            calendar.setTimeInMillis(milliSecond);
            int year = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(System.currentTimeMillis());
            int currentYear = calendar.get(Calendar.YEAR);
            if (currentYear - year > 0) {
                return milliToDateFour(milliSecond);
            } else {
                return milliToDateDay(milliSecond);
            }
        }

    }

    /**
     * MM月DD日
     *
     * @param time
     * @return
     */
    public static String getDateCNDay(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
        String date1 = format1.format(new Date(time * 1000L));
        return date1;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }


    /**
     * 倒计时
     *
     * @param textView
     * @param time
     */
    public static void setCountTime(TextView textView, long time) {

        long oneSecont = time % 60;
        long twoSecont = (time % 3600) % 60;
        long threeSecont = ((time % 86400) % 3600) % 60;
        String stroneSecont = String.format("%02d", oneSecont);
        String strtwoSecont = String.format("%02d", twoSecont);
        String strthreeSecont = String.format("%02d", threeSecont);

        long oneMinute = time / 60;
        long twoMinute = (time % 3600) / 60;
        long threeMinute = ((time % 86400) % 3600) / 60;
        String stroneMinute = String.format("%02d", oneMinute);
        String strtwoMinute = String.format("%02d", twoMinute);
        String strthreeMinute = String.format("%02d", threeMinute);

        long oneHour = (time / 3600);
        long twoHour = ((time % 86400) / 3600);
        long day = time / 86400;
        String stroneHour = String.format("%02d", oneHour);
        String strtwoHour = String.format("%02d", twoHour);
        String strDay = String.valueOf(day);

        if (time < 60) {
            textView.setText(time + "");
        }
        if ((time >= 60) && (time < 3600)) {
            textView.setText(stroneMinute + ":" + stroneSecont);
        }
        if ((time >= 3600) && (time < 86400)) {
            textView.setText(stroneHour + ":" + strtwoMinute + ":" + strtwoSecont);
        }
        if (time >= 86400) {
            textView.setText(strDay + "天" + strtwoHour + "小时");
        }
    }

    /**
     * 倒计时重载,时间段
     */
    @SuppressLint("SimpleDateFormat")
    public static void countDown(TextView tvCountView, int remainTime) {
        Calendar calendar = Calendar.getInstance();
        // 设置传入时间
        calendar.setTime(new Date(remainTime * 1000));
        long timeInMillis = calendar.getTimeInMillis();
        // 当前时间
        calendar.setTimeInMillis(System.currentTimeMillis());
        long currentTimeMillis = calendar.getTimeInMillis();
        timeInMillis = timeInMillis + currentTimeMillis;
        countDownTime(tvCountView, timeInMillis, currentTimeMillis);
    }

    public static void countDownTime(final TextView tv_time, long _Millis,
                                     long currentTimeMillis) {
        if (_Millis > currentTimeMillis) {
            long time = _Millis - currentTimeMillis;
            new CountDownTimer(time, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    long hasSecond = millisUntilFinished / 1000;
                    if (hasSecond < 60) {
                        tv_time.setText(hasSecond + "s");
                    }
                    if ((hasSecond >= 60) && (hasSecond < 3600)) {
                        tv_time.setText((hasSecond / 60) + "m"
                                + (hasSecond % 60) + "s");
                    }
                    if ((hasSecond >= 3600) && (hasSecond < 86400)) {
                        tv_time.setText((hasSecond / 3600) + "h"
                                + ((hasSecond % 3600) / 60) + "m"
                                + ((hasSecond % 3600) % 60) + "s");
                    }
                    if (hasSecond >= 86400) {
                        tv_time.setText((hasSecond / 86400) + "d"
                                + ((hasSecond % 86400) / 3600) + "h"
                                + (((hasSecond % 86400) % 3600) / 60) + "m"
                                + (((hasSecond % 86400) % 3600) % 60) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    tv_time.setText("重新获取");
                    tv_time.setEnabled(true);
                    tv_time.setTextColor(Color.parseColor("#007eff"));
                }
            }.start();
        } else {
            tv_time.setText("重新获取");
            tv_time.setEnabled(true);
            tv_time.setTextColor(Color.parseColor("#007eff"));
        }
    }


    /**
     * 当前时间距离当天凌晨的秒
     *
     * @return
     */
    public static int beyonndZeroSecond() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;
        int minute = calendar.get(Calendar.MINUTE) * 60;
        int second = calendar.get(Calendar.SECOND);
        return hour + minute + second;
    }

    /**
     * 将格式时间转为时间戳
     *
     * @param dateString 时间字符串
     * @return 时间戳
     * @throws ParseException
     */
    public static long dateToMillon(String dateString) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM");
        Date date = format.parse(dateString);
        return date.getTime();
    }

    /**
     * 获取转换后的时间：24小时内发布的展示为：xx分钟前／xx小时前；超过24小时的展示日期month-day e.g 01-22
     *
     * @param milliSecond 时间戳
     * @return 转换后的时间
     */
    public static String getConvertTime(long milliSecond) {
        return milliToDateTwo(milliSecond);
    }

    public static String getConvertTime1(long milliSecond) {
        return milliToDateTwo(milliSecond);
    }

    /**
     * 获取转换后的时间：格式为02-20
     *
     * @param milliSecond 时间戳
     * @return 转换后的时间
     */
    public static String getMonthDay(long milliSecond) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.getDefault());
        return format.format(new Date(milliSecond));
    }

    /**
     * 获取视频播放时长
     *
     * @param seconds 秒
     * @return 视频播放时长，格式：40'30''
     */
    public static String getDuration(long seconds) {
        StringBuilder builder = new StringBuilder();
        if (seconds < 60) {
            String secondsStr = seconds >= 10 ? String.valueOf(seconds) : ("0" + seconds);
            builder.append("00'").append(secondsStr).append("''");
        } else {
            long minutes = seconds / 60;
            long subSeconds = seconds % 60;
            String minutesStr = minutes >= 10 ? String.valueOf(minutes) : ("0" + minutes);
            String subSecondsStr = subSeconds >= 10 ? String.valueOf(subSeconds) : ("0" + subSeconds);
            builder.append(minutesStr).append("'").append(subSecondsStr).append("''");
        }
        return builder.toString();
    }

    /**
     * 获取视频播放时长
     *
     * @param seconds 秒
     * @return 视频播放时长，格式：40:30
     */

    public static String getDuration(int seconds, String split) {
        StringBuilder builder = new StringBuilder();
        if (seconds < 60) {
            String secondsStr = seconds >= 10 ? String.valueOf(seconds) : ("0" + seconds);
            builder.append("00").append(split).append(secondsStr);
        } else {
            int minutes = seconds / 60;
            int subSeconds = seconds % 60;
            String minutesStr = minutes >= 10 ? String.valueOf(minutes) : ("0" + minutes);
            String subSecondsStr = subSeconds >= 10 ? String.valueOf(subSeconds) : ("0" + subSeconds);
            builder.append(minutesStr).append(split).append(subSecondsStr);
        }
        return builder.toString();
    }

    /**
     * 获取视频播放时长
     *
     * @param seconds 秒
     * @return 视频播放时长，格式：02:40:30（hour:minutes:second）
     */

    public static String getDuration1(long seconds, String split) {
        StringBuilder builder = new StringBuilder();
        if (seconds < 60) {
            String secondsStr = seconds >= 10 ? String.valueOf(seconds) : ("0" + seconds);
            builder.append("00").append(split).append(secondsStr);
        } else if (seconds < 3600){
            long minutes = seconds / 60;
            long subSeconds = seconds % 60;
            String minutesStr = minutes >= 10 ? String.valueOf(minutes) : ("0" + minutes);
            String subSecondsStr = subSeconds >= 10 ? String.valueOf(subSeconds) : ("0" + subSeconds);
            builder.append(minutesStr).append(split).append(subSecondsStr);
        }else {
            long hour = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long subSeconds = seconds % 60;
            String hourStr = hour >= 10 ? String.valueOf(hour) : ("0" + hour);
            String minutesStr = minutes >= 10 ? String.valueOf(minutes) : ("0" + minutes);
            String subSecondsStr = subSeconds >= 10 ? String.valueOf(subSeconds) : ("0" + subSeconds);
            builder.append(hourStr).append(split).append(minutesStr).append(split).append(subSecondsStr);
        }
        return builder.toString();
    }

    /**
     * 获取视频直播展示时间
     *
     * @param startTime 开始时间，时间戳
     * @param endTime   结束时间，时间戳
     * @return 视频直播展示时间，格式：XX 月 XX 日 星期几 时间
     */
    public static String getLiveTime(long startTime, long endTime) {
        if (startTime <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        SimpleDateFormat startDateFormat = new SimpleDateFormat("MM/dd  E HH:mm", Locale.getDefault());
        String startDateString = startDateFormat.format(new Date(startTime));
        builder.append(startDateString);
        if (endTime > 0 && endTime > startTime) {
            builder.append("—");
            SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String endDateString = endDateFormat.format(new Date(endTime));
            builder.append(endDateString);
        }
        return builder.toString();
    }

    /**
     * 获取视频直播展示时间
     *
     * @param startTime 开始时间，时间戳
     * @param endTime   结束时间，时间戳
     * @return 视频直播展示时间，格式：XX 月 XX 日 星期几 时间
     */
    public static String getLiveTime2(long startTime, long endTime) {
        if (startTime <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(startTime);
        builder.append(getFormat0Result(mCalendar.get(Calendar.MONTH) + 1) + "/" + getFormat0Result(mCalendar.get(Calendar.DAY_OF_MONTH)));
        builder.append((mCalendar.get(Calendar.AM_PM)) == 0 ? " 上午" : " 下午");
        builder.append(getFormat0Result(mCalendar.get(Calendar.HOUR)) + ":" + getFormat0Result(mCalendar.get(Calendar.MINUTE)));

        if (endTime > 0 && endTime > startTime) {
            builder.append("—");
            SimpleDateFormat endDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String endDateString = endDateFormat.format(new Date(endTime));
            builder.append(endDateString);
        }
        return builder.toString();
    }

    /**
     * 获取未开始的直播视频展示时间
     * 规则：当距离开始时间大于1天时“XX月XX日XX时XX分”，当距离开始时间不足一天时，展示倒计时。
     * @param startTime 开始时间，时间戳
     * @return
     */
    public static String getLiveTime3(long startTime,long countdown) {
        if (startTime <= 0) {
            return "";
        }
        // 一天86400s
        int day = 86400;
        if (countdown <= day){
            return getDuration1(countdown,":");
        }else {
            return formatCnMonthDayHourMinute(startTime);
        }
    }

    private static String getFormat0Result(int number) {
        if (number < 10) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    /**
     * 开始时间距离现在的时间，直播需要
     *
     * @param startTime
     * @return
     */
    public static Calendar getMinuetCountForNow(long startTime) {
        Calendar calendar = Calendar.getInstance();
        long time = (startTime - 300) - System.currentTimeMillis() / 1000;//提前五分钟提醒
        if (time > 0) {
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            calendar.add(Calendar.SECOND, Integer.parseInt(String.valueOf(time)));
        }
        return calendar;
    }

    /**
     * 格式化时间，比如视频的播放时间
     */
    public static String formatTimeMillseconds(long millseconds) {
        if (millseconds < 0) {
            throw new IllegalStateException("millseconds must >0");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(millseconds));  //1970-01-01 08:00:00 begin
        int hour = cal.get(Calendar.HOUR_OF_DAY) - 8;
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        StringBuilder sb = new StringBuilder();
        sb.append(hour < 10 ? ("0" + hour) : String.valueOf(hour)).append(":");
        sb.append(minute < 10 ? ("0" + minute) : String.valueOf(minute)).append(":");
        sb.append(second < 10 ? ("0" + second) : String.valueOf(second));
        return sb.toString();
    }

    public static boolean isStartTimeValid(long time) {
        long nowTimeMile = System.currentTimeMillis() / 1000;
        return time != 0 && time >= nowTimeMile;
    }

    public static void printCurrentTime(String flag) {
        LogUtil.e("printCurrentTime", flag + ":" + Calendar.getInstance().getTimeInMillis());
    }

    public static String getYear(long millseconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        String format = simpleDateFormat.format(new Date(millseconds));
        return format;
    }

    public static boolean isCloseEnough(long var0, long var2) {
        long var4 = var0 - var2;
        if (var4 < 0L) {
            var4 = -var4;
        }
        return var4 >= 5 * 60 * 1000L;
    }


    //秒转小时，分，秒
    public static String getMatchHourTime(long secondTime) {
        long hour = secondTime / 3600; //小时
        long minute = secondTime % 3600 / 60;//分钟
        long second = secondTime % 60; //秒
        if (hour >= 1) {
            return hour + "时" + minute + "分";
        } else {
            return minute + "分" + second + "秒";
        }
    }

    //秒转小时，分，秒
    public static String getMatchHourTime1(long secondTime) {
        long hour = secondTime / 3600; //小时
        long minute = secondTime % 3600 / 60;//分钟
        long second = secondTime % 60; //秒
        StringBuilder builder = new StringBuilder();

        if(hour >= 1){
            builder.append(hour).append("小时");
        }

        if (minute >= 1){
            builder.append(minute).append("分钟");
        }

        if (second >= 1){
            builder.append(second).append("秒");
        }
        return builder.toString();
    }

    //秒转小时，分，秒
    public static String getMatchHourTime2(long secondTime) {
        long hour = secondTime / 3600; //小时
        long minute = secondTime % 3600 / 60;//分钟
        StringBuilder builder = new StringBuilder();
        builder.append(hour).append("时");
        if (minute < 10){
            builder.append("0");
        }
        builder.append(minute).append("分");
        return builder.toString();
    }

    //秒转小时，分，秒
    public static String getRankHourTime(long secondTime) {
        long minute = secondTime % 3600 / 60;//分钟
        long second = secondTime % 60; //秒
        if (minute >= 1) {
            return minute + "分";
        } else {
            return second + "秒";
        }
    }

    /**å
     * 判断是否实名时间
     *
     * @return
     */
    public static boolean isRealNameTime() {
        return isInRange("08:00", "19:59");
    }

    /**
     * 判断是否在时间范围内
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean isInRange(String startTime, String endTime) {
        if (!MedRegexUtil.isTimeValid(startTime)) {
            throw new IllegalArgumentException("Illegal Argument arg:" + startTime);
        }
        if (!MedRegexUtil.isTimeValid(endTime)) {
            throw new IllegalArgumentException("Illegal Argument arg:" + endTime);
        }
        String[] startSplit = startTime.split(":");
        final int start = Integer.valueOf(startSplit[0]) * 60 + Integer.valueOf(startSplit[1]);// 起始时间
        String[] endSplit = endTime.split(":");
        final int end = Integer.valueOf(endSplit[0]) * 60 + Integer.valueOf(endSplit[1]);// 结束时间
        if (start > end) {
            throw new IllegalArgumentException("Illegal Argument startTime " + startTime + " must less than endTime " + endTime);
        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));// 当前日期，东八区
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 从00:00开始到目前的分钟数
        return minuteOfDay >= start && minuteOfDay <= end;
    }

    /**
     * 根据生日获取年龄
     *
     * @param birthday 出生日期
     * @return 年龄
     */
    public static int getAgeByBirthday(long birthday) {
        if (birthday <= 0) {
            return 0;
        }
        if (birthday / 1000000000L < 10) {
            birthday = birthday * 1000L;
        }
        Date date = new Date(birthday);
        Calendar cal = Calendar.getInstance();
        if (cal.before(date)) {
            return 0;
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(date);

        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }
        return age;
    }

    /**
     * im中的时间
     *
     * @param milliSecond
     * @return
     */
    public static String formatImTime(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }

        Calendar calendar = Calendar.getInstance();
        long oneDayMilli = 24 * 60 * 60 * 1000;
        long hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
        long minute = calendar.get(Calendar.MINUTE) * 60 * 1000;
        long second = calendar.get(Calendar.SECOND) * 1000;
        long milli = calendar.get(Calendar.MILLISECOND);

        long milliBeyondZeroClock = hour + minute + second + milli; //当前时间距离当天凌晨的毫秒数

        long difference = System.currentTimeMillis() - milliSecond;

        if (difference < milliBeyondZeroClock) {
            //当天
            return milliToDate(milliSecond);
        } else {
            if (difference < milliBeyondZeroClock + oneDayMilli) {
                //昨天
                return MedAppInfo.getAppContext().getString(R.string.util_yesterday) + " " + milliToDate(milliSecond);
            } else if (difference < milliBeyondZeroClock + (oneDayMilli * 7)) {
                //星期x
                calendar.setTimeInMillis(milliSecond);
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                return getWeekStr(week) + " " + milliToDate(milliSecond);
            } else {
                //超过一周 xx年xx月xx日
                return formatCnYearMonthDayHourMinute(milliSecond);
            }
        }
    }

    /**
     * 数据流时间
     * 1小时以内：N分钟前
     * 1天以内：N小时前
     * 昨天：昨天
     * 当年：1-1
     * 不在当年：2000-1-1
     */
    public static String formatStreamTime(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(milliSecond);
        Calendar currentCalendar = Calendar.getInstance();

        Calendar targetDayCalendar = Calendar.getInstance();
        targetDayCalendar.setTime(targetCalendar.getTime());
        targetDayCalendar.set(targetDayCalendar.get(Calendar.YEAR), targetDayCalendar.get(Calendar.MONTH),
                targetDayCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        targetDayCalendar.set(Calendar.MILLISECOND, 0);

        Calendar currentDayCalendar = Calendar.getInstance();
        currentDayCalendar.setTime(currentCalendar.getTime());
        currentDayCalendar.set(currentDayCalendar.get(Calendar.YEAR), currentDayCalendar.get(Calendar.MONTH),
                currentDayCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        currentDayCalendar.set(Calendar.MILLISECOND, 0);

        int targetHour = targetCalendar.get(Calendar.HOUR_OF_DAY);
        int targetMinute = targetCalendar.get(Calendar.MINUTE);

        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);
        if (currentDayCalendar.compareTo(targetDayCalendar) == 0) { //今天
            int oneHour = 60;
            int hour = currentHour - targetHour;
            int minute = currentMinute - targetMinute;

            minute = hour * oneHour + minute;
            if (minute < oneHour) { //小于一小时
                if (minute < 1) {
                    minute = 1;
                }
                return minute + "分钟前";
            } else { //大于一小时
                hour = minute / oneHour;
                return hour + "小时前";
            }
        } else {
            targetDayCalendar.add(Calendar.DAY_OF_MONTH, 1); //日期加1
            if (currentDayCalendar.compareTo(targetDayCalendar) == 0) { //昨天
                return MedAppInfo.getAppContext().getString(R.string.util_yesterday);
            } else { //昨天之前
                int targetYear = targetCalendar.get(Calendar.YEAR);
                int currentYear = currentCalendar.get(Calendar.YEAR);
                if (targetYear == currentYear) { // 当年
                    return formatMonthDay(milliSecond);
                } else {
                    return formatYearMonthDay(milliSecond);
                }
            }

        }
    }

    /**
     * @param milliSecond
     * @return 当天：20:00 当年：9-1 隔年2019-1-1
     */
    public static String formatNormalTime(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        Calendar calendar = Calendar.getInstance();
        long hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
        long minute = calendar.get(Calendar.MINUTE) * 60 * 1000;
        long second = calendar.get(Calendar.SECOND) * 1000;
        long milli = calendar.get(Calendar.MILLISECOND);

        long milliBeyondZeroClock = hour + minute + second + milli; //当前时间距离当天凌晨的毫秒数
        long difference = calendar.getTimeInMillis() - milliSecond;
        if (difference < milliBeyondZeroClock) {
            //当天
            return milliToDate(milliSecond);
        } else {
            int curYear = calendar.get(Calendar.YEAR);
            calendar.setTimeInMillis(milliSecond);
            int targetYear = calendar.get(Calendar.YEAR);
            if (curYear == targetYear) {
                return formatMonthDay(milliSecond);
            } else {
                return formatYearMonthDay(milliSecond);
            }
        }

    }

    /**
     * 待处理订单时间
     *
     * @param milliSecond
     * @return 当天则显示为 09:43，昨天显示：昨天，超过48小时：星期x，超过一周：2015月09月20日
     */
    public static String formatOrderTime(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }

        Calendar calendar = Calendar.getInstance();
        long oneDayMilli = 24 * 60 * 60 * 1000;
        long hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000;
        long minute = calendar.get(Calendar.MINUTE) * 60 * 1000;
        long second = calendar.get(Calendar.SECOND) * 1000;
        long milli = calendar.get(Calendar.MILLISECOND);

        long milliBeyondZeroClock = hour + minute + second + milli; //当前时间距离当天凌晨的毫秒数

        long difference = System.currentTimeMillis() - milliSecond;

        if (difference < milliBeyondZeroClock) {
            //当天
            return milliToDate(milliSecond);
        } else {
            if (difference < milliBeyondZeroClock + oneDayMilli) {
                //昨天
                return formatMonthDay(milliSecond);
            } else if (difference < milliBeyondZeroClock + (oneDayMilli * 7)) {
                //星期x
                calendar.setTimeInMillis(milliSecond);
                int week = calendar.get(Calendar.DAY_OF_WEEK);
                return getWeekStr(week);
            } else {
                //超过一周 xx年xx月xx日
                return formatCnYearMonthDay(milliSecond);
            }
        }
    }

    /**
     * 时间段：凌晨0-3:59;早上4-8:59;上午9-11:59:中午12-12:59;下午13-17:59晚上18-23:59
     *
     * @return 凌晨/早上/上午/中午/下午/晚上
     */
    private static String getTimePeriod(long milliSecond) {
        String str = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            Date targetDate = df.parse(df.format(new Date(milliSecond)));
            if (belongCalendar(targetDate, df.parse("00:00"), df.parse("03:59"))) {
                str = "凌晨";
            } else if (belongCalendar(targetDate, df.parse("04:00"), df.parse("08:59"))) {
                str = "早上";
            } else if (belongCalendar(targetDate, df.parse("09:00"), df.parse("11:59"))) {
                str = "上午";
            } else if (belongCalendar(targetDate, df.parse("12:00"), df.parse("12:59"))) {
                str = "中午";
            } else if (belongCalendar(targetDate, df.parse("13:00"), df.parse("17:59"))) {
                str = "下午";
            } else if (belongCalendar(targetDate, df.parse("18:00"), df.parse("23:59"))) {
                str = "晚上";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 判断时间是否在时间段内
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        return nowTime.getTime() >= beginTime.getTime() && nowTime.getTime() <= endTime.getTime();
    }


    private static String getWeekStr(int week) {
        String[] weekStrs = MedAppInfo.getAppContext().getResources().getStringArray(R.array.util_day_of_week);
        return weekStrs[week - 1];
    }

    /**
     * 09-12
     */
    public static String formatMonthDay(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(new Date(milliSecond));
    }

    /**
     * 2015-09-12
     */
    public static String formatYearMonthDay(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(milliSecond));
    }

    /**
     * @param milliSecond
     * @return 2018年11月1日
     */
    public static String formatCnYearMonthDay(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date(milliSecond));
    }

    /**
     * @param milliSecond
     * @return 2018年11月1日 00:00
     */
    public static String formatCnYearMonthDayHourMinute(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        return format.format(new Date(milliSecond));
    }


    /**
     * @param milliSecond
     * @return 11月1日 00:00
     */
    public static String formatCnMonthDayHourMinute(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH时mm分");
        return format.format(new Date(milliSecond));
    }

    /**
     * @param milliSecond
     * @return 2018-11-1 00:00
     */
    public static String formatYearMonthDayHourMinute(long milliSecond) {
        if (milliSecond <= 0) {
            return "";
        }
        if (milliSecond / 1000000000 < 10) {
            milliSecond *= 1000;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date(milliSecond));
    }

    /**
     * 现在是否是晚上（时空）
     */
    public static boolean isNightTimeSpaceOnNow() {
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        return curHour < 8 || curHour >= 20;
    }

    /**
     * 当前是否是12小时之后
     */
    public static boolean isAfterTwelveHours(long time) {
        if (time <= 0) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.HOUR_OF_DAY, 12);
        Calendar currentCal = Calendar.getInstance();
        return currentCal.compareTo(calendar) > 0;
    }

    /**
     * 这个时间戳是不是今天
     * @param oldTime
     * @return
     */
    public static boolean isTodayTimeByTime(long oldTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String d1 = format.format(new Date(oldTime));
        Date newDate = new Date();
        String d2 = format.format(newDate);
        if (d1.equalsIgnoreCase(d2)) {
            return true;
        } else {
            return false;
        }
    }
}


