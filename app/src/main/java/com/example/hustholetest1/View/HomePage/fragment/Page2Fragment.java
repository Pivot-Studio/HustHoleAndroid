package com.example.hustholetest1.View.HomePage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.HomePageActivity;
import com.example.hustholetest1.View.HomePage.Activity.Page2_AllForestsActivity;
import com.example.hustholetest1.View.RegisterAndLogin.Activity.RegisterActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.List;
import java.util.UUID;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Page2Fragment extends Fragment {
    private RecyclerView recyclerView,recyclerView2;
    private TextView textView;
    public static Page2Fragment newInstance() {

          Page2Fragment fragment = new Page2Fragment();
          return fragment;

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page2fragment, container, false);
        RefreshLayout refreshLayout = (RefreshLayout)rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(4000/*,false*/);
//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(4000/*,false*/);
                //传入false表示加载失败
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView2);
        recyclerView2=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new CircleForestsAdapter());


        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        recyclerView2.setAdapter(new ForestHoleAdapter());
        //recyclerView2.setLayoutManager();

        textView=(TextView)rootView.findViewById(R.id.textView27);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Page2_AllForestsActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }



    public class CircleForestsAdapter extends RecyclerView.Adapter<CircleForestsAdapter.ViewHolder>{
        //private static List<Event> events;



        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView forestname;
            private ImageView forestphoto;
            ConstraintLayout next;
            public ViewHolder(View view) {
                super(view);
               forestname = (TextView) view.findViewById(R.id.textView28);
               forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                    }
                });
            }


            public void bind(int position){
            }

        }

        public CircleForestsAdapter(){
            Log.d(TAG,"数据传入了");
            //this.events=events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foresticon_model,parent,false );
            Log.d(TAG,"已经创建适配器布局");
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //Event event=events.get(position);
            Log.d(TAG,"已经设置单个信息了"+position);
            holder.bind(position);

        }
        @Override
        public int getItemCount() {
            return 8;
        }
    }
    public class ForestHoleAdapter extends RecyclerView.Adapter<ForestHoleAdapter.ViewHolder>{
        //private static List<Event> events;



        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView forestname;
            private ImageView forestphoto;
            ConstraintLayout next;
            public ViewHolder(View view) {
                super(view);
                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                    }
                });
            }


            public void bind(int position){
            }

        }

        public ForestHoleAdapter(){
            Log.d(TAG,"数据传入了");
            //this.events=events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foresthole_model,parent,false );
            Log.d(TAG,"已经创建适配器布局");
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //Event event=events.get(position);
            Log.d(TAG,"已经设置单个信息了"+position);
            holder.bind(position);

        }
        @Override
        public int getItemCount() {
            return 10;
        }
    }
}
