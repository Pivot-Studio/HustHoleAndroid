package com.example.hustholetest1.view.retrievepassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.R;

import com.example.hustholetest1.view.registerandlogin.activity.LoginActivity;
import com.githang.statusbar.StatusBarCompat;


public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText editText1;
    private TextView textView1;
    private Button button1;
    private ImageView imageView,back;
    private boolean flag=false;

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
                if(true) {
                    intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //清除前面所有活动
                    startActivity(intent);
                } else{
                   //密码格式错误
                }
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            default:
                break;
        }
    }

}