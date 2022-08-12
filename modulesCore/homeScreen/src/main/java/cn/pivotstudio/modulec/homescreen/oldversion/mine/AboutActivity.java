package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import com.githang.statusbar.StatusBarCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        ImageView img = findViewById(R.id.about_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

