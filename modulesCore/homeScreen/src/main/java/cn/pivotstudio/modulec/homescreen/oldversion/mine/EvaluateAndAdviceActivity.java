package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.AdviceFragment;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.EvaluateFragment;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class EvaluateAndAdviceActivity extends AppCompatActivity {
    Intent intent;
    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView imgMy;
    private TextView myTitle;
    private final ViewPager.OnPageChangeListener pageChangeListener =
        new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                                       float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hole_star);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        mTabLayout = findViewById(R.id.tl_hole_star);

        mViewPager = findViewById(R.id.vp_hole_star);
        imgMy = findViewById(R.id.my_img);
        imgMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                finish();
            }
        });

        myTitle = findViewById(R.id.my_title);
        myTitle.setText("评价与建议");

        fragments.add(EvaluateFragment.newInstance());
        fragments.add(AdviceFragment.newInstance());

        //      注意这里TabLayout的写法。
        titles.add("评价一下");
        titles.add("给点建议");

        //        initViewPager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles.get(position);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);

        intent = getIntent();
        mViewPager.setCurrentItem(intent.getIntExtra("initFragmentID", 0));
    }
}
