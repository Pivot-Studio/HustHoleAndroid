package com.example.hustholetest1.view.homescreen.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.R;

import com.example.hustholetest1.view.homescreen.mypage.Update;
import com.example.hustholetest1.view.homescreen.mypage.UpdateRecycleViewAdapter;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {

    private List<Update> updates = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        ImageView img = findViewById(R.id.update_img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
        View updateView;
        RecyclerView  updateRecycleView = (RecyclerView)findViewById(R.id.update_recyclerview);
        updateRecycleView.setLayoutManager(new LinearLayoutManager(this));
       UpdateRecycleViewAdapter adapter = new UpdateRecycleViewAdapter(updates);
        updateRecycleView.setAdapter(adapter);
    }
    private void init(){
        for(int i = 0; i < 10; i++) {
            updates.add(new Update("v 1." + i, "2021-03-08", "新增功能：\n- 关键词搜索;\n体验优化:\n- 点击树洞评论昵称时跳转的评论会闪动提醒\n"));
        }
    }
}
