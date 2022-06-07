package cn.pivotstudio.modulec.homescreen.viewmodel;

import com.example.libbase.base.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulec.homescreen.model.ForestResponse;

/**
 * @classname ForestViewModel
 * @description:
 * @date 2022/5/2 23:09
 * @version:1.0
 * @author: mhh
 */
public class ForestViewModel extends BaseViewModel {

    private List<ForestResponse.ForestHole> forestHoles = new ArrayList<>();

    private List<ForestResponse.ForestHead> forestHeads = new ArrayList<>();

    // TODO 数据应该从model层获取， 这里暂时在 viewModel 层创建
    public ForestViewModel() {
        forestHoles.add(new ForestResponse.ForestHole());
        forestHoles.add(new ForestResponse.ForestHole());
        forestHoles.add(new ForestResponse.ForestHole());

        forestHeads.add(new ForestResponse.ForestHead());
        forestHeads.add(new ForestResponse.ForestHead());
        forestHeads.add(new ForestResponse.ForestHead());
    }

    public List<ForestResponse.ForestHole> getForestHoles() {
        return forestHoles;
    }

    public List<ForestResponse.ForestHead> getForestHeads() {
        return forestHeads;
    }
}
