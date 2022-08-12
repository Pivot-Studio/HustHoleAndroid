package cn.pivotstudio.moduleb.libbase.util.data;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @classname:TimeUtil
 * @description:
 * @date:2022/5/4 22:21
 * @version:1.0
 * @author:
 */
public class TimeUtil {
    /**
     * 回复的时间和标准时间相差8小时，后端没改
     */
    public static String replyTime(String created_timestamp) {
        String myDate =
            created_timestamp.substring(0, 10) + " " + created_timestamp.substring(11, 19);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        try {
            long dt1 = dateFormat.parse(myDate).getTime();
            long dt2 = dateFormat.parse(dateString).getTime();
            long dc = Math.abs(dt2 - dt1);
            Log.d("dc", dc + "");
            long seconds = (dc / 1000) - (60 * 60 * 8);
            long day = seconds / (24 * 60 * 60);     //相差的天数
            long hour = (seconds - day * 24 * 60 * 60) / (60 * 60);//相差的小时数
            long minute = (seconds - day * 24 * 60 * 60 - hour * 60 * 60) / (60);//相差的分钟数
            long second = (seconds - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);//相差的秒数

            if (day > 8) {
                return created_timestamp.substring(0, 10);
            } else if (day > 0) {
                return day + "天前";
            } else if (hour > 0) {
                return hour + "小时前";
            } else if (minute > 0) {
                return minute + "分钟前";
            } else {
                return "刚刚";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 标准时间解析
     */
    public static String time(String created_timestamp) {
        String myDate =
            created_timestamp.substring(0, 10) + " " + created_timestamp.substring(11, 19);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        try {
            long dt1 = dateFormat.parse(myDate).getTime();
            long dt2 = dateFormat.parse(dateString).getTime();
            long dc = Math.abs(dt2 - dt1);
            long seconds = dc / 1000;
            long day = seconds / (24 * 60 * 60);     //相差的天数
            long hour = (seconds - day * 24 * 60 * 60) / (60 * 60);//相差的小时数
            long minute = (seconds - day * 24 * 60 * 60 - hour * 60 * 60) / (60);//相差的分钟数
            long second = (seconds - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);//相差的秒数

            if (day > 8) {
                return created_timestamp.substring(0, 10);
            } else if (day > 0) {
                return day + "天前";
            } else if (hour > 0) {
                return hour + "小时前";
            } else if (minute > 0) {
                return minute + "分钟前";
            } else {
                return "刚刚";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}

