package cn.pivotstudio.husthole.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PictureReading {//弃用
    public static Bitmap getBitmap(String path) throws IOException {
        Bitmap bitmap=null;
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
        return bitmap;
    }

}
