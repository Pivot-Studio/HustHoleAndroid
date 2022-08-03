package cn.pivotstudio.modulec.homescreen.oldversion.model;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

public class BaseViewPager extends ViewPager {//用来禁掉滑动
    private final boolean scrollable = true;

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    //2.禁掉viewpager左右滑动事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return false;
    }
}
