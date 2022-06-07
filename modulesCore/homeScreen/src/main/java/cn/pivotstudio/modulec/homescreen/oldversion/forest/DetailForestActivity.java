package cn.pivotstudio.modulec.homescreen.oldversion.forest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken;
import cn.pivotstudio.modulec.homescreen.oldversion.model.MaxHeightRecyclerView;
import cn.pivotstudio.modulec.homescreen.oldversion.model.TransparentRefreshHeader;
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import cn.pivotstudio.modulec.homescreen.oldversion.network.TokenInterceptor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DetailForestActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ConstraintLayout back;
    private ImageView head,head_2,transformblock;
    private TextView mTitlebarTextTv;
    private JSONArray jsonArray2;
    private ArrayList<String[]> mDetailforestHoleslist=new ArrayList<>();
   // private TextView title;
    private String[] data;
    private String data_2;
    private RequestInterface request;
//    private ForestHoleAdapter mForestHoleAdapter;
    private MaxHeightRecyclerView recyclerView;
    private FloatingActionButton addhole;
    private static final String key="key_1";
    private static final String key_2="key_2";
    private int mStartingLoadId = 0,mLastLoadId;
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


    private ImageView mReturnIsThumbup,mReturnIsReply,mReturnIsFollow;
    private TextView mReturnThumbupNUmber,mReturnReplyNumber,mReturnFollowNumber;
    private int mReturnPosition;
    private int RESULTCODE_COMMENT=1,REQUESTCODE_COMMENT=4;

    private String mSendBackDateJoinCondtion;
    private int RESULTCODE_COMMENT_2=2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailforest);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.Grayscale_200), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mDetailForestCl=(ConstraintLayout)findViewById(R.id.cl_detailforest);

        back = (ConstraintLayout) findViewById(R.id.cl_titlebartransparent_back);
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
                getBack();
                finish();
            }
        });
        TokenInterceptor.getContext(DetailForestActivity.this);
        retrofit= RetrofitManager.getRetrofit();
        request=RetrofitManager.getRequest();
        //request = retrofit.create(RequestInterface.class);//创建接口实例
        data=getIntent().getStringArrayExtra(key);
        addhole=(FloatingActionButton)findViewById(R.id.fab_detailforest_publishhole);
        addhole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(CheckingToken.IfTokenExist()) {
                    //关闭掉对话框,拿到对话框的对象
                 //   Intent intent = PublishHoleActivity.newIntent(DetailForestActivity.this, data[7],data[3]);
                 //   startActivity(intent);
                }else{
                   // Intent intent=new Intent(DetailForestActivity.this, EmailVerifyActivity.class);
                  //  startActivity(intent);

                }
            }
        });
//        mForestHoleAdapter=new ForestHoleAdapter();
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
                            if(response.code()==200) {
                                String json = "null";
                                if (response.body() != null) {
                                    try {
                                        data = new String[8];
                                        json = response.body().string();
                                        JSONObject jsonObject = new JSONObject(json);
                                        JSONArray jsonArray0 = jsonObject.getJSONArray("forests");
                                        JSONObject sonObject = jsonArray0.getJSONObject(0);
                                        data[0] = sonObject.getString("background_image_url");
                                        data[1] = sonObject.getString("cover_url");
                                        data[2] = sonObject.getString("description");
                                        data[3] = sonObject.getInt("forest_id") + "";
                                        data[4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                                        data[5] = sonObject.getBoolean("joined") + "";
                                        data[6] = sonObject.getString("last_active_time");
                                        data[7] = sonObject.getString("name");
                                        update();
                                        addhole.setVisibility(View.VISIBLE);
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }else{
                                ErrorMsg.getErrorMsg(response,DetailForestActivity.this);
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



    @Override
    public void onBackPressed() {
        getBack();
        super.onBackPressed();

        //System.out.println("按下了back键   onBackPressed()");
    }

    private void getBack(){
        Intent data=new Intent();
        data.putExtra("JoinCondition",mSendBackDateJoinCondtion);
        //data.putExtra("FollowCondition",mSendBackDateFollowCondtion);
        setResult(RESULTCODE_COMMENT_2,data);
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
                Call<ResponseBody> call = request.detailholes2(RetrofitManager.API+"forests/"+data[3]+"/holes?start_id="+mStartingLoadId+"&list_size="+CONSTANT_STANDARD_LOAD_SIZE+"&is_descend=true");//进行封装

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }


                                if (mRefreshConditionRl != null) {
                                    mDetailforestHoleslist = new ArrayList<>();
                                }
                                if (mDeleteCondition) {
                                    mDeleteCondition = false;
                                    mDetailforestHoleslist = new ArrayList<>();
                                }
                                if (json.equals("[]") && mLoadMoreCondotionRl != null) {
                                    Toast.makeText(DetailForestActivity.this, "加载到底辣", Toast.LENGTH_SHORT).show();
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    mLoadMoreCondotionRl.finishLoadMore();
                                    mLoadMoreCondotionRl = null;
                                } else {
                                    jsonArray2 = new JSONArray(json);
                                    new DownloadTask().execute();
                                }
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            failureAction();
                            ErrorMsg.getErrorMsg(response,DetailForestActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        Toast.makeText(DetailForestActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                         failureAction();
                    }
                    private void failureAction(){

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
//                    recyclerView.setAdapter(mForestHoleAdapter);
                    mIfFirstLoad=false;
                }else{
//                    mForestHoleAdapter.notifyDataSetChanged();
                }

//                mForestHoleAdapter.notifyDataSetChanged();
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
//                mForestHoleAdapter.notifyDataSetChanged();
            }else if(mPrestrainCondition==true){
                  mPrestrainCondition=false;
//                  mForestHoleAdapter.notifyDataSetChanged();
            }else{
                mIfFirstLoad=false;
//            recyclerView.setAdapter(mForestHoleAdapter);
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
                  // singleHole[14]=sonObject.getString("role")+"";
                   mDetailforestHoleslist.add(singleHole);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
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
