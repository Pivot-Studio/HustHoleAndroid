package cn.pivotstudio.modulep.hole.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.List;

import cn.pivotstudio.modulep.hole.BR;

/**
 * @classname:CommentListResponse
 * @description:
 * @date:2022/5/8 13:35
 * @version:1.0
 * @author:
 */
public class ReplyListResponse {
    List<ReplyResponse> msg;
    private String model;

    public String getModel() {
        if(model==null) {model="BASE";}
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<ReplyResponse> getMsg() {
        return msg;
    }


    public void setMsg(List<ReplyResponse> msg) {
        this.msg = msg;
    }


    public static class ReplyResponse extends BaseObservable {
        private String alias;
        private String content;
        private String created_timestamp;
        private Integer hole_id;
        private Integer id;
        private Boolean is_mine;
        private Boolean is_thumbup;
        private Integer reply_local_id;
        private Integer reply_to;
        private String reply_to_alias;
        private String reply_to_content;
        private Integer thumbup_num;
        private Boolean is_hot;

        @Bindable
        public Boolean getIs_hot() {
            return is_hot;
        }

        public void setIs_hot(Boolean is_hot) {
            this.is_hot = is_hot;
            notifyPropertyChanged(BR.is_hot);
        }

        @Bindable
        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
            notifyPropertyChanged(BR.alias);
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
        public Integer getHole_id() {
            return hole_id;
        }

        public void setHole_id(Integer hole_id) {
            this.hole_id = hole_id;
            notifyPropertyChanged(BR.hole_id);
        }

        @Bindable
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
            notifyPropertyChanged(BR.id);
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
        public Boolean getIs_thumbup() {
            return is_thumbup;
        }

        public void setIs_thumbup(Boolean is_thumbup) {
            this.is_thumbup = is_thumbup;
            notifyPropertyChanged(BR.is_thumbup);
        }

        @Bindable
        public Integer getReply_local_id() {
            return reply_local_id;
        }

        public void setReply_local_id(Integer reply_local_id) {
            this.reply_local_id = reply_local_id;
            notifyPropertyChanged(BR.reply_local_id);
        }

        @Bindable
        public Integer getReply_to() {
            return reply_to;
        }

        public void setReply_to(Integer reply_to) {
            this.reply_to = reply_to;
            notifyPropertyChanged(BR.reply_to);
        }

        @Bindable
        public String getReply_to_alias() {
            return reply_to_alias;
        }

        public void setReply_to_alias(String reply_to_alias) {
            this.reply_to_alias = reply_to_alias;
            notifyPropertyChanged(BR.reply_to_alias);
        }

        @Bindable
        public String getReply_to_content() {
            return reply_to_content;
        }

        public void setReply_to_content(String reply_to_content) {
            this.reply_to_content = reply_to_content;
            notifyPropertyChanged(BR.reply_to_content);
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


}
