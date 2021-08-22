package com.example.hustholetest1.view.homescreen.mine.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.R;
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyHoleFragment extends Fragment {

    private static final String BASE_URL = "http://husthole.pivotstudio.cn/api/";
    private ArrayList<String[]> myHolesList=new ArrayList<>();
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private RecyclerView myRecycleView;

    String TAG = "myHole";
//    ArrayList<String[]> list1=new ArrayList<>();
//    ArrayList<String[]> list2;

    public static MyHoleFragment newInstance() {
        MyHoleFragment fragment = new MyHoleFragment();
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myHoleView = inflater.inflate(R.layout.fragment_myhole, container, false);
        Log.d(TAG+"111","this is my hole fragment");
        myRecycleView = myHoleView.findViewById(R.id.myHoleRecyclerView);
        myRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        update();
//        String[] str1 = new String[2];
//        str1[0] = "aa";
//        str1[1] = "bb";
//        list1.add(str1);
//        String[] str2 = new String[2];
//        str2[0] = "aaa";
//        list1.add(str2);
//        tt(list1);

        return myHoleView;
    }
//    void tt(ArrayList<String[]> list){
//        list2 = list;
//        String[] s = list2.get(0);
//        Log.d("myHole",list2.size()+"*"+list2.get(0).length+"*"+"*"+s.length+s[0]);
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);

    }
    @Override
    public void onResume() {
        super.onResume();
        update();
    }

//    private class DownLoadTask extends AsyncTask<Void, Void, Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//    }
    private void update(){
        Log.d("myHole","in update");
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                Call<ResponseBody> call = request.myHoles(0,20);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                                Log.d("myHole" ,"this is myHoles reply: "+json);
                            }
                            jsonArray=new JSONArray(json);
