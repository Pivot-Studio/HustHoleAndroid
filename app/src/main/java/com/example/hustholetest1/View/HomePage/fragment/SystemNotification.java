package com.example.hustholetest1.View.HomePage.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hustholetest1.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.hustholetest1.View.HomePage.fragment.Page3Fragment.removeCharAt;
import static com.example.hustholetest1.View.HomePage.fragment.ParseNotificationData.parseJson;
import static com.example.hustholetest1.View.HomePage.fragment.ParseNotificationData.parseSysJson;

public class SystemNotification extends AppCompatActivity {
    private TextView textView;
    private List<SystemNotificationBean> mSystemNotificationList = new ArrayList<>();
    private SystemNotificationAdapter adapter;
    private RecyclerView sysNotificationRecyclerView;
    private String TAG = "tag";
    public String url = "http://hustholetest.pivotstudio.cn/api/system_notices";
    private final static int list_size = 10;
    private int start_id = 0;
    private ImageView backView;
    //private MyItemDecoration myItemDecoration = new MyItemDecoration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_notification);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        textView = findViewById(R.id.mTextview);
        textView.setText("系统通知");
        sysNotificationRecyclerView = this.findViewById(R.id.system_notification);
        backView = findViewById(R.id.backView);
        backView.setOnClickListener(new onClickBack(this));

        //initData();
        //myItemDecoration.setColor(Color.parseColor("#F3F3F3"));
        /*adapter = new SystemNotificationAdapter(mSystemNotificationList);
        sysNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        sysNotificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        sysNotificationRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new SystemNotificationAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(SystemNotification.this, "click " + position, Toast.LENGTH_SHORT).show();
            }
        });*/
        getStringByOkhttp(url + "?" + "start_id=" + start_id + "&" +"list_size=" + list_size);

    }

    public class onClickBack implements View.OnClickListener {

        private Context context;

        public onClickBack(Context context)
        {
            this.context = context;
        }
        @Override
        public void onClick(View v) {
            finish();
        }

    }


    /*private void initData(){
        Log.d(TAG, "initData: init first");
        SystemNotificationBean systemNotification1 = new SystemNotificationBean("1", "1037树洞", "巴拉巴拉巴拉拉拉"
                , "false", "2021.7.27");
        Log.d(TAG, "initData: init first2");
        SystemNotificationBean systemNotification2 = new SystemNotificationBean("1", "1037树洞2", "巴拉巴拉巴拉拉拉"
                , "false", "2021.7.27");
        SystemNotificationBean systemNotification3 = new SystemNotificationBean("1", "1037树洞3", "巴拉巴拉巴拉拉拉"
                , "false", "2021.7.27");
        mSystemNotificationList.add(systemNotification1);
        mSystemNotificationList.add(systemNotification2);
        mSystemNotificationList.add(systemNotification3);
        Log.d(TAG, "onCreate: bbbbb");
    }*/
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: msr.what " +msg.what);
            switch (msg.what){
                case 0://请求网络成功
                    String Data = (String)msg.obj;
                    Log.d(TAG, "handleMessage: get Data "+ Data);
                    mSystemNotificationList = parseSysJson(Data);
                    Log.d(TAG, "handler: mSystemNotificationList "+ mSystemNotificationList.get(0).getSystemContent());
                    adapter = new SystemNotificationAdapter(mSystemNotificationList);
                    sysNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(getParent(),
                            LinearLayoutManager.VERTICAL, false));
                    sysNotificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    sysNotificationRecyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SystemNotificationAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            Toast.makeText(SystemNotification.this, "click " + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 1://失败
                    break;
            }
        }
    };

    public void getStringByOkhttp(String path) {
        OkHttpClient client = new OkHttpClient();
        Message message = Message.obtain();
        Request request = new Request.Builder().get().addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Ikp" +
                "XVCJ9.eyJlbWFpbCI6IjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwZmM2NGNlZTM5ZTA1ZGJjNWI2ODViNDM2OWMyNTR" +
                "iNDg5OTBkZmU1ZmQ5YTciLCJyb2xlIjoidXNlciIsInRpbWVTdGFtcCI6MTYyNjQ4OTE2Mn0.L_L0AFqPFEwoiQJHicJi3P4vy9aj_h" +
                "x8E8aq0OkC74s").url(path).build();

        Log.d(TAG, "getStringByOkhttp: request");
        try {
            Call call = client.newCall(request);
            Log.d(TAG, "getStringByOkhttp: call");
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    message.what = 1;
                    message.obj = e.getMessage();
                    Log.d(TAG, "onFailure: e.toString " + e.toString());
                    Log.d(TAG, "e.getLocalizedMessage" + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {//回调的方法执行在子线程。
                        Log.d(TAG, "获取数据成功了");
                        Log.d(TAG, "response.code()==" + response.code());
                        final String responseData = response.body().string();
                        Log.d(TAG, "response.body().string()== " + responseData);
                        Log.d(TAG, "obtain message");
                        String temp = responseData.replace("{\"system_notices\":", "");
                        temp = removeCharAt(temp, temp.length()-1);
                        message.what = 0;
                        message.obj = temp;
                        handler.sendMessage(message);
                        Log.d(TAG, "onResponse: handler message");
                    } else {
                        message.what = 1;
                        Log.d(TAG, "onResponse: response " + response.networkResponse());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getStringByOkhttp:i am exception, e.toString() " + e.toString());
        }
    }

    /*public class MyItemDecoration extends RecyclerView.ItemDecoration{
        private Paint mPaint;
        public MyItemDecoration() {
            mPaint = new Paint();
            *//*mPaint.setAntiAlias(true);          抗锯齿*//*
            mPaint.setColor(Color.GRAY);        //默认颜色
        }
        public MyItemDecoration setColor(int color) {
            mPaint.setColor(color);
            return this;
        }
    }*/


}