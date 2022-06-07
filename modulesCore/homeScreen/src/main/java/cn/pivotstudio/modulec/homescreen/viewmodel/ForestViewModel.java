package cn.pivotstudio.modulec.homescreen.viewmodel;

import com.example.libbase.base.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulec.homescreen.model.ForestHoleResponse;

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
public class ForestViewModel extends BaseViewModel {

    private List<ForestHoleResponse.ForestHole> forestHoles = new ArrayList<>();

    private List<ForestHoleResponse.ForestHead> forestHeads = new ArrayList<>();

    // TODO 数据应该从model层获取， 这里暂时在 viewModel 层创建
    public ForestViewModel() {
        forestHoles.add(new ForestHoleResponse.ForestHole());
        forestHoles.add(new ForestHoleResponse.ForestHole());
        forestHoles.add(new ForestHoleResponse.ForestHole());

        forestHeads.add(new ForestHoleResponse.ForestHead());
        forestHeads.add(new ForestHoleResponse.ForestHead());
        forestHeads.add(new ForestHoleResponse.ForestHead());
    }

    public List<ForestHoleResponse.ForestHole> getForestHoles() {
        return forestHoles;
    }

    public List<ForestHoleResponse.ForestHead> getForestHeads() {
        return forestHeads;
    }
}
