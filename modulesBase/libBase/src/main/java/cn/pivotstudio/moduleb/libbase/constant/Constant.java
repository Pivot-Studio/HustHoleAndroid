package cn.pivotstudio.moduleb.libbase.constant;

/**
 * @classname:Constant
 * @description:字符串
 * @date:2022/4/28 19:45
 * @version:1.0
 * @author:
 */
public class Constant {
    /**
     * MMKV
     */
    //是否登录
    public static final String IS_LOGIN = "isLogin";
    //登录获得的token
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String USER_TOKEN_V2 = "USER_TOKEN_V2";
    //是否第一次使用app
    public static final String IS_FIRST_USED = "isFirstUseD";
    //发布数洞的内容
    public static final String HOLE_TEXT = "HOLE_TEXT";
    //近期使用的表情list
    public static final String UsedEmoji = "USED_EMOJI";
    /**
     * ARouter
     */
    //树洞号的标识
    public static final String HOLE_ID = "HOLE_ID";
    // 小树林id key
    public static final String FOREST_ID = "FOREST_ID";
    public static final String FOREST_NAME = "FOREST_NAME";
    // 是否从小树林跳转 key
    public static final String FROM_DETAIL_FOREST = "FROM_DETAIL_FOREST";
    //选择号的标识
    public static final String REPLY_LOCAL_ID = "reply_local_id";
    //是否需要打开键盘的标识
    public static final String IF_OPEN_KEYBOARD = "IF_OPEN_KEYBOARD";
    //内容的拥有者的标识
    public static final String ALIAS = "alias";

    /**
     * onActivityResult
     */
    //从树洞界面返回数据的标识
    public static final String HOLE_RETURN_INFO = "holeReturnInfo";
    /**
     * new Activity
     */
    //邮箱头，及学号
    public static final String EMAIL_HEAD = "email_head";
    //验证码内容
    public static final String VERIFY_CODE = "verify_code";

    public static final String EMAIL_SUFFIX = "@hust.edu.cn";
    /**
     * Base
     */
    //网络请求url头
    public static final String BASE_URL = "https://husthole.pivotstudio.cn/api/";
    //每次加载的列表长度
    public static final Integer CONSTANT_STANDARD_LOAD_SIZE = 20;
    //数据库
    public static final String ROOM_DATABASE_NAME = "hust_hole";
}
