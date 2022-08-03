package cn.pivotstudio.modulec.homescreen.oldversion.model;

import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.widget.Button;
import android.widget.EditText;
import cn.pivotstudio.modulec.homescreen.R;

public class EditTextReaction {
    public static void ButtonReaction(EditText editText1,
                                      Button button1) {//EditText输入内容后让按钮颜色变化并且可点击
        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText1.getText().toString().trim().length() != 0) {
                    button1.setBackgroundResource(R.drawable.button);
                    button1.setEnabled(true);
                } else {
                    button1.setBackgroundResource(R.drawable.standard_button_gray);
                    button1.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (editText1.getText().toString().trim().length() != 0) {
                    button1.setBackgroundResource(R.drawable.button);
                    button1.setEnabled(true);
                } else {
                    button1.setBackgroundResource(R.drawable.standard_button_gray);
                    button1.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText1.getText().toString().trim().length() != 0) {
                    button1.setBackgroundResource(R.drawable.button);
                    button1.setEnabled(true);
                } else {
                    button1.setBackgroundResource(R.drawable.standard_button_gray);
                    button1.setEnabled(false);
                }
            }
        });
    }

    public static void EditTextSize(EditText editText1,
                                    SpannableString string1,
                                    int textsize) {//设置EditText中的hint文字大小
        AbsoluteSizeSpan size = new AbsoluteSizeSpan(textsize, true);
        string1.setSpan(size, 0, string1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText1.setHint(new SpannedString(string1));
    }
}
