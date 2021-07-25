package com.example.hustholetest1.View.HomePage.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.NotificationBean;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.example.hustholetest1.View.HomePage.fragment.ParseNotificationData.parseJson;

public class Page3Fragment extends Fragment {
    private static List<NotificationBean> myNotificationList = new ArrayList<>();
    private static Boolean isNotification = false;
    private NotificationAdapter adapter;
    private RecyclerView notificationRecyclerView;
    private final static String TAG = "tag";
    private ImageView imageView3;
    private TextView textView16;
    private int page = 1;
    private final static int list_size = 15;
    private int start_id = 0;
    private ConstraintLayout constraintLayout;

    private Boolean isAll = false;
    private Boolean hasInit = false;

    public String url = "http://hustholetest.pivotstudio.cn/api/notices/";
    public static Page3Fragment newInstance() {
        Page3Fragment fragment = new Page3Fragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page3fragment, container, false);
        RefreshLayout refreshLayout = rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
       //refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);
        //refreshLayout.setEnableScrollContentWhenLoaded(false);//是否在加载完成时滚动列表显示新的内容
        //refreshLayout.setEnableAutoLoadMore(false);//是否启用列表惯性滑动到底部时自动加载更多

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(4000/*,false*/);//传入false表示刷新失败

        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NotNull RefreshLayout refreshlayout) {
                if(!isAll) {
                    getStringByOkhttp(url + "?" + "start_id=" + start_id + "&" +
                            "list_size=" + list_size);
                    //refreshlayout.finishLoadMore();
                }
                else{
                    refreshlayout.finishLoadMoreWithNoMoreData();
                }
                refreshlayout.finishLoadMore(4000/*,false*/);//传入false表示加载失败
            }
        });

        constraintLayout = rootView.findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(),SystemNotification.class);
            startActivity(intent);
        });

        imageView3 =  rootView.findViewById(R.id.imageView3);
        textView16 =  rootView.findViewById(R.id.textView16);
        constraintLayout = rootView.findViewById(R.id.constraintLayout);
        notificationRecyclerView = rootView.findViewById(R.id.notification);
        if(!hasInit){
            getStringByOkhttp(url+"?"+"start_id="+start_id+"&"+
                    "list_size="+list_size);
        }
        else if(myNotificationList!= null && hasInit){ //已经初始化直接显示,每次回到这个页面时直接显示
            imageView3.setVisibility(View.GONE);
            textView16.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
            adapter = new NotificationAdapter(getActivity(), myNotificationList);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
            notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
            notificationRecyclerView.setAdapter(adapter);
        }
        else{ //数据为null
            imageView3.setVisibility(View.VISIBLE);
            textView16.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
        }

        //加上底部的分割线
        //notificationRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //Log.d(TAG, "onCreateView: 6");

        return rootView;
    }


    //弃
    /*private void loadData(final String url){
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
    }*/

    //test Adapter
    /*private void getMyNotificationList() {
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
    }*/

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: msr.what " +msg.what);
            switch (msg.what){
                case 0://初次请求网络成功
                    String Data = (String)msg.obj;
                    Data = Data.replace("{\"msg\":","");
                    Data = removeCharAt(Data, Data.length()-1);
                    Log.d(TAG, "handleMessage: get Data1 "+ Data);
                    myNotificationList = parseJson(Data);
                    isNotification = true;
                    if(isNotification){ //有通知就不显示
                        imageView3.setVisibility(View.GONE);
                        textView16.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.GONE);
                        Log.d(TAG, "onCreateView: Gone");
                        Log.d(TAG, "handleMessage: size  " + myNotificationList.size());
                        adapter = new NotificationAdapter(getActivity(), myNotificationList);
                        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        notificationRecyclerView.setAdapter(adapter);
                        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                if(position>0){
                                    Toast.makeText(getActivity(), "click " + position, Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Intent intent = new Intent(getActivity(),SystemNotification.class);
                                    startActivity(intent);
                                }

                            }
                        });
                        page ++;
                        start_id+=list_size;
                        Log.d(TAG, "handleMessage: start_id :"+start_id);
                    }
                    else {
                        imageView3.setVisibility(View.VISIBLE);
                        textView16.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onCreateView: visible");
                    }
                    break;
                case 1://失败
                    constraintLayout.setVisibility(View.VISIBLE);
                    textView16.setText("网络连接失败");
                    break;
                case 2://不是首次请求网络
                    String Data2 = (String)msg.obj;
                    Data2 = Data2.replace("{\"msg\":","");
                    Data2 = removeCharAt(Data2, Data2.length()-1);
                    Log.d(TAG, "handleMessage: get Data2 "+ Data2);
                    myNotificationList.addAll(parseJson(Data2));
                    Log.d(TAG, "handleMessage: size  " + myNotificationList.size());
                    /*adapter = new NotificationAdapter( getActivity(), myNotificationList);
                    notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                    notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    notificationRecyclerView.setAdapter(adapter);*/
                    adapter.notifyDataSetChanged();
                    page ++;
                    start_id+=list_size;
                    Log.d(TAG, "handleMessage: start_id :"+start_id);
                    break;
                case 3:
                    if(myNotificationList != null){
                        /*adapter = new NotificationAdapter( getActivity(), myNotificationList);
                        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        notificationRecyclerView.setAdapter(adapter);*/
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        imageView3.setVisibility(View.VISIBLE);
                        textView16.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onCreateView: visible");
                    }
                    break;
                    
            }
        }
    };

    public void getStringByOkhttp(String path){
        OkHttpClient client = new OkHttpClient();
        Message message = Message.obtain();
        Request request = new Request.Builder().get().addHeader("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Ikp" +
                "XVCJ9.eyJlbWFpbCI6IjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwZmM2NGNlZTM5ZTA1ZGJjNWI2ODViNDM2OWMyNTR" +
                "iNDg5OTBkZmU1ZmQ5YTciLCJyb2xlIjoidXNlciIsInRpbWVTdGFtcCI6MTYyNjQ4OTE2Mn0.L_L0AFqPFEwoiQJHicJi3P4vy9aj_h" +
                "x8E8aq0OkC74s").url(path).build();

        Log.d(TAG, "getStringByOkhttp: request");
        try {
            Call call = client.newCall(request);
            Log.d(TAG, "getStringByOkhttp: call");
            call.enqueue(new Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    message.what = 1;
                    message.obj = e.getMessage();
                    System.out.println("Failed");
                    Log.d(TAG, "onFailure: i am on Failure");
                    Log.d(TAG, "onFailure: e.toString "+ e.toString());
                    Log.d(TAG,"e.getLocalizedMessage"+e.getLocalizedMessage());
                }
                @Override
                public void onResponse(Call call,Response response)throws IOException{
                    if(response.isSuccessful()){//回调的方法执行在子线程。
                        Log.d(TAG,"获取数据成功了");
                        Log.d(TAG,"response.code()=="+response.code());
                        final String responseData = response.body().string();
                        Log.d(TAG,"response.body().string()== "+responseData);
                        Log.d(TAG,"obtain message");

                        String temp = responseData.replace("{\"msg\":","");
                        temp = temp.replace("}","");
                        if(temp.equals("null")){  //已经全部请求完或者是第一次请求就没有消息
                            message.what = 3;
                            isAll = true;
                            hasInit = true;
                        }
                        else if(page ==1){
                            message.what = 0;
                            hasInit = true;
                        }
                        else if(page >1){
                            message.what = 2;
                        }
                        message.obj = responseData;
                        handler.sendMessage(message);
                        Log.d(TAG, "onResponse: handler message");
                    }
                    else{
                        Log.d(TAG, "onResponse: response " + response.networkResponse());
                    }
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "getStringByOkhttp: i am exception");
            e.printStackTrace();
            Log.d(TAG, "getStringByOkhttp: e.toString() "+ e.toString());
        }
        Log.d(TAG, "getStringByOkhttp: getResponseData: null");
    }
}


