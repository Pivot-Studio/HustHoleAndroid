package cn.pivotstudio.modulec.homescreen.network;

/**
 * @classname:VersionResponse
 * @description:版本数据类
 * @date:2022/5/3 0:47
 * @version:1.0
 * @author:
 */
public class VersionResponse {
    String Androidversion;
    String AndroidUpdateUrl;

    public String getAndroidversion() {
        return Androidversion;
    }

    public void setAndroidversion(String androidversion) {
        Androidversion = androidversion;
    }

    public String getAndroidUpdateUrl() {
        return AndroidUpdateUrl;
    }

    public void setAndroidUpdateUrl(String androidUpdateUrl) {
        AndroidUpdateUrl = androidUpdateUrl;
    }
}
