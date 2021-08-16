package com.example.hustholetest1.view.homescreen.mine;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hustholetest1.R;
import com.example.hustholetest1.view.homescreen.mypage.FragmentAdapter;
import com.example.hustholetest1.view.homescreen.mine.fragment.MyHoleFragment;
import com.example.hustholetest1.view.homescreen.mine.fragment.MyStarFragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyHoleAndMyStarActivity extends AppCompatActivity{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    List<Fragment> fragments = new ArrayList<>();
    List<String> titles = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hole_star);

//        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
//        if(getSupportActionBar()!=null){//隐藏上方ActionBar
//            getSupportActionBar().hide();
//        }

        mTabLayout = findViewById(R.id.tl_hole_star);
        mViewPager = findViewById(R.id.vp_hole_star);

        fragments.add(MyHoleFragment.newInstance());
        fragments.add(MyStarFragment.newInstance());


//      注意这里TabLayout的写法。
        titles.add("我的树洞");
        titles.add("我的关注");
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
    }

    private void initViewPager() {


//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
//        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));




        mViewPager.setOffscreenPageLimit(2);

        FragmentAdapter mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(mFragmentAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mFragmentAdapter);

        mViewPager.addOnPageChangeListener(pageChangeListener);
    }
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}