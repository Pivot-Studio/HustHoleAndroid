package cn.pivotstudio.moduleb.libbase.util.data;

import java.io.UnsupportedEncodingException;

/**
 * @classname:GetUrlUtil
 * @description:上传数据时转换类型
 * @date:2022/5/6 15:46
 * @version:1.0
 * @author:
 */
public class GetUrlUtil {
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
