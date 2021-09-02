package com.example.hustholetest1.view.homescreen.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.R;
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.githang.statusbar.StatusBarCompat;

public class SettingsActivity extends AppCompatActivity {
    ImageView img;
    TextView isVerified;
//    Boolean isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        RelativeLayout email = findViewById(R.id.email);
        RelativeLayout security = findViewById(R.id.security);
        img = findViewById(R.id.settings_img);
        isVerified = findViewById(R.id.tv);

        email.setOnClickListener(this::onClick);
        security.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);

        initView();
    }
    public void initView() {
        isVerified.setText(CheckingToken.IfTokenExist() ? "已验证" : "未验证");
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.email:
                intent = CheckingToken.IfTokenExist() ? new Intent(this, VerifyOkActivity.class)
                                                : new Intent(this, EmailVerifyActivity.class);
                startActivity(intent);
                break;
            case R.id.security:
                intent = new Intent(this,SecurityActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_img:
                finish();
                break;
        }
    }

}

