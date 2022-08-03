package cn.pivotstudio.modulec.homescreen.oldversion.fragment;

public class MyReplyItem {
    public String alias;
    public String content;
    public String created_timestamp;
    public String hole_content;
    public int hole_id;
    public int local_reply_id;

    public MyReplyItem(String alias,
                       String content,
                       String created_timestamp,
                       String hole_content,
                       int hole_id,
                       int local_reply_id) {
        this.alias = alias;
        this.content = content;
        this.created_timestamp = created_timestamp;
        this.hole_content = hole_content;
        this.hole_id = hole_id;
        this.local_reply_id = local_reply_id;
    }
}

