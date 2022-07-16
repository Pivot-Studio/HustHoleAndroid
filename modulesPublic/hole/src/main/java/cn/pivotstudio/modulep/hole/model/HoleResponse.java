package cn.pivotstudio.modulep.hole.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import cn.pivotstudio.modulep.hole.BR;
/**
 * @classname:HoleResponse
 * @description:
 * @date:2022/5/8 13:36
 * @version:1.0
 * @author:
 */
public class HoleResponse extends BaseObservable {

    private String content;
    private String created_timestamp;
    private Integer follow_num;
    private Integer forest_id;
    private String forest_name;
    private Integer hole_id;
    private String image;
    private Boolean is_follow;
    private Boolean is_mine;
    private Boolean is_reply;
    private Boolean is_thumbup;
    private Integer reply_num;
    private String role;
    private Integer thumbup_num;

    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {

        this.content = content;
        notifyPropertyChanged(BR.content);
    }


    @Bindable
    public String getCreated_timestamp() {
        return created_timestamp;
    }

    public void setCreated_timestamp(String created_timestamp) {
        this.created_timestamp = created_timestamp;
        notifyPropertyChanged(BR.created_timestamp);
    }

    @Bindable
    public Integer getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(Integer follow_num) {
        this.follow_num = follow_num;
        notifyPropertyChanged(BR.follow_num);
    }

    @Bindable
    public Integer getForest_id() {
        return forest_id;
    }

    public void setForest_id(Integer forest_id) {
        this.forest_id = forest_id;
        notifyPropertyChanged(BR.forest_id);
    }

    @Bindable
    public String getForest_name() {
        return forest_name;
    }

    public void setForest_name(String forest_name) {
        this.forest_name = forest_name;
        notifyPropertyChanged(BR.forest_name);
    }

    @Bindable
    public Integer getHole_id() {
        return hole_id;
    }

    public void setHole_id(Integer hole_id) {
        this.hole_id = hole_id;
        notifyPropertyChanged(BR.hole_id);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public Boolean getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(Boolean is_follow) {
        this.is_follow = is_follow;
        notifyPropertyChanged(BR.is_follow);
    }

    @Bindable
    public Boolean getIs_mine() {
        return is_mine;
    }

    public void setIs_mine(Boolean is_mine) {
        this.is_mine = is_mine;
        notifyPropertyChanged(BR.is_mine);
    }

    @Bindable
    public Boolean getIs_reply() {
        return is_reply;
    }

    public void setIs_reply(Boolean is_reply) {
        this.is_reply = is_reply;
        notifyPropertyChanged(BR.is_reply);
    }

    @Bindable
    public Boolean getIs_thumbup() {
        return is_thumbup;
    }

    public void setIs_thumbup(Boolean is_thumbup) {
        this.is_thumbup = is_thumbup;
        notifyPropertyChanged(BR.is_thumbup);
    }

    @Bindable
    public Integer getReply_num() {
        return reply_num;
    }

    public void setReply_num(Integer reply_num) {
        this.reply_num = reply_num;
        notifyPropertyChanged(BR.reply_num);
    }

    @Bindable
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
        notifyPropertyChanged(BR.role);
    }

    @Bindable
    public Integer getThumbup_num() {
        return thumbup_num;
    }

    public void setThumbup_num(Integer thumbup_num) {
        this.thumbup_num = thumbup_num;
        notifyPropertyChanged(BR.thumbup_num);
    }
}
