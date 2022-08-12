package cn.pivotstudio.moduleb.libbase.base.ui.activity;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

/**
 * @classname:ActivityManager
 * @description:
 * @date:2022/4/28 20:24
 * @version:1.0
 * @author:
 */
public class ActivityManager {
    public static ActivityManager mInstance;
    /**
     * 保存所有创建的Activity
     */
    private final List<Activity> activityList = new ArrayList<>();

    public static ActivityManager getInstance() {
        if (mInstance == null) {
            synchronized (ActivityManager.class) {
                if (mInstance == null) {
                    mInstance = new ActivityManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            activityList.add(activity);
        }
    }

    /**
     * 移除Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
        }
    }

    /**
     * 关闭所有Activity
     */
    public void finishAllActivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
