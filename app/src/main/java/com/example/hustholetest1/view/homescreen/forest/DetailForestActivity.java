package com.example.hustholetest1.view.homescreen.forest;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hustholetest1.model.BlurTransformation;
import com.example.hustholetest1.model.TransparentRefreshHeader;
import com.example.hustholetest1.network.CommenRequestManager;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.network.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.example.hustholetest1.view.homescreen.publishhole.PublishHoleActivity;
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

    private JSONArray jsonArray2;
    private ArrayList<String[]> mDetailforestHoleslist=new ArrayList<>();
   // private TextView title;
    private String[] data;
    private String data_2;
    private RequestInterface request;
    private ForestHoleAdapter mForestHoleAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton addhole;
    private static final String key="key_1";
    private static final String key_2="key_2";
    private int mStartingLoadId = 0;
    private RefreshLayout mRefreshConditionRl, mLoadMoreCondotionRl;
    private int CONSTANT_STANDARD_LOAD_SIZE = 20;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailforest);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.Grayscale_200), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        back = (ImageView) findViewById(R.id.iv_titlebartransparent_back);
        head_2=(ImageView)findViewById(R.id.iv_detailforest_cover);



        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.srl_detailforest_loadmore);
        refreshLayout.setRefreshHeader(new TransparentRefreshHeader(DetailForestActivity.this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(DetailForestActivity.this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mRefreshConditionRl =refreshlayout;
                mStartingLoadId=0;
                mDetailforestHoleslist=new ArrayList<>();
                update();
//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                mLoadMoreCondotionRl = refreshlayout;
                mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                update();
            }
        });

        //title = (TextView) findViewById(R.id.title);
        //title.setText("发现小树林");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(Page2_DetailAllForestsActivity.this, HomePageActivity.class);
                //startActivity(intent);
                finish();
            }
        });
        TokenInterceptor.getContext(DetailForestActivity.this);
        //TokenInterceptor.getContext(RegisterActivity.this);
        System.out.println("提交了context");
        retrofit= RetrofitManager.getRetrofit();
        Log.e(TAG, "token99：");
        request = retrofit.create(RequestInterface.class);//创建接口实例
        Log.e(TAG, "token100：");
        data=getIntent().getStringArrayExtra(key);
        addhole=(FloatingActionButton)findViewById(R.id.fab_detailforest_publishhole);
        addhole.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //关闭掉对话框,拿到对话框的对象
                Intent intent= PublishHoleActivity.newIntent(DetailForestActivity.this,data[7]);
                startActivity(intent);
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
                            Toast.makeText(DetailForestActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }else{
            update();
        }

        recyclerView = (RecyclerView)findViewById(R.id.rv_detailforest);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailForestActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void update(){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                //Call<ResponseBody> call = request.detailholes(data[3],mStartingLoadId,CONSTANT_STANDARD_LOAD_SIZE,false);//进行封装
                Call<ResponseBody> call = request.detailholes2("http://hustholetest.pivotstudio.cn/api/forests/"+data[3]+"/holes?start_id="+mStartingLoadId+"&list_size="+CONSTANT_STANDARD_LOAD_SIZE+"&is_last_active=false");//进行封装

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }
                            //Log.e(TAG, "token2："+json);
                            if(json.equals("[]")){
                               Toast.makeText(DetailForestActivity.this,"加载到底辣",Toast.LENGTH_SHORT).show();
                               mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                                if(mLoadMoreCondotionRl!=null){
                                    mLoadMoreCondotionRl.finishLoadMore();
                                    mLoadMoreCondotionRl=null;
                                }
                            }else{
                            jsonArray2 = new JSONArray(json);
                            //jsonArray =response.body();
                            //mDetailforestHoleslist =new String[jsonArray2.length()][14];
                            new DownloadTask().execute();
                            }
                        } catch (IOException | JSONException e) {
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

    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            if(mRefreshConditionRl !=null){
                mRefreshConditionRl.finishRefresh();
                mRefreshConditionRl =null;
                mForestHoleAdapter=new ForestHoleAdapter();
                recyclerView.setAdapter(mForestHoleAdapter);
            } else if(mLoadMoreCondotionRl!=null){

                mLoadMoreCondotionRl.finishLoadMore();
                mLoadMoreCondotionRl=null;

                mForestHoleAdapter.notifyDataSetChanged();


            }else{

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
        private Boolean more_condition=false;
        private  ConstraintLayout morewhat0;

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
            private Button button;
            private ImageView head;
            public HeadHolder(View view){
                super(view);
                title=(TextView)view.findViewById(R.id.tv_detailforesthead_title);
                hole_and_number=(TextView)view.findViewById(R.id.tv_detailforesthead_number);
                content=(TextView)view.findViewById(R.id.tv_detailforesthead_content);
                button=(Button)view.findViewById(R.id.btn_detailforesthead_join);
                head=(ImageView)view.findViewById(R.id.iv_detailforesthead_icon);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(data[5].equals("true")){
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

                                            Call<ResponseBody> call2 = request.delete( "http://hustholetest.pivotstudio.cn/api/forests/quit/"+data[3]);//进行封装
                                            call2.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    Toast.makeText(DetailForestActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                                    data[5]="false";
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
                                                    Toast.makeText(DetailForestActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
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
                                    Call<ResponseBody> call23= request.join( "http://hustholetest.pivotstudio.cn/api/forests/join/"+data[3]);//进行封装
                                    call23.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Toast.makeText(DetailForestActivity.this, "加入成功", Toast.LENGTH_SHORT).show();

                                            data[5]="true";
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
                                            Toast.makeText(DetailForestActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }

                    }
                });
            }
            public void bind(int position){
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
                Glide.with(DetailForestActivity.this)
                        .load(data[1])
                        .transform(new BlurTransformation(DetailForestActivity.this,24))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(head_2);
            }
        }

       public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content,created_timestamp,follow_num,reply_num,thumbup_num,hole_id,more_2;
            private ImageView is_follow,is_reply,is_thumbup,more,more_1;
            ConstraintLayout morewhat;
            private int position;

            public ViewHolder(View view) {
                super(view);
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
                            morewhat0=morewhat;
                            more_condition=true;
                        }else{
                            morewhat0.setVisibility(View.INVISIBLE);
                            morewhat.setVisibility(View.VISIBLE);
                            morewhat0=morewhat;
                        }
                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        morewhat.setVisibility(View.INVISIBLE);
                        more_condition = false;
                        if(mDetailforestHoleslist.get(position-1)[9].equals("true")){
                            CommenRequestManager.DeleteRequest(DetailForestActivity.this,request, mDetailforestHoleslist.get(position-1)[6]);
                            finish();
                        }else {
                            CommenRequestManager.ReportRequest(DetailForestActivity.this, request, mDetailforestHoleslist.get(position-1)[6], "-1");
                        }
                    }
                });



                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent= CommentListActivity.newIntent(DetailForestActivity.this, mDetailforestHoleslist.get(position-1));
                        startActivity(intent);
                    }
                });
                is_thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(mDetailforestHoleslist.get(position-1)[11].equals("false")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+ mDetailforestHoleslist.get(position-1)[6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.active);
                                            mDetailforestHoleslist.get(position-1)[11]="true";
                                            mDetailforestHoleslist.get(position-1)[13]=(Integer.parseInt( mDetailforestHoleslist.get(position-1)[13])+1)+"";

                                            thumbup_num.setText(mDetailforestHoleslist.get(position-1)[13]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(DetailForestActivity.this,"点赞失败,请检查网络状况",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+ mDetailforestHoleslist.get(position-1)[6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                            mDetailforestHoleslist.get(position-1)[11]="false";
                                            mDetailforestHoleslist.get(position-1)[13]=(Integer.parseInt( mDetailforestHoleslist.get(position-1)[13])-1)+"";

                                            thumbup_num.setText(mDetailforestHoleslist.get(position-1)[13]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(DetailForestActivity.this,"取消点赞失败,请检查网络状况",Toast.LENGTH_SHORT).show();
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
                        if(mDetailforestHoleslist.get(position-1)[8].equals("false")){
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/follows/"+ mDetailforestHoleslist.get(position-1)[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.active_3);
                                            mDetailforestHoleslist.get(position-1)[8]="true";
                                            mDetailforestHoleslist.get(position-1)[3]=(Integer.parseInt(mDetailforestHoleslist.get(position-1)[3])+1)+"";

                                            follow_num.setText(mDetailforestHoleslist.get(position-1)[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(DetailForestActivity.this,"关注失败,请检查网络状况",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消点赞失败");
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/follows/"+ mDetailforestHoleslist.get(position-1)[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                            mDetailforestHoleslist.get(position-1)[8]="false";
                                            mDetailforestHoleslist.get(position-1)[3]=(Integer.parseInt( mDetailforestHoleslist.get(position-1)[3])-1)+"";

                                            follow_num.setText(mDetailforestHoleslist.get(position-1)[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(DetailForestActivity.this,"取消关注失败,请检查网络状况",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消关注失败");
                                        }
                                    });
                                }
                            }).start();
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
                }

                if (mDetailforestHoleslist.get(position-1)[10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }
                if (mDetailforestHoleslist.get(position-1)[11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
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
}
