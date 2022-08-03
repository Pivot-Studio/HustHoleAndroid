package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.chip.Chip;

public class AdviceActivity extends AppCompatActivity {

    //    MaterialButton
    Chip advice, error, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View bar;
        TextView tv;
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        bar = findViewById(R.id.bar);
        tv = bar.findViewById(R.id.title);
        tv.setText("建议");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        advice = findViewById(R.id.cp_advice);
        advice.setOnClickListener(this::onClick);
    }

    void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.cp_advice) {//                advice.setText("123");
            //                ColorStateList list1 = new ColorStateList(R.color.HH_BandColor_3,R.color.HH_BandColor_3);
            //                ColorStateList list = new ColorStateList(getResources().getColor(R.color.HH_BandColor_3),getResources().getColor(R.color.HH_BandColor_5));
        }
    }
}

