package cn.pivotstudio.modulec.homescreen.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;

import cn.pivotstudio.modulec.homescreen.R;

//import cn.pivotstudio.modulec.homescreen.R;


/**
 * @classname:BottomNavigationBar
 * @description:底部选项栏
 * @date:2022/5/2 19:43
 * @version:1.0
 * @author:
 */
public class BottomNavigationBar extends LinearLayout {
    private OptionsListener mOptionsListener;
    ConstraintLayout mHomepageCl,mForestCl,mMessageCl,mMineCl;
    private ImageView mHomepageIv,mForestIv,mMessageIv,mMineIv;
    private TextView mHomepageTv,mForestTv,mMessageTv,mMineTv;
    private int[] itemId;

    /**
     * 额外监听器
     * @param mOptionsListener
     */
    public void setOptionsListener(OptionsListener mOptionsListener) {
        this.mOptionsListener = mOptionsListener;
    }

    public BottomNavigationBar(Context context) {
        super(context);
        initView(context);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BottomNavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }



    private void initView(Context context){
        itemId=new int[2];
        LayoutInflater.from(context).inflate(R.layout.linearlayout_bottomnavigationbar, this,true);

        mHomepageCl=findViewById(R.id.cl_homescreen_hompage);
        mForestCl=findViewById(R.id.cl_homescreen_forest);
        mMessageCl=findViewById(R.id.cl_homescreen_message);
        mMineCl=findViewById(R.id.cl_homescreen_mine);
        mHomepageCl.setOnClickListener(this::onClick);
        mForestCl.setOnClickListener(this::onClick);
        mMessageCl.setOnClickListener(this::onClick);
        mMineCl.setOnClickListener(this::onClick);

        mHomepageIv=findViewById(R.id.iv_homescreen_hompageicon);
        mForestIv=findViewById(R.id.iv_homescreen_foresticon);
        mMessageIv=findViewById(R.id.iv_homescreen_messageicon);
        mMineIv=findViewById(R.id.iv_homescreen_mineicon);

        mHomepageTv=findViewById(R.id.tv_homescreen_homepagename);
        mForestTv=findViewById(R.id.tv_homescreen_forestname);
        mMessageTv=findViewById(R.id.tv_homescreen_messagename);
        mMineTv=findViewById(R.id.tv_homescreen_minename);


        itemId[0]=R.id.cl_homescreen_hompage;
        itemId[1]=R.id.cl_homescreen_hompage;


    }

    /**
     * 点击事件
     * @param v
     */
    private void onClick(View v){
        setBottomBarPhoto(itemId,v);
        mOptionsListener.onClick(v);
    }

    /**
     * 图片文字变化
     * @param id 分别记录上次点击图标id和本次点击图标id
     * @param v
     */
    private void setBottomBarPhoto(int[] id,View v){
        itemId[0]=itemId[1];
        itemId[1]=v.getId();
        if (id[0] == R.id.cl_homescreen_hompage) {
            mHomepageIv.setImageResource(R.mipmap.group267);
            mHomepageTv.setTextColor(getResources().getColor(R.color.GrayScale_50));
        } else if (id[0] == R.id.cl_homescreen_forest) {
            mForestIv.setImageResource(R.mipmap.group264);
            mForestTv.setTextColor(getResources().getColor(R.color.GrayScale_50));
        } else if (id[0] == R.id.cl_homescreen_message) {
            mMessageIv.setImageResource(R.mipmap.group265);
            mMessageTv.setTextColor(getResources().getColor(R.color.GrayScale_50));
        } else if (id[0] == R.id.cl_homescreen_mine) {
            mMineIv.setImageResource(R.mipmap.group266);
            mMineTv.setTextColor(getResources().getColor(R.color.GrayScale_50));
        }
        if (id[1] == R.id.cl_homescreen_hompage) {
            mHomepageIv.setImageResource(R.mipmap.group263);
            mHomepageTv.setTextColor(getResources().getColor(R.color.GrayScale_0));
        } else if (id[1] == R.id.cl_homescreen_forest) {
            mForestIv.setImageResource(R.mipmap.group268);
            mForestTv.setTextColor(getResources().getColor(R.color.GrayScale_0));
        } else if (id[1] == R.id.cl_homescreen_message) {
            mMessageIv.setImageResource(R.mipmap.group274);
            mMessageTv.setTextColor(getResources().getColor(R.color.GrayScale_0));
        } else if (id[1] == R.id.cl_homescreen_mine) {
            mMineIv.setImageResource(R.mipmap.group270);
            mMineTv.setTextColor(getResources().getColor(R.color.GrayScale_0));
        }
    }
}
