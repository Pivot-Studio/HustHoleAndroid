package com.example.hustholetest1.Model;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class PictureReading {//弃用
    public static Bitmap getBitmap(String path) throws IOException {
        Bitmap bitmap=null;
        //new Thread(){
           // @Override
           // public void run(){
                try {
                  URL url = new URL(path);
                  HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                  conn.setConnectTimeout(5000);
                  conn.setRequestMethod("GET");
                if (conn.getResponseCode() == 200) {
                  InputStream inputStream = conn.getInputStream();
                   bitmap = BitmapFactory.decodeStream(inputStream);
                 }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            //}
       // }.start();

        return bitmap;

       // }
        //return null;
    }

}
