package com.example.hustholetest1.view.homescreen.mine;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity {
    private RelativeLayout email,security;
    ImageView img;
    TextView tv;
    Boolean isVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        email = findViewById(R.id.email);
        security = findViewById(R.id.security);
        img = findViewById(R.id.settings_img);
        tv = findViewById(R.id.tv);

//        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//        editor.putBoolean("isVerified",false);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        isVerified = pref.getBoolean("isVerified",false);
        tv.setText(isVerified? "已验证":"未验证");

        email.setOnClickListener(this::onClick);
        security.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);

        initView();
    }
    public void initView() {
        RelativeLayout email,security;
        email = findViewById(R.id.email);
        security = findViewById(R.id.security);
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.email:
                if(isVerified){
                    intent = new Intent(this, VerifyOkActivity.class);
                }else {
                    intent = new Intent(this, EmailActivity.class);
                }
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

