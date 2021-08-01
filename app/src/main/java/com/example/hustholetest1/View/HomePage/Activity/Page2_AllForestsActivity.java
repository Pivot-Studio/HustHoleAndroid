package com.example.hustholetest1.View.HomePage.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hustholetest1.Model.Forest;
import com.example.hustholetest1.Model.GlideRoundTransform;
import com.example.hustholetest1.Model.RequestInterface;
import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.Model.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.fragment.Page2Fragment;
import com.example.hustholetest1.View.RegisterAndLogin.Activity.RegisterActivity;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Page2_AllForestsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private ImageView back;
    private TextView title;
    private Button build;
    private RequestInterface request;
    private String[] title_list_2;//暂时储存所有纵向列表标题
    private String[][][] forest_list_0;//暂时储存所有文字
    private Bitmap[][][] bitmaps;//暂时储存所有图片
    private RefreshLayout refreshlayout1;
    private int tab2;//用于记录加载的横向整体数目
    private int number1[];//记录加载的各个横向列表的数目
    private boolean[] networkcondition;//记录是否加载过了，加载过了直接从forest_list_0和bitmaps中拿recyclerView翻阅达到一定长度再返回会重新加载
    private static final String key="key_1";
    private Boolean refreshcondition=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2_allforests);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(Page2_AllForestsActivity.this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(Page2_AllForestsActivity.this));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout1=refreshlayout;
                refreshcondition=true;
                Update0();

