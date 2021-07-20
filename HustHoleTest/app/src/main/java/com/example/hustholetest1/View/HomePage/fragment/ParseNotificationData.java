package com.example.hustholetest1.View.HomePage.fragment;

import android.app.Notification;

import com.example.hustholetest1.View.HomePage.NotificationBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParseNotificationData {
    public static List<NotificationBean> parseJson(String responseDate){
        List<NotificationBean> mNotificationList = new ArrayList<>();
        NotificationBean myNotification = null;
        try {
            JSONArray jsonArray = new JSONArray(responseDate);
            for (int i = 0; i<jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String hole_id = jsonObject.getString("hole_id");
                String reply_local_id = jsonObject.getString("reply_local_id");
                String alias = jsonObject.getString("alias");
                String created_timestamp = jsonObject.getString("created_timestamp");
                String is_read = jsonObject.getString("is_read");
                String type = jsonObject.getString("type");
                String content = jsonObject.getString("content");
                String id = jsonObject.getString("id");
                if(is_read.equals("0")){
                    myNotification = new NotificationBean();
                    myNotification.setAlias(alias);
                    myNotification.setHole_id(hole_id);
                    myNotification.setIs_read(is_read);
                    myNotification.setReplyContent(content);
                    myNotification.setReplyLocalId(reply_local_id);
                    myNotification.setTime(created_timestamp);
                    myNotification.setType(type);
                    myNotification.setId(id);
                }
                if(myNotification!=null) {
                    mNotificationList.add(myNotification);
                }
                myNotification = null;
            }
            return mNotificationList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

