package com.example.hustholetest1.View.RegisterAndLogin.Activity;

import android.content.Intent;
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

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.Model.Login;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.HomePageActivity;
import com.example.hustholetest1.View.RetrievePassword.Activity.ForgetPasswordActivity;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONArray;
import org.json.JSONObject;



import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText1,editText2;
    private Button button1;
    private ImageView back;
    private TextView wrong,textView1,textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        editText1 = (EditText) findViewById(R.id.EditText);
        editText2 = (EditText) findViewById(R.id.EditText1);
        button1 = (Button) findViewById(R.id.button4);
        back= (ImageView) findViewById(R.id.backView);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        wrong=(TextView)findViewById(R.id.textView6);
        textView1=(TextView)findViewById(R.id.textView7);
        textView2=(TextView)findViewById(R.id.textView5);
        wrong.setVisibility(View.INVISIBLE);
        button1.setEnabled(false);
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.register_2));//定义hint的值
        SpannableString string2 = new SpannableString(this.getResources().getString(R.string.register_4));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.EditTextSize(editText2,string2,14);
        EditTextReaction.ButtonReaction(editText1,button1);
    }
    public void onClick(View v) throws IOException {
        Intent intent;
        switch (v.getId()) {
            case R.id.textView7://注册
                intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.textView5://忘记密码
                 intent=new Intent(RegisterActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.backView://退回键
                finish();
                break;
            case R.id.button4://登录
                String str1=editText1.getText().toString();
                String str2=editText2.getText().toString();
                //Login.post(str1,str2);
                if(Character.isUpperCase(str1.charAt(0))||Character.isLowerCase(str1.charAt(0))) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Response response;
                                OkHttpClient client = new OkHttpClient();//
                                Log.d("TAG", "jjj");
                                FormBody body = new FormBody.Builder()
                                        .add("email", str1)
                                        .add("password", str2)
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://hustholetest.pivotstudio.cn/api/auth/mobileLogin")
                                        .post(body)
                                        .build();
                                Log.d("TAG", "jjj1");
                                response = client.newCall(request).execute();
                                Log.d("TAG", "jjjjjj5");
                                //Response response=client.newCall(request).execute();
                                String json = response.body().string();
                                JSONObject jsonObject = new JSONObject(json);
                                //读取
                                String condition = jsonObject.getString("msg");
                                String token = jsonObject.getString("token");
                                System.out.println("状态:" + condition);
                                showResponse(condition);
                                // Log.d("TAG", sssss);
                                if (condition.equals("登录成功")) {//判断账号密码是否正确
                                    //登录成功进入主界面
                                    Intent intent = new Intent(RegisterActivity.this, HomePageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else {
                                    //登录失败给与账户或密码错误提示
                                }
                            } catch (Exception E) {
                                E.printStackTrace();
                                Log.d("TAG", "jjjjjj4");
                            }
                        }
                    }).start();
                }else{
                    showResponse("学号格式错误，请重新输入");
                    Log.d("TAG","jjjjjj3");
                   // Toast.makeText(RegisterActivity.this, "学号格式错误，请重新输入", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }
    public  void showResponse(final String condition){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(RegisterActivity.this, condition, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
