package cn.pivotstudio.modulec.homescreen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mhh
 * @version :1.0
 * @classname ForestHoleResponse
 * @description: 小树林
 * @date :2022/6/4 1:27
 */
public class ForestResponse {

    public static class ForestHole {
        private String content = "content";
        private String holeId = "holdId";
        private String forestId = "forestId";
        private String title = "title";

        public String getContent() {
            return content;
        }

        public String getHoleId() {
            return holeId;
        }

        public String getForestId() {
            return forestId;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ForestHead {
        private String content = "content";
        private String holeId = "holdId";
        private String forestId = "forestId";
        private String title = "title";

        public String getContent() {
            return content;
        }

        public String getHoleId() {
            return holeId;
        }

        public String getForestId() {
            return forestId;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ForestCard {
        private Integer backgroundImage;
        private Integer avatar;
        private Integer followerCount;
        private Integer holeCount;
        private String overview;
        private String title;
        private boolean joined = false;

        //==================================================================
        //  GETTER & SETTER
        //==================================================================
        public Integer getBackgroundImage() {
            return backgroundImage;
        }

        public Integer getAvatar() {
            return avatar;
        }

        public String getOverview() {
            return overview;
        }

        public boolean isJoined() {
            return joined;
        }

        public void setBackgroundImage(Integer backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        public void setAvatar(Integer avatar) {
            this.avatar = avatar;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public void Join(boolean joined) {
            this.joined = joined;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Integer getFollowerCount() {
            return followerCount;
        }

        public void setFollowerCount(Integer followerCount) {
            this.followerCount = followerCount;
        }

        public Integer getHoleCount() {
            return holeCount;
        }

        public void setHoleCount(Integer holeCount) {
            this.holeCount = holeCount;
        }
    }

    public static class ForestDetail {
        private boolean joined = false;
        List<ForestHole> forestDetailHoles = new ArrayList<>();


        public boolean isJoined() {
            return joined;
        }

        public void Join() {
            this.joined = true;
        }

        public List<ForestHole> getForestDetailHoles() {
            return forestDetailHoles;
        }

        public void setForestDetailHoles(List<ForestHole> forestDetailHoles) {
            this.forestDetailHoles = forestDetailHoles;
        }
    }


}
