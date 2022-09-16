package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.MyHoleFragment;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.MyReplyFragment;
import cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment.MyStarFragment;
import cn.pivotstudio.modulec.homescreen.oldversion.mypage.FragmentAdapter;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.tabs.TabLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HoleStarReplyActivity extends AppCompatActivity {
    Intent intent;
    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ImageView imgMy;
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
        imgMy.setOnClickListener(v -> finish());

        fragments.add(cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFragment.newInstance());
        fragments.add(MyStarFragment.newInstance());
        fragments.add(MyReplyFragment.newInstance());

        //      注意这里TabLayout的写法。
        titles.add("我的树洞");
        titles.add("我的关注");
        titles.add("我的评论");
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
        changeTabIndicatorWidth(mTabLayout, 10);
    }

    private void initViewPager() {

        //        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        //        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));

        mViewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter =
            new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(pageChangeListener);
    }

    public void changeTabIndicatorWidth(final TabLayout tabLayout, final int margin) {
        Log.d("inin", "in this");
        tabLayout.post(() -> {
            try {
                Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
                mTabStripField.setAccessible(true);

                LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

                int dp10 = margin == 0 ? 50 : margin;

                for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                    View tabView = mTabStrip.getChildAt(i);

                    Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                    mTextViewField.setAccessible(true);

                    TextView mTextView = (TextView) mTextViewField.get(tabView);

                    tabView.setPadding(0, 0, 0, 0);

                    int width = 0;
                    width = mTextView.getWidth();
                    if (width == 0) {
                        mTextView.measure(0, 0);
                        width = mTextView.getMeasuredWidth();
                    }

                    LinearLayout.LayoutParams params =
                        (LinearLayout.LayoutParams) tabView.getLayoutParams();
                    params.width = width;
                    params.leftMargin = dp10;
                    params.rightMargin = dp10;
                    tabView.setLayoutParams(params);
                    tabView.invalidate();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
}