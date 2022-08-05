package cn.pivotstudio.moduleb.libbase.util.ui;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.widget.Button;
import android.widget.EditText;
import cn.pivotstudio.moduleb.libbase.R;

/**
 * @author
 * @version:1.0
 * @classname:EditTextUtil
 * @description:editText工具类
 * @date :2022/4/26 14:36
 */
public class EditTextUtil {
    /**
     * 为edittext设置监听器，未输入内容时按钮为灰，输入后按钮为绿
     */
    public static void ButtonReaction(EditText editText, Button button) {//EditText输入内容后让按钮颜色变化并且可点击
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().trim().length() != 0) {
                    button.setBackgroundResource(R.drawable.button);
                    button.setEnabled(true);
                } else {
                    button.setBackgroundResource(R.drawable.standard_button_gray);
                    button.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (editText.getText().toString().trim().length() != 0) {
                    button.setBackgroundResource(R.drawable.button);
                    button.setEnabled(true);
                } else {
                    button.setBackgroundResource(R.drawable.standard_button_gray);
                    button.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.getText().toString().trim().length() != 0) {
                    button.setBackgroundResource(R.drawable.button);
                    button.setEnabled(true);
                } else {
                    button.setBackgroundResource(R.drawable.standard_button_gray);
                    button.setEnabled(false);
                }
            }
        });
    }

    /**
     * 设置editText中hint的内容和大小
     *
     * @param string hint内容
     * @param textSize hint大小
     */
    public static void EditTextSize(EditText editText,
                                    SpannableString string,
                                    int textSize) {//设置EditText中的hint文字大小
        AbsoluteSizeSpan size = new AbsoluteSizeSpan(textSize, true);
        string.setSpan(size, 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannedString(string));
    }
}
