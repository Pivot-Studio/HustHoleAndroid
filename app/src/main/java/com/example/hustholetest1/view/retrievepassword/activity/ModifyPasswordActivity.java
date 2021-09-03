package com.example.hustholetest1.view.retrievepassword.activity;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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


public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText editText1;
    private TextView textView1;
    private Button button1;
    private ImageView imageView,back;
    private boolean flag=false;
    private static final String ACTIVITY_IDENTIFY="identify1";
    private static final String ACTIVITY_IDENTIFY2="identify2";
    private RequestInterface request;
    private Retrofit retrofit;
    private String vertify,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrievepasswordmodify);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);
        editText1=(EditText)findViewById(R.id.et_modify_email);
        editText1.setTransformationMethod(new PasswordTransformationMethod());
        button1=(Button)findViewById(R.id.btn_modify_jumptohomescreen);
        textView1=(TextView)findViewById(R.id.tv_modify_warn);
        imageView=(ImageView)findViewById(R.id.iv_modify_visible);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        textView1.setVisibility(View.INVISIBLE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.retrieve_password_modify_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);

        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitManager.API+"auth/")
                .client(OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        request = retrofit.create(RequestInterface.class);//创建接口实例
        id=(String) getIntent()
                .getStringExtra(ACTIVITY_IDENTIFY);
        vertify=(String) getIntent()
                .getStringExtra(ACTIVITY_IDENTIFY);
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_modify_visible://隐藏/显示密码
                if(flag){
                    imageView.setImageResource(R.drawable.checkbox_false);
                    editText1.setTransformationMethod(new PasswordTransformationMethod());
                }else{
                    imageView.setImageResource(R.drawable.checkbox_true);
                    editText1.setTransformationMethod(null);
                }
                flag = !flag;
                break;
            case R.id.btn_modify_jumptohomescreen://重新设置完密码后进入登录界面，并且将之前的活动清除
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            String password=editText1.getText().toString();
                            map.put("email", id);
                            map.put("verify_code",vertify);
                            map.put("new_password",password);
                            Call<ResponseBody> call = request.resetPassword(RetrofitManager.API+"auth/mobileChangePassword?email="+id+"&verify_code="+vertify+"&new_password="+password);//进行封装
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
                                        Intent intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        //清除前面所有活动
                                        startActivity(intent);
                                    }else{
                                        String json = "null";
                                        String returncondition = null;
                                        if (response.errorBody() != null) {
                                            try {
                                                json = response.errorBody().string();
                                               // JSONObject jsonObject = new JSONObject(json);
                                               // returncondition = jsonObject.getString("msg");
                                                Toast.makeText(ModifyPasswordActivity.this, json, Toast.LENGTH_SHORT).show();
                                            } catch (IOException  e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            Toast.makeText(ModifyPasswordActivity.this, R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                        }
                                        //ErrorMsg.getErrorMsg(response,ModifyPasswordActivity.this);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(ModifyPasswordActivity.this, R.string.network_loginfailure, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }).start();
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            default:
                break;
        }
    }
    public static Intent newIntent(Context context, String id1,String vertify){
        Intent intent1=new Intent(context,ModifyPasswordActivity.class);
        intent1.putExtra(ACTIVITY_IDENTIFY,id1);
        intent1.putExtra(ACTIVITY_IDENTIFY2,vertify);
        return intent1;
    }
}

