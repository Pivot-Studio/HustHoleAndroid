package cn.pivotstudio.moduleb.libbase.util.emoji;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication;
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment;
import cn.pivotstudio.moduleb.libbase.constant.Constant;

/**
 * @classname: SpanStringUtils
 * @description: 解析【】内的表情
 * @date: 2022/5/16 16:13
 * @version:1.0
 * @author: lzt
 */
public class SpanStringUtil {
    /**
     * 供标准String解析
     */
    public static SpannableString getEmotionContent(int emotion_map_type,
                                                    final Context context,
                                                    final View tv,
                                                    String source) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();
        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利⽤表情名字获取到对应的图⽚
            Integer imgRes = EmotionUtil.getImgByName(emotion_map_type, key);
            if (imgRes != -1) {
                // 压缩表情图⽚
                int size = 0;
                if (tv instanceof EditText) {
                    size = (int) ((EditText) tv).getTextSize();
                } else if (tv instanceof TextView) {
                    size = (int) ((TextView) tv).getTextSize();
                }

                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    /**
     * 供markdown解析后的CharSequence使用
     */
    public static SpannableString getEmotionMarkdownContent(int emotion_map_type,
                                                            final Fragment fragment,
                                                            final View tv,
                                                            CharSequence source,
                                                            String currentId
    ) {
        SpannableString spannableString = new SpannableString(source);
        Resources res = fragment.getResources();
        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            String key = matcherEmotion.group();
            // 匹配字符串的开始位置
            int start = matcherEmotion.start();
            // 利⽤表情名字获取到对应的图⽚
            Integer imgRes = EmotionUtil.getImgByName(emotion_map_type, key);
            if (imgRes != -1) {
                // 压缩表情图⽚
                int size = 0;
                if (tv instanceof EditText) {
                    size = (int) ((EditText) tv).getTextSize() * 13 / 10;
                } else if (tv instanceof TextView) {
                    size = (int) ((TextView) tv).getTextSize() * 13 / 10;
                }

                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap =
                        Bitmap.createScaledBitmap(bitmap, (12 * size / 10), size, true);
                ImageSpan span = new ImageSpan(fragment.getContext(), scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        String regexHoleId = "#\\d+";
        Pattern patternHoleId = Pattern.compile(regexHoleId);
        Matcher matcherHoleId = patternHoleId.matcher(spannableString);
        while (matcherHoleId.find()) {
            String key = matcherHoleId.group();
            int start = matcherHoleId.start();
            //这里限制最长树洞号为8位，百万够用了吧。。
            int length = Math.min(key.length(), 8);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    String id = key.substring(1, length);
                    if (currentId.equals(id))
                        Toast.makeText(tv.getContext(), "你已经在这个树洞了", Toast.LENGTH_SHORT).show();
                    else
                        ARouter.getInstance()
                                .build("/hole/HoleActivity")
                                .withInt(
                                        Constant.HOLE_ID,
                                        Integer.parseInt(id)
                                )
                                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                                .navigation(fragment.requireActivity(), 2);
                }
            }, start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (tv instanceof TextView) {
                ((TextView) tv).setMovementMethod(LinkMovementMethod.getInstance());
            }

        }
        return spannableString;
    }

    /**
     * 解析树洞页跳转（参照上面的写法orz）
     */
    public static SpannableString setTvLoginPrivacyPolicySpecialText(
            final TextView tv,
            CharSequence content
    ) {
        SpannableString spannableString = new SpannableString(content);

        return spannableString;
        // 不需要点击的文字
//        tvLoginPrivacyPolicy.setText(R.string.Bylogging);
        // 设置需要点击的文字
//        SpannableString clickString1 = new SpannableString();
        // 设置需要点击文字的样式
//        clickString1.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                // 点击之后需要做的操作
////                forwardPrivacyPolicy();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                // 设置可点击文字的颜色
////                ds.setColor(getResources().getColor(R.color.clickText));
//            }
//            // 0-->clickString1.length()这个长度就是需要点击的文字长度
//        }, 0, clickString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将需要点击的文字添加到我们的TextView中
//        tvLoginPrivacyPolicy.append(clickString1);
        // 继续添加不需要点击的文字组装TextView
//        tvLoginPrivacyPolicy.append(new SpannableString(" " + getString(R.string.And)) + " ");
        // 添加需要点击的文字
//        SpannableString clickString2 = new SpannableString(getString(R.string.TermsAndConditions));
        // 设置需要点击文字的样式
//        clickString2.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
////                forwardPrivacyPolicy();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                //设置颜色
////                ds.setColor(getResources().getColor(R.color.clickText));
//            }
//            // 0-->clickString2.length()这个长度就是需要点击的文字长度
//        }, 0, clickString2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvLoginPrivacyPolicy.append(clickString2);
        // 设置点击文字点击效果为透明
//        tvLoginPrivacyPolicy.setHighlightColor(Color.TRANSPARENT);
        // 开始响应点击事件
//        tvLoginPrivacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

