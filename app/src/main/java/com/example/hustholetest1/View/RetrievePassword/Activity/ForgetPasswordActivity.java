package com.example.hustholetest1.View.RetrievePassword.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.RetrievePassword.Activity.VerificationCodeActivity;
import com.githang.statusbar.StatusBarCompat;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class ForgetPasswordActivity extends AppCompatActivity {
    private TextView textView1;
    private Button button1;
    private EditText editText1;
    private ImageView back;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrievepassword_forget);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        textView1=(TextView)findViewById(R.id.textView6);
        textView1.setVisibility(View.INVISIBLE);
        button1=(Button)findViewById(R.id.button4);
        back= (ImageView) findViewById(R.id.backView);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        button1.setEnabled(false);
        editText1=(EditText)findViewById(R.id.EditText);

        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.retrieve_password_forget_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.button4://进入验证码验证界面
                if(true){//判断学号格式是否正确
                    id=editText1.getText().toString();

                    intent=VerificationCodeActivity.newIntent(ForgetPasswordActivity.this,id);
                    startActivity(intent);
                }else{
                    //给与学号格式错误提示
                }
                break;
            case R.id.backView://退回键
                finish();
                break;
            default:
                break;
        }
    }
}
