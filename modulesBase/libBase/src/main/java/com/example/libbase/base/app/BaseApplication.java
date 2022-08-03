package com.example.libbase.base.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import cn.pivotstudio.husthole.moduleb.network.NetworkApi;
import cn.pivotstudio.husthole.moduleb.network.NetworkRequiredInfo;
import cn.pivotstudio.moduleb.database.AppDatabase;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.BuildConfig;
import com.example.libbase.base.ui.activity.ActivityManager;
import com.example.libbase.util.emoji.EmotionUtil;

/**
 * @classname:BaseApplication
 * @description:
 * @date:2022/4/28 19:55
 * @version:1.0
 * @author:
 */

public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    //数据库
    public static AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //初始化网络框架
        NetworkApi.init(new NetworkRequiredInfo(this));
        //创建本地数据库
        db = AppDatabase.getInstance(this);

        if (!BuildConfig.isRelease) {
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();
        }
        //组件化初始化
        ARouter.init(this);

        //表情包资源初始化
        EmotionUtil.init(this);
    }

    public static Context getContext() {
        return context;
    }

    public static AppDatabase getDb() {
        return db;
    }

    public static ActivityManager getActivityManager() {
        return ActivityManager.getInstance();
    }
}
