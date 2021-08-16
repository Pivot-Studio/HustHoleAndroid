package com.example.hustholetest1.view.retrievepassword.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;


public class ForgetPasswordActivity extends AppCompatActivity {
    private TextView textView1;
    private Button button1;
    private EditText editText1;
    private ImageView back;
    private String id;
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
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_forget_jumptohomescreen://进入验证码验证界面
                if(true){//判断学号格式是否正确
                    id=editText1.getText().toString();

                    intent= VerificationCodeActivity.newIntent(ForgetPasswordActivity.this,id);
                    startActivity(intent);
                }else{
                    //给与学号格式错误提示
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
