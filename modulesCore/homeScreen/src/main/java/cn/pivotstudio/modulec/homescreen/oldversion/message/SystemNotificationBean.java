package cn.pivotstudio.modulec.homescreen.oldversion.message;

public class SystemNotificationBean {
    private String id;
    private String title;
    private String content;
    private String have_read;
    private String timestamp;

    public SystemNotificationBean() {
    }

    public SystemNotificationBean(String id,
                                  String title,
                                  String content,
                                  String have_read,
                                  String timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.have_read = have_read;
        this.title = title;
    }

    public void setSystemId(String n) {
        this.id = n;
    }

    public void setSystemContent(String n) {
        this.content = n;
    }

    public void setTitle(String n) {
        this.title = n;
    }

    public void setHave_read(String n) {
        this.have_read = n;
    }

    public void setTimestamp(String n) {
        this.timestamp = n;
    }

    public String getSystemId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHave_read() {
        return this.have_read;
    }

    public String getSystemContent() {
        return this.content;
    }

    public String getTimestamp() {
        return this.timestamp;
    }
}

