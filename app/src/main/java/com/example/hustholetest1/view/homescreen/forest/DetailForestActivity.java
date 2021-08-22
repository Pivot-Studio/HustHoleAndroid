package com.example.hustholetest1.view.homescreen.forest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hustholetest1.model.BlurTransformation;
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.JointBitmapView;
import com.example.hustholetest1.model.MaxHeightRecyclerView;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.model.TransparentRefreshHeader;
import com.example.hustholetest1.network.CommenRequestManager;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.network.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.example.hustholetest1.view.homescreen.publishhole.PublishHoleActivity;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class DetailForestActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ImageView back,head,head_2,transformblock;
    private TextView mTitlebarTextTv;
    private JSONArray jsonArray2;
    private ArrayList<String[]> mDetailforestHoleslist=new ArrayList<>();
   // private TextView title;
    private String[] data;
    private String data_2;
    private RequestInterface request;
    private ForestHoleAdapter mForestHoleAdapter;
    private MaxHeightRecyclerView recyclerView;
    private FloatingActionButton addhole;
    private static final String key="key_1";
    private static final String key_2="key_2";
    private int mStartingLoadId = 0;
    private RefreshLayout mRefreshConditionRl, mLoadMoreCondotionRl;
    private int CONSTANT_STANDARD_LOAD_SIZE = 20;
    private  ConstraintLayout titlebar,mDetailForestCl;
    private Float mDistanceY = 0.0f;
    private Window window;
    private int headHeight=0;
    private RecyclerView.OnScrollListener mOnscrollListener;
    private Boolean mIfFirstLoad=true;
    private Boolean mPrestrainCondition=false;
    private Boolean mDeleteCondition=false;
    private Boolean more_condition=false;
    private  ConstraintLayout mMoreWhatCl;
    private RecyclerView.OnScrollListener mOnscrollListener2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailforest);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.Grayscale_200), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mDetailForestCl=(ConstraintLayout)findViewById(R.id.cl_detailforest);

        back = (ImageView) findViewById(R.id.iv_titlebartransparent_back);
        head_2=(ImageView)findViewById(R.id.iv_detailforest_cover);
        mTitlebarTextTv=(TextView)findViewById(R.id.tv_titlebartransparent_title);

        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.srl_detailforest_loadmore);
        refreshLayout.setRefreshHeader(new TransparentRefreshHeader(DetailForestActivity.this));
        ClassicsFooter mclassicsfooter=new ClassicsFooter(DetailForestActivity.this);
        mclassicsfooter.setBackgroundColor(getResources().getColor(R.color.GrayScale_95));
        refreshLayout.setRefreshFooter(mclassicsfooter);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                mRefreshConditionRl =refreshlayout;
                mStartingLoadId=0;
                update();
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });

