package com.example.hustholetest1.View.RegisterAndLogin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.HomePageActivity;
import com.example.hustholetest1.View.RetrievePassword.Activity.ForgetPasswordActivity;
import com.githang.statusbar.StatusBarCompat;

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
    public void onClick(View v){
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
                if(true){//判断账号密码是否正确
                   //登录成功进入主界面
                    intent=new Intent(RegisterActivity.this, HomePageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);;
                    startActivity(intent);
                }else{
                    //登录失败给与账户或密码错误提示
                }
                break;
            default:
                break;
        }
    }
}
