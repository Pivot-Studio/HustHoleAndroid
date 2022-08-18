package cn.pivotstudio.moduleb.libbase.base.app

import cn.pivotstudio.moduleb.database.HustHoleRoomDatabase
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.util.emoji.EmotionUtil
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.pivotstudio.husthole.moduleb.network.*
import cn.pivotstudio.moduleb.libbase.BuildConfig
import cn.pivotstudio.moduleb.libbase.base.ui.activity.ActivityManager

/**
 * @classname:BaseApplication
 * @description:
 * @date:2022/4/28 19:55
 * @version:1.0
 * @author:
 */
class BaseApplication : Application() {

    init {
        NetworkApi.init(NetworkRequiredInfo(this))
        HustHoleApi.init(this)
    }

    val database: HustHoleRoomDatabase by lazy { HustHoleRoomDatabase.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        DB = database

        //创建本地数据库
        if (!BuildConfig.isRelease) {
            ARouter.openLog() // 打印日志
            ARouter.openDebug()
        }
        //组件化初始化
        ARouter.init(this)

        //表情包资源初始化
        EmotionUtil.init(this)
    }

    companion object {
        @JvmField
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        //数据库
        @Deprecated("Java遗留")
        @JvmStatic
        var DB: HustHoleRoomDatabase? = null

        @JvmStatic
        val activityManager: ActivityManager
            get() = ActivityManager.getInstance()
    }
}