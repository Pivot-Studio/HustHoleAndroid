package cn.pivotstudio.moduleb.libbase.base.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import cn.pivotstudio.moduleb.libbase.R;

/**
 * @classname:MaxHeightRecyclerView
 * @description:允许设置最大长度的recyclerview
 * @date:2022/5/3 16:47
 * @version:1.0
 * @author:
 */
public class MaxHeightRecyclerView extends RecyclerView {

    private int mHeight;

    public MaxHeightRecyclerView(@NonNull Context context) {
        super(context);
    }

    public MaxHeightRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightRecyclerView(@NonNull Context context,
                                 @Nullable AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setMaxHeight(int height) {
        mHeight = height;
        invalidate();
    }

    private void initAttr(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray appearance =
            context.obtainStyledAttributes(attrs, R.styleable.MaxHeightRecyclerView, defStyle, 0);
        mHeight = appearance.getDimensionPixelSize(R.styleable.MaxHeightRecyclerView_maxHeight, 0);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (mHeight > 0) {
            heightSpec = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
