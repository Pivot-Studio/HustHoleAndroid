package cn.pivotstudio.modulep.publishhole.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * @classname: ForestExpandableListView
 * @description: 自定义Elv
 * @date: 2022/5/6 21:53
 * @version:1.0
 * @author:
 */
public class ForestExpandableListView extends ExpandableListView {

    public ForestExpandableListView(Context context) {
        super(context);
    }

    public ForestExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForestExpandableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ForestExpandableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
