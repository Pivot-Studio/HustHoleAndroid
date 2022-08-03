package cn.pivotstudio.modulec.homescreen.oldversion.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pivotstudio.modulec.homescreen.R;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.wang.avi.AVLoadingIndicatorView;

public class MessageStandardRefreshFooter extends LinearLayout implements RefreshFooter {
    private final AVLoadingIndicatorView mLoadmore;
    //private ImageView mImage,mImage2,mImage3;
    private final TextView text;
    private AnimationDrawable pullDownAnim;
    private AnimationDrawable refreshingAnim, refreshingAnim2, refreshingAnim3;
    private Boolean refreshCondition = false;
    private boolean hasSetPullDownAnim = false;

    public MessageStandardRefreshFooter(Context context) {
        this(context, null, 0);
    }

    public MessageStandardRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageStandardRefreshFooter(Context context,
                                        @Nullable AttributeSet attrs,
                                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = View.inflate(context, R.layout.message_refresh_footer, this);
        mLoadmore = (AVLoadingIndicatorView) view.findViewById(R.id.AVLoadingIndicatorView);
        text = (TextView) view.findViewById(R.id.refreshfooter_text);
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
    public void onStateChanged(RefreshLayout refreshLayout,
                               RefreshState oldState,
                               RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh:
                //下拉刷新开始。正在下拉还没松手时调用
                //每次重新下拉时，将图片资源重置为小人的大脑袋
                // mImage.setImageResource(R.drawable.refresh_point);
                //mImage2.setImageResource(R.drawable.refresh_point);
                // mImage3.setImageResource(R.drawable.refresh_point);
                //Log.e("刷新：","PullDownToRefresh");
                break;
            case Refreshing: //正在刷新。只调用一次
                //状态切换为正在刷新状态时，设置图片资源为小人卖萌的动画并开始执行
                mLoadmore.show();
                //text.setText("jiazaizhong");
                refreshCondition = true;
                    /*
                    mImage.setImageResource(R.drawable.refresh_2);
                    mImage2.setImageResource(R.drawable.refresh_3);
                    mImage3.setImageResource(R.drawable.refresh_1);
                    refreshingAnim = (AnimationDrawable) mImage.getDrawable();
                    refreshingAnim.start();
                    refreshingAnim2 = (AnimationDrawable) mImage2.getDrawable();
                    refreshingAnim2.start();
                    refreshingAnim3 = (AnimationDrawable) mImage3.getDrawable();
                    refreshingAnim3.start();
                    Log.e("刷新：","Refreshing");

                     */
                break;
            case ReleaseToRefresh:

                Log.e("刷新：", "ReleaseToRefresh");
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
        if (refreshCondition == true) {
            mLoadmore.hide();
            refreshCondition = false;
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
    public void onMoving(boolean isDragging,
                         float percent,
                         int offset,
                         int height,
                         int maxDragHeight) {

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

