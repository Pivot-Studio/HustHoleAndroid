package com.example.hustholetest1.view.homescreen.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.hustholetest1.R;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.network.RequestInterface;
import com.githang.statusbar.StatusBarCompat;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyActivity extends AppCompatActivity {
    Button btn_verify;
    Boolean match = false;
    EditText et;
    TextView tv_again, tv_howTo;
    ImageView img;
    Retrofit retrofit;
    private RequestInterface request;

    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify2);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        et = findViewById(R.id.et_verify);
        btn_verify = findViewById(R.id.btn_verify);
        tv_again = findViewById(R.id.tv_again);
        tv_howTo = findViewById(R.id.tv_howTo);
        img = findViewById(R.id.email_verify2_img);

        btn_verify.setOnClickListener(this::onClick);
        tv_again.setOnClickListener(this::onClick);
        tv_howTo.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);

    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_verify:
                String verifyCode = et.getText().toString();
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://hustholetest.pivotstudio.cn/api/")
                        .client(OkHttpUtil.getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
//        Log.e(TAG, "token99：");
                request = retrofit.create(RequestInterface.class);//创建接口实例
//                if(request == null){Log.d("xxx","ttt");}
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Call<ResponseBody> call = request.verifyCodeMatch();//进行封装
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.body() != null) {
                                    try {
                                        Log.d("xxx", response.body().string());
                                        match = response.body().string().equals("true");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }
                }).start();
                match = true;
//                match = verifyCodeMatch(verifyCode);
                if (match) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putBoolean("isVerified", true);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "验证成功。", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "验证码不匹配，请重新输入。", Toast.LENGTH_SHORT).show();
                    et.setText("");
                }
                break;
            case R.id.tv_again:
                Toast.makeText(getApplicationContext(), "已重新发送。", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_howTo:
                Toast.makeText(getApplicationContext(), "查询邮箱。", Toast.LENGTH_SHORT).show();
                break;
            case R.id.email_verify2_img:
                finish();
                break;
        }
    }
}