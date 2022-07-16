package cn.pivotstudio.modulep.hole.custom_view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @classname:EmojiRecyclerView
 * @description:
 * @date:2022/5/12 22:45
 * @version:1.0
 * @author:
 */
public class EmojiList extends ConstraintLayout {
    public EmojiList(@NonNull Context context) {
        super(context);
        initView();
    }
    public EmojiList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public EmojiList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){

    }
}
