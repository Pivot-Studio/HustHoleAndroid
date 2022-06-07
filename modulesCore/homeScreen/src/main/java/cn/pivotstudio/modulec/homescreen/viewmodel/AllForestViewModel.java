package cn.pivotstudio.modulec.homescreen.viewmodel;

import com.example.libbase.base.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulec.homescreen.model.ForestResponse;

public class AllForestViewModel extends BaseViewModel {

    private List<ForestResponse.ForestCard> forestCards = new ArrayList<>();

    public AllForestViewModel() {
        for (int i = 0; i < 4; i++) {
            forestCards.add(new ForestResponse.ForestCard());
        }
    }

    public List<ForestResponse.ForestCard> getForestCards() {
        return forestCards;
    }

}
