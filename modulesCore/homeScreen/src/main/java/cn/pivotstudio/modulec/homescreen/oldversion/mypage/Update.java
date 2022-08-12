package cn.pivotstudio.modulec.homescreen.oldversion.mypage;

public class Update {
    private final String version;
    private final String date;
    private final String detail;

    public Update(String version, String date, String detail) {
        this.version = version;
        this.date = date;
        this.detail = detail;
    }

    public String getVersion() {
        return version;
    }

    public String getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }
}
