package cn.pivotstudio.modulec.homescreen.ui.custom_view.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.wang.avi.AVLoadingIndicatorView;

import cn.pivotstudio.modulec.homescreen.R;


@SuppressLint("RestrictedApi")
public class HomePageRefreshFooter extends LinearLayout implements RefreshFooter {
    private AVLoadingIndicatorView mLoadmore;
    private TextView text;
    private AnimationDrawable pullDownAnim;
    private AnimationDrawable refreshingAnim,refreshingAnim2,refreshingAnim3;
    private Boolean refreshCondition=false;
    private boolean hasSetPullDownAnim = false;

    public HomePageRefreshFooter(Context context) {
        this(context, null, 0);
    }

    public HomePageRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomePageRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.refresh_footer_homepage, this);
        mLoadmore=(AVLoadingIndicatorView)view.findViewById(R.id.AVLoadingIndicatorView);
        text=(TextView) view.findViewById(R.id.refreshfooter_text);
            /*
            mImage = (ImageView) view.findViewById(R.id.refresh_header);
            mImage2 = (ImageView) view.findViewById(R.id.refresh_header_2);
            mImage3 = (ImageView) view.findViewById(R.id.refresh_header_3);

            Log.e("刷新：","刷心前");
             */
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {

    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh, ReleaseToRefresh:
                break;
            case Refreshing:
                mLoadmore.show();
                refreshCondition=true;

                break;
        }
    }


    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        // 结束动画
            /*if (pullDownAnim != null && pullDownAnim.isRunning()) {
                pullDownAnim.stop();
                Log.e("刷新："," pullDownAnim.stop();");

            }
            if (refreshingAnim != null && refreshingAnim.isRunning()) {
                refreshingAnim.stop();
                refreshingAnim2.stop();
                refreshingAnim3.stop();
                Log.e("刷新："," refreshingAnim n.stop();");
            }
            //重置状态
            hasSetPullDownAnim = false;

             */
        if(refreshCondition==true) {
            mLoadmore.hide();
            refreshCondition=false;
        }
        hasSetPullDownAnim = false;
        return 0;
    }

    /*@Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onRefreshReleased(RefreshLayout layout, int headerHeight, int extendHeight) {

    }*/
    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

// 下拉的百分比小于100%时，不断调用 setScale 方法改变图片大小
        // mImage.setScaleX(1);
        // mImage.setScaleY(1);

        if (percent < 1) {

            if (hasSetPullDownAnim) {
                hasSetPullDownAnim = false;
            }
        }


        if (percent >= 1.0) {
            //因为这个方法是不停调用的，防止重复
            if (!hasSetPullDownAnim) {
                mLoadmore.show();
                hasSetPullDownAnim = true;


            }
        }




    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {




    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }
}



