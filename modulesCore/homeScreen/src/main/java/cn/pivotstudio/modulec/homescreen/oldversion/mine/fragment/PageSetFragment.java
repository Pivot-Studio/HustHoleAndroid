package cn.pivotstudio.modulec.homescreen.oldversion.mine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.pivotstudio.modulec.homescreen.oldversion.mypage.Item1;


public class PageSetFragment {

    private List<Item1> item4List = new ArrayList<>();

    private View pageSetView;
    public RecyclerView myrecycleview4;

    public static PageSetFragment newInstance() {
        PageSetFragment fragment = new PageSetFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        pageSetView = inflater.inflate(R.layout.activity_setting, container, false);
//        init();
//
//        myrecycleview4 = (RecyclerView)pageSetView.findViewById(R.id.my_recycleView4);
//        //myrecycleview4.setLayoutManager(new LinearLayoutManager(getActivity()));
//        RecycleViewAdapter1 adapter4 = new RecycleViewAdapter1(item4List);
//        myrecycleview4.setAdapter(adapter4);

        return pageSetView;
    }


//    private void init(){
//        item4List.add(new Item1("校园邮箱",R.drawable.back_black));
//        item4List.add(new Item1("隐私安全",R.drawable.back_black));
//    }
}
