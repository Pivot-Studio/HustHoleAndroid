package cn.pivotstudio.moduleb.libbase.base.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;
import android.view.View;
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication;
import cn.pivotstudio.moduleb.libbase.util.emoji.SpanStringUtil;
import cn.pivotstudio.moduleb.libbase.R;

/**
 * @classname:EmojiEdittext
 * @description:支持表情包的输入框
 * @date:2022/5/3 16:47
 * @version:1.0
 * @author:
 */
public class EmojiEdittext extends androidx.appcompat.widget.AppCompatEditText {
    private View view = null;
    private int mEmojiconSize;
    private int mEmojiconAlignment;
    private int mEmojiconTextSize;
    private boolean mUseSystemDefault = false;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (view != null) {
                view.setEnabled(!editable.toString().isEmpty());//输入完不为空内容才可以点击
            }
            int start = getSelectionStart();
            int end = getSelectionEnd();
            removeTextChangedListener(this);//取消监听

            SpannableString spannableString =
                SpanStringUtil.getEmotionContent(0x0001, BaseApplication.context, EmojiEdittext.this,
                    editable.toString());
            setText(spannableString);

            setSelection(start, end);
            addTextChangedListener(this);
        }
    };

    public EmojiEdittext(Context context) {
        super(context);
        initView(null);
    }

    public EmojiEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public EmojiEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        mEmojiconSize = (int) getTextSize();
        mEmojiconTextSize = (int) getTextSize();
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiSize, getTextSize());
            mEmojiconAlignment =
                a.getInt(R.styleable.Emojicon_emojiAlignment, DynamicDrawableSpan.ALIGN_BASELINE);
            mUseSystemDefault = a.getBoolean(R.styleable.Emojicon_emojiUseSystemDefault, false);
            a.recycle();
        }
        setText(getText());
        addTextChangedListener(textWatcher);
    }

    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }

    public void setUseSystemDefault(boolean useSystemDefault) {
        mUseSystemDefault = useSystemDefault;
    }

    /**
     * 未输入的时候让其他控件不能点击
     */
    public void isEnable(View view) {
        this.view = view;
    }
}
