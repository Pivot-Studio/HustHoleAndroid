package cn.pivotstudio.modulec.homescreen.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import cn.pivotstudio.modulec.homescreen.R;

/**
 * @classname:HomePageOptionBox
 * @description:选项栏
 * @date:2022/5/5 2:19
 * @version:1.0
 * @author:
 */
public class HomePageOptionBox extends ConstraintLayout {
    private OptionsListener mOptionsListener;
    private ImageView mTriangleIv;
    private PopupWindow mSlideBoxPpw, mDarkScreenPpw;
    private Button mNewPublishBtn, mNewCommentBtn;
    private Boolean mFlag = false;

    public HomePageOptionBox(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public HomePageOptionBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HomePageOptionBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public HomePageOptionBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    /**
     * 额外监听器
     *
     * @param mOptionsListener
     */
    public void setOptionsListener(OptionsListener mOptionsListener) {
        this.mOptionsListener = mOptionsListener;
    }

    /**
     * 初始化view
     *
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.constraintlayout_homepageoptionbox, this, true);
        mTriangleIv = (ImageView) findViewById(R.id.iv_homepage_triangle);
        mTriangleIv.setImageResource(R.mipmap.triangle);
        ConstraintLayout mForestSquareCl = findViewById(R.id.cl_homepage_forestsquare);
        mForestSquareCl.setOnClickListener(this::onClick);
        View contentView = LayoutInflater.from(context).inflate(R.layout.ppw_homepage, null);
        View darkScreen = LayoutInflater.from(context).inflate(R.layout.ppw_homepagedarkscreen, null);
        mSlideBoxPpw = new PopupWindow(contentView);
        mDarkScreenPpw = new PopupWindow(darkScreen);
        mNewPublishBtn = (Button) contentView.findViewById(R.id.btn_ppwhomepage_newpublish);
        mNewCommentBtn = (Button) contentView.findViewById(R.id.btn_ppwhomepage_newcomment);
        mNewPublishBtn.setOnClickListener(this::onClick);
        mNewCommentBtn.setOnClickListener(this::onClick);
        darkScreen.setOnClickListener(this::onClick);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    private void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cl_homepage_forestsquare) {
            if (!mFlag) {
                beginTriangleAnim();
            } else {
                EndTriangleAnim();
            }
            mFlag = !mFlag;
        } else if (id == R.id.btn_ppwhomepage_newpublish) {
            mNewPublishBtn.setBackgroundResource(R.drawable.standard_button);
            mNewPublishBtn.setTextAppearance(v.getContext(), R.style.popupwindow_button_click);
            mNewCommentBtn.setBackgroundResource(R.drawable.standard_button_g95);
            mNewCommentBtn.setTextAppearance(v.getContext(), R.style.popupwindow_button);
            EndTriangleAnim();
            mFlag = !mFlag;
            mOptionsListener.onClick(v);
        } else if (id == R.id.btn_ppwhomepage_newcomment) {
            mNewCommentBtn.setBackgroundResource(R.drawable.standard_button);
            mNewCommentBtn.setTextAppearance(v.getContext(), R.style.popupwindow_button_click);
            mNewPublishBtn.setBackgroundResource(R.drawable.standard_button_g95);
            mNewPublishBtn.setTextAppearance(v.getContext(), R.style.popupwindow_button);
            EndTriangleAnim();
            mFlag = !mFlag;
            mOptionsListener.onClick(v);
        } else if (id == R.id.ppw_homepagedarkscreen) {
            EndTriangleAnim();
            mFlag = !mFlag;
            mSlideBoxPpw.dismiss();
            mDarkScreenPpw.dismiss();
        }
    }

    /**
     * 启动树洞广场后的动画
     */
    private void beginTriangleAnim() {
        RotateAnimation rotate;
        rotate = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mTriangleIv.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTriangleIv.setImageResource(R.mipmap.triangle);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mDarkScreenPpw.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mDarkScreenPpw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mDarkScreenPpw.setAnimationStyle(R.style.darkScreenAnim);
        mDarkScreenPpw.showAsDropDown(this);

        mSlideBoxPpw.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSlideBoxPpw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSlideBoxPpw.setAnimationStyle(R.style.contextMenuAnim);
        mSlideBoxPpw.showAsDropDown(this);

    }

    /**
     * 关闭树洞广场选项的动画
     */
    private void EndTriangleAnim() {
        RotateAnimation rotate;
        rotate = new RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mTriangleIv.startAnimation(rotate);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTriangleIv.setImageResource(R.mipmap.triangle);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSlideBoxPpw.dismiss();
        mDarkScreenPpw.dismiss();
    }
}
