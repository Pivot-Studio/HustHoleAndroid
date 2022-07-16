package cn.pivotstudio.modulec.homescreen.network;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.util.List;

import cn.pivotstudio.modulec.homescreen.BR;

/**
 * @classname:HomepageHoleResponse
 * @description:树洞列表数据类
 * @date:2022/5/3 22:50
 * @version:1.0
 * @author:
 */
public class HomepageHoleResponse {
    private List<DataBean> data;
    private String model;//记录通过何种方式获得的数据

    public String getModel() {
        if (model == null) {
            model = "BASE";
        }
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean extends BaseObservable implements Serializable {

        String content;
        String created_timestamp;
        Integer follow_num;
        Integer forest_id;
        String forest_name;
        Integer hole_id;
        Boolean is_follow;
        Boolean is_mine;
        Boolean is_reply;
        Boolean is_thumbup;
        Integer reply_num;
        Integer thumbup_num;
        String role;
        Boolean is_last_reply;

        @Bindable
        public Boolean getIs_last_reply() {
            return is_last_reply;
        }

        public void setIs_last_reply(Boolean is_last_reply) {
            this.is_last_reply = is_last_reply;
            notifyPropertyChanged(BR.is_last_reply);
        }

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
        public Integer getThumbup_num() {
            return thumbup_num;
        }

        public void setThumbup_num(Integer thumbup_num) {
            this.thumbup_num = thumbup_num;
            notifyPropertyChanged(BR.thumbup_num);
        }

        @Bindable
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
            notifyPropertyChanged(BR.role);
        }
    }
}
