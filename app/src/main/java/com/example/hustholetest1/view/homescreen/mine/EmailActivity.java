package com.example.hustholetest1.view.homescreen.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;

public class EmailActivity extends AppCompatActivity {

    Button btn1,btn2;
    ImageView img;

    protected void onCreate(Bundle savedInstanceState) {
        View bar;
        TextView tv;
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify1);

        btn1 = findViewById(R.id.btn_1);
        btn2 = findViewById(R.id.btn_2);
        img  = findViewById(R.id.email_verify1_img);

        btn1.setOnClickListener(this::onClick);
        btn2.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);

    }
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_1:
                intent = new Intent(this, VerifyActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_2:
            case R.id.email_verify1_img:
                finish();
                break;
        }
    }


}

