package cn.pivotstudio.moduleb.database;

import android.content.Context;
import android.os.Parcelable;
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @classname: MMKVUtil
 * @description:
 * @date: 2022/4/28 19:40
 * @version:1.0
 * @author:
 */

public class MMKVUtil {

    private static MMKV mmkv;
    private static MMKVUtil mmkvUtil;

    public static MMKVUtil getMMKV(Context context) {
        if (mmkvUtil == null) {
            synchronized (MMKVUtil.class) {
                if (mmkvUtil == null) {
                    MMKV.initialize(context);
                    mmkv = MMKV.defaultMMKV();
                    mmkvUtil = new MMKVUtil();
                }
            }
        }
        return mmkvUtil;
    }

    /**
     * 写入基本数据类型缓存
     *
     * @param key 键
     * @param object 值
     */
    public void put(String key, Object object) {
        if (object instanceof String) {
            mmkv.encode(key, (String) object);
        } else if (object instanceof Integer) {
            mmkv.encode(key, (Integer) object);
        } else if (object instanceof Boolean) {
            mmkv.encode(key, (Boolean) object);
        } else if (object instanceof Float) {
            mmkv.encode(key, (Float) object);
        } else if (object instanceof Long) {
            mmkv.encode(key, (Long) object);
        } else if (object instanceof Double) {
            mmkv.encode(key, (Double) object);
        } else if (object instanceof byte[]) {
            mmkv.encode(key, (byte[]) object);
        } else {
            mmkv.encode(key, object.toString());
        }
    }

    public void putSet(String key, Set<String> sets) {
        mmkv.encode(key, sets);
    }

    public void putParcelable(String key, Parcelable obj) {
        mmkv.encode(key, obj);
    }

    public Integer getInt(String key) {
        return mmkv.decodeInt(key, 0);
    }

    public Integer getInt(String key, int defaultValue) {
        return mmkv.decodeInt(key, defaultValue);
    }

    public Double getDouble(String key) {
        return mmkv.decodeDouble(key, 0.00);
    }

    public Double getDouble(String key, double defaultValue) {
        return mmkv.decodeDouble(key, defaultValue);
    }

    public Long getLong(String key) {
        return mmkv.decodeLong(key, 0L);
    }

    public Long getLong(String key, long defaultValue) {
        return mmkv.decodeLong(key, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return mmkv.decodeBool(key, false);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }

    public Float getFloat(String key) {
        return mmkv.decodeFloat(key, 0F);
    }

    public Float getFloat(String key, float defaultValue) {
        return mmkv.decodeFloat(key, defaultValue);
    }

    public byte[] getBytes(String key) {
        return mmkv.decodeBytes(key);
    }

    public byte[] getBytes(String key, byte[] defaultValue) {

        return mmkv.decodeBytes(key, defaultValue);
    }

    public String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    /**
     * 存放array
     */
    public <T> void setArray(String name, List<T> list) {

        if (list == null || list.size() == 0) { //清空
            mmkv.putInt(name + "size", 0);
            int size = mmkv.getInt(name + "size", 0);
            for (int i = 0; i < size; i++) {
                if (mmkv.getString(name + i, null) != null) {
                    mmkv.remove(name + i);
                }
            }
        } else {
            mmkv.putInt(name + "size", list.size());
            if (list.size() > 20) {
                list.remove(0);   //只保留后20条记录
            }
            for (int i = 0; i < list.size(); i++) {
                mmkv.remove(name + i);
                mmkv.remove(new Gson().toJson(list.get(i)));//删除重复数据 先删后加
                mmkv.putString(name + i, new Gson().toJson(list.get(i)));
            }
        }
    }

    /**
     * 获取存取的array,主要为近期使用表情包服务
     */
    public <T> List<T> getArray(String name, T bean) {

        List<T> list = new LinkedList<T>();
        int size = mmkv.getInt(name + "size", 0);
        for (int i = 0; i < size; i++) {
            if (mmkv.getString(name + i, null) != null) {
                try {
                    list.add(
                        (T) new Gson().fromJson(mmkv.getString(name + i, null), bean.getClass()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public Set<String> getStringSet(String key) {
        return mmkv.decodeStringSet(key, Collections.emptySet());
    }

    public Parcelable getParcelable(String key) {
        return mmkv.decodeParcelable(key, null);
    }

    /**
     * 移除某个key对
     */
    public void removeKey(String key) {
        mmkv.removeValueForKey(key);
    }

    /**
     * 清除所有key
     */
    public void clearAll() {
        mmkv.clearAll();
    }
}