//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                if(mIfFirstLoad){
                    refreshlayout.finishLoadMore();
                }else {
                    if (mPrestrainCondition == false) {
                        mLoadMoreCondotionRl = refreshlayout;
                        mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                        update();
                    } else {
                        mLoadMoreCondotionRl = refreshlayout;
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TokenInterceptor.getContext(DetailForestActivity.this);
        retrofit= RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);//创建接口实例
        data=getIntent().getStringArrayExtra(key);
        addhole=(FloatingActionButton)findViewById(R.id.fab_detailforest_publishhole);
        addhole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckingToken.IfTokenExist()) {
                    //关闭掉对话框,拿到对话框的对象
                    Intent intent = PublishHoleActivity.newIntent(DetailForestActivity.this, data[7]);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
                }
            }
        });
        mForestHoleAdapter=new ForestHoleAdapter();
        if(data==null){
            addhole.setVisibility(View.INVISIBLE);
            data_2=getIntent().getStringExtra(key_2);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<ResponseBody> call2 = request.detailforest(data_2);//进行封装
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String json = "null";
                            if (response.body() != null) {
                                try {
                                    data=new String[8];
                                    json = response.body().string();
                                    JSONObject jsonObject = new JSONObject(json);
                                    JSONArray jsonArray0 = jsonObject.getJSONArray("forests");
                                    JSONObject sonObject = jsonArray0.getJSONObject(0);
                                    data[0] = sonObject.getString("background_image_url");
                                    data[1] = sonObject.getString("cover_url");
                                    data[2] = sonObject.getString("description");
                                    data[3] = sonObject.getInt("forest_id") + "";
                                    data[4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                                    data[5] = sonObject.getBoolean("joined")+"";
                                    data[6] = sonObject.getString("last_active_time");
                                    data[7] = sonObject.getString("name");
                                    update();
                                    addhole.setVisibility(View.VISIBLE);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(DetailForestActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }else{
            update();
        }
        recyclerView = (MaxHeightRecyclerView) findViewById(R.id.rv_detailforest);
        WindowManager wm = (WindowManager)DetailForestActivity.this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);
        int[] location=new int[2];
        back.getLocationOnScreen(location);
         titlebar=(ConstraintLayout) findViewById(R.id.include2);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        titlebar.measure(w, h);
        int height = titlebar.getMeasuredHeight();
        int bb=DetailForestActivity.this.getResources().getDisplayMetrics().densityDpi;
        recyclerView.setMaxHeight(dm.heightPixels-height-20);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailForestActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    public static void transparentStatusBar(Window window) {
        /**
         * 透明状态栏方法(SDK_INT >= 21)
         * */
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void close(View view) {
        finish();
    }




    private void update(){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                //Call<ResponseBody> call = request.detailholes(data[3],mStartingLoadId,CONSTANT_STANDARD_LOAD_SIZE,false);//进行封装
                Call<ResponseBody> call = request.detailholes2("http://hustholetest.pivotstudio.cn/api/forests/"+data[3]+"/holes?start_id="+mStartingLoadId+"&list_size="+CONSTANT_STANDARD_LOAD_SIZE+"&is_last_active=true");//进行封装

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }


                            if(mRefreshConditionRl!=null){
                                mDetailforestHoleslist=new ArrayList<>();
                            }
                            if(mDeleteCondition){
                              mDeleteCondition=false;
                                mDetailforestHoleslist=new ArrayList<>();
                            }
                            //Log.e(TAG, "token2："+json);
                            if(json.equals("[]")&&mLoadMoreCondotionRl!=null){
                               Toast.makeText(DetailForestActivity.this,"加载到底辣",Toast.LENGTH_SHORT).show();
                               // if(mStartingLoadId!=0) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                               // }
                                    mLoadMoreCondotionRl.finishLoadMore();
                                    mLoadMoreCondotionRl=null;
                                /*if(mIfFirstLoad==true){
                                    recyclerView.setAdapter(mForestHoleAdapter);
                                }
                                */
                            }else{
                            jsonArray2 = new JSONArray(json);
                            new DownloadTask().execute();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        Toast.makeText(DetailForestActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();

                        if(mRefreshConditionRl !=null){
                            mRefreshConditionRl.finishRefresh();
                            mRefreshConditionRl =null;
                            recyclerView.removeOnScrollListener(mOnscrollListener);
                            //recyclerView.addOnScrollListener(null);


                            if(mIfFirstLoad){
                                //recyclerView.setAdapter(mForestHoleAdapter);
                               // mIfFirstLoad=false;
                            }else{
                              //  mForestHoleAdapter.notifyDataSetChanged();
                            }

                           // mForestHoleAdapter.notifyDataSetChanged();
                            mDistanceY=0.0f;
                            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });
                        } else if(mLoadMoreCondotionRl!=null){
                            mLoadMoreCondotionRl.finishLoadMore();
                            mLoadMoreCondotionRl=null;
                            mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                            if(mPrestrainCondition==true){
                                mPrestrainCondition=false;
                               // mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                                //  mForestHoleAdapter.notifyDataSetChanged();
                            }
                           // mForestHoleAdapter.notifyDataSetChanged();
                        }else if(mPrestrainCondition==true){
                            mPrestrainCondition=false;
                            mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                          //  mForestHoleAdapter.notifyDataSetChanged();
                        }else{
                           // mIfFirstLoad=false;
                          //  recyclerView.setAdapter(mForestHoleAdapter);
                        }
                    }


                });
            }
        }).start();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            if(mRefreshConditionRl !=null){
                mRefreshConditionRl.finishRefresh();
                mRefreshConditionRl =null;
                recyclerView.removeOnScrollListener(mOnscrollListener);
                //recyclerView.addOnScrollListener(null);


                if(mIfFirstLoad){
                    recyclerView.setAdapter(mForestHoleAdapter);
                    mIfFirstLoad=false;
                }else{
                    mForestHoleAdapter.notifyDataSetChanged();
                }

                mForestHoleAdapter.notifyDataSetChanged();
                mDistanceY=0.0f;
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            } else if(mLoadMoreCondotionRl!=null){
                mLoadMoreCondotionRl.finishLoadMore();
                mLoadMoreCondotionRl=null;
                mForestHoleAdapter.notifyDataSetChanged();
            }else if(mPrestrainCondition==true){
                  mPrestrainCondition=false;
                  mForestHoleAdapter.notifyDataSetChanged();
            }else{
                mIfFirstLoad=false;
            recyclerView.setAdapter(mForestHoleAdapter);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                for(int f=0;f<jsonArray2.length();f++) {
                    JSONObject sonObject = jsonArray2.getJSONObject(f);
                    String[] singleHole=new String[14];
                    //detailforest[f][0] = sonObject.getString("background_image_url");
                   singleHole[1] = sonObject.getString("content");
                   singleHole[2] = sonObject.getString("created_timestamp");
                   singleHole[3] = sonObject.getInt("follow_num")+"";
                    //detailforest[f][4] = sonObject.getInt("forest_id")+"";
                   singleHole[5] = data[7];
                   singleHole[6] = sonObject.getInt("hole_id")+"";
                    //detailforest2[f][1] = sonObject.getString("image");
                   singleHole[8] = sonObject.getBoolean("is_follow")+"";
                   singleHole[9] = sonObject.getBoolean("is_mine")+"";
                   singleHole[10] = sonObject.getBoolean("is_reply")+"";
                   singleHole[11] = sonObject.getBoolean("is_thumbup")+"";
                   singleHole[12] = sonObject.getInt("reply_num")+"";
                   singleHole[13] = sonObject.getInt("thumbup_num")+"";
                   mDetailforestHoleslist.add(singleHole);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class ForestHoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
      //  private Boolean more_condition=false;
       // private  ConstraintLayout morewhat0;

        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_BOTTOM = 2;
        private int mHeaderCount=1;//头部View个数
        //private Boolean more_condition=false;
        //private  ConstraintLayout morewhat0;
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



        public class HeadHolder extends RecyclerView.ViewHolder{ ;
            private TextView title,hole_and_number,content;
            private Button button,greenline;
            private ImageView head;
            public HeadHolder(View view){
                super(view);
                title=(TextView)view.findViewById(R.id.tv_detailforesthead_title);
                hole_and_number=(TextView)view.findViewById(R.id.tv_detailforesthead_number);
                content=(TextView)view.findViewById(R.id.tv_detailforesthead_content);
                button=(Button)view.findViewById(R.id.btn_detailforesthead_join);
                head=(ImageView)view.findViewById(R.id.iv_detailforesthead_icon);
                greenline=(Button)view.findViewById(R.id.btn_detailforesthead_greenline);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if (data[5].equals("true")) {
                                View mView = View.inflate(getApplicationContext(), R.layout.dialog_forestquitnotice, null);
                                // mView.setBackgroundResource(R.drawable.homepage_notice);
                                //设置自定义的布局
                                //mBuilder.setView(mView);
                                Dialog dialog = new Dialog(DetailForestActivity.this);
                                dialog.setContentView(mView);
                                TextView no = (TextView) mView.findViewById(R.id.tv_dialogforestquit_notquit);
                                TextView yes = (TextView) mView.findViewById(R.id.tv_dialogforestquit_quit);
                                dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
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

                                                Call<ResponseBody> call2 = request.delete("http://hustholetest.pivotstudio.cn/api/forests/quit/" + data[3]);//进行封装
                                                call2.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        Toast.makeText(DetailForestActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                        data[5] = "false";
                                                        button.setPadding(30, 5, 6, 6);
                                                        button.setBackground(getDrawable(R.drawable.forest_button));
                                                        button.setText("加入");
                                                        button.setTextColor(getResources().getColor(R.color.GrayScale_100));
                                                        Drawable homepressed = getResources().getDrawable(R.mipmap.group243, null);
                                                        homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                                                        button.setCompoundDrawables(homepressed, null, null, null);
                                                        dialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        Toast.makeText(DetailForestActivity.this, R.string.network_quitfailture, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                dialog.show();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call23 = request.join("http://hustholetest.pivotstudio.cn/api/forests/join/" + data[3]);//进行封装
                                        call23.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                Toast.makeText(DetailForestActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                                data[5] = "true";

                                                button.setPadding(0, 0, 0, 0);
                                                //button.setPadding(-30,-5,-6,-6);
                                                //button=(Button)view.findViewById(R.id.rectangle_4);
                                                button.setBackground(getDrawable(R.drawable.forest_button_white));
                                                button.setText("已加入");
                                                // button.setGravity(Gravity.CENTER_VERTICAL);
                                                button.setCompoundDrawables(null, null, null, null);
                                                button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(DetailForestActivity.this, R.string.network_joinfailture, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }else{
                            Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }

                    }
                });
            }
            public void bind(int position){
                window = getWindow();
                transparentStatusBar(window);
                    //RecyclerView设置滑动监听
                mOnscrollListener=new RecyclerView.OnScrollListener() {
                    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);


                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        //滑动的距离
                        int bb=DetailForestActivity.this.getResources().getDisplayMetrics().densityDpi;
                            mDistanceY += dy;
                        Log.d("Y",dy+"");
                            if((mDistanceY*160/bb) <0){
                                mDistanceY =0.0f;
                            }
                        Log.d("mDis",mDistanceY+"");
                        //toolbar的高度
                        if(headHeight==0) {
                            headHeight = greenline.getBottom()+30;
                        }
                        Log.d("headHeight",headHeight+"");
                        //当滑动的距离 <= toolbar高度的时候，改变Toolbar背景色的透明度，达到渐变的效果
                        if ((mDistanceY*160/bb) <= headHeight) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                //设置状态栏透明
                                window.setStatusBarColor(Color.TRANSPARENT);
                            }
                            float d = (mDistanceY*160/bb) / headHeight;
                            //int i = Double.valueOf(d * 255).intValue();    //i 有可能小于0
                            Log.d("d",d+"");
                            window.setStatusBarColor(Color.parseColor("#"+intToHex((int)(d*255))+"4B9F79"));
                            titlebar.setBackgroundColor(Color.parseColor("#"+intToHex((int)(d*255))+"4B9F79"));
                            mTitlebarTextTv.setAlpha(d);
                            mTitlebarTextTv.setText(data[7]);
                            back.setAlpha(1f);
                        } else {
                            if (Build.VERSION.SDK_INT >= 21) {
                                window.setStatusBarColor(getResources().getColor(R.color.HH_BandColor_1));
                                titlebar.setAlpha(1f);
                                titlebar.setBackgroundColor(getResources().getColor(R.color.HH_BandColor_1));
                                mTitlebarTextTv.setAlpha(1f);
                            }
                        }
                    }
                };
                recyclerView.addOnScrollListener(mOnscrollListener);





                title.setText(data[7]);
                hole_and_number.setText(data[4]);
                content.setText(data[2]);
                if(data[5].equals("false")){
                    button.setPadding(30,5,6,6);
                    button.setBackground(getDrawable(R.drawable.forest_button));
                    button.setText("加入");
                    button.setTextColor(getResources().getColor(R.color.GrayScale_100));
                    Drawable homepressed=getResources().getDrawable(R.mipmap.group243,null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    button.setCompoundDrawables(homepressed,null,null,null);
                }else{
                    //button=(Button)view.findViewById(R.id.btn_detailforesthead_join);
                    button.setBackground(getDrawable(R.drawable.forest_button_white));
                    button.setPadding(0,0,0,0);
                    button.setText("已加入");
                    button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                    // button.setApp(Page2_AllForestsActivity.this,R.style.button_2);
                }
                RoundedCorners roundedCorners = new RoundedCorners(16);
                RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);
                Glide.with(DetailForestActivity.this)
                        .load(data[0])
                        .apply(options1)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(head);
                 //TODO  这样你就可以拿到这个bitmap了
                Glide.with(DetailForestActivity.this)
                        .asBitmap()
                        .load(data[1])
                        .transform(new BlurTransformation(DetailForestActivity.this,24))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {



                                int width =resource.getWidth();
                                int height = resource.getHeight()*2;
                                //创建一个空的Bitmap(内存区域),宽度等于第一张图片的宽度，高度等于两张图片高度总和
                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                //将bitmap放置到绘制区域,并将要拼接的图片绘制到指定内存区域

                                ColorDrawable drawable = new ColorDrawable(Color.parseColor("#F3F3F3"));
                                Bitmap bitmapblock = Bitmap.createBitmap(resource.getWidth(),resource.getHeight(),Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmapblock);
                                drawable.draw(canvas);


                                Canvas canvas2 = new Canvas(bitmap);
                                canvas2.drawBitmap(resource, 0, 0, null);
                                canvas2.drawBitmap(bitmapblock, 0, resource.getHeight(), null);


                                Drawable drawable2 = new BitmapDrawable(bitmap);
                               // drawable.setBounds(0,0,0,dm.heightPixels/2);
                                mDetailForestCl.setBackground(drawable2);

                               // mDetailForestCl.setForegroundGravity(View.SCROLL_INDICATOR_TOP);
                            }
                        });

                int result = 0;
                //获取状态栏高度的资源id
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    result = getResources().getDimensionPixelSize(resourceId);
                }
                head_2.setPadding(0, -result, 0, 0);
                //fullScreen(DetailForestActivity.this);
            }
        }

       public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content,created_timestamp,follow_num,reply_num,thumbup_num,hole_id,more_2;
            private ImageView is_follow,is_reply,is_thumbup,more,more_1;
            private ConstraintLayout morewhat,thumbup,follow;
            private int position;
            private Boolean thumbupCondition=false,followCondition=false;

            public ViewHolder(View view) {
                super(view);
                thumbup=(ConstraintLayout)view.findViewById(R.id.cl_detailforest_thumbup);
                follow=(ConstraintLayout)view.findViewById(R.id.cl_detailforest_follow);


                content=(TextView)view.findViewById(R.id.tv_itemdetailforest_content);
                created_timestamp=(TextView)view.findViewById(R.id.tv_itemdetailforest_time);
               // forest_name=(TextView)view.findViewById(R.id.textView32);
                thumbup_num=(TextView)view.findViewById(R.id.tv_itemdetailforest_thumbupnumber);
                reply_num=(TextView)view.findViewById(R.id.tv_itemdetailforest_replynumber);
                follow_num =(TextView)view.findViewById(R.id.tv_itemdetailforest_follownumber);
                hole_id=(TextView)view.findViewById(R.id.tv_itemdetailforest_title);
               // background_image_url=(ImageView)view.findViewById(R.id.imageView12);
                is_thumbup =(ImageView)view.findViewById(R.id.iv_itemdetailforest_thumbup);
                is_reply=(ImageView)view.findViewById(R.id.iv_itemdetailforest_reply);
                is_follow=(ImageView)view.findViewById(R.id.iv_itemdetailforest_follow);

                more=(ImageView)view.findViewById(R.id.iv_itemdetailforest_more);
                more_1=(ImageView)view.findViewById(R.id.iv_itemdetailforest_moreicon);
                more_2=(TextView)view.findViewById(R.id.tv_itemdetailforest_moretext);
                morewhat=(ConstraintLayout)view.findViewById(R.id.cl_itemdetailforest_morelist);
                morewhat.setVisibility(View.INVISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(more_condition==false){
                            morewhat.setVisibility(View.VISIBLE);
                            mMoreWhatCl=morewhat;
                            more_condition=true;
                        }else{
                            mMoreWhatCl.setVisibility(View.INVISIBLE);
                            morewhat.setVisibility(View.VISIBLE);
                            mMoreWhatCl=morewhat;
                        }
                        if (mOnscrollListener2 != null) {
                            recyclerView.removeOnScrollListener(mOnscrollListener2);
                        }
                        mOnscrollListener2 = new RecyclerView.OnScrollListener() {
                            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                //super.onScrollStateChanged(recyclerView, newState);
                                mMoreWhatCl.setVisibility(View.INVISIBLE);
                                more_condition = false;
                               recyclerView.removeOnScrollListener(mOnscrollListener2);
                            }
                        };
                        recyclerView.addOnScrollListener(mOnscrollListener2);
                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        if(CheckingToken.IfTokenExist()) {
                            RemoveOnScrollListener();
                           // morewhat.setVisibility(View.INVISIBLE);
                           // more_condition = false;
                            if (mDetailforestHoleslist.get(position - 1)[9].equals("true")) {
                                new Thread(new Runnable() {//加载纵向列表标题
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.delete_hole(mDetailforestHoleslist.get(position - 1)[6]);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                Toast.makeText(DetailForestActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                                mStartingLoadId=0;
                                                mDeleteCondition=true;
                                                update();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(DetailForestActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                                mDeleteCondition = false;
                                            }
                                        });
                                    }
                                }).start();


                                //CommenRequestManager.DeleteRequest(DetailForestActivity.this, request, mDetailforestHoleslist.get(position - 1)[6]);

                                //finish();
                            } else {
                                CommenRequestManager.ReportRequest(DetailForestActivity.this, request, mDetailforestHoleslist.get(position - 1)[6], "-1");
                            }
                        }else{
                            Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });



                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        RemoveOnScrollListener();
                        Intent intent= CommentListActivity.newIntent(DetailForestActivity.this, mDetailforestHoleslist.get(position-1));
                        startActivity(intent);
                    }
                });
                thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if(thumbupCondition==false) {
                                thumbupCondition=true;
                                if (mDetailforestHoleslist.get(position - 1)[11].equals("false")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + mDetailforestHoleslist.get(position - 1)[6] + "/-1");//进行封装
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    is_thumbup.setImageResource(R.mipmap.active);
                                                    mDetailforestHoleslist.get(position - 1)[11] = "true";
                                                    mDetailforestHoleslist.get(position - 1)[13] = (Integer.parseInt(mDetailforestHoleslist.get(position - 1)[13]) + 1) + "";
                                                    thumbupCondition=false;
                                                    thumbup_num.setText(mDetailforestHoleslist.get(position - 1)[13]);
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    thumbupCondition=false;
                                                    Toast.makeText(DetailForestActivity.this, R.string.network_thumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + mDetailforestHoleslist.get(position - 1)[6] + "/-1");//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    is_thumbup.setImageResource(R.mipmap.inactive);
                                                    mDetailforestHoleslist.get(position - 1)[11] = "false";
                                                    mDetailforestHoleslist.get(position - 1)[13] = (Integer.parseInt(mDetailforestHoleslist.get(position - 1)[13]) - 1) + "";
                                                    thumbupCondition=false;
                                                    thumbup_num.setText(mDetailforestHoleslist.get(position - 1)[13]);
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    thumbupCondition=false;
                                                    Toast.makeText(DetailForestActivity.this, R.string.network_notthumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }else{
                            Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                follow.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if (followCondition == false){
                                followCondition=true;
                                if (mDetailforestHoleslist.get(position - 1)[8].equals("false")) {
                                    new Thread(new Runnable() {//加载纵向列表标题
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/" + mDetailforestHoleslist.get(position - 1)[6]);//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_follow.setImageResource(R.mipmap.active_3);
                                                        mDetailforestHoleslist.get(position - 1)[8] = "true";
                                                        mDetailforestHoleslist.get(position - 1)[3] = (Integer.parseInt(mDetailforestHoleslist.get(position - 1)[3]) + 1) + "";
                                                        followCondition = false;
                                                        follow_num.setText(mDetailforestHoleslist.get(position - 1)[3]);
                                                    }else{
                                                        followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.body() != null) {
                                                            try {
                                                                json = response.body().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(DetailForestActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else{
                                                            Toast.makeText(DetailForestActivity.this,"过于频繁请求！",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    followCondition=false;
                                                    Toast.makeText(DetailForestActivity.this, R.string.network_followfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/" + mDetailforestHoleslist.get(position - 1)[6]);//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_follow.setImageResource(R.mipmap.inactive_3);
                                                        mDetailforestHoleslist.get(position - 1)[8] = "false";
                                                        mDetailforestHoleslist.get(position - 1)[3] = (Integer.parseInt(mDetailforestHoleslist.get(position - 1)[3]) - 1) + "";
                                                        followCondition = false;
                                                        follow_num.setText(mDetailforestHoleslist.get(position - 1)[3]);
                                                    }else{
                                                        followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.body() != null) {
                                                            try {
                                                                json = response.body().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(DetailForestActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else{
                                                            Toast.makeText(DetailForestActivity.this,"过于频繁请求！",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    followCondition=false;
                                                    Toast.makeText(DetailForestActivity.this, R.string.network_notfollowfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();

                                }
                            }
                        }else{
                            Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }


            public void bind(int position){
                this.position=position;
                content.setText(mDetailforestHoleslist.get(position-1)[1]);
                created_timestamp.setText(mDetailforestHoleslist.get(position-1)[2]);
                follow_num.setText(mDetailforestHoleslist.get(position-1)[3]);
                reply_num.setText(mDetailforestHoleslist.get(position-1)[12]);
                thumbup_num.setText(mDetailforestHoleslist.get(position-1)[13]);
                hole_id.setText("#" + mDetailforestHoleslist.get(position-1)[6]);
                Log.d(TAG, mDetailforestHoleslist.get(position-1)[8]);
                Log.d(TAG, mDetailforestHoleslist.get(position-1)[10]);
                Log.d(TAG, mDetailforestHoleslist.get(position-1)[11]);

                if (mDetailforestHoleslist.get(position-1)[8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                    //is_follow.setImageDrawable(R.drawable.hole_active_1);
                }else{
                    is_follow.setImageResource(R.mipmap.inactive_3);
                }
                if (mDetailforestHoleslist.get(position-1)[9].equals("true")) {
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }else{
                    more_1.setImageResource(R.mipmap.vector4);
                    more_2.setText("举报");
                }
                if (mDetailforestHoleslist.get(position-1)[10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }else{
                    is_reply.setImageResource(R.mipmap.inactive_2);
                }
                if (mDetailforestHoleslist.get(position-1)[11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }else{
                    is_thumbup.setImageResource(R.mipmap.inactive);
                }

            }

        }

        public ForestHoleAdapter(){
            Log.d(TAG,"数据传入了");
            //this.events=events;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


            if (viewType ==ITEM_TYPE_HEADER) {
                return new DetailForestActivity.ForestHoleAdapter.HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_detailforesthead,parent,false));
            } else if (viewType == ITEM_TYPE_CONTENT) {
                return new DetailForestActivity.ForestHoleAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_detailforest,parent,false));
            }



            /*View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_detailforest,parent,false );
            Log.d(TAG,"已经创建适配器布局");
           ForestHoleAdapter.ViewHolder holder=new ForestHoleAdapter.ViewHolder(view);
            return holder;
            *
             */
            return null;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);
            if (holder instanceof DetailForestActivity.ForestHoleAdapter.HeadHolder) {
                ((DetailForestActivity.ForestHoleAdapter.HeadHolder)holder).bind(position);
            } else if (holder instanceof DetailForestActivity.ForestHoleAdapter.ViewHolder) {
                ((DetailForestActivity.ForestHoleAdapter.ViewHolder) holder).bind(position);

            }
            if(position==mDetailforestHoleslist.size()-4&&(mDetailforestHoleslist.size()%CONSTANT_STANDARD_LOAD_SIZE==0)){
                mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                mPrestrainCondition=true;
                update();
            }
           // Log.d(TAG,"已经设置单个信息了"+position);
            //holder.bind(position);

        }
        @Override
        public int getItemCount() {
            return mDetailforestHoleslist.size()+1;
        }
    }



    public static Intent newIntent(Context packageContext, String[] data){
        Intent intent = new Intent(packageContext, DetailForestActivity.class);
        intent.putExtra(key,data);
        return intent;
    }
    public static Intent newIntent(Context packageContext,String data){
        Intent intent = new Intent(packageContext, DetailForestActivity.class);
        intent.putExtra(key_2,data);
        return intent;
    }
    private void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
//                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
//                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }
    private static String intToHex(int n) {
        if (n == 0) {
            return "00";
        }
        StringBuilder sb = new StringBuilder(8);
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            sb = sb.append(b[n % 16]);
            n = n / 16;
        }
        a = sb.reverse().toString();
        return a.length() < 2 ? "0" + a : a;
    }
    private void RemoveOnScrollListener() {
        if (mOnscrollListener2 != null) {
           recyclerView.removeOnScrollListener(mOnscrollListener2);
        }
        if(mMoreWhatCl!=null) {
            mMoreWhatCl.setVisibility(View.INVISIBLE);
            more_condition = false;
        }
    }
}
