package cn.pivotstudio.husthole.view.homescreen.forest;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import cn.pivotstudio.husthole.model.CheckingToken;
import cn.pivotstudio.husthole.model.Forest;
import cn.pivotstudio.husthole.model.GlideRoundTransform;
import cn.pivotstudio.husthole.model.MaxHeightRecyclerView;
import cn.pivotstudio.husthole.network.ErrorMsg;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.model.StandardRefreshHeader;
import cn.pivotstudio.husthole.network.RetrofitManager;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.view.emailverify.EmailVerifyActivity;
import cn.pivotstudio.husthole.view.homescreen.activity.HomeScreenActivity;
import cn.pivotstudio.husthole.view.homescreen.commentlist.CommentListActivity;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.wang.avi.AVLoadingIndicatorView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class AllForestsActivity extends AppCompatActivity {
    private MaxHeightRecyclerView recyclerView;

    private ConstraintLayout back;
    private TextView title;
    private Button build;
    private RequestInterface request;
    private Retrofit retrofit;
    private String[] title_list_2;//暂时储存所有纵向列表标题
    private String[][][] forest_list_0;//暂时储存所有文字
    private Bitmap[][][] bitmaps;//暂时储存所有图片
    private RefreshLayout refreshlayout1;
    private int tab2;//用于记录加载的横向整体数目
    private int number1[];//记录加载的各个横向列表的数目
    private boolean[] networkcondition;//记录是否加载过了，加载过了直接从forest_list_0和bitmaps中拿recyclerView翻阅达到一定长度再返回会重新加载
    private static final String key="key_1";
    private Boolean refreshcondition=false;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;

    private int RESULTCODE_COMMENT_2=2,REQUESTCODE_COMMENT=1;
    private Button mReturnJoinButton;
    private String mConditionIfJoined;
    private int mFatherClPosition,mClPosition;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if(resultCode!=RESULTCODE_COMMENT_2){
            return;
        }
        if(requestCode==REQUESTCODE_COMMENT) {
            String joinedCondition = data.getStringExtra("JoinCondition");
            if (joinedCondition!= null) {
                if(forest_list_0[mFatherClPosition][mClPosition][5].equals("true")&&joinedCondition.equals("false")){
                    //mConditionIfJoined="false";
                    forest_list_0[mFatherClPosition][mClPosition][5] = "false";
                    mReturnJoinButton.setPadding(30, 5, 6, 6);
                    mReturnJoinButton.setBackground(getDrawable(R.drawable.forest_button));
                    mReturnJoinButton.setText("加入");
                    mReturnJoinButton.setTextColor(getResources().getColor(R.color.GrayScale_100));
                    Drawable homepressed = getResources().getDrawable(R.mipmap.group243, null);
                    homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                    mReturnJoinButton.setCompoundDrawables(homepressed, null, null, null);
                }else if (forest_list_0[mFatherClPosition][mClPosition][5].equals("false")&&joinedCondition.equals("true")){
                   // mConditionIfJoined="true";
                    forest_list_0[mFatherClPosition][mClPosition][5] = "true";
                    mReturnJoinButton.setPadding(0, 0, 0, 0);
                    mReturnJoinButton.setBackground(getDrawable(R.drawable.forest_button_white));
                    mReturnJoinButton.setText("已加入");
                    // button.setGravity(Gravity.CENTER_VERTICAL);
                    mReturnJoinButton.setCompoundDrawables(null, null, null, null);
                    mReturnJoinButton.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                }
            }
        }


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allforest);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.srl_allforest_loadmore);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(AllForestsActivity.this));
        //refreshLayout.setRefreshFooter(new ClassicsFooter(AllForestsActivity.this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout1=refreshlayout;
                refreshcondition=true;
                Update0();
                mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
                mAVLoadingIndicatorView.show();
                title.setText("加载中...");
