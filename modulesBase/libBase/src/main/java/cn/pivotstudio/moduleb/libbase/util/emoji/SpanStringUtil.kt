package cn.pivotstudio.moduleb.libbase.util.emoji

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.regex.Pattern

/**
 * @classname: SpanStringUtils
 * @description: 解析【】内的表情
 * @date: 2022/5/16 16:13
 * @version:1.0
 * @author: lzt
 */
object SpanStringUtil {
    /**
     * 供标准String解析
     */
    @JvmStatic
    fun getEmotionContent(
        emotion_map_type: Int,
        context: Context,
        tv: View?,
        source: String?
    ): SpannableString {
        val spannableString = SpannableString(source)
        val res = context.resources
        val regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]"
        val patternEmotion = Pattern.compile(regexEmotion)
        val matcherEmotion = patternEmotion.matcher(spannableString)
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            val key = matcherEmotion.group()
            // 匹配字符串的开始位置
            val start = matcherEmotion.start()
            // 利⽤表情名字获取到对应的图⽚
            val imgRes = EmotionUtil.getImgByName(emotion_map_type, key)
            if (imgRes != -1) {
                // 压缩表情图⽚
                var size = 0
                if (tv is EditText) {
                    size = tv.textSize.toInt()
                } else if (tv is TextView) {
                    size = tv.textSize.toInt()
                }
                val bitmap = BitmapFactory.decodeResource(res, imgRes)
                val scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
                val span = ImageSpan(context, scaleBitmap)
                spannableString.setSpan(
                    span, start, start + key.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannableString
    }

    /**
     * 供markdown解析后的CharSequence使用
     */
    fun getEmotionMarkdownContent(
        emotion_map_type: Int,
        tv: View,
        source: CharSequence?
    ): SpannableString {
        val spannableString = SpannableString(source)
        val res = tv.resources
        val regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]"
        val patternEmotion = Pattern.compile(regexEmotion)
        val matcherEmotion = patternEmotion.matcher(spannableString)
        while (matcherEmotion.find()) {
            // 获取匹配到的具体字符
            val key = matcherEmotion.group()
            // 匹配字符串的开始位置
            val start = matcherEmotion.start()
            // 利⽤表情名字获取到对应的图⽚
            val imgRes = EmotionUtil.getImgByName(emotion_map_type, key)
            if (imgRes != -1) {
                // 压缩表情图⽚
                var size = 0
                if (tv is EditText) {
                    size = tv.textSize.toInt() * 13 / 10
                } else if (tv is TextView) {
                    size = tv.textSize.toInt() * 13 / 10
                }
                val bitmap = BitmapFactory.decodeResource(res, imgRes)
                val scaleBitmap = Bitmap.createScaledBitmap(bitmap, 12 * size / 10, size, true)
                val span = ImageSpan(tv.context, scaleBitmap)
                spannableString.setSpan(
                    span, start, start + key.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannableString
    }
}