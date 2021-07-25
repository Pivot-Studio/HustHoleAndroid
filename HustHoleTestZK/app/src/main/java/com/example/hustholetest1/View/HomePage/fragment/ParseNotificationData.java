package com.example.hustholetest1.View.HomePage.fragment;


import android.util.Log;

import com.example.hustholetest1.View.HomePage.NotificationBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ParseNotificationData {
    public static List<NotificationBean> parseJson(String responseDate){
        List<NotificationBean> mNotificationList = new ArrayList<>();
        NotificationBean myNotification = null;
        String TAG = "tag";
        try {
            JSONArray jsonArray = new JSONArray(responseDate);
            Log.d(TAG, "parseJson: hey i am json_array");
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String hole_id = jsonObject.getString("hole_id");
                String reply_local_id = jsonObject.getString("reply_local_id");
                String alias = jsonObject.getString("alias");
                String created_timestamp = jsonObject.getString("created_timestamp");
                created_timestamp = parseTime(created_timestamp);
                String is_read = jsonObject.getString("is_read");
                String type = jsonObject.getString("type");
                String content = jsonObject.getString("content");
                String id = jsonObject.getString("id");
                //if(is_read.equals("0")){  //是否已读
                    myNotification = new NotificationBean();
                    myNotification.setAlias(alias);
                    myNotification.setHole_id(hole_id);
                    myNotification.setIs_read(is_read);
                    myNotification.setReplyContent(content);
                    myNotification.setReplyLocalId(reply_local_id);
                    myNotification.setTime(created_timestamp);
                    myNotification.setType(type);
                    myNotification.setId(id);
                //}
                if(myNotification!=null) {
                    mNotificationList.add(myNotification);
                    //Log.d(TAG, "parseJson: add "+ i);
                }
                myNotification = null;
            }
            return mNotificationList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "parseJson: hey i am exception");
            Log.d(TAG, "parseJson: e.toString() :"+ e.toString());
        }
        return null;
    }
    public static String parseTime(String created_timestamp){
        StringBuilder sb = new StringBuilder(created_timestamp);
        sb.replace(4,5," ");
        created_timestamp = sb.toString();
        created_timestamp = created_timestamp.replace("+08:00","");
        created_timestamp = created_timestamp.replace("T","");
        String time = created_timestamp.substring(10,15);
        /*char stan ='0';
        if(stan == time.charAt(0)){*/
            StringBuilder stb = new StringBuilder(time);
            stb.replace(0,0,"");
            time = stb.toString();
        //}
        String date = created_timestamp.substring(0,10);
        return time+" "+date;
    }

}

