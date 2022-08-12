package cn.pivotstudio.moduleb.libbase.util.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @classname:SpanStringUtils
 * @description:解析【】内的表情
 * @date:2022/5/16 16:13
 * @version:1.0
 * @author:
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
                                                            final Context context,
                                                            final View tv,
                                                            CharSequence source) {
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
                    size = (int) ((EditText) tv).getTextSize() * 13 / 10;
                } else if (tv instanceof TextView) {
                    size = (int) ((TextView) tv).getTextSize() * 13 / 10;
                }

                Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes);
                Bitmap scaleBitmap =
                    Bitmap.createScaledBitmap(bitmap, (12 * size / 10), size, true);
                ImageSpan span = new ImageSpan(context, scaleBitmap);
                spannableString.setSpan(span, start, start + key.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}

