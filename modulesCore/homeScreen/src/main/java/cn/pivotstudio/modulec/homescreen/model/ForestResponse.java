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
