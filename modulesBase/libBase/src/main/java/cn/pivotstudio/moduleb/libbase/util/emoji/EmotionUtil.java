package cn.pivotstudio.moduleb.libbase.util.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname:EmojiUtils
 * @description:
 * @date:2022/5/16 16:11
 * @version:1.0
 * @author:
 */
public class EmotionUtil {

    public static final int EMOTION_CLASSIC_TYPE = 0x0001;//经典表情

    public static ArrayMap<String, Integer> EMPTY_MAP;
    public static ArrayMap<String, Integer> EMOTION_CLASSIC_MAP;

    static {
        EMPTY_MAP = new ArrayMap<>();
        EMOTION_CLASSIC_MAP = new ArrayMap<>();
        EMOTION_CLASSIC_MAP.put("[微笑]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji1);
        EMOTION_CLASSIC_MAP.put("[委屈]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji2);
        EMOTION_CLASSIC_MAP.put("[星星眼]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji3);
        EMOTION_CLASSIC_MAP.put("[发呆]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji4);
        EMOTION_CLASSIC_MAP.put("[酷]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji5);
        EMOTION_CLASSIC_MAP.put("[大哭]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji6);
        EMOTION_CLASSIC_MAP.put("[害羞]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji7);
        EMOTION_CLASSIC_MAP.put("[困]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji8);
        EMOTION_CLASSIC_MAP.put("[抽泣]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji9);
        EMOTION_CLASSIC_MAP.put("[生气]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji10);
        EMOTION_CLASSIC_MAP.put("[耶]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji11);
        EMOTION_CLASSIC_MAP.put("[惊讶]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji12);
        EMOTION_CLASSIC_MAP.put("[尴尬]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji13);
        EMOTION_CLASSIC_MAP.put("[抓狂]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji14);
        EMOTION_CLASSIC_MAP.put("[呕吐]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji15);
        EMOTION_CLASSIC_MAP.put("[白眼]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji16);
        EMOTION_CLASSIC_MAP.put("[大笑]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji17);
        EMOTION_CLASSIC_MAP.put("[疑问]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji18);
        EMOTION_CLASSIC_MAP.put("[晕]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji19);
        EMOTION_CLASSIC_MAP.put("[叹气]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji20);
        EMOTION_CLASSIC_MAP.put("[吃瓜]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji21);
        EMOTION_CLASSIC_MAP.put("[再见]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji22);
        EMOTION_CLASSIC_MAP.put("[喜欢]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji23);
        EMOTION_CLASSIC_MAP.put("[奸笑]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji24);
        EMOTION_CLASSIC_MAP.put("[打call]",
            cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji25);
        EMOTION_CLASSIC_MAP.put("[偷看]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji26);
        EMOTION_CLASSIC_MAP.put("[奋斗]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji27);
        EMOTION_CLASSIC_MAP.put("[思考]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji28);
        EMOTION_CLASSIC_MAP.put("[ok]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji29);
        EMOTION_CLASSIC_MAP.put("[酸]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji30);
        EMOTION_CLASSIC_MAP.put("[哼]", cn.pivotstudio.moduleb.resbase.R.mipmap.resource_emoji31);
    }

    public static Context gContext;
    private static List<String> emojiNameList = new ArrayList<>();
    private static List<Integer> emojiResourceList = new ArrayList<>();//资源文件

    /**
     * 初始化
     */
    public static void init(Context context) {
        gContext = context.getApplicationContext();
        Resources resources = gContext.getResources();
        String[] codes =
            resources.getStringArray(cn.pivotstudio.moduleb.resbase.R.array.emoji_code_list);
        TypedArray array =
            resources.obtainTypedArray(cn.pivotstudio.moduleb.resbase.R.array.emoji_res_list);
        if (codes.length != array.length()) {//数据长度必须一致
            array.recycle();
            throw new IndexOutOfBoundsException("Code and resource are not match in Emoji xml.");
        }
        for (int i = 0; i < codes.length; i++) {
            emojiNameList.add(codes[i]);
            emojiResourceList.add(array.getResourceId(i, -1));
        }
        array.recycle();
    }

    public static List<Integer> getResourceList() {//得到每页的数据
        return emojiResourceList;
    }

    public static List<String> getResourceName() {//得到每页的数据
        return emojiNameList;
    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName 名称
     */
    public static int getImgByName(int EmotionType, String imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:

                integer = EMOTION_CLASSIC_MAP.get(imgName);
                break;
            default:
                //LogUtils.e("the emojiMap is null!!");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     */
    public static ArrayMap<String, Integer> getEmojiMap(int EmotionType) {
        ArrayMap EmojiMap = null;
        switch (EmotionType) {
            case EMOTION_CLASSIC_TYPE:
                EmojiMap = EMOTION_CLASSIC_MAP;
                break;
            default:
                EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }
}

