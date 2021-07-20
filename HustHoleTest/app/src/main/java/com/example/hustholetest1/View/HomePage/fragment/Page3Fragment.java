package com.example.hustholetest1.View.HomePage.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.NotificationBean;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.hustholetest1.View.HomePage.fragment.MyNotificationOkhttp.getStringByOkhttp;
import static com.example.hustholetest1.View.HomePage.fragment.ParseNotificationData.parseJson;
import static com.example.hustholetest1.View.HomePage.fragment.getScreenData.getScreenWidth;

public class Page3Fragment extends Fragment {
    private List<NotificationBean> myNotificationList = new ArrayList<>();
    private Boolean isNotification = false;
    private NotificationAdapter adapter;
    private RecyclerView notificationRecyclerView;

    public String url = "http://hustholetest.pivotstudio.cn/api/notices/?start_id=1&list_size=8";
    public String urlLogin = "http://hustholetest.pivotstudio.cn/api/auth/mobileLogin";
    public static Page3Fragment newInstance() {
        Page3Fragment fragment = new Page3Fragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page3fragment, container, false);
        RefreshLayout refreshLayout = (RefreshLayout)rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
       // refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

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

        //通知部分
        String TAG = "tag";
        getMyNotificationList();
        isNotification = true;


        String responseData = getStringByOkhttp("url");
        //Log.d(TAG, "onCreateView: resposedata "+responseData);

        ImageView imageView3 = (ImageView) rootView.findViewById(R.id.imageView3);
        TextView textView16 = (TextView) rootView.findViewById(R.id.textView16);
        if(isNotification == true){ //有通知就不显示
            imageView3.setVisibility(View.GONE);
            textView16.setVisibility(View.GONE);
            Log.d(TAG, "onCreateView: Gone");
        }
        else {
            imageView3.setVisibility(View.VISIBLE);
            textView16.setVisibility(View.VISIBLE);
            Log.d(TAG, "onCreateView: visible");
        }


        notificationRecyclerView = rootView.findViewById(R.id.notification);

        adapter = new NotificationAdapter( getActivity(), myNotificationList);

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));

        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());

        notificationRecyclerView.setAdapter(adapter);

        //notificationRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //Log.d(TAG, "onCreateView: 6");

        /*String responseData = getStringByOkhttp(url);
        Log.d(TAG, "onCreateView: i am here2  "+responseData);
        loadData(url);*/

        ConstraintLayout constraintLayout = (ConstraintLayout) rootView.findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SystemNotification.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
    private void loadData(final String url){
        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                String responseData = getStringByOkhttp(url);
                return responseData;
            }
            @Override
            protected void onPostExecute(String responseData) {
                if(responseData!=null){
                    List<NotificationBean> mNotificationList =
                            parseJson(responseData);
                    myNotificationList.addAll(mNotificationList);
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }
    private void getMyNotificationList() {
        NotificationBean myNotificationBean1 = new NotificationBean("107316", "18:37 2021-06-09", "评论了你的树洞", "balabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabalabala",
                    "8857", "西二炸酱面", "0","1");
        String TAG="tag";
        NotificationBean myNotificationBean2 = new NotificationBean("107228", "19:11 2021-05-23", "评论了你的树洞", "和山山水水",
                "8830", "东湖皮卡丘", "0","1");
        NotificationBean myNotificationBean3 = new NotificationBean("107227", "19:11 2021-05-23", "评论了你的树洞", "你好",
                "8830", "东湖皮卡丘", "0","1");
        myNotificationList.add(myNotificationBean1);
        myNotificationList.add(myNotificationBean2);
        myNotificationList.add(myNotificationBean3);
        Log.d(TAG, "getMyNotificationList: get in method");
    }


}



