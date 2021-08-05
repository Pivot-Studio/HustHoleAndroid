package com.example.hustholetest1.Model;


import java.util.Calendar;

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
