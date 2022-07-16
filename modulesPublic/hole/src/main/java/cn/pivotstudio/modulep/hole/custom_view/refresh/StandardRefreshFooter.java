package cn.pivotstudio.modulep.hole.custom_view.refresh;

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

import cn.pivotstudio.modulep.hole.R;


@SuppressLint("RestrictedApi")
public class StandardRefreshFooter extends LinearLayout implements RefreshFooter {
        private AVLoadingIndicatorView mLoadmore;
        //private ImageView mImage,mImage2,mImage3;
        private TextView text;
        private AnimationDrawable pullDownAnim;
        private AnimationDrawable refreshingAnim,refreshingAnim2,refreshingAnim3;
        private Boolean refreshCondition=false;
        private boolean hasSetPullDownAnim = false;

        public StandardRefreshFooter(Context context) {
            this(context, null, 0);
        }

        public StandardRefreshFooter(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public StandardRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            View view = View.inflate(context, R.layout.refresh_footer, this);
            mLoadmore=(AVLoadingIndicatorView)view.findViewById(R.id.AVLoadingIndicatorView);
            text=(TextView) view.findViewById(R.id.refreshfooter_text);
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
                case PullDownToRefresh:

                    break;
                case Refreshing: //正在刷新。只调用一次
                    mLoadmore.show();
                    refreshCondition=true;

                    break;
                case ReleaseToRefresh:


                    break;
            }
        }


        @Override
        public int onFinish(RefreshLayout layout, boolean success) {

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

