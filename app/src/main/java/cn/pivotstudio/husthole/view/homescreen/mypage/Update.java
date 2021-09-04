package cn.pivotstudio.husthole.view.homescreen.mypage;

public class Update {
    private String version;
    private String date;
    private String detail;

    public Update(String version, String date, String detail){
        this.version = version;
        this.date = date;
        this.detail = detail;
    }

    public String getVersion(){return version;}

    public String getDate(){return date;}

    public String getDetail(){return detail;}

}
