package cn.pivotstudio.moduleb.database.bean;

import androidx.databinding.BaseObservable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @classname: Hole
 * @description: 存放树洞书写内容以及选择的回复人的相关信息
 * @date: 2022/5/12 11:20
 * @version: 1.0
 * @author:
 */
@Entity(tableName = "hole")
public class Hole extends BaseObservable {
    @PrimaryKey
    private int uid;
    private String content;
    private String alias;
    private Boolean is_mine;
    private Integer reply_local_id;

    @Ignore
    public Hole(int uid, String content, String alias, Boolean is_mine, Integer reply_local_id) {
        this.uid = uid;
        this.content = content;
        this.alias = alias;
        this.is_mine = is_mine;
        this.reply_local_id = reply_local_id;
    }

    public Hole() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getIs_mine() {
        return is_mine;
    }

    public void setIs_mine(Boolean is_mine) {
        this.is_mine = is_mine;
    }

    public Integer getReply_local_id() {
        return reply_local_id;
    }

    public void setReply_local_id(Integer reply_local_id) {
        this.reply_local_id = reply_local_id;
    }
}
