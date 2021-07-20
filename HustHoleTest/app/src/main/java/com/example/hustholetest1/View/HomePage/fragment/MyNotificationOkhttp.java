package com.example.hustholetest1.View.HomePage.fragment;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyNotificationOkhttp {
    public static String getStringByOkhttp(String path){
        OkHttpClient client = new OkHttpClient();
        //String token = getToken();
        String TAG = "tag";
        Request request = new Request.Builder().get().addHeader("Authorization","eyJhbGciOiJIUzI1NiIsInR5cCI6Ikp" +
                "XVCJ9.eyJlbWFpbCI6IjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwZmM2NGNlZTM5ZTA1ZGJjNWI2ODViNDM2OWMyNTR" +
                "iNDg5OTBkZmU1ZmQ5YTciLCJyb2xlIjoidXNlciIsInRpbWVTdGFtcCI6MTYyNjQ4OTE2Mn0.L_L0AFqPFEwoiQJHicJi3P4vy9aj_h" +
                "x8E8aq0OkC74s").url("http://hustholetest.pivotstudio.cn/api/notices/?start_id=1&list_size=8").build();

        Log.d(TAG, "getStringByOkhttp: request");
        try {
            Call call = client.newCall(request);
            Log.d(TAG, "getStringByOkhttp: call");
            call.enqueue(new Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    System.out.println("Failed");
                    Log.d(TAG, "onFailure: i am on Failure");
                    Log.d(TAG, "onFailure: e.tostring "+ e.toString());
                    Log.d(TAG,"e.getLocalizedMessage"+e.getLocalizedMessage());

                }
                @Override
                public void onResponse(Call call,Response response)throws IOException{
                    if(response.isSuccessful()){//回调的方法执行在子线程。
                        Log.d(TAG,"获取数据成功了");
                        Log.d(TAG,"response.code()=="+response.code());
                        Log.d(TAG,"response.body().string()== "+response.body().string());
                    }
                    else{
                        Log.d(TAG, "onResponse: response " + response.networkResponse());
                    }
                    /*if (response.body()!=null){
                        Log.d(TAG, "onResponse: i am close");
                        response.body().close();
                    }*/
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "getStringByOkhttp: i am exception");
            e.printStackTrace();
            Log.d(TAG, "getStringByOkhttp: e.toString() "+ e.toString());
        }
        Log.d(TAG, "getStringByOkhttp: getResponseData: null");
        return null;
    }
    private static String getToken() {
        return null;
    }
}
