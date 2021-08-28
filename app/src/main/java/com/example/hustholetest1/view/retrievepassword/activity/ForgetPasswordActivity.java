package com.example.hustholetest1.view.retrievepassword.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.R;
import com.example.hustholetest1.network.ErrorMsg;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.view.homescreen.activity.HomeScreenActivity;
import com.example.hustholetest1.view.registerandlogin.activity.LoginActivity;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ForgetPasswordActivity extends AppCompatActivity {
    private TextView textView1;
    private Button button1;
    private EditText editText1;
    private ImageView back;
    private String id;
    private RequestInterface request;
    private Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrievepasswordforget);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        textView1=(TextView)findViewById(R.id.tv_forget_warn);
        textView1.setVisibility(View.INVISIBLE);
        button1=(Button)findViewById(R.id.btn_forget_jumptohomescreen);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        button1.setEnabled(false);
        editText1=(EditText)findViewById(R.id.et_forget_email);

        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.retrieve_password_forget_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitManager.API+"auth/")
                .client(OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        request = retrofit.create(RequestInterface.class);//创建接口实例
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_forget_jumptohomescreen://进入验证码验证界面

                    id=editText1.getText().toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("email", id);
                            map.put("isResetPassword", "true");
                            Call<ResponseBody> call = request.sendVerifyCode(map);//进行封装
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.code()==200) {
                                        String json = "null";
                                        try {
                                            if (response.body() != null) {
                                                json = response.body().string();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();

                                        }
                                            Intent intent= VerificationCodeActivity.newIntent(ForgetPasswordActivity.this,id);
                                            startActivity(intent);
                                    }else{
                                        ErrorMsg.getErrorMsg(response,ForgetPasswordActivity.this);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(ForgetPasswordActivity.this, R.string.network_loginfailure, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }).start();
                    //api/auth/sendVerifyCode
                    //intent= VerificationCodeActivity.newIntent(ForgetPasswordActivity.this,id);
                   // startActivity(intent);
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            default:
                break;
        }
    }
}
