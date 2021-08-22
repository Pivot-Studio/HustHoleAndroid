package com.example.hustholetest1.view.homescreen.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.R;
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SecurityActivity extends AppCompatActivity {
    SwitchMaterial isUnderSecurity;
    private static final String BASE_URL = "http://husthole.pivotstudio.cn/api/";
    private String TAG = "insecurity";
    Retrofit retrofit;
    Boolean turnOn;
    RequestInterface request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        isUnderSecurity = findViewById(R.id.st_security);
        setInitMode();

        if(CheckingToken.IfTokenExist()) {
            isUnderSecurity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        changeSecurityMode(true);
                    }else{
                        changeSecurityMode(false);
                    }
                }
            });
        }else{
            Intent intent=new Intent(this, EmailVerifyActivity.class);
            startActivity(intent);
        }
    }
    private void  setInitMode(){
        final Boolean[] isUnder = new Boolean[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request.isUnderSecurity();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                       try {
                            JSONObject mode = new JSONObject(response.body().string());
                            Log.d(TAG,response.body().string());
                            isUnder[0] = mode.getBoolean("is_incognito");
                            Log.d(TAG,"现在处于安全模式？" + isUnder[0]);
                        } catch (IOException | JSONException e) {
                           Log.d(TAG,"in this");
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });
            }
        }).start();
        if(isUnder[0] != null){
            isUnderSecurity.setChecked(isUnder[0]);
        }else{
            isUnderSecurity.setChecked(false);
        }
    }
    private void changeSecurityMode(Boolean turnOn){
        new Thread(new Runnable() {
            @Override
            public void run() {
                request = retrofit.create(RequestInterface.class);
                HashMap map = new HashMap();
                map.put("to_incognito",turnOn);
                Call<ResponseBody> call = request.changeSecurityMode(map);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG,"切换成功，现在是打开？" + response.body());
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });
            }
        }).start();
    }
}