//                            new DownLoadTask().execute();
                            try {
                                for(int f=0;f<jsonArray.length();f++) {
                                    JSONObject sonObject = jsonArray.getJSONObject(f);
                                    String[] SingleHole =new String[9];
                                    SingleHole[1] = sonObject.getString("content");
                                    SingleHole[2] = sonObject.getString("created_timestamp");
                                    SingleHole[3] = sonObject.getInt("follow_num")+"";
                                    SingleHole[4] = sonObject.getInt("hole_id")+"";
                                    SingleHole[5] = sonObject.getBoolean("is_follow")+"";
                                    SingleHole[6] = sonObject.getBoolean("is_thumbup")+"";
                                    SingleHole[7] = sonObject.getInt("reply_num")+"";
                                    SingleHole[8] = sonObject.getInt("thumbup_num")+"";
                                    myHolesList.add(SingleHole);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            myRecycleView.setAdapter(new CardsRecycleViewAdapter());
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) { }
                });
            }
        }).start();
    }



    public class CardsRecycleViewAdapter extends RecyclerView.Adapter<CardsRecycleViewAdapter.ViewHolder>{

        private Boolean mHolesSequenceCondition = false;
//        private ArrayList<String[]> myList;

        public class ViewHolder extends RecyclerView.ViewHolder{
            private Boolean more_condition=false;
            private int position;
            private View totalView;
            private TextView ID,date,content,text_up,text_talk,text_star;
            private ImageView img_up, img_talk, img_star, moreWhat,moreWhat0;
            private ConstraintLayout myInform,myDelete;
            private RelativeLayout rv;

            public ViewHolder(View view){

                super(view);

                totalView = view;
                rv = (RelativeLayout) view.findViewById(R.id.myhole_rv);
                ID = (TextView) view.findViewById(R.id.hole_id);
                date = (TextView) view.findViewById(R.id.date);
                content = (TextView) view.findViewById(R.id.content);

                text_up = (TextView) view.findViewById(R.id.text_up);
                text_talk = (TextView) view.findViewById(R.id.text_talk);
                text_star = (TextView) view.findViewById(R.id.text_star);

                img_up = (ImageView) view.findViewById(R.id.img_up);
                img_talk = (ImageView) view.findViewById(R.id.img_talk);
                img_star = (ImageView) view.findViewById(R.id.img_star);
                moreWhat = (ImageView) view.findViewById(R.id.threePoint);

                myDelete = (ConstraintLayout) view.findViewById(R.id.delete);
                myDelete.setVisibility(View.GONE);
                moreWhat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(more_condition==false){
                            myDelete.setVisibility(View.VISIBLE);
                            more_condition = true;
                        }else{
                            myDelete.setVisibility(View.GONE);
                            more_condition = false;
                        }
                    }
                });
                myDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView title = new TextView(getContext());
                        title.setGravity(Gravity.CENTER);
                        title.setTextColor(Color.BLACK);
                        title.setText("删除");
                        new AlertDialog.Builder(getContext())
                                .setCustomTitle(title)
                                .setMessage("你确认要删除该内容吗？")
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        myHolesList.remove(position);
                                        notifyDataSetChanged();
                                        myDelete.setVisibility(View.GONE);
                                        more_condition = false;
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        myDelete.setVisibility(View.GONE);
                                        more_condition = false;
                                    }
                                })
                                .show();
                    }
                });
                img_up.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if (myHolesList.get(position)[6].equals("false")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        request = retrofit.create(RequestInterface.class);
                                        Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + myHolesList.get(position)[4] + "/-1");//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                img_up.setImageResource(R.mipmap.active);
                                                myHolesList.get(position)[6] = "true";
                                                myHolesList.get(position)[8] = (Integer.parseInt(myHolesList.get(position)[8]) + 1) + "";
                                                text_up.setText(myHolesList.get(position)[8]);
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + myHolesList.get(position)[4] + "/-1");//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                Log.d(TAG,"取消点赞 ： " + response.body());
                                                img_up.setImageResource(R.mipmap.inactive);
                                                myHolesList.get(position)[6] = "false";
                                                myHolesList.get(position)[8] = (Integer.parseInt(myHolesList.get(position)[8]) - 1) + "";
                                                text_up.setText(myHolesList.get(position)[8]);
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(getContext(), "取消点赞失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }else{
                            Intent intent=new Intent(getContext(), EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                img_star.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if (myHolesList.get(position)[5].equals("false")) {
                                new Thread(new Runnable() {//加载纵向列表标题
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/" + myHolesList.get(position)[4]);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                try {
//                                                    Log.d(TAG,"收藏 ： " + response.body().string());
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
                                                img_star.setImageResource(R.mipmap.active_3);
                                                myHolesList.get(position)[5] = "true";
                                                myHolesList.get(position)[3] = (Integer.parseInt(myHolesList.get(position)[3]) + 1) + "";
                                                text_star.setText(myHolesList.get(position)[3]);
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(getContext(), "关注失败", Toast.LENGTH_SHORT).show();
                                                Log.d("", "关注失败");
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/" + myHolesList.get(position)[4]);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                                try {
//                                                    Log.d(TAG,"取消收藏 ： " + response.body().string());
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
                                                img_star.setImageResource(R.mipmap.inactive_3);
                                                myHolesList.get(position)[5] = "false";
                                                myHolesList.get(position)[3] = (Integer.parseInt(myHolesList.get(position)[3]) - 1) + "";
                                                notifyDataSetChanged();
                                                text_star.setText(myHolesList.get(position)[3]);
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(getContext(), "取消关注失败", Toast.LENGTH_SHORT).show();
                                                Log.d("", "取消关注失败");
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }else{
                            Intent intent=new Intent(getContext(), EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                content.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d("data[2]1", myHolesList.get(position)[2]);
                        Intent intent= CommentListActivity.newIntent(getActivity(), myHolesList.get(position));
                        startActivity(intent);
                    }
                });
            }
            public void bind(int position) {
                this.position = position;

                content.setText(myHolesList.get(position)[1]);
                date.setText(myHolesList.get(position)[2]);
                text_star.setText(myHolesList.get(position)[3]);
                ID.setText("# " + myHolesList.get(position)[4]);
                img_star.setImageResource(myHolesList.get(position)[5].equals("true") ? R.mipmap.active_3 : R.mipmap.inactive_3);
                img_up.setImageResource(myHolesList.get(position)[6].equals("true") ? R.mipmap.active : R.mipmap.inactive);
                text_talk.setText(myHolesList.get(position)[7]);
                text_up.setText(myHolesList.get(position)[8]);

            }

        }
        public CardsRecycleViewAdapter(){

        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.card_myhole,parent,false );
//            ViewHolder holder = new ViewHolder(view);
//
//            return holder;
            return new CardsRecycleViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_myhole,parent,false ));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            String[] hole = myHolesList.get(position);
//            holder.ID.setText("#" + hole[4]);
//            holder.date.setText(hole[2]+(mHolesSequenceCondition==true?"更新":"发布"));
//            holder.content.setText(hole[1]);
//            holder.text_star.setText(hole[3]);
//            holder.text_talk.setText(hole[7]);
//            holder.text_up.setText(hole[8]);
//
//            holder.img_star.setImageResource(hole[5].equals("true") ? R.mipmap.active_3 : R.mipmap.inactive_3);
//            holder.img_up.setImageResource(hole[6].equals("true") ? R.mipmap.active : R.mipmap.inactive);
            ((CardsRecycleViewAdapter.ViewHolder) holder).bind(position);
        }

        @Override
        public int getItemCount() { return myHolesList.size(); }

    }
}
