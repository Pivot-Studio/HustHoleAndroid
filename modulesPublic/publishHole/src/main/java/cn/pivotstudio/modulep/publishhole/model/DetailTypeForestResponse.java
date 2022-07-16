package cn.pivotstudio.modulep.publishhole.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.List;

import cn.pivotstudio.modulep.publishhole.BR;

/**
 * @classname:ForestResponse
 * @description:某一类型的所有小树林
 * @date:2022/5/6 0:56
 * @version:1.0
 * @author:
 */
public class DetailTypeForestResponse {
    List<ForestResponse> forests;

    public List<ForestResponse> getForests() {
        return forests;
    }

    public void setForests(List<ForestResponse> forests) {
        this.forests = forests;
    }

    public static class ForestResponse extends BaseObservable {
        String background_image_url;
        String cover_url;
        String description;
        Integer forest_id;
        Integer hole_number;
        Boolean joined;
        Integer joined_number;
        String last_active_time;
        String name;
        
        @Bindable
        public String getBackground_image_url() {
            return background_image_url;
        }

        public void setBackground_image_url(String background_image_url) {
            this.background_image_url = background_image_url;
            notifyPropertyChanged(BR.background_image_url);
        }

        @Bindable
        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
            notifyPropertyChanged(BR.cover_url);
        }

        @Bindable
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
            notifyPropertyChanged(BR.description);
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
        public Integer getHole_number() {
            return hole_number;
        }

        public void setHole_number(Integer hole_number) {
            this.hole_number = hole_number;
            notifyPropertyChanged(BR.hole_number);
        }

        @Bindable
        public Boolean getJoined() {
            return joined;
        }

        public void setJoined(Boolean joined) {
            this.joined = joined;
            notifyPropertyChanged(BR.joined);
        }

        @Bindable
        public Integer getJoined_number() {
            return joined_number;
        }

        public void setJoined_number(Integer joined_number) {
            this.joined_number = joined_number;
            notifyPropertyChanged(BR.joined_number);
        }

        @Bindable
        public String getLast_active_time() {
            return last_active_time;
        }

        public void setLast_active_time(String last_active_time) {
            this.last_active_time = last_active_time;
            notifyPropertyChanged(BR.last_active_time);
        }

        @Bindable
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            notifyPropertyChanged(BR.name);
        }


        @Override
        public String toString() {
            return name+"+"+background_image_url;
        }
    }
}
