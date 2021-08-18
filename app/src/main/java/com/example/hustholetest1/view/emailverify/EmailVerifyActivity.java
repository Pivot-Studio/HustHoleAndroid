package com.example.hustholetest1.view.emailverify;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.R;
import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.view.registerandlogin.activity.LoginActivity;
import com.example.hustholetest1.view.retrievepassword.activity.ModifyPasswordActivity;
import com.example.hustholetest1.view.retrievepassword.activity.VerificationCodeActivity;
import com.githang.statusbar.StatusBarCompat;

public class EmailVerifyActivity extends AppCompatActivity {
    private Button mLoginBtn,mStayBtn;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverify);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);
        mLoginBtn=(Button)findViewById(R.id.btn_emailverify_jumptologin);
        mStayBtn=(Button)findViewById(R.id.btn_emailverify_stay);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(EmailVerifyActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mStayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
       back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });
    }
}
