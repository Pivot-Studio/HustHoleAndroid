package com.example.hustholetest1.View.HomePage.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hustholetest1.Model.Forest;
import com.example.hustholetest1.Model.RequestInterface;
import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.Model.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.CommentActivity;
import com.example.hustholetest1.View.HomePage.Activity.HomePageActivity;
import com.example.hustholetest1.View.HomePage.Activity.Page2_AllForestsActivity;
import com.example.hustholetest1.View.HomePage.Activity.Page2_DetailAllForestsActivity;
import com.example.hustholetest1.View.RegisterAndLogin.Activity.RegisterActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class Page2Fragment extends Fragment {
    private RecyclerView recyclerView,recyclerView2;
    private TextView textView;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray,jsonArray2;
    private String[][] detailforest;
    private ForestHoleAdapter adapter;
    //private String[][] detailforest2;

    private List<String[]> detailforest2=new ArrayList<String[]>();


    private Bitmap[][] bitmapss;
    private int number1=0;
    private int listsize=20;
    private String refreshcondition="false",refreshcondition2="false";
    private RefreshLayout refreshlayout1,refreshlayout2;

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
                refreshlayout1=refreshlayout;
                refreshcondition="true";
                listsize=20;
                detailforest2=new ArrayList<String[]>();
                update();

//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout2=refreshlayout;
                refreshcondition2="true";
                listsize=listsize+20;
                Log.d("listsize",""+listsize);
                update();
                //refreshlayout.finishLoadMore(4000);//false
                //传入false表示加载失败

            }
        });


        recyclerView2=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager2);

        //recyclerView2.setLayoutManager();


        TokenInterceptor.getContext(getActivity());
        //TokenInterceptor.getContext(RegisterActivity.this);
        System.out.println("提交了context");
        retrofit = new Retrofit.Builder()
                .baseUrl("http://hustholetest.pivotstudio.cn/api/forests/")
                .client(com.example.hustholetest1.Model.OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.e(TAG, "token99：");
        request = retrofit.create(RequestInterface.class);//创建接口实例
        //update();
        return rootView;
    }
    public int number(){
        number1++;
        return number1;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
        listsize=20;
    }

    public void update(){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                Call<ResponseBody> call = request.joined();//进行封装
                Call<ResponseBody> call2=request.joined_holes("http://hustholetest.pivotstudio.cn/api/forests/holes?list_size=20&start_id="+(listsize-20)+"&is_last_reply=true");
                Log.e(TAG, "token2：");
                if(listsize==20||refreshcondition.equals("true")){
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                JSONObject jsonObject = new JSONObject(json);
                                //读取
                                jsonArray = jsonObject.getJSONArray("forests");
                                detailforest = new String[jsonArray.length()][8];
                                //bitmapss=new Bitmap[jsonArray.length()][2];
                                //for(int f=0;f<jsonArray.length();f++) {
                                new DownloadTask2().execute();
                                Log.d("???????????????????????", "" + jsonArray);
                                //}

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {

                        }


                    });
                }
                call2.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }
                            jsonArray2 = new JSONArray(json);
                            //jsonArray =response.body();
                            //detailforest2=new String[jsonArray2.length()][14];
                            new DownloadTask().execute();
                           // Log.d("?????????????????????2", "" + jsonArray2);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "网络请求失败", Toast.LENGTH_SHORT).show();
                        if (listsize!=20) {
                           listsize=listsize-20;
                        }
                    }
                });



            }
        }).start();
    }



    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片
        @Override
        protected void onPostExecute(Void unused) {
            if(number()==2) {
                if(refreshcondition.equals("true")) {
                    refreshlayout1.finishRefresh();
                    refreshcondition="false";
                }
                adapter=new ForestHoleAdapter();
                recyclerView2.setAdapter(adapter);
                number1=0;
            }
            Log.d("状态1","sssss");
            if(refreshlayout2!=null){
                refreshlayout2.finishRefresh();
                refreshlayout2=null;
                refreshcondition2="false";
                adapter.notifyDataSetChanged();
                Log.d("AAA","ssss");
                //recyclerView2.setAdapter(new ForestHoleAdapter());
            }
        }
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("状态2","sss");
            try {
                for(int f=0;f<jsonArray2.length();f++) {
                    JSONObject sonObject = jsonArray2.getJSONObject(f);
                    String[] list=new String[14];
                    list[0] = sonObject.getString("background_image_url");
                    list[1] = sonObject.getString("content");
                    list[2] = sonObject.getString("created_timestamp");
                    list[3] = sonObject.getInt("follow_num")+"";
                    list[4] = sonObject.getInt("forest_id")+"";
                    list[5] = sonObject.getString("forest_name");
                    list[6] = sonObject.getInt("hole_id")+"";
                    //detailforest2[f][1] = sonObject.getString("image");
                    list[8] = sonObject.getBoolean("is_follow")+"";
                    list[9] = sonObject.getBoolean("is_mine")+"";
                    list[10] = sonObject.getBoolean("is_reply")+"";
                    list[11] = sonObject.getBoolean("is_thumbup")+"";
                    list[12] = sonObject.getInt("reply_num")+"";
                    list[13] = sonObject.getInt("thumbup_num")+"";
                    detailforest2.add(list);
                    /*
                    detailforest2[f][0] = sonObject.getString("background_image_url");
                    detailforest2[f][1] = sonObject.getString("content");
                    detailforest2[f][2] = sonObject.getString("created_timestamp");
                    detailforest2[f][3] = sonObject.getInt("follow_num")+"";
                    detailforest2[f][4] = sonObject.getInt("forest_id")+"";
                    detailforest2[f][5] = sonObject.getString("forest_name");
                    detailforest2[f][6] = sonObject.getInt("hole_id")+"";
                    //detailforest2[f][1] = sonObject.getString("image");
                    detailforest2[f][8] = sonObject.getBoolean("is_follow")+"";
                    detailforest2[f][9] = sonObject.getBoolean("is_mine")+"";
                    detailforest2[f][10] = sonObject.getBoolean("is_reply")+"";
                    detailforest2[f][11] = sonObject.getBoolean("is_thumbup")+"";
                    detailforest2[f][12] = sonObject.getInt("reply_num")+"";
                    detailforest2[f][13] = sonObject.getInt("thumbup_num")+"";

                     */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class DownloadTask2 extends AsyncTask<Void, Void, Void> {//用于加载图片
        @Override
        protected void onPostExecute(Void unused) {
            if(number()==2) {
                if(refreshcondition.equals("true")) {
                    refreshlayout1.finishRefresh();
                    refreshcondition="false";
                }
                recyclerView2.setAdapter(new ForestHoleAdapter());
                number1=0;
            }
           // number1++;
           // if(jsonArray.length()==number1){

            //}
        }
        @Override
        protected Void doInBackground(Void... voids) {
            //int allnumber=voids[0];
            try {
                for(int f=0;f<jsonArray.length();f++) {
                    JSONObject sonObject = jsonArray.getJSONObject(f);


                    detailforest[f][0] = sonObject.getString("background_image_url");
                    detailforest[f][1] = sonObject.getString("cover_url");
                    detailforest[f][2] = sonObject.getString("description");
                    detailforest[f][3] = sonObject.getInt("forest_id") + "";
                    detailforest[f][4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                    detailforest[f][5] = "true";
                    detailforest[f][6] = sonObject.getString("last_active_time");
                    detailforest[f][7] = sonObject.getString("name");



                    //detailforest [f][0] = sonObject.getString("cover_url");
                    //detailforest[f][1] = sonObject.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }





    public class CircleForestsAdapter extends RecyclerView.Adapter<CircleForestsAdapter.ViewHolder>{
        //private static List<Event> events;

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView forestname;
            private ImageView forestphoto;
            private int position;
            ConstraintLayout next;
            public ViewHolder(View view) {
                super(view);
               forestname = (TextView) view.findViewById(R.id.textView28);
               forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(position!=jsonArray.length()) {
                            Intent intent = Page2_DetailAllForestsActivity.newIntent(getContext(), detailforest[position]);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(getContext(), Page2_AllForestsActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }


            public void bind(int position){
                this.position=position;
                if(position!=jsonArray.length()) {
                    String name = detailforest[position][7];
                    if (name.length() > 6) {
                        forestname.setText(name.substring(0, 5) + "...");
                    } else {
                        forestname.setText(detailforest[position][7]);
                    }


                    Glide.with(getActivity())
                            .load(detailforest[position][0])
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .into(forestphoto);

                    //forestphoto.setImageBitmap(bitmapss[position][1]);
                }else{
                    forestname.setText("加载更多");
                    forestphoto.setImageResource(R.mipmap.more);

                }
            }

        }

        public CircleForestsAdapter(){
            //Log.d(TAG,"数据传入了");
            //this.events=events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foresticon_model,parent,false );
            ViewHolder holder=new ViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //Event event=events.get(position);
            holder.bind(position);

        }
        @Override
        public int getItemCount() {
            return jsonArray.length()+1;
        }
    }



    public class ForestHoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_BOTTOM = 2;
        private int mHeaderCount=1;//头部View个数
        private Boolean more_condition=false;
        private  ConstraintLayout morewhat0;
        @Override
        public int getItemViewType(int position) {
            if (mHeaderCount != 0 && position < mHeaderCount) {
//头部View
                return ITEM_TYPE_HEADER;

            } else {
//内容Vie
                return ITEM_TYPE_CONTENT;
            }
        }

        public class HeadHolder extends RecyclerView.ViewHolder{
            public HeadHolder(View view){
               super(view);

                textView=(TextView)view.findViewById(R.id.textView27);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), Page2_AllForestsActivity.class);
                        startActivity(intent);
                    }
                });
                recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView2);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(linearLayoutManager);
            }
            public void bind(int position){
                recyclerView.setAdapter(new CircleForestsAdapter());
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content,created_timestamp,forest_name,follow_num,reply_num,thumbup_num,hole_id,more_2;
            private ImageView background_image_url,is_follow,is_reply,is_thumbup,more,more_1;
            ConstraintLayout morewhat;
            private int position;


            public ViewHolder(View view) {
                super(view);
                content=(TextView)view.findViewById(R.id.textView37);
                created_timestamp=(TextView)view.findViewById(R.id.textView33);
                forest_name=(TextView)view.findViewById(R.id.textView32);
                thumbup_num =(TextView)view.findViewById(R.id.textView34);
                reply_num=(TextView)view.findViewById(R.id.textView35);
                follow_num =(TextView)view.findViewById(R.id.textView36);
                hole_id=(TextView)view.findViewById(R.id.textView39);
                background_image_url=(ImageView)view.findViewById(R.id.imageView12);
                is_thumbup=(ImageView)view.findViewById(R.id.imageView13);
                is_reply=(ImageView)view.findViewById(R.id.imageView14);
                is_follow=(ImageView)view.findViewById(R.id.imageView15);


                more=(ImageView)view.findViewById(R.id.imageView17);
                more_1=(ImageView)view.findViewById(R.id.imageView34);
                more_2=(TextView)view.findViewById(R.id.textView76);
                morewhat=(ConstraintLayout)view.findViewById(R.id.morewhat);
                morewhat.setVisibility(View.INVISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(more_condition==false){
                            morewhat.setVisibility(View.VISIBLE);
                            morewhat0=morewhat;
                            more_condition=true;
                        }else{
                            morewhat0.setVisibility(View.INVISIBLE);
                            morewhat.setVisibility(View.VISIBLE);
                            morewhat0=morewhat;
                        }

                        /*View contentView3 = LayoutInflater.from(getActivity()).inflate(R.layout.more, null);
                        popupWindow3=new PopupWindow(contentView3);

                        popupWindow3.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        popupWindow3.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


                        contentView3.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        int popupHeight = contentView3.getMeasuredHeight();
                        int popupWidth = contentView3.getMeasuredWidth();


                        int[] location = new int[2];
                        more.getLocationOnScreen(location);

                        popupWindow3.setAnimationStyle(R.style.contextMenuAnim);
                        popupWindow3.showAtLocation(more, Gravity.NO_GRAVITY, (location[0] + more.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight-70);
*/

                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        morewhat.setVisibility(View.INVISIBLE);
                        more_condition = false;
                        if(detailforest2.get(position-1)[9].equals("true")){
                            //if(detailforest2[position-1][9].equals("true")){
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.delete_hole("http://hustholetest.pivotstudio.cn/api/holes/" + detailforest2.get(position-1)[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.report("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + detailforest2.get(position-1)[6]+"&reply_local_id=-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Toast.makeText(getContext(), "举报成功", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(getContext(), "举报失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();



                        }




                    }
                });






                is_thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(detailforest2.get(position-1)[11].equals("false")){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+detailforest2.get(position-1)[6]+"/-1");//进行封装
                                Log.e(TAG, "token2：");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        is_thumbup.setImageResource(R.mipmap.active);
                                        detailforest2.get(position-1)[11]="true";
                                       detailforest2.get(position-1)[13]=(Integer.parseInt(detailforest2.get(position-1)[13])+1)+"";

                                        thumbup_num.setText(detailforest2.get(position-1)[13]);
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(getContext(),"点赞失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+detailforest2.get(position-1)[6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                           detailforest2.get(position-1)[11]="false";
                                           detailforest2.get(position-1)[13]=(Integer.parseInt(detailforest2.get(position-1)[13])-1)+"";

                                            thumbup_num.setText(detailforest2.get(position-1)[13]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(getContext(),"取消点赞失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });
                is_follow.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(detailforest2.get(position-1)[8].equals("false")){
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest2.get(position-1)[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.active_3);
                                           detailforest2.get(position-1)[8]="true";
                                           detailforest2.get(position-1)[3]=(Integer.parseInt(detailforest2.get(position-1)[3])+1)+"";

                                            follow_num.setText(detailforest2.get(position-1)[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(getContext(),"点赞失败",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消点赞失败");
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest2.get(position-1)[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                           detailforest2.get(position-1)[8]="false";
                                           detailforest2.get(position-1)[3]=(Integer.parseInt(detailforest2.get(position-1)[3])-1)+"";

                                            follow_num.setText(detailforest2.get(position-1)[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(getContext(),"取消关注失败",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消关注失败");
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                });


                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d("data[2]1",detailforest2.get(position-1)[2]);
                      Intent intent= CommentActivity.newIntent(getActivity(),detailforest2.get(position-1));
                        startActivity(intent);
                    }
                });
                background_image_url.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        for(int k=0;k<detailforest.length;k++){
                            if(detailforest2.get(position-1)[5].equals(detailforest[k][7])){
                                Intent intent = Page2_DetailAllForestsActivity.newIntent(getContext(), detailforest[k]);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }


            public void bind(int position){
                        this.position=position;
                        content.setText(detailforest2.get(position-1)[1]);
                        created_timestamp.setText(detailforest2.get(position-1)[2]);
                        forest_name.setText(detailforest2.get(position-1)[5]);
                        follow_num.setText(detailforest2.get(position-1)[3]);
                        reply_num.setText(detailforest2.get(position-1)[12]);
                        thumbup_num.setText(detailforest2.get(position-1)[13]);
                        hole_id.setText("#" +detailforest2.get(position-1)[6]);
                       // Log.d(TAG,detailforest2.get(position-1)[8]);
                       // Log.d(TAG,detailforest2.get(position-1)[10]);
                       // Log.d(TAG,detailforest2.get(position-1)[11]);

                        if (detailforest2.get(position-1)[8].equals("true")) {
                            is_follow.setImageResource(R.mipmap.active_3);
                        }
                        if(detailforest2.get(position-1)[9].equals("true")){
                            more_1.setImageResource(R.mipmap.vector6);
                            more_2.setText("删除");
                        }
                        if (detailforest2.get(position-1)[10].equals("true")) {
                            is_reply.setImageResource(R.mipmap.active_2);
                        }
                        if (detailforest2.get(position-1)[11].equals("true")) {
                            is_thumbup.setImageResource(R.mipmap.active);
                        }
                        if(detailforest2.get(position-1)[0].equals("")){
                        }else{
                            RoundedCorners roundedCorners = new RoundedCorners(16);
                            RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);

                            Glide.with(getActivity())
                                    .load(detailforest2.get(position-1)[0])
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .apply(options1)
                                    .into(background_image_url);
                        }
            }

        }
        public ForestHoleAdapter(){

        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {


            if (viewType ==ITEM_TYPE_HEADER) {
                return new HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.page2fragment_head,parent,false ));
            } else if (viewType == ITEM_TYPE_CONTENT) {
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.foresthole_model,parent,false ));
            }
            return null;

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);

            if (holder instanceof HeadHolder) {
                ((ForestHoleAdapter.HeadHolder)holder).bind(position);
            } else if (holder instanceof ViewHolder) {
                ((ForestHoleAdapter.ViewHolder) holder).bind(position);
            }
        }
        @Override
        public int getItemCount() {
            return listsize+1;
        }
    }
}
