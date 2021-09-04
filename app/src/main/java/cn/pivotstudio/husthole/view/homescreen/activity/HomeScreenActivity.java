package cn.pivotstudio.husthole.view.homescreen.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import cn.pivotstudio.husthole.model.BaseViewPager;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.model.CheckingToken;
import cn.pivotstudio.husthole.network.RetrofitManager;
import cn.pivotstudio.husthole.network.TokenInterceptor;
import cn.pivotstudio.husthole.view.emailverify.EmailVerifyActivity;
import cn.pivotstudio.husthole.view.homescreen.forest.DetailForestActivity;
import cn.pivotstudio.husthole.view.homescreen.publishhole.PublishHoleActivity;
import cn.pivotstudio.husthole.view.homescreen.fragment.HomePageFragment;
import cn.pivotstudio.husthole.view.homescreen.fragment.ForestFragment;
import cn.pivotstudio.husthole.view.homescreen.fragment.MessageFragment;
import cn.pivotstudio.husthole.view.homescreen.fragment.MineFragment;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class HomeScreenActivity extends AppCompatActivity {
    private BaseViewPager mViewPager;
    private List<Fragment> mFragments;
    private MyFragmentPagerAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private int[] a=new int[2];

   // private RadioGroup mTabRadioGroup;
    private ImageView imageView1,imageView2,imageView3,imageView4;
    private ConstraintLayout constraint1,constraint2,constraint3,constraint4;
    private TextView textView0,textView1,textView2,textView3,textView4;
    private FloatingActionButton addhole;
    private ImageView mOptionBoxIv;
    private ConstraintLayout mTitleBarLl;
    private String mTags;
    public static int gOptionBoxAndBarHeight;
    public static int GetOBAndTBHeight(){
        return gOptionBoxAndBarHeight;
    }
    private long exitTime = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }


        int[] location=new int[2];
        //titleBar.getLocationOnScreen(location);
        DisplayMetrics outMetrics = new DisplayMetrics();
        HomeScreenActivity.this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int widthPixels = outMetrics.widthPixels;
        int heightPixels = outMetrics.heightPixels;

        mFragments = new ArrayList<>(4);
        mFragments.add(HomePageFragment.newInstance());
        mFragments.add(ForestFragment.newInstance());
        mFragments.add(MessageFragment.newInstance());
        mFragments.add(MineFragment.newInstance());




        imageView1=(ImageView)findViewById(R.id.iv_homescreen_hompageicon);
        imageView2=(ImageView)findViewById(R.id.iv_homescreen_foresticon);
        imageView3=(ImageView)findViewById(R.id.iv_homescreen_messageicon);
        imageView4=(ImageView)findViewById(R.id.iv_homescreen_mineicon);

        constraint1=(ConstraintLayout) findViewById(R.id.cl_homescreen_hompage);
        constraint2=(ConstraintLayout) findViewById(R.id.cl_homescreen_forest);
        constraint3=(ConstraintLayout) findViewById(R.id.cl_homescreen_message);
        constraint4=(ConstraintLayout) findViewById(R.id.cl_homescreen_mine);
        textView0=(TextView)findViewById(R.id.tv_homescreen_titlebarname);
        textView1=(TextView)findViewById(R.id.tv_homescreen_homepagename);
        textView2=(TextView)findViewById(R.id.tv_homescreen_forestname);
        textView3=(TextView)findViewById(R.id.tv_homescreen_messagename);
        textView4=(TextView)findViewById(R.id.tv_homescreen_minename);
        addhole=(FloatingActionButton)findViewById(R.id.fab_homescreen_publishhole);
        mOptionBoxIv=(ImageView)findViewById(R.id.iv_homescreen_optionbox);
        mTitleBarLl=(ConstraintLayout)findViewById(R.id.ll_homescreen_titlebar);
        mViewPager = (BaseViewPager) findViewById(R.id.vp_homescreen);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mOptionBoxIv.measure(w, h);
        int height = mOptionBoxIv.getMeasuredHeight();
        int w2 = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h2 = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mTitleBarLl.measure(w2, h2);
        int height2 = mTitleBarLl.getMeasuredHeight();
        gOptionBoxAndBarHeight=height+height2;
        Log.d("height+height2",height+"+"+height2);
        WindowManager wm = (WindowManager)HomeScreenActivity.this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);
        //gOptionBoxTopLocation=mOptionBoxIv.getTop();



        addhole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //关闭掉对话框,拿到对话框的对象
                if(CheckingToken.IfTokenExist()) {
                    Intent intent = PublishHoleActivity.newIntent(HomeScreenActivity.this, "1","无");
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(HomeScreenActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
                }
            }
        });
        CheckingToken.getContext(HomeScreenActivity.this);
        TokenInterceptor.getContext(HomeScreenActivity.this);
        RetrofitManager.RetrofitBuilder(RetrofitManager.API);
        a[0]=R.id.cl_homescreen_hompage;
        a[1]=R.id.cl_homescreen_hompage;
        SharedPreferences editor = HomeScreenActivity.this.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        Boolean condition = editor.getBoolean("iffirstlogin", true);
        String token=editor.getString("token","");
        if(condition) {
            View mView = View.inflate(getApplicationContext(), R.layout.dialog_homepage, null);
            // mView.setBackgroundResource(R.drawable.homepage_notice);
            //设置自定义的布局
            //mBuilder.setView(mView);
            Dialog dialog = new Dialog(this);
            dialog.setContentView(mView);
            TextView mEt_first_password = (TextView) mView.findViewById(R.id.tv_dialoghomepage_title);
            TextView mEt_second_password = (TextView) mView.findViewById(R.id.tv_dialoghomepage_content);
            mEt_second_password.setMovementMethod(ScrollingMovementMethod.getInstance());
            mEt_first_password.setText(R.string.homepage_2);
            String aa = ("<small><font color=\"#00000000\">你好，这里是1037树洞~</font></small><br>" +
                    " <small><font color=\"#666666\">请先别急着跳过噢，花三十秒听听树洞的悄悄话吧</font></small>" + "<font color=\"#666666\">(●'◡'●)</font><br><br>" +
                    "<strong><font color=\"#00000000\">1037树洞是什么</font></strong><br><br>" +
                    "<small><font color=\"#666666\">1037树洞是专属于HUSTer的</font></small><small><font color=\"#00000000\">匿名社区</font></small>，" +
                    "<small><font color=\"#666666\">通过学号绑定的校园邮箱来验证你的华科在校学生身份。你的学号邮箱仅会被用于验证，而不会在社区中被展示；通过后台加密算法，除了在严重违反社区规范的情况下且运营者认为有必要时，</font></small>" +
                    "<small><font color=\"#00000000\">任何人都无法获知你的发言身份</font></small><small><font color=\"#666666\">。在这里，你可以真正地畅所欲言。</font></small><br><br>" +
                    "<strong><font color=\"#00000000\">我们的初衷</font></strong><br><br>" +
                    " <small><font color=\"#666666\">敲下几行文字，1037树洞可以满足你任何的交流需求：</font></small><br><br>" +
                    " <small><font color=\"#00000000\">倾诉自己内心深处的伤感或喜悦，分享华科最新发生的大小趣事，寻找校内拥有小众爱好的朋友，寻求学长学姐们给自己的建议，交流对于热点社会问题的看法……</font></small><br><br>" +
                    " <small><font color=\"#666666\">树洞的本质是人和人之间的互相倾诉，只有人来人往，树洞才会好玩儿~在1037树洞，所有的声音都会被认真倾听，你的一切发言不用担心被熟人监视，而你的交流对象都是和你思维高度同频的HUSTer~</font></small><br><br>" +
                    "<strong><font color=\"#00000000\">我们的期望</font></strong><br><br>" +
                    " <small><font color=\"#666666\">为了让每一位洞友都能在1037树洞找到温暖，我们希望你：</font></small><br><br>" +
                    " <small><font color=\"#00000000\">&#160;&#160;●&#160;做一个友善的倾听者，尊重他人，即使TA与你观点相异；</font></small><br>" +
                    " <small><font color=\"#00000000\">&#160;&#160;●&#160;不要发布令人感到不适或者违反法律法规的内容，包括但不限于侮辱他人、侵犯隐私、发布暴力或色情内容等；</font></small><br>" +
                    " <small><font color=\"#00000000\">&#160;&#160;●&#160;在参与讨论时，请与我们一起维护社区的安全，对于社区内令人不适的内容主动制止。</font></small><br><br>" +
                    " <small><font color=\"#666666\">匿名社区的良好环境需要你我共同维护~感谢你的支持！</font></small><br><br>" +
                    "<small><font color=\"#00000000\">祝你在1037树洞玩得愉快。</font></small><br>");
            mEt_second_password.setText(Html.fromHtml(aa));

            //mEt_second_password.setMaxHeight(400);
            Button mBtn_ok = (Button) mView.findViewById(R.id.btn_dialoghomepage_sure);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            //AlertDialog dialog=mBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            mBtn_ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //关闭掉对话框,拿到对话框的对象
                    dialog.dismiss();
                }
            });

            dialog.show();
            SharedPreferences.Editor editor2 = getSharedPreferences("Depository", Context.MODE_PRIVATE).edit();//获取编辑器
            if(token.equals("")){
                editor2.putBoolean("iffirstlogin",true);
            }else{
                editor2.putBoolean("iffirstlogin",false);
            }
            editor2.commit();//提
        }
        /*if(token.equals("")){

        }else{

        }
*/

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        // register listener
        mViewPager.addOnPageChangeListener(mPageChangeListener);
       /* mTabRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);//radioButton部分，因为会自动跳转到最上层，源码不知道怎么改，已舍弃
        RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(0);
        radioButton.setChecked(true);*/
        ImageView imageView0=(ImageView)findViewById(R.id.fab_homescreen_publishhole);
        imageView0.bringToFront();
    }
  /*  private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override//用于将滑动界面与按钮同步，因为不要求滑动所以这个暂时没用了
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    mViewPager.setCurrentItem(i);
                    return;
                }
            }
        }
    };*/

    private BaseViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            //RadioButton radioButton = (RadioButton) mTabRadioGroup.getChildAt(position);
           // radioButton.setChecked(true);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mList;
        private FragmentManager mFragmentManager1;
        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.mList = list;
            mFragmentManager=fm;
        }



        @Override
        public Object instantiateItem( ViewGroup container, int position) {
            //return super.instantiateItem(container, position);
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if(fragment instanceof HomePageFragment){
                 mTags = fragment.getTag();
                Log.e("instantiateItem", mTags);
            }
            return fragment;

        }

        @Override
        public Fragment getItem(int position) {
             Log.d("position",position+"");


                 return this.mList == null ? null : this.mList.get(position);
        }

        @Override
        public int getCount() {
            return this.mList == null ? 0 : this.mList.size();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(mPageChangeListener);
    }

    public void onClick(View v) {
        if(a[1]==R.id.cl_homescreen_hompage&&v.getId()==R.id.cl_homescreen_hompage){
            HomePageFragment fragment = (HomePageFragment)mFragmentManager.findFragmentByTag(mTags);
            fragment.homeScreenUpdate();
           // Toast.makeText(HomeScreenActivity.this,"点击了两次",Toast.LENGTH_SHORT).show();
            // Log.d("点击了两次","点击了两次");
        }
        if(v.getId()!=R.id.fab_homescreen_publishhole){
            a[0]=a[1];
            a[1]=v.getId();
        }
        switch (v.getId()) {
            case R.id.fab_homescreen_publishhole://下一步设置密码

                break;
            case R.id.cl_homescreen_hompage:

                mViewPager.setCurrentItem(0,false);
                textView0.setText(R.string.homepage_1);
                setBottombarPhoto(a);
                break;
            case R.id.cl_homescreen_forest:
                mViewPager.setCurrentItem(1,false);
                textView0.setText(R.string.homepage_4);
                setBottombarPhoto(a);
                if(CheckingToken.IfTokenExist()){

                }else{
                    Intent intent=new Intent(HomeScreenActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.cl_homescreen_message:
                mViewPager.setCurrentItem(2,false);
                textView0.setText(R.string.homepage_5);
                setBottombarPhoto(a);
                if(CheckingToken.IfTokenExist()){

                }else{
                    Intent intent=new Intent(HomeScreenActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.cl_homescreen_mine:
                mViewPager.setCurrentItem(3,false);
                textView0.setText(R.string.homepage_6);
                setBottombarPhoto(a);
                if(CheckingToken.IfTokenExist()){

                }else{
                    Intent intent=new Intent(HomeScreenActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }
    public void setBottombarPhoto(int[] id){
        switch (id[0]) {
            case R.id.cl_homescreen_hompage:
                imageView1.setImageResource(R.mipmap.group267);
                textView1.setTextColor(getResources().getColor(R.color.GrayScale_50));
                break;
            case R.id.cl_homescreen_forest:
                imageView2.setImageResource(R.mipmap.group264);
                textView2.setTextColor(getResources().getColor(R.color.GrayScale_50));
                break;
            case R.id.cl_homescreen_message:
                imageView3.setImageResource(R.mipmap.group265);
                textView3.setTextColor(getResources().getColor(R.color.GrayScale_50));
                break;
            case R.id.cl_homescreen_mine:
                imageView4.setImageResource(R.mipmap.group266);
                textView4.setTextColor(getResources().getColor(R.color.GrayScale_50));
                break;
            default:
                break;
        }
        switch (id[1]){
            case R.id.cl_homescreen_hompage:
                imageView1.setImageResource(R.mipmap.group263);
                textView1.setTextColor(getResources().getColor(R.color.GrayScale_0));
                break;
            case R.id.cl_homescreen_forest:
                imageView2.setImageResource(R.mipmap.group268);
                textView2.setTextColor(getResources().getColor(R.color.GrayScale_0));
                break;
            case R.id.cl_homescreen_message:
                imageView3.setImageResource(R.mipmap.group274);
                textView3.setTextColor(getResources().getColor(R.color.GrayScale_0));
                break;
            case R.id.cl_homescreen_mine:
                imageView4.setImageResource(R.mipmap.group270);
                textView4.setTextColor(getResources().getColor(R.color.GrayScale_0));
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }
}