//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000);//false
                //传入false表示加载失败
            }
        });





        back= (ImageView) findViewById(R.id.backView);
        title=(TextView)findViewById(R.id.title);
        title.setText("发现小树林");
        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Page2_AllForestsActivity.this, HomePageActivity.class);
                                        startActivity(intent);
                                    }
                                });

       /* RefreshLayout refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(Page2_AllForestsActivity.this));
        // refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(4000/*,false*//*);
//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(4000);
                //传入false表示加载失败
            }
        });*/


        TokenInterceptor.getContext(Page2_AllForestsActivity.this);
        //TokenInterceptor.getContext(RegisterActivity.this);
        System.out.println("提交了context");
        retrofit = new Retrofit.Builder()
                .baseUrl("http://hustholetest.pivotstudio.cn/api/forests/")
                .client(com.example.hustholetest1.Model.OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.e(TAG, "token99：");
        request = retrofit.create(RequestInterface.class);//创建接口实例
        Log.e(TAG, "token100：");









        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { //表示未授权时
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Update0();
    }

    public void Update0(){
    new Thread(new Runnable() {//加载纵向列表标题
        @Override
        public void run() {
            Call<ResponseBody> call = request.getCall();//进行封装
            Log.e(TAG, "token2：");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = "null";
                    try {
                        if (response.body() != null) {
                            json = response.body().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String titlelist = null;
                    try {

                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray =jsonObject.getJSONArray("types");
                        title_list_2=new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            title_list_2[i] = jsonArray.getString(i);
                            System.out.println(""+ title_list_2[i]);
                        }
                        number1=new int[title_list_2.length+1];
                        Update();
                        tab2=-1;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {

                }


            });
        }
    }).start();
}
    public void Update(){//加载标题所对应的所以小树林
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String gg="type/"+title_list_2[cycle(true)]+"?start_id=0&list_size=10&is_last_active=false";
                    Call<ResponseBody> call = request.testUrl(gg);//进行封装
                    call.enqueue(new Callback<ResponseBody>() {
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                            System.out.println("总:" + json);

                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                //读取
                                JSONArray jsonArray = jsonObject.getJSONArray("forests");
                                Forest.setForest_list(cycle(false)+1, jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(cycle(false)!= title_list_2.length-1){
                               Update();
                            }else{
                                Update2();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }).start();
       // }

    }
    public int cycle(boolean ww){
        if(ww){ tab2++; } else {return tab2;}
        return tab2;
    }
public void Update2(){//加载热门小树林
    new Thread(new Runnable() {
        @Override
        public void run() {
            Call<ResponseBody> call2 = request.getCall2();//进行封装
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = "null";
                    try {
                        if (response.body() != null) {
                            json = response.body().string();
                        }
                         String titlelist = null;
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("forests");

                        Forest.setForest_list(0, jsonArray);

                        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Page2_AllForestsActivity.this);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(linearLayoutManager);


                        networkcondition=new boolean[title_list_2.length+1];
                        for(int a=0;a<title_list_2.length;a++){
                              networkcondition[a]=false;
                        }
                        forest_list_0=new String[title_list_2.length+1][][];
                        bitmaps=new Bitmap[title_list_2.length+1][][];
                        recyclerView.setAdapter(new AllForestsAdapter());
                        if(refreshcondition==true){
                            refreshlayout1.finishRefresh();
                            refreshcondition=false;
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                    // Log.e(TAG, "URLSASFSDGS"+background_image_url_list[0]);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {

                }
            });
        }
    }).start();

}
    public  class MyTaskParams2 {//加载图片所用
        int[] list1=new int[2];
        RecyclerView recyclerView;

        MyTaskParams2(int[] list1,RecyclerView recyclerView) {
            this.list1=list1;
            this.recyclerView=recyclerView;
        }
    }
    private class DownloadTask2 extends AsyncTask<MyTaskParams2, Void, Void> {//用于加载图片
        private int DT2number,DT2number2;
        private RecyclerView recyclerViewin;
        private JSONArray jsonArray;
        @Override
        protected void onPostExecute(Void unused) {
            //Downloadanswer(DT2number);
            number1[DT2number]++;
            System.out.println(DT2number+"468");
            if(jsonArray.length()==number1[DT2number]){
            recyclerViewin.setAdapter(new AllForestsDetailAdapter(DT2number,true));
                networkcondition[DT2number]=true;
            }
        }
        @Override
        protected Void doInBackground(MyTaskParams2...voids) {
            DT2number=voids[0].list1[0];
            DT2number2=voids[0].list1[1];
            recyclerViewin=voids[0].recyclerView;
            jsonArray=Forest.getForest_list(DT2number);
            Bitmap[] bitmapss=new Bitmap[2];
            try {
                    JSONObject sonObject=jsonArray.getJSONObject(DT2number2);
                URL url = new URL(sonObject.getString("background_image_url"));
                URL url2 = new URL(sonObject.getString("cover_url"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                InputStream inputStream = conn.getInputStream();
                bitmapss[0]= BitmapFactory.decodeStream(inputStream);
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                conn2.setConnectTimeout(5000);
                conn2.setRequestMethod("GET");
                InputStream inputStream2 = conn2.getInputStream();
                bitmapss[1]=BitmapFactory.decodeStream(inputStream2);
            } catch (IOException | JSONException  e) {
                e.printStackTrace();
            }
            bitmaps[DT2number][DT2number2]=bitmapss;
           return null;
        }


    }
    public  class MyTaskParams{//加载文字所用
        int a;
        RecyclerView recyclerView;
        MyTaskParams(int a,RecyclerView recyclerView) {
            this.a=a;
            this.recyclerView=recyclerView;
        }
    }
    private class DownloadTask extends AsyncTask<MyTaskParams, Void, Void> {//加载文字
        private int DT2number;
        private RecyclerView recyclerViewin;
        private JSONArray jsonArray;

        @Override
        protected void onPostExecute(Void unused) {
            //recyclerViewin.setAdapter(new AllForestsDetailAdapter(DT2number,false));
            recyclerViewin.setAdapter(new AllForestsDetailAdapter(DT2number,true));

        }
        @Override
        protected Void doInBackground(MyTaskParams...voids) {
            DT2number=voids[0].a;
            recyclerViewin=voids[0].recyclerView;
            jsonArray=Forest.getForest_list(DT2number);
             String[][] detailforest=new String[jsonArray.length()][8];
            try {
                for(int j=0;j<jsonArray.length();j++) {
                    JSONObject sonObject = jsonArray.getJSONObject(j);
                    detailforest[j][0] = sonObject.getString("background_image_url");
                    detailforest[j][1] = sonObject.getString("cover_url");
                    detailforest[j][2] = sonObject.getString("description");
                    detailforest[j][3] = sonObject.getInt("forest_id") + "";
                    detailforest[j][4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                    detailforest[j][5] = sonObject.getBoolean("joined") + "";
                    detailforest[j][6] = sonObject.getString("last_active_time");
                    detailforest[j][7] = sonObject.getString("name");
                }
            } catch ( JSONException  e) {
                e.printStackTrace();
            }
            forest_list_0[DT2number]=detailforest;
            return null;
        }
    }



        public class AllForestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                private Button build;
                public HeadHolder(View view){
                    super(view);
                    build=(Button)view.findViewById(R.id.build);
                    build.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Page2_AllForestsActivity.this, Page2_ApplyForestActivity.class);
                            startActivity(intent);
                        }
                    });

                }
                public void bind(int position){

                }
            }
            public class ViewHolder extends RecyclerView.ViewHolder {
                private TextView type;
                private RecyclerView recyclerViewin;
                private int position;
                public ViewHolder(View view) {
                    super(view);
                    type = (TextView) view.findViewById(R.id.textView45);
                    recyclerViewin = (RecyclerView) view.findViewById(R.id.recyclerView22);
                    LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(Page2_AllForestsActivity.this);
                    linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerViewin.setLayoutManager(linearLayoutManager2);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }


                public void bind(int position2) throws JSONException {
                    //if(networkcondition[position2]==false){
                        position2=position2-1;
                        MyTaskParams II=new MyTaskParams(position2, recyclerViewin);
                        new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,II);
                        JSONArray jsonArray=Forest.getForest_list(position2);


                     /*   bitmaps[position2]=new Bitmap[jsonArray.length()][];
                        for(int f=0;f<jsonArray.length();f++) {
                            int[] list1=new int[2];
                            list1[0]=position2;
                            list1[1]=f;
                            MyTaskParams2 II2=new MyTaskParams2(list1, recyclerViewin);
                            System.out.println(f+"424");
                            new DownloadTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,II2);

                        }
                    }else{
                        recyclerViewin.setAdapter(new AllForestsDetailAdapter(position2,true));
                    }*/



                    position=position2;
                    if(position2==0){
                        type.setText("热门小树林");
                    }else{
                        type.setText(title_list_2[position-1]);
                    }

                }

            }

            public AllForestsAdapter() {

            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if (viewType ==ITEM_TYPE_HEADER) {
                    return new AllForestsAdapter.HeadHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.page2_allforest_head,parent,false));
                } else if (viewType == ITEM_TYPE_CONTENT) {
                    return new AllForestsAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.forestdetailiconlist_model,parent,false));
                }
                return null;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof AllForestsAdapter.HeadHolder) {
                    ((AllForestsAdapter.HeadHolder)holder).bind(position);
                } else if (holder instanceof AllForestsAdapter.ViewHolder) {
                    try {
                        ((ViewHolder) holder).bind(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public int getItemCount() {
                return (title_list_2.length+2);
            }
        }
        public class AllForestsDetailAdapter extends RecyclerView.Adapter<AllForestsDetailAdapter.ViewHolder> {
              private int father_position;
              private boolean tab;


            class ViewHolder extends RecyclerView.ViewHolder {
                private ImageView background_image_url;
                private ImageView cover_url;
                private Button button;
                private TextView name;
                private TextView joined_and_hole;
                private TextView description;
                private String condition_if;
                ConstraintLayout next;

                private int position;
                public ViewHolder(View view)  {
                    super(view);

                    background_image_url=(ImageView)view.findViewById(R.id.imageView10);
                    cover_url=(ImageView)view.findViewById(R.id.imageView29);
                    name=(TextView)view.findViewById(R.id.textView29);
                    joined_and_hole=(TextView)view.findViewById(R.id.textView30);
                    description=(TextView)view.findViewById(R.id.textView31);
                    button=(Button)view.findViewById(R.id.rectangle_4);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(condition_if.equals("true")){
                                View mView = View.inflate(getApplicationContext(), R.layout.forest_quit_notice, null);
                                // mView.setBackgroundResource(R.drawable.homepage_notice);
                                //设置自定义的布局
                                //mBuilder.setView(mView);
                                Dialog dialog = new Dialog(Page2_AllForestsActivity.this);
                                dialog.setContentView(mView);
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                                TextView no = (TextView) mView.findViewById(R.id.textView47);
                                TextView yes = (TextView) mView.findViewById(R.id.textView46);
                                no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                Call<ResponseBody> call2 = request.delete( "http://hustholetest.pivotstudio.cn/api/forests/quit/"+forest_list_0[father_position][position][3]);//进行封装
                                                call2.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Toast.makeText(Page2_AllForestsActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                        condition_if="false";
                                                        forest_list_0[father_position][position][5]="false";
                                                        button.setPadding(30,5,6,6);
                                                        button.setBackground(getDrawable(R.drawable.forest_button));
                                                        button.setText("加入");
                                                        button.setTextColor(getResources().getColor(R.color.GrayScale_100));
                                                        Drawable homepressed=getResources().getDrawable(R.mipmap.group243,null);
                                                        homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                                                        button.setCompoundDrawables(homepressed,null,null,null);
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Toast.makeText(Page2_AllForestsActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                dialog.show();
                            }else{
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                Call<ResponseBody> call23= request.join( "http://hustholetest.pivotstudio.cn/api/forests/join/"+forest_list_0[father_position][position][3]);//进行封装
                                call23.enqueue(new Callback<ResponseBody>() {

                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        Toast.makeText(Page2_AllForestsActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                        forest_list_0[father_position][position][5]="true";
                                        condition_if="true";
                                        button.setPadding(0,0,0,0);
                                        //button.setPadding(-30,-5,-6,-6);
                                        //button=(Button)view.findViewById(R.id.rectangle_4);
                                        button.setBackground(getDrawable(R.drawable.forest_button_white));
                                        button.setText("已加入");
                                       // button.setGravity(Gravity.CENTER_VERTICAL);
                                        button.setCompoundDrawables(null,null,null,null);
                                        button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(Page2_AllForestsActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                    }
                                }).start();
                            }

                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = Page2_DetailAllForestsActivity.newIntent(Page2_AllForestsActivity.this,forest_list_0[father_position][position]);
                            startActivity(intent);
                        }
                    });
                }
                public void bind(int position)  {
                    this.position=position;
                    condition_if=forest_list_0[father_position][position][5];
                    if(tab) {
                        name.setText(forest_list_0[father_position][position][7]);
                        joined_and_hole.setText(forest_list_0[father_position][position][4]);
                        description.setText(forest_list_0[father_position][position][2]);
                        if(condition_if.equals("false")){
                            button.setPadding(30,5,6,6);
                            button.setBackground(getDrawable(R.drawable.forest_button));
                            button.setText("加入");
                            button.setTextColor(getResources().getColor(R.color.GrayScale_100));
                            Drawable homepressed=getResources().getDrawable(R.mipmap.group243,null);
                            homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());

                            button.setCompoundDrawables(homepressed,null,null,null);

                        }else{
                            button.setCompoundDrawables(null,null,null,null);
                            button.setPadding(0,0,0,0);
                            button.setBackground(getDrawable(R.drawable.forest_button_white));
                            button.setGravity(Gravity.CENTER);
                            button.setText("已加入");
                            button.setTextSize(14);
                            button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                            // button.setApp(Page2_AllForestsActivity.this,R.style.button_2);
                        }
                        RoundedCorners roundedCorners = new RoundedCorners(16);
                        RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);

                        Glide.with(Page2_AllForestsActivity.this)
                                .load(forest_list_0[father_position][position][0])
                                .apply(options1)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(background_image_url);


                        Glide.with(Page2_AllForestsActivity.this)
                                .load(forest_list_0[father_position][position][1])

                                .transform(new GlideRoundTransform(16))
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(cover_url);


                        //background_image_url.setImageBitmap(bitmaps[father_position][position][0]);
                        //Bitmap bitmap2 = bitmaps[father_position][position][1];
                        //cover_url.setImageBitmap(bitmap2);

                    }else{
                        button.setVisibility(View.INVISIBLE);
                        name.setText(forest_list_0[father_position][position][7]);
                        joined_and_hole.setText(forest_list_0[father_position][position][4]);
                        description.setText(forest_list_0[father_position][position][2]);
                    }
                }

            }

            public AllForestsDetailAdapter(int father_position_1,boolean tab) {
                father_position=father_position_1;
                 this.tab=tab;//用来判断是加载完全还是只加载了文字
                Log.d(TAG, "数据传入了elelelelelelele");

            }

            @Override
            public AllForestsDetailAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.forestdetailicon_model, parent, false);
                ViewHolder holder = null;
                holder = new ViewHolder(view);
                return holder;
            }

            @Override
            public void onBindViewHolder(AllForestsDetailAdapter.ViewHolder holder, int position) {
                    holder.bind(position);
            }

            @Override
            public int getItemCount() {
                    return forest_list_0[father_position].length;
            }
        }




    }
