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

public class MyStarFragment extends Fragment {
    private List<Card> cards = new ArrayList<>();

    private View myStarView;
    public RecyclerView myStarRecycleView;

    public static MyStarFragment newInstance() {
        MyStarFragment fragment = new MyStarFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myStarView = inflater.inflate(R.layout.fragment_mystar, container, false);
        init();

        myStarRecycleView = (RecyclerView)myStarView.findViewById(R.id.my_starRecyclerView);
        myStarRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CardsRecycleViewAdapter adapter = new CardsRecycleViewAdapter(cards);
        myStarRecycleView.setAdapter(adapter);

        return myStarView;
    }

    private void init(){
//        ,1,2,3,4
        for(int i = 0; i < 10; i++){
            String aa = "";
            for(int j = 0; j < 20; j++){
                aa += "这是我的关注" + i + " ,";
            }
            cards.add(new Card("# 12345","2021-08-07",aa + i ,"1","3","2"));
        }
    }
}
