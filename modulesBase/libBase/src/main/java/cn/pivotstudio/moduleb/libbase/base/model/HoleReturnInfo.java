package cn.pivotstudio.moduleb.libbase.base.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @classname:HoleReturnInfo
 * @description:holeactivity经过finish()后从树洞界面返回的数据
 * @date:2022/5/17 16:20
 * @version:1.0
 * @author:
 */
public class HoleReturnInfo implements Parcelable {
    public static final Creator<HoleReturnInfo> CREATOR = new Creator<HoleReturnInfo>() {
        @Override
        public HoleReturnInfo createFromParcel(Parcel source) {
            return new HoleReturnInfo(source);
        }

        @Override
        public HoleReturnInfo[] newArray(int size) {
            return new HoleReturnInfo[size];
        }
    };
    Boolean is_follow;
    Boolean is_reply;
    Boolean is_thumbup;
    Integer reply_num;
    Integer thumbup_num;
    Integer follow_num;

    public HoleReturnInfo() {
    }

    protected HoleReturnInfo(Parcel in) {
        this.is_follow = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.is_reply = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.is_thumbup = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.reply_num = (Integer) in.readValue(Integer.class.getClassLoader());
        this.thumbup_num = (Integer) in.readValue(Integer.class.getClassLoader());
        this.follow_num = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public Boolean getIs_follow() {
        return is_follow;
    }

    public void setIs_follow(Boolean is_follow) {
        this.is_follow = is_follow;
    }

    public Boolean getIs_reply() {
        return is_reply;
    }

    public void setIs_reply(Boolean is_reply) {
        this.is_reply = is_reply;
    }

    public Boolean getIs_thumbup() {
        return is_thumbup;
    }

    public void setIs_thumbup(Boolean is_thumbup) {
        this.is_thumbup = is_thumbup;
    }

    public Integer getReply_num() {
        return reply_num;
    }

    public void setReply_num(Integer reply_num) {
        this.reply_num = reply_num;
    }

    public Integer getThumbup_num() {
        return thumbup_num;
    }

    public void setThumbup_num(Integer thumbup_num) {
        this.thumbup_num = thumbup_num;
    }

    public Integer getFollow_num() {
        return follow_num;
    }

    public void setFollow_num(Integer follow_num) {
        this.follow_num = follow_num;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.is_follow);
        dest.writeValue(this.is_reply);
        dest.writeValue(this.is_thumbup);
        dest.writeValue(this.reply_num);
        dest.writeValue(this.thumbup_num);
        dest.writeValue(this.follow_num);
    }

    public void readFromParcel(Parcel source) {
        this.is_follow = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.is_reply = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.is_thumbup = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.reply_num = (Integer) source.readValue(Integer.class.getClassLoader());
        this.thumbup_num = (Integer) source.readValue(Integer.class.getClassLoader());
        this.follow_num = (Integer) source.readValue(Integer.class.getClassLoader());
    }
}
