package cn.pivotstudio.modulec.homescreen.oldversion.message;

public class NotificationBean {
    private String reply_local_id;
    private String type;
    private String hole_id;
    private String created_timestamp;
    private String content;
    private String alias;
    private String is_read;
    private String id;

    public NotificationBean() {
    }

    public NotificationBean(String reply_local_id,
                            String time,
                            String type,
                            String content,
                            String hole_id,
                            String alias,
                            String is_read,
                            String id) {
        this.content = content;
        this.created_timestamp = time;
        this.type = type;
        this.hole_id = hole_id;
        this.reply_local_id = reply_local_id;
        this.alias = alias;
        this.is_read = is_read;
        this.id = id;
    }

    public void setReplyLocalId(String n) {
        this.reply_local_id = n;
    }

    public void setType(String n) {
        this.type = n;
    }

    public void setHole_id(String n) {
        this.hole_id = n;
    }

    public void setTime(String n) {
        this.created_timestamp = n;
    }

    public void setReplyContent(String n) {
        this.content = n;
    }

    public void setAlias(String n) {
        this.alias = n;
    }

    public void setIs_read(String n) {
        this.is_read = n;
    }

    public void setId(String n) {
        this.id = n;
    }

    public String getReplyLocalId() {
        return this.reply_local_id;
    }

    public String getType() {
        return this.type;
    }

    public String getHole_id() {
        return this.hole_id;
    }

    public String getTime() {
        return this.created_timestamp;
    }

    public String getReplyContent() {
        return this.content;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getIs_read() {
        return this.is_read;
    }

    public String getId() {
        return this.id;
    }
}

