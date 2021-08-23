package com.example.hustholetest1.view.homescreen.fragment;

import static com.example.hustholetest1.view.homescreen.message.ParseNotificationData.parseJson;
import static com.example.hustholetest1.view.homescreen.message.ParseNotificationData.parseSysJson;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.R;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.example.hustholetest1.view.homescreen.message.NotificationAdapter;
import com.example.hustholetest1.view.homescreen.message.NotificationBean;
import com.example.hustholetest1.view.homescreen.message.SystemNotification;
import com.example.hustholetest1.view.homescreen.message.SystemNotificationBean;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MessageFragment extends Fragment {
    private static List<NotificationBean> myNotificationList = new ArrayList<>();
    private static Boolean isNotification = false;
    private NotificationAdapter adapter;
    private RecyclerView notificationRecyclerView;
    private final static String TAG = "tag";
    private ImageView noNotificationImage;
    private TextView thereIsNoNotification;
    private int page = 1;
    private final static int list_size = 15;
    private int start_id = 0;
    private ConstraintLayout constraintLayout;

    private Boolean isAll = false;
    private Boolean hasInit = false;

    public String url = "http://hustholetest.pivotstudio.cn/api/notices/";
    private String token;


    private List<SystemNotificationBean> mSystemNotificationList = new ArrayList<>();
    private TextView latestSystemNotification;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page3fragment, container, false);
        RefreshLayout refreshLayout = rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setEnableLoadMore(true);
        refreshLayout.setEnableRefresh(true);
        //refreshLayout.setEnableScrollContentWhenLoaded(false);//是否在加载完成时滚动列表显示新的内容
        //refreshLayout.setEnableAutoLoadMore(false);//是否启用列表惯性滑动到底部时自动加载更多

        refreshLayout.setOnRefreshListener(refreshlayout -> {
            if(CheckingToken.IfTokenExist()){
                Log.d(TAG, "onCreateView: here are refresh");
                if(page > 1){
                    myNotificationList.clear();
                    mSystemNotificationList.clear();
                    adapter.getSystemNotification(null);
                    page = 1;
                }
                isAll = false;
                hasInit = false;
                start_id = 0;
                getLatestSystemNotification();
                getStringByOkhttp(url + "?" + "start_id=" + start_id + "&" +
                        "list_size=" + list_size);
                refreshlayout.finishRefresh(4000/*,false*/);//传入false表示刷新失败
            }
            else{
                refreshLayout.finishRefresh();
                Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                startActivity(intent);
            }

        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            if(!isAll) {
                getStringByOkhttp(url + "?" + "start_id=" + start_id + "&" +
                        "list_size=" + list_size);
            }
            else{
                refreshlayout.finishLoadMoreWithNoMoreData();
            }
            /*new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(5000);
                        Log.d("T", "run: sleep");
                        refreshlayout.finishLoadMore(4000*//*,false*//*);//传入false表示加载失败
                        Log.d("T", "onLoadMore: i am finishloadmore");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();*/
            refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
        });


        constraintLayout = rootView.findViewById(R.id.constraintLayout);
        constraintLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SystemNotification.class);
            startActivity(intent);
        });

        noNotificationImage = rootView.findViewById(R.id.no_notification_image);
        thereIsNoNotification = rootView.findViewById(R.id.there_is_no_notification);
        constraintLayout = rootView.findViewById(R.id.constraintLayout);
        notificationRecyclerView = rootView.findViewById(R.id.notification);
        if(!hasInit){
            getStringByOkhttp(url+"?"+"start_id="+start_id+"&"+
                    "list_size="+list_size);
        }
        else if(myNotificationList!= null && hasInit){ //已经初始化直接显示,每次回到这个页面时直接显示
            noNotificationImage.setVisibility(View.GONE);
            thereIsNoNotification.setVisibility(View.GONE);
            constraintLayout.setVisibility(View.GONE);
            adapter = new NotificationAdapter(getActivity(), myNotificationList);
            notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
            notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
            notificationRecyclerView.setAdapter(adapter);
            adapter.getSystemNotification(mSystemNotificationList.get(0).getSystemContent());
            adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    if(position>0){
                        startHoleActivity(position);
                        //Toast.makeText(getActivity(), "click " + position, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent = new Intent(getActivity(),SystemNotification.class);
                        startActivity(intent);
                    }

                }
            });
        }
        else{ //数据为null
            noNotificationImage.setVisibility(View.VISIBLE);
            thereIsNoNotification.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.VISIBLE);
        }

        //加上底部的分割线
        //notificationRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //Log.d(TAG, "onCreateView: 6");

        latestSystemNotification = rootView.findViewById(R.id.latest_system_notification);
        getLatestSystemNotification();

        return rootView;
    }


    public void getLatestSystemNotification(){
        String mPath= "http://hustholetest.pivotstudio.cn/api/system_notices?start_id=0&list_size=1";
        OkHttpClient client = new OkHttpClient();
        Message message = Message.obtain();

        SharedPreferences editor = getContext().getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        token = editor.getString("token", "");
        Log.d(TAG, "getStringByOkhttp: token "+token);
        Request request = new Request.Builder().get().addHeader("Authorization", "Bearer "+token).url(mPath).build();

        Log.d(TAG, "getStringByOkhttp: request");
        try {
            Call call = client.newCall(request);
            Log.d(TAG, "getStringByOkhttp: call");
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    message.what = 1;
                    message.obj = e.getMessage();
                    Log.d(TAG, "onFailure: e.toString " + e.toString());
                    Log.d(TAG, "e.getLocalizedMessage" + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {//回调的方法执行在子线程。
                        Log.d(TAG, "获取数据成功了");
                        Log.d(TAG, "response.code()==" + response.code());
                        final String responseData = response.body().string();
                        Log.d(TAG, "response.body().string()== " + responseData);
                        Log.d(TAG, "obtain message");
                        String temp = responseData.replace("{\"system_notices\":", "");
                        temp = removeCharAt(temp, temp.length()-1);
                        message.what = 0;
                        message.obj = temp;
                        systemNotificationHandler.sendMessage(message);
                        Log.d(TAG, "onResponse: handler message");
                    } else {
                        message.what = 1;
                        Log.d(TAG, "onResponse: response " + response.networkResponse());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "getStringByOkhttp:i am exception, e.toString() " + e.toString());
        }
    }

    private Handler systemNotificationHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: msr.what " +msg.what);
            switch (msg.what){
                case 0://请求网络成功
                    String Data = (String)msg.obj;
                    Log.d(TAG, "SystemNotificationhandleMessage: get Data "+ Data);
                    mSystemNotificationList = parseSysJson(Data);
                    latestSystemNotification.setText(mSystemNotificationList.get(0).getSystemContent());
                    Log.d(TAG, "handleMessage: adapter.content"+mSystemNotificationList.get(0).getSystemContent());
                    break;
                case 1://失败
                    break;
            }
        }
    };




    public void startHoleActivity(int mPosition){
        String data_hole_id = myNotificationList.get(mPosition-1).getHole_id();//position-1是因为SystemNotification为第一个item
        String[] data = null;
        Intent intent= CommentListActivity.newIntent(getActivity(),data);
        intent.putExtra("data_hole_id",data_hole_id);
        Log.d(TAG, "startActivity: get data_hole_id = "+data_hole_id);
        startActivity(intent);
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
                        noNotificationImage.setVisibility(View.GONE);
                        thereIsNoNotification.setVisibility(View.GONE);
                        constraintLayout.setVisibility(View.GONE);
                        Log.d(TAG, "handleMessage: size  " + myNotificationList.size());
                        adapter = new NotificationAdapter(getActivity(), myNotificationList);
                        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        notificationRecyclerView.setAdapter(adapter);
                        adapter.getSystemNotification(mSystemNotificationList.get(0).getSystemContent());
                        adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(int position) {
                                if(position>0){
                                    startHoleActivity(position);
                                    //Toast.makeText(getActivity(), "click " + position, Toast.LENGTH_SHORT).show();
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
                        noNotificationImage.setVisibility(View.VISIBLE);
                        thereIsNoNotification.setVisibility(View.VISIBLE);
                        constraintLayout.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onCreateView: visible");
                    }
                    break;
                case 1://失败
                    constraintLayout.setVisibility(View.VISIBLE);
                    thereIsNoNotification.setText("");
                    break;
                case 2://不是首次请求网络
                    String Data2 = (String)msg.obj;
                    Data2 = Data2.replace("{\"msg\":","");
                    Data2 = removeCharAt(Data2, Data2.length()-1);
                    Log.d(TAG, "handleMessage: get Data2 "+ Data2);
                    myNotificationList.addAll(parseJson(Data2));
                    adapter.getSystemNotification(mSystemNotificationList.get(0).getSystemContent());
                    Log.d(TAG, "handleMessage: size  " + myNotificationList.size());
                    /*adapter = new NotificationAdapter( getActivity(), myNotificationList);
                    notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                    notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    notificationRecyclerView.setAdapter(adapter);*/
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /**
                             *要执行的操作
                             */
                            adapter.notifyDataSetChanged();
                        }
                    }, 800);//3秒后执行Runnable中的run方法

                    Log.d(TAG, "handleMessage: case2");
                    adapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(int position) {
                            if(position>0){
                                startHoleActivity(position);
                                //Toast.makeText(getActivity(), "click " + position, Toast.LENGTH_SHORT).show();
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
                    break;
                case 3:
                    if(myNotificationList != null && page!=1 ){
                        /*adapter = new NotificationAdapter( getActivity(), myNotificationList);
                        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity() , LinearLayoutManager.VERTICAL,false));
                        notificationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        notificationRecyclerView.setAdapter(adapter);*/
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "handleMessage: case3");
                    }
                    else {
                        noNotificationImage.setVisibility(View.VISIBLE);
                        thereIsNoNotification.setVisibility(View.VISIBLE);
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


        SharedPreferences editor = getContext().getSharedPreferences("Depository", Context.MODE_PRIVATE);//
        token = editor.getString("token", "");
        Log.d("bala", "getStringByOkhttp: token "+token);

        Request request = new Request.Builder().addHeader("Authorization","Bearer "+token).get().url(path).build();
        /* addHeader("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6Ikp" +
                "XVCJ9.eyJlbWFpbCI6IjAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwZmM2NGNlZTM5ZTA1ZGJjNWI2ODViNDM2OWMyNTR" +
                "iNDg5OTBkZmU1ZmQ5YTciLCJyb2xlIjoidXNlciIsInRpbWVTdGFtcCI6MTYyNjQ4OTE2Mn0.L_L0AFqPFEwoiQJHicJi3P4vy9aj_h" +
                "x8E8aq0OkC74s"). */
        Log.d(TAG, "getStringByOkhttp: request ");
        try {
            Call call = client.newCall(request);
            Log.d(TAG, "getStringByOkhttp: call");
            call.enqueue(new Callback(){
                @Override
                public void onFailure(Call call, IOException e){
                    message.what = 1;
                    message.obj = e.getMessage();
                    System.out.println("Failed");
                    Log.d(TAG, "onFailure: i am on Failure");
                    Log.d(TAG, "onFailure: e.toString "+ e.toString());
                    Log.d(TAG,"e.getLocalizedMessage"+e.getLocalizedMessage());
                }
                @Override
                public void onResponse(Call call, Response response)throws IOException{
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
        Log.d(TAG, "getStringByOkhttp: getResponseData: null ");
    }
}


