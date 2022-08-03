package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import com.githang.statusbar.StatusBarCompat;

public class ScreenActivity extends AppCompatActivity {

    ImageView screen_img;
    TextView screen_title;
    private RelativeLayout screen_keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        screen_keyword = findViewById(R.id.screen_keyword);
        screen_img = findViewById(R.id.screen_img);
        //screen_title = findViewById(R.id.screen_title);

        //        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        //        editor.putBoolean("isVerified",false);

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);

        //screen_keyword .setOnClickListener(this::onClick);
        //screen_img.setOnClickListener(this::onClick);
        //screen_title.setOnClickListener(this::onClick);

        initView();
        screen_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScreenActivity.this, SetScreenKeyWordActivity.class);
                startActivity(intent);
            }
        });
        screen_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initView() {
        RelativeLayout screen_keyword;
        screen_keyword = findViewById(R.id.screen_keyword);
    }

    /*
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.screen_keyword:
                intent = new Intent(this,SetScreenKeyWordActivity.class);
                startActivity(intent);
                break;
            case R.id.screen_img:
                finish();
                break;
        }
    }

     */
}

