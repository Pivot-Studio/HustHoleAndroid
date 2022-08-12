package cn.pivotstudio.moduleb.libbase.util.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author
 * @version :1.0
 * @classname :SharedPreferenceUtil
 * @description :存放小型数据,不需要，已由mmkv代替
 * @date :2022/4/26 13:05
 */
public class SharedPreferenceUtil {
    private static final String FILE_NAME = "SharedPreferenceUtil"; //文件名
    private static SharedPreferenceUtil mInstance;

    private SharedPreferenceUtil() {
    }

    public static SharedPreferenceUtil getInstance() {
        if (mInstance == null) {
            synchronized (SharedPreferenceUtil.class) {
                if (mInstance == null) {
                    mInstance = new SharedPreferenceUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 向sp中添加元素
     */
    public void put(Context context, String key, Object value) {
        //判断类型
        String type = value.getClass().getSimpleName();
        SharedPreferences sharedPreferences =
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (type) {
            case "Integer":
                editor.putInt(key, (Integer) value);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) value);
                break;
            case "Float":
                editor.putFloat(key, (Float) value);
                break;
            case "Long":
                editor.putLong(key, (Long) value);
                break;
            case "String":
                editor.putString(key, (String) value);
                break;
        }
        editor.apply();
    }

    /**
     * 获取sp中的元素
     *
     * @param defValue 代表数据类型
     */
    public Object get(Context context, String key, Object defValue) {
        SharedPreferences sharedPreferences =
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String type = defValue.getClass().getSimpleName();
        switch (type) {
            case "Integer":
                return sharedPreferences.getInt(key, (Integer) defValue);
            case "Boolean":
                return sharedPreferences.getBoolean(key, (Boolean) defValue);
            case "Float":
                return sharedPreferences.getFloat(key, (Float) defValue);
            case "Long":
                return sharedPreferences.getLong(key, (Long) defValue);
            case "String":
                return sharedPreferences.getString(key, (String) defValue);
        }
        return null;
    }
}
