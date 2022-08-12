package cn.pivotstudio.modulec.homescreen.oldversion.model;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeCount {

    public static void main(String[] args) {
        String created_timestamp = "2021-03-06T19:30:41+08:00";
        String created_timestamp1 = "2021-08-15T10:30:41+08:00";
        String set = time(created_timestamp);
        String set1 = time(created_timestamp1);
        System.out.println(set);
        System.out.println(set1);
    }

    public static String replytime(String created_timestamp) {
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

/*
public class TimeCount {
    public static String time(String time) {
        int year = Integer.parseInt(time.substring(0, 4));
        int month = Integer.parseInt(time.substring(5, 7));
        int day = Integer.parseInt(time.substring(8, 10));
        int hour = Integer.parseInt(time.substring(11, 13));
        int minute = Integer.parseInt(time.substring(14, 16));
        int second = Integer.parseInt(time.substring(17, 19));


        Calendar calendar = Calendar.getInstance();
        int year_now = calendar.get(Calendar.YEAR);
        int month_now = calendar.get(Calendar.MONTH) + 1;
        int day_now = calendar.get(Calendar.DAY_OF_MONTH);
        int hour_now = calendar.get(Calendar.HOUR_OF_DAY);
        int minute_now = calendar.get(Calendar.MINUTE);
        int second_now = calendar.get(Calendar.SECOND);


        String time1 = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
        String time2 =  year_now+"-"+month_now+"-"+day_now+" "+hour_now+":"+minute_now+":"+second_now;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt1 = LocalDateTime.parse(time1, dtf);
        LocalDateTime ldt2 = LocalDateTime.parse(time2, dtf);
        Duration differenceValue = Duration.between(ldt1,ldt2);
        // 获取的是两个时间相差的分钟数,如果想要相差小时数就调用toHours()
       Long minutesTime = differenceValue .toMinutes();
       Long hoursTime = differenceValue .toHours();
       Long daysTime= differenceValue.toDays();
       Log.d("detailtime",daysTime+"+"+hoursTime+"+"+minutesTime);


        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year_now); // 2010年
        c.set(Calendar.MONTH, month_now - 1); // 6
        int daynumber = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        if((year_now>year+1)){
            return year+"-"+month+"-"+day;
        }else if(year_now==year+1){//隔年
            if(12-month+month_now==1){
                if((daynumber+day_now-day<=7)){
                    if(daynumber+day_now-day>1) {
                        return daynumber - day + day_now + "天前";
                    }else if(daynumber+day_now-day==1){
                        if(hour_now>=hour){
                            return "1天前";
                        }else if(hour_now<hour&&24-hour+hour_now>1){
                            return 24-hour+hour_now+"小时前";
                        }else if(hour_now<hour&&24-hour+hour_now==1){
                            if(minute_now>=minute){
                                return "1小时前";
                            }else{
                                if(minute_now<minute&&60-minute+minute_now>1){
                                   return 60-minute+minute_now+"分钟前";
                                }else if(minute_now<minute&&60-minute+minute_now==1){
                                    return "1分钟前";
                                }

                            }
                        }
                    }
                } else{
                    return year+"-"+month+"-"+day;
                }
            }else{
                return year+"-"+month+"-"+day;
            }
        }else if(
                (year_now==year&&month_now>month+1)
                ||(year_now==year&&month_now==month+1&&daynumber+day_now-day>7)
                ||(year_now==year&&month_now==month&&(day_now-day)>7)){
            return year+"-"+month+"-"+day;
        }else if((year_now==year&&month_now==month+1&&daynumber+day_now-day<=7)){//隔月
            if(daynumber+day_now-day>1) {
                return daynumber - day + day_now + "天前";
            }else if(daynumber+day_now-day==1){
               if(hour_now>=hour){
                   return "1天前";
               }else if(hour_now<hour&&24-hour+hour_now>1){
                   return 24-hour+hour_now+"小时前";
               }else if(hour_now<hour&&24-hour+hour_now==1){
                   if(minute_now>=minute){
                       return "1小时前";
                   }else{
                       if(minute_now<minute&&60-minute+minute_now>1){
                           return 60-minute+minute_now+"分钟前";
                       }else if(minute_now<minute&&60-minute+minute_now==1){
                           return "1分钟前";
                       }
                   }
               }
            }
        }else if((year_now==year&&month_now==month&&(day_now-day)<=7)){//同月
            if(day_now-day>1){
                return day_now-day+"天前";
            }else if(day_now-day==1){
                if(hour_now>=hour){
                    return "1天前";
                }else if(hour_now<hour&&24-hour+hour_now>1){
                    return 24-hour+hour_now+"小时前";
                }else if(hour_now<hour&&24-hour+hour_now==1){
                    if(minute_now>=minute){
                        return "1小时前";
                    }else{
                        if(minute_now<minute&&60-minute+minute_now>1){
                            return 60-minute+minute_now+"分钟前";
                        }else if(minute_now<minute&&60-minute+minute_now==1){
                            return "1分钟前";
                        }
                    }
                }
            }else if(day_now==day){
                if(hour_now>hour+1){
                    return hour_now-hour+"小时前";
                }else if(hour_now==hour+1){
                    if(minute_now>=minute){
                       return "1小时前";
                    }else{
                        return 60-minute+minute_now+"分钟前";
                    }
                }else if(hour_now==hour){
                        return minute_now-minute+"分钟前";
                }
            }
        }

        //time2.setText("Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);

        return null;
    }
}
*/