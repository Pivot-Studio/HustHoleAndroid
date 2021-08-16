package com.example.hustholetest1.view.homescreen.mine.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.R;
import com.example.hustholetest1.view.homescreen.mypage.Card;
import com.example.hustholetest1.view.homescreen.mypage.CardsRecycleViewAdapter;


import java.util.ArrayList;
import java.util.List;

public class MyHoleFragment extends Fragment {
    private List<Card> cards = new ArrayList<>();



    public static MyHoleFragment newInstance() {
        MyHoleFragment fragment = new MyHoleFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myHoleView;
        RecyclerView myHoleRecycleView;
        myHoleView = inflater.inflate(R.layout.fragment_myhole, container, false);
        init();

        myHoleRecycleView = (RecyclerView)myHoleView.findViewById(R.id.myHoleRecyclerView);
        myHoleRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CardsRecycleViewAdapter adapter = new CardsRecycleViewAdapter(cards);
        myHoleRecycleView.setAdapter(adapter);

        return myHoleView;
    }

    private void init(){
        for(int i = 0; i < 10; i++){
            String aa = "";
            for(int j = 0; j < 20; j++){
                aa += "这是我的关注" + i + " ,";
            }
            cards.add(new Card("# 12345","2021-08-07",aa + i ,"1","3","2"));
        }

    }
}
