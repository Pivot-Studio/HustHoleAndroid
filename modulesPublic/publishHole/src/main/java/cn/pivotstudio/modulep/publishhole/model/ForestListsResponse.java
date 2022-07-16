package cn.pivotstudio.modulep.publishhole.model;

import java.util.List;

/**
 * @classname:ForestListsResponse
 * @description:按类型分类的所有的小树林
 * @date:2022/5/7 14:48
 * @version:1.0
 * @author:
 */
public class ForestListsResponse {
    int itemNumber;
    List<DetailTypeForestResponse> lists;

    public int getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public List<DetailTypeForestResponse> getLists() {
        return lists;
    }

    public void setLists(List<DetailTypeForestResponse> lists) {
        this.lists = lists;
    }
}
