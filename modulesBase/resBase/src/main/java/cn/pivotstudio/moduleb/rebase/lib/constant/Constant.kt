package cn.pivotstudio.moduleb.rebase.lib.constant

/**
 * @classname:Constant
 * @description:字符串
 * @date:2022/4/28 19:45
 * @version:1.0
 * @author:
 */
object Constant {
    /**
     * MMKV
     */
    //是否登录
    const val IS_LOGIN = "isLogin"

    //登录获得的token
    const val USER_TOKEN = "USER_TOKEN"
    const val USER_TOKEN_V2 = "USER_TOKEN_V2"

    //是否第一次使用app
    const val IS_FIRST_USED = "isFirstUseD"

    //发布数洞的内容
    const val HOLE_TEXT = "HOLE_TEXT"

    //近期使用的表情list
    const val UsedEmoji = "USED_EMOJI"

    /**
     * ARouter
     */
    //树洞号的标识
    const val HOLE_ID = "HOLE_ID"

    // 小树林id key
    const val FOREST_ID = "FOREST_ID"
    const val FOREST_NAME = "FOREST_NAME"

    // 是否从小树林跳转 key
    const val FROM_DETAIL_FOREST = "FROM_DETAIL_FOREST"

    //选择号的标识
    const val REPLY_ID = "reply_local_id"

    //是否需要打开键盘的标识
    const val IF_OPEN_KEYBOARD = "IF_OPEN_KEYBOARD"

    //内容的拥有者的标识
    const val ALIAS = "alias"

    /**
     * onActivityResult
     */
    //从树洞界面返回数据的标识
    const val HOLE_RETURN_INFO = "holeReturnInfo"
    const val HOLE_LIKED = "HOLE_LIKED"
    const val HOLE_LIKE_COUNT = "HOLE_LIKE_COUNT"
    const val HOLE_REPLIED = "HOLE_REPLIED"
    const val HOLE_REPLY_COUNT = "HOLE_REPLY_COUNT"
    const val HOLE_FOLLOWED = "HOLE_FOLLOWED"
    const val HOLE_FOLLOW_COUNT = "HOLE_FOLLOW_COUNT"

    /**
     * new Activity
     */
    //邮箱头，及学号
    const val EMAIL_HEAD = "email_head"

    //验证码内容
    const val VERIFY_CODE = "verify_code"
    const val EMAIL_SUFFIX = "@hust.edu.cn"

    /**
     * Base
     */
    //网络请求url头
    const val BASE_URL = "https://husthole.pivotstudio.cn/api/"

    //每次加载的列表长度
    const val CONSTANT_STANDARD_LOAD_SIZE = 20

    //数据库
    const val ROOM_DATABASE_NAME = "hust_hole"

    //深色模式
    const val IS_DARK_MODE = "is_dark_mode"
}