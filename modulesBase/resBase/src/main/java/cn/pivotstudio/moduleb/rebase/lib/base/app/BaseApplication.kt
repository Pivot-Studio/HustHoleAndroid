package cn.pivotstudio.moduleb.rebase.lib.base.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import cn.pivotstudio.moduleb.rebase.database.HustHoleRoomDatabase
import cn.pivotstudio.moduleb.rebase.lib.util.emoji.EmotionUtil
import cn.pivotstudio.moduleb.rebase.network.HustHoleApi
import cn.pivotstudio.moduleb.rebase.network.NetworkApi
import cn.pivotstudio.moduleb.rebase.network.NetworkRequiredInfo
import com.alibaba.android.arouter.BuildConfig
import com.alibaba.android.arouter.launcher.ARouter

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
        if (BuildConfig.DEBUG) {
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
    }
}