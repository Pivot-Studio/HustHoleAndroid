package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.network.OkHttpUtil;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;

import com.githang.statusbar.StatusBarCompat;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {
    ImageView img;
    TextView isVerified;
    Retrofit retrofit;
    RequestInterface request;
    String returncondition = "0";
    String emailid = "";

    //    Boolean isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
                true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        RelativeLayout email = findViewById(R.id.email);
        RelativeLayout security = findViewById(R.id.security);
        img = findViewById(R.id.settings_img);
        isVerified = findViewById(R.id.tv);

        email.setOnClickListener(this::onClick);
        security.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);
        retrofit = RetrofitManager.getRetrofit();
        request = RetrofitManager.getRequest();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request.isUnderSecurity();
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call,
                                           Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                JSONObject jsonObject = new JSONObject(json);
                                returncondition = jsonObject.getString("is_email_activated");
                                emailid = jsonObject.getString("email");
                                //   Log.d("email",returncondition);
                                if (returncondition.equals("false")) {
                                    isVerified.setText("未验证");
                                } else if (returncondition.equals("true")) {
                                    isVerified.setText("已验证");
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //followCondition = false;
                            String json = "null";
                            if (response.errorBody() != null) {
                                try {
                                    json = response.errorBody().string();
                                    //JSONObject jsonObject = new JSONObject(json);
                                    // returncondition = jsonObject.getString("msg");
                                    Toast.makeText(SettingsActivity.this, json, Toast.LENGTH_SHORT)
                                            .show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this,
                                        R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        isVerified.setText("请检查网络");
                        Toast.makeText(SettingsActivity.this, R.string.network_loadfailure,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

        initView();
    }

    public void initView() {

    }

    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.email) {
            if (returncondition.equals("false")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Retrofit retrofit2 = new Retrofit.Builder().baseUrl(RetrofitManager.API)
                                .client(OkHttpUtil.getOkHttpClient2())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RequestInterface request2 = retrofit2.create(RequestInterface.class);
                        HashMap map = new HashMap();
                        SharedPreferences editor =
                                SettingsActivity.this.getSharedPreferences("Depository",
                                        Context.MODE_PRIVATE);//
                        String condition = editor.getString("email", "");
                        Call<ResponseBody> call = request2.sendVerifyCode(RetrofitManager.API
                                + "auth/sendVerifyCode?email="
                                + condition
                                + "&isResetPassword=false");
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call,
                                                   Response<ResponseBody> response) {
                                if (response.code() == 200) {
                                    String json = "null";
                                    try {
                                        if (response.body() != null) {
                                            json = response.body().string();
                                            JSONObject jsonObject = new JSONObject(json);
                                            returncondition = jsonObject.getString("msg");
                                            Toast.makeText(SettingsActivity.this, returncondition,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //                                    Intent intent = CheckingToken.IfTokenExist() ? VerifyActivity.newIntent(SettingsActivity.this, emailid)
                                    //                                            : new Intent(SettingsActivity.this, EmailVerifyActivity.class);
                                    //                                    startActivity(intent);
                                } else {
                                    String json = "null";
                                    String returncondition = null;
                                    if (response.errorBody() != null) {

                                        try {
                                            json = response.errorBody().string();
                                            Toast.makeText(SettingsActivity.this, json,
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(SettingsActivity.this,
                                                        R.string.network_unknownfailture, Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    // ErrorMsg.getErrorMsg(response,SettingsActivity.this);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                Toast.makeText(SettingsActivity.this, R.string.network_loginfailure,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            } else if (returncondition.equals("true")) {
                //                    intent = CheckingToken.IfTokenExist() ? new Intent(this, VerifyOkActivity.class)
                //                            : new Intent(this, EmailVerifyActivity.class);
                //                    startActivity(intent);

            }
        } else if (id == R.id.security) {
            intent = new Intent(this, SecurityActivity.class);
            startActivity(intent);
        } else if (id == R.id.settings_img) {
            finish();
        }
    }
}

