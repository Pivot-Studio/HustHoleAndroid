package cn.pivotstudio.husthole.view.registerandlogin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.husthole.model.EditTextReaction;
//import cn.pivotstudio.husthole.Model.Login;
import cn.pivotstudio.husthole.network.ErrorMsg;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.network.RetrofitManager;
import cn.pivotstudio.husthole.network.TokenInterceptor;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.network.OkHttpUtil;
import cn.pivotstudio.husthole.view.homescreen.activity.HomeScreenActivity;
import cn.pivotstudio.husthole.view.homescreen.publishhole.PublishHoleActivity;
import cn.pivotstudio.husthole.view.retrievepassword.activity.ForgetPasswordActivity;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;



import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {
    private EditText editText1,editText2;
    private Button button1;
    private ImageView back;
    private TextView wrong,textView1,textView2;
    private Retrofit retrofit;
    private RequestInterface request;
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        editText1 = (EditText) findViewById(R.id.et_login_email);
        editText2 = (EditText) findViewById(R.id.et_login_password);
        button1 = (Button) findViewById(R.id.btn_login_jumptohomescreen);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        wrong=(TextView)findViewById(R.id.tv_login_warn);
        textView1=(TextView)findViewById(R.id.tv_login_jumptocheckemail);
        textView2=(TextView)findViewById(R.id.tv_login_jumptoforgetpassword);
        wrong.setVisibility(View.INVISIBLE);
        button1.setEnabled(false);
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.register_2));//定义hint的值
        SpannableString string2 = new SpannableString(this.getResources().getString(R.string.register_4));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.EditTextSize(editText2,string2,14);
        EditTextReaction.ButtonReaction(editText1,button1);


        TokenInterceptor.getContext(LoginActivity.this);
        System.out.println("提交了context");
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitManager.API+"auth/")
                .client(OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        request = retrofit.create(RequestInterface.class);//创建接口实例

    }
    public void onClick(View v) throws IOException {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_login_jumptocheckemail://注册
                intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login_jumptoforgetpassword://忘记密码
                 intent=new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            case R.id.btn_login_jumptohomescreen://登录
                String str1=editText1.getText().toString();
                String str2=editText2.getText().toString();
                //Login.post(str1,str2);
                //if(Character.isUpperCase(str1.charAt(0))||Character.isLowerCase(str1.charAt(0))) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("email", str1);
                            map.put("password", str2);
                            Call<ResponseBody> call = request.mobileLogin(map);//进行封装
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
                                        System.out.println("总:" + json);
                                        String condition = null;
                                        try {
                                            JSONObject jsonObject = new JSONObject(json);
                                            //读取
                                            condition = jsonObject.getString("msg");
                                            String token = jsonObject.getString("token");
                                            System.out.println("token的具体值:" + token);


                                            SharedPreferences.Editor editor = getSharedPreferences("Depository", Context.MODE_PRIVATE).edit();//获取编辑器
                                            editor.putString("token", token);
                                            editor.putBoolean("iffirstlogin", true);
                                            editor.putString("email",str1);
                                            editor.commit();//提交修改
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if (condition != null && condition.equals("登录成功")) {//判断账号密码是否正确
                                            //登录成功进入主界面
                                            showResponse("登录成功");
                                            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            showResponse("邮箱或密码错误");
                                            //登录失败给与账户或密码错误提示
                                        }
                                    }else{
                                        ErrorMsg.getErrorMsg(response,LoginActivity.this);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(LoginActivity.this, R.string.network_loginfailure, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                        }).start();


                break;
            default:
                break;
        }
    }

    public  void showResponse(final String condition){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(LoginActivity.this, condition, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
