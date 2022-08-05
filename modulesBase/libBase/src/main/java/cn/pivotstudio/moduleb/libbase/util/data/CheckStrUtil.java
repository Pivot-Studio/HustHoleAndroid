package cn.pivotstudio.moduleb.libbase.util.data;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @classname:CheckStrUtil
 * @description:检查是否是纯数字，供搜索使用
 * @date:2022/5/5 14:23
 * @version:1.0
 * @author:
 */
public class CheckStrUtil {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");

    public static boolean checkStrIsNum(String str) {
        String bigStr;
        try {
            /** 先将str转成BigDecimal，然后在转成String */
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            /** 如果转换数字失败，说明该str并非全部为数字 */
            return false;
        }
        Matcher isNum = NUMBER_PATTERN.matcher(str);
        return isNum.matches();
    }
}