//传入false表示刷新失败
            }
        });
        mAVLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.titlebargreen_AVLoadingIndicatorView);
        back= (ConstraintLayout) findViewById(R.id.cl_titlebargreen_back);
        title=(TextView)findViewById(R.id.tv_titlebargreen_title);

        back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(AllForestsActivity.this, HomeScreenActivity.class);
                                        startActivity(intent);
                                    }
                                });
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { //表示未授权时
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
        }
        mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
        mAVLoadingIndicatorView.show();
        retrofit= RetrofitManager.getRetrofit();
        request=RetrofitManager.getRequest();
       // request = retrofit.create(RequestInterface.class);
        Update0();
    }
    public void BackUpdate(){
        mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
        mAVLoadingIndicatorView.show();
        title.setText("加载中...");
        Update0();
    }


    public void Update0(){
    new Thread(new Runnable() {//加载纵向列表标题
        @Override
        public void run() {
            Call<ResponseBody> call = request.getType(0,10);//进行封装
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
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
                            JSONArray jsonArray = jsonObject.getJSONArray("types");
                            title_list_2 = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                title_list_2[i] = jsonArray.getString(i);
                                System.out.println("" + title_list_2[i]);
                            }
                            number1 = new int[title_list_2.length + 1];
                            Update();

                            tab2 = -1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        ErrorMsg.getErrorMsg(response,AllForestsActivity.this);
                        failureAction();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                    failureAction();
                    Toast.makeText(AllForestsActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                }
               private void failureAction(){
                   if(refreshcondition==true){
                       refreshlayout1.finishRefresh();
                       refreshcondition=false;
                   }
                   title.setText("加载失败");
                   mAVLoadingIndicatorView.hide();
                   mAVLoadingIndicatorView.setVisibility(View.GONE);
               }
            });
        }
    }).start();
}
    public void Update(){//加载标题所对应的所以小树林
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //String gg=title_list_2[cycle(true)]+"?start_id=0&list_size=10&is_last_active=false";
                    Call<ResponseBody> call = request.getDetailTypeForest(title_list_2[cycle(true)],0,10,false);//进行封装
                    call.enqueue(new Callback<ResponseBody>() {
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code()==200) {
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
                                    Forest.setForest_list(cycle(false) + 1, jsonArray);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (cycle(false) != title_list_2.length - 1) {
                                    Update();
                                } else {
                                    Update2();
                                }
                            }else{
                                failureAction();
                                ErrorMsg.getErrorMsg(response,AllForestsActivity.this);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            failureAction();
                            Toast.makeText(AllForestsActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                        }
                        private void failureAction(){
                            title.setText("加载失败");
                            mAVLoadingIndicatorView.hide();
                            mAVLoadingIndicatorView.setVisibility(View.GONE);
                            if(refreshcondition==true){
                                refreshlayout1.finishRefresh();
                                refreshcondition=false;
                            }
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
            Call<ResponseBody> call2 = request.getHotForest(0,10);//进行封装
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }
                            String titlelist = null;
                            JSONObject jsonObject = new JSONObject(json);
                            JSONArray jsonArray = jsonObject.getJSONArray("forests");

                            Forest.setForest_list(0, jsonArray);

                            recyclerView = (MaxHeightRecyclerView) findViewById(R.id.rv_allforest);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllForestsActivity.this);
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            recyclerView.setLayoutManager(linearLayoutManager);


                            networkcondition = new boolean[title_list_2.length + 1];
                            for (int a = 0; a < title_list_2.length; a++) {
                                networkcondition[a] = false;
                            }
                            forest_list_0 = new String[title_list_2.length + 1][][];
                            bitmaps = new Bitmap[title_list_2.length + 1][][];
                            title.setText("发现小树林");
                            mAVLoadingIndicatorView.hide();
                            mAVLoadingIndicatorView.setVisibility(View.GONE);


                            recyclerView.setAdapter(new AllForestsAdapter());
                            if (refreshcondition == true) {
                                refreshlayout1.finishRefresh();
                                refreshcondition = false;
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        failureAction();
                        ErrorMsg.getErrorMsg(response,AllForestsActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                    failureAction();
                    Toast.makeText(AllForestsActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                }
                private void failureAction(){
                    title.setText("加载失败");
                    mAVLoadingIndicatorView.hide();
                    mAVLoadingIndicatorView.setVisibility(View.GONE);
                    if(refreshcondition==true){
                        refreshlayout1.finishRefresh();
                        refreshcondition=false;
                    }
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
           // System.out.println(DT2number+"468");
            if(jsonArray.length()==number1[DT2number]){
                mAVLoadingIndicatorView.hide();
                mAVLoadingIndicatorView.setVisibility(View.GONE);
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
            recyclerViewin.setAdapter(new AllForestsDetailAdapter(DT2number, true));
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
                    return ITEM_TYPE_HEADER;

                } else {
                    return ITEM_TYPE_CONTENT;
                }
            }

            public class HeadHolder extends RecyclerView.ViewHolder{
                private Button build;
                public HeadHolder(View view){
                    super(view);
                    build=(Button)view.findViewById(R.id.btn_allforesthead_build);
                    build.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AllForestsActivity.this, ApplyForestActivity.class);
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
                    type = (TextView) view.findViewById(R.id.tv_forestdetailiconlist_title);
                    recyclerViewin = (RecyclerView) view.findViewById(R.id.rv_forestdetailiconlist);
                    LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(AllForestsActivity.this);
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
                            .inflate(R.layout.item_allforesthead,parent,false));
                } else if (viewType == ITEM_TYPE_CONTENT) {
                    return new AllForestsAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_forestdetailiconlist,parent,false));
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
                //private String condition_if;
                ConstraintLayout next;
                private int position;
                public ViewHolder(View view)  {
                    super(view);
                    background_image_url=(ImageView)view.findViewById(R.id.iv_allforesticon_icon);
                    cover_url=(ImageView)view.findViewById(R.id.iv_allforesticon_cover);
                    name=(TextView)view.findViewById(R.id.tv_allforesticon_title);
                    joined_and_hole=(TextView)view.findViewById(R.id.tv_allforesticon_number);
                    description=(TextView)view.findViewById(R.id.tv_allforesticon_content);
                    button=(Button)view.findViewById(R.id.btn_allforesticon_join);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(CheckingToken.IfTokenExist()) {
                                if (forest_list_0[father_position][position][5].equals("true")) {
                                    View mView = View.inflate(getApplicationContext(), R.layout.dialog_forestquitnotice, null);
                                    Dialog dialog = new Dialog(AllForestsActivity.this);
                                    dialog.setContentView(mView);
                                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.notice);
                                    TextView no = (TextView) mView.findViewById(R.id.tv_dialogforestquit_notquit);
                                    TextView yes = (TextView) mView.findViewById(R.id.tv_dialogforestquit_quit);
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

                                                    Call<ResponseBody> call2 = request.delete(RetrofitManager.API+"forests/quit/" + forest_list_0[father_position][position][3]);//进行封装
                                                    call2.enqueue(new Callback<ResponseBody>() {
                                                        @Override
                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                            if(response.code()==200) {
                                                                Toast.makeText(AllForestsActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                                forest_list_0[father_position][position][5]= "false";
                                                                forest_list_0[father_position][position][5] = "false";
                                                                button.setPadding(30, 5, 6, 6);
                                                                button.setBackground(getDrawable(R.drawable.forest_button));
                                                                button.setText("加入");
                                                                button.setTextColor(getResources().getColor(R.color.GrayScale_100));
                                                                Drawable homepressed = getResources().getDrawable(R.mipmap.group243, null);
                                                                homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                                                                button.setCompoundDrawables(homepressed, null, null, null);
                                                                dialog.dismiss();
                                                            }else{
                                                                ErrorMsg.getErrorMsg(response,AllForestsActivity.this);
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                            Toast.makeText(AllForestsActivity.this, R.string.network_quitfailture, Toast.LENGTH_SHORT).show();
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
                                            Call<ResponseBody> call23 = request.join(RetrofitManager.API+"forests/join/" + forest_list_0[father_position][position][3]);//进行封装
                                            call23.enqueue(new Callback<ResponseBody>() {

                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        Toast.makeText(AllForestsActivity.this, "加入成功", Toast.LENGTH_SHORT).show();
                                                        forest_list_0[father_position][position][5] = "true";
                                                        forest_list_0[father_position][position][5]= "true";
                                                        button.setPadding(0, 0, 0, 0);
                                                        //button.setPadding(-30,-5,-6,-6);
                                                        //button=(Button)view.findViewById(R.id.rectangle_4);
                                                        button.setBackground(getDrawable(R.drawable.forest_button_white));
                                                        button.setText("已加入");
                                                        // button.setGravity(Gravity.CENTER_VERTICAL);
                                                        button.setCompoundDrawables(null, null, null, null);
                                                        button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                                                    }else{
                                                        ErrorMsg.getErrorMsg(response,AllForestsActivity.this);
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(AllForestsActivity.this, R.string.network_joinfailture, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }else{
                                Intent intent=new Intent(AllForestsActivity.this, EmailVerifyActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFatherClPosition=father_position;
                            mClPosition=position;
                            mConditionIfJoined=forest_list_0[mFatherClPosition][mClPosition][5];
                            mReturnJoinButton=button;
                            Intent intent = DetailForestActivity.newIntent(AllForestsActivity.this,forest_list_0[father_position][position]);
                            startActivityForResult(intent,REQUESTCODE_COMMENT);
                            //startActivity(intent);
                        }
                    });
                }
                public void bind(int position)  {
                    this.position=position;
                    //condition_if=forest_list_0[father_position][position][5];
                    if(tab) {
                        name.setText(forest_list_0[father_position][position][7]);
                        joined_and_hole.setText(forest_list_0[father_position][position][4]);
                        description.setText(forest_list_0[father_position][position][2]);
                        if(forest_list_0[father_position][position][5].equals("false")){
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

                        Glide.with(AllForestsActivity.this)
                                .load(forest_list_0[father_position][position][0])
                                .apply(options1)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(background_image_url);


                        Glide.with(AllForestsActivity.this)
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
                        .inflate(R.layout.item_allforesticon, parent, false);
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
