package com.example.hustholetest1.View.RetrievePassword.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;

import java.util.UUID;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class VerificationCodeActivity extends AppCompatActivity {
    private EditText editText1;
    private Button button1;
    private TextView time1,hint1,textView2,textView3;
    private ImageView back;
    private static final String ACTIVITY_IDENTIFY="identify";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrievepassword_vcode);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);
        editText1=(EditText)findViewById(R.id.EditText);
        button1=(Button)findViewById(R.id.button4);
        time1=(TextView)findViewById(R.id.time);
        hint1=(TextView)findViewById(R.id.textView13);
        textView2=(TextView)findViewById(R.id.textView);
        textView3=(TextView)findViewById(R.id.textView2);
        button1=(Button)findViewById(R.id.button4);
        back= (ImageView) findViewById(R.id.backView);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        button1.setEnabled(false);
        //hint1.setVisibility(View.INVISIBLE);
        editText1=(EditText)findViewById(R.id.EditText);
        String id=(String) getIntent()
                .getStringExtra(ACTIVITY_IDENTIFY);
        hint1.setText("验证邮箱已发送至"+id+"@hust.edu.cn");

        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.retrieve_password_vcode_3));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
        CountDownTimer timer = new CountDownTimer(60000, 1000) {//倒计时
            public void onTick(long millisUntilFinished) {
                time1.setText( millisUntilFinished / 1000 + "s");
            }
            public void onFinish() {
                time1.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
            }
        };
        timer.start();
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.button4://验证成功后进入重新设置密码界面
                if(true){//判断验证码是否正确
                intent=new Intent(VerificationCodeActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
                }else{
                  //验证码错误提示
                }
                break;
            case R.id.backView://退回键
                finish();
                break;
            default:
                break;
        }
    }
    public static Intent newIntent(Context context, String id1){
        Intent intent1=new Intent(context,VerificationCodeActivity.class);
        Log.d(TAG,"????"+id1);
        intent1.putExtra(ACTIVITY_IDENTIFY,id1);
        return intent1;
    }
}
