package com.example.hustholetest1.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class ForestJsonParsing {//已弃用
    public static String[][] getDetailForest(int number) throws JSONException {
        JSONArray jsonArray=Forest.getForest_list(number);
        String[][] detailforest=new String[jsonArray.length()][8];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject sonObject=jsonArray.getJSONObject(i);
            Log.e(TAG, "sonObject"+sonObject);
            detailforest[i][0]=sonObject.getString("background_image_url");
            detailforest[i][1]=sonObject.getString("cover_url");
            detailforest[i][2]=sonObject.getString("description");
            detailforest[i][3]=sonObject.getInt("forest_id")+"";
            detailforest[i][4]=sonObject.getInt("hole_number")+"Huster . "+sonObject.getInt("joined_number")+"树洞";
            detailforest[i][5]=sonObject.getBoolean("joined")+"";
            detailforest[i][6]=sonObject.getString("last_active_time");
            detailforest[i][7]=sonObject.getString("name");
        }
        return detailforest;
    }
    public static Bitmap[][] getPng(int number) throws JSONException {
        JSONArray jsonArray=Forest.getForest_list(number);
        Bitmap[][] bitmaps=new Bitmap[jsonArray.length()][2];
         int i = 0;
        for (; i < jsonArray.length(); i++) {

            JSONObject sonObject=jsonArray.getJSONObject(i);
            Log.e(TAG, "sonObject"+sonObject);
             final int p=i;
           // new Thread(){
                //@Override
                //public void run(){
                    try {

                        URL url = new URL(sonObject.getString("background_image_url"));
                        URL url2 = new URL(sonObject.getString("cover_url"));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        //if (conn.getResponseCode() == 200) {
                            InputStream inputStream = conn.getInputStream();
                            bitmaps[p][0]= BitmapFactory.decodeStream(inputStream);
                        //}
                        HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                        conn2.setConnectTimeout(5000);
                        conn2.setRequestMethod("GET");
                       // if (conn2.getResponseCode() == 200) {
                            InputStream inputStream2 = conn2.getInputStream();
                            bitmaps[p][1]=BitmapFactory.decodeStream(inputStream2);
                        //}
                    } catch (IOException | JSONException  e) {
                        e.printStackTrace();
                    }
                //}
           // }.start();
        }
        /*try {
            TimeUnit.SECONDS.sleep(5);//秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        Log.e(TAG, "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        return bitmaps;
    }
}
