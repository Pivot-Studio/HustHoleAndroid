package com.example.hustholetest1.view.homescreen.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.MaxHeightRecyclerView;
import com.example.hustholetest1.model.StandardRefreshFooter;
import com.example.hustholetest1.network.CommenRequestManager;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.R;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.activity.HomeScreenActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.example.hustholetest1.view.homescreen.forest.AllForestsActivity;
import com.example.hustholetest1.view.homescreen.forest.DetailForestActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

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

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class ForestFragment extends Fragment {
    private RecyclerView mCircleForestsRv;
    private MaxHeightRecyclerView mJoinedHolesRv;
    //private TextView mJumpToAllforestTv;
    private ConstraintLayout mJumpToAllforestTv;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray mJoinedForestsJsonArray, mJoinedHolesJsonArray;
    private String[][] mJoinedForestsList;
    private ForestHoleAdapter mForestHoleAdapter;
    private List<String[]> mJoinedHolesList = new ArrayList<String[]>();
    private int mAdapterLoadCompleteNumber = 0;
    private int mStartingLoadId = 20;
    private RefreshLayout mRefreshConditionRl, mLoadMoreCondotionRl;
    private int CONSTANT_STANDARD_LOAD_SIZE = 20;
    private Boolean mIfFirstLoad=true;
    private SmartRefreshLayout mTitleBarSrl;
    private Boolean mPrestrainCondition=false;
    private Boolean mDeleteCondition=false;

    private Boolean more_condition=false;
    private  ConstraintLayout mMoreWhatCl;
    private RecyclerView.OnScrollListener mOnscrollListener;
    public static ForestFragment newInstance() {
        ForestFragment fragment = new ForestFragment();
        return fragment;

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_forest, container, false);
        RefreshLayout refreshLayout = (RefreshLayout) rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new StandardRefreshFooter(getActivity()));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                if(CheckingToken.IfTokenExist()) {
                    mRefreshConditionRl = refreshlayout;
                    mStartingLoadId = CONSTANT_STANDARD_LOAD_SIZE;
                    update();
                    mJoinedHolesRv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return true;
                        }
                    });

                }else{
                    refreshLayout.finishRefresh();
                    Intent intent=new Intent(getContext(), EmailVerifyActivity.class);
                    startActivity(intent);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                if(CheckingToken.IfTokenExist()) {
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
                        //int titleBarBottomLocation=mTitleBarSrl.getBottom();
                        // mJoinedHolesRv.setMaxHeight(HomeScreenActivity.GetOBTopLocation()-titleBarBottomLocation-2000);
                    }
                }else{
                        refreshLayout.finishLoadMore();
                        Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                        startActivity(intent);
                }
            }
        });



        mJoinedHolesRv = (MaxHeightRecyclerView) rootView.findViewById(R.id.rv_forest);
        /*WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 从默认显示器中获取显示参数保存到dm对象中
        wm.getDefaultDisplay().getMetrics(dm);

        mJoinedHolesRv.setMaxHeight(dm.heightPixels-HomeScreenActivity.GetOBAndTBHeight());

        */

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mJoinedHolesRv.setLayoutManager(linearLayoutManager2);
        mForestHoleAdapter = new ForestHoleAdapter();
        mTitleBarSrl=(SmartRefreshLayout)rootView.findViewById(R.id.refreshLayout);



        //System.out.println("提交了context");
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);//创建接口实例


        mStartingLoadId = CONSTANT_STANDARD_LOAD_SIZE;
        mJoinedHolesList = new ArrayList<String[]>();
        if (CheckingToken.IfTokenExist()) {
            update();
        } else {
            NoTokenUpdate();
        }
        return rootView;

    }

    public void NoTokenUpdate(){
        mForestHoleAdapter = new ForestHoleAdapter();

        mJoinedHolesRv.setAdapter(mForestHoleAdapter);
    }

    public int number() {
        mAdapterLoadCompleteNumber++;
        return mAdapterLoadCompleteNumber;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request.joined(30, 0);
                Call<ResponseBody> call2 = request.joined_holes(CONSTANT_STANDARD_LOAD_SIZE, mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE, true);
                if (mStartingLoadId == CONSTANT_STANDARD_LOAD_SIZE || mRefreshConditionRl != null) {
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override

                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                JSONObject jsonObject = new JSONObject(json);

                                mJoinedForestsJsonArray = jsonObject.getJSONArray("forests");
                                mJoinedForestsList = new String[mJoinedForestsJsonArray.length()][8];
                                new JoinedForestsDownloadTask().execute();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            Toast.makeText(getContext(), R.string.network_loadfailure, Toast.LENGTH_SHORT).show();

                                if (mLoadMoreCondotionRl != null) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    //将上拉刷新变量滞空同时结束掉上拉刷新
                                    mLoadMoreCondotionRl.finishLoadMore();
                                    mLoadMoreCondotionRl = null;
                                    // if (! mForestHoleAdapter.hasObservers()) {
                                    //     mForestHoleAdapter.setHasStableIds(true);
                                    // }
                                    // mForestHoleAdapter.notifyDataSetChanged();
                                    if (mPrestrainCondition == true) {
                                        mPrestrainCondition = false;
                                    }
                                } else if (mPrestrainCondition == true) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    mPrestrainCondition = false;
                                    //  mForestHoleAdapter.notifyDataSetChanged();
                                    if (mLoadMoreCondotionRl != null) {
                                        //将上拉刷新变量滞空同时结束掉上拉刷新
                                        mLoadMoreCondotionRl.finishLoadMore();
                                        mLoadMoreCondotionRl = null;
                                    }
                                } else {
                                    //if (number() == 2) {//两个加载全部完毕后设置adpter
                                    if (mRefreshConditionRl != null) {//判断是否由由下拉加载引起的
                                        mRefreshConditionRl.finishRefresh();
                                        mRefreshConditionRl = null;
                                        mJoinedHolesRv.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                return false;
                                            }
                                        });
                                        if (mIfFirstLoad) {
                                            // mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                                            //  mIfFirstLoad=false;
                                        } else {
                                            //  mForestHoleAdapter.notifyDataSetChanged();
                                        }
                                    } else {

                                        //mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                                    }
                                    //mAdapterLoadCompleteNumber = 0;
                                    //}
                                }
                        }


                    });
                }

                if (mJoinedHolesList.size() % CONSTANT_STANDARD_LOAD_SIZE == 0) {
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                if(mRefreshConditionRl!=null){
                                    mJoinedHolesList = new ArrayList<String[]>();
                                }
                                if(mDeleteCondition){
                                    mDeleteCondition=false;
                                    mJoinedHolesList = new ArrayList<String[]>();
                                }
                                mJoinedHolesJsonArray = new JSONArray(json);
                                new JoinedHolesDownloadTask().execute();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getContext(), R.string.network_loadfailure, Toast.LENGTH_SHORT).show();

                                if (mLoadMoreCondotionRl != null) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    //将上拉刷新变量滞空同时结束掉上拉刷新
                                    mLoadMoreCondotionRl.finishLoadMore();
                                    mLoadMoreCondotionRl = null;
                                    // if (! mForestHoleAdapter.hasObservers()) {
                                    //     mForestHoleAdapter.setHasStableIds(true);
                                    // }
                                    // mForestHoleAdapter.notifyDataSetChanged();
                                    if (mPrestrainCondition == true) {
                                        mPrestrainCondition = false;
                                    }
                                } else if (mPrestrainCondition == true) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    mPrestrainCondition = false;
                                    //  mForestHoleAdapter.notifyDataSetChanged();
                                    if (mLoadMoreCondotionRl != null) {
                                        //将上拉刷新变量滞空同时结束掉上拉刷新
                                        mLoadMoreCondotionRl.finishLoadMore();
                                        mLoadMoreCondotionRl = null;
                                    }
                                } else {
                                    //if (number() == 2) {//两个加载全部完毕后设置adpter
                                    if (mRefreshConditionRl != null) {//判断是否由由下拉加载引起的
                                        mRefreshConditionRl.finishRefresh();
                                        mRefreshConditionRl = null;
                                        mJoinedHolesRv.setOnTouchListener(new View.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                return false;
                                            }
                                        });
                                        if (mIfFirstLoad) {
                                            // mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                                            //  mIfFirstLoad=false;
                                        } else {
                                            //  mForestHoleAdapter.notifyDataSetChanged();
                                        }
                                    } else {

                                        //mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                                    }
                                    //}
                                }




                        }
                    });

                } else {
                    if (mLoadMoreCondotionRl != null) {
                        mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                        mLoadMoreCondotionRl.finishLoadMore();
                        mLoadMoreCondotionRl = null;
                    }
                }


            }
        }).start();
    }


    private class JoinedHolesDownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            if (mLoadMoreCondotionRl != null) {

                //将上拉刷新变量滞空同时结束掉上拉刷新
                mLoadMoreCondotionRl.finishLoadMore();
                mLoadMoreCondotionRl = null;
                if (! mForestHoleAdapter.hasObservers()) {
                    mForestHoleAdapter.setHasStableIds(true);
                }
                mForestHoleAdapter.notifyDataSetChanged();
                if(mPrestrainCondition==true){

                    mPrestrainCondition=false;
                }
            }else if(mPrestrainCondition==true){
                mPrestrainCondition=false;
                mForestHoleAdapter.notifyDataSetChanged();
                if (mLoadMoreCondotionRl != null) {
                    //将上拉刷新变量滞空同时结束掉上拉刷新
                    mLoadMoreCondotionRl.finishLoadMore();
                    mLoadMoreCondotionRl = null;
                }
            } else {
                if (number() == 2) {//两个加载全部完毕后设置adpter
                    if (mRefreshConditionRl != null) {//判断是否由由下拉加载引起的
                        mRefreshConditionRl.finishRefresh();
                        mRefreshConditionRl = null;
                        mJoinedHolesRv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                        if(mIfFirstLoad){
                            mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                            mIfFirstLoad=false;
                        }else{
                        mForestHoleAdapter.notifyDataSetChanged();
                        }
                    }else {
                        mIfFirstLoad=false;
                        mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                    }
                    mAdapterLoadCompleteNumber = 0;
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                for (int f = 0; f < mJoinedHolesJsonArray.length(); f++) {
                    JSONObject sonObject = mJoinedHolesJsonArray.getJSONObject(f);
                    String[] list = new String[14];
                    list[0] = sonObject.getString("background_image_url");
                    list[1] = sonObject.getString("content");
                    list[2] = sonObject.getString("created_timestamp");
                    list[3] = sonObject.getInt("follow_num") + "";
                    list[4] = sonObject.getInt("forest_id") + "";
                    list[5] = sonObject.getString("forest_name");
                    list[6] = sonObject.getInt("hole_id") + "";
                    //detailforest2[f][1] = sonObject.getString("image");
                    list[8] = sonObject.getBoolean("is_follow") + "";
                    list[9] = sonObject.getBoolean("is_mine") + "";
                    list[10] = sonObject.getBoolean("is_reply") + "";
                    list[11] = sonObject.getBoolean("is_thumbup") + "";
                    list[12] = sonObject.getInt("reply_num") + "";
                    list[13] = sonObject.getInt("thumbup_num") + "";
                    mJoinedHolesList.add(list);
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

    private class JoinedForestsDownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            if (mLoadMoreCondotionRl != null) {
                //将上拉刷新变量滞空同时结束掉上拉刷新
                mLoadMoreCondotionRl.finishLoadMore();
                mLoadMoreCondotionRl = null;
                mForestHoleAdapter.setHasStableIds(true);
                mForestHoleAdapter.notifyDataSetChanged();
                if(mPrestrainCondition==true){
                    mPrestrainCondition=false;
                }
            } else if(mPrestrainCondition==true){
                mPrestrainCondition=false;
                mForestHoleAdapter.notifyDataSetChanged();

            }else {
                if (number() == 2) {//两个加载全部完毕后设置adpter
                    if (mRefreshConditionRl != null) {//判断是否由由下拉加载引起的
                        mRefreshConditionRl.finishRefresh();
                        mRefreshConditionRl = null;
                        mJoinedHolesRv.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                        if(mIfFirstLoad){
                            mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                            mIfFirstLoad=false;
                        }else {
                            mForestHoleAdapter.notifyDataSetChanged();
                        }
                    }else {
                        mIfFirstLoad=false;
                      //  mForestHoleAdapter = new ForestHoleAdapter();
                        mJoinedHolesRv.setAdapter(mForestHoleAdapter);
                    }
                    mAdapterLoadCompleteNumber = 0;
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //int allnumber=voids[0];
            try {
                for (int f = 0; f < mJoinedForestsJsonArray.length(); f++) {
                    JSONObject sonObject = mJoinedForestsJsonArray.getJSONObject(f);


                    mJoinedForestsList[f][0] = sonObject.getString("background_image_url");
                    mJoinedForestsList[f][1] = sonObject.getString("cover_url");
                    mJoinedForestsList[f][2] = sonObject.getString("description");
                    mJoinedForestsList[f][3] = sonObject.getInt("forest_id") + "";
                    mJoinedForestsList[f][4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                    mJoinedForestsList[f][5] = "true";
                    mJoinedForestsList[f][6] = sonObject.getString("last_active_time");
                    mJoinedForestsList[f][7] = sonObject.getString("name");


                    //detailforest [f][0] = sonObject.getString("cover_url");
                    //detailforest[f][1] = sonObject.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        public void OptionBoxRefresh(){

        }
    }


    public class CircleForestsAdapter extends RecyclerView.Adapter<CircleForestsAdapter.ViewHolder> {
        //private static List<Event> events;

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView forestname;
            private ImageView forestphoto;
            private int position;
            ConstraintLayout next;

            public ViewHolder(View view) {
                super(view);
                forestname = (TextView) view.findViewById(R.id.textView28);
                forestphoto = (ImageView) view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()){
                            if (position != mJoinedForestsJsonArray.length()) {
                                Intent intent = DetailForestActivity.newIntent(getContext(), mJoinedForestsList[position]);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(getContext(), AllForestsActivity.class);
                                startActivity(intent);
                            }
                        }else{
                            Intent intent = new Intent(getContext(), AllForestsActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }


            public void bind(int position) {
                this.position = position;
                if(CheckingToken.IfTokenExist()) {
                    if (position != mJoinedForestsJsonArray.length()) {
                        String name = mJoinedForestsList[position][7];
                        if (name.length() > 6) {
                            forestname.setText(name.substring(0, 5) + "...");
                        } else {
                            forestname.setText(mJoinedForestsList[position][7]);
                        }


                        Glide.with(getActivity())
                                .load(mJoinedForestsList[position][0])
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .into(forestphoto);

                        //forestphoto.setImageBitmap(bitmapss[position][1]);
                    } else {
                        forestname.setText("加载更多");
                        forestphoto.setImageResource(R.mipmap.more);

                    }
                }else{
                    forestname.setText("加载更多");
                    forestphoto.setImageResource(R.mipmap.more);
                }
            }

        }

        public CircleForestsAdapter() {
            //Log.d(TAG,"数据传入了");
            //this.events=events;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_foresticon, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //Event event=events.get(position);
            holder.bind(position);

        }

        @Override
        public int getItemCount() {
            if(CheckingToken.IfTokenExist()){
                return mJoinedForestsJsonArray.length() + 1;
            }else{
                return 1;
            }
        }
    }


    public class ForestHoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        //private static List<Event> events;
        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_BOTTOM = 2;
        private int mHeaderCount = 1;//头部View个数
        //private Boolean more_condition = false;
       // private ConstraintLayout  mMoreWhatCl;

        @Override
        public int getItemViewType(int position) {
            if (position==0) {
                return ITEM_TYPE_HEADER;
            } else {
                return ITEM_TYPE_CONTENT;
            }
        }

        public class HeadHolder extends RecyclerView.ViewHolder {
            public HeadHolder(View view) {
                super(view);

                mJumpToAllforestTv = (ConstraintLayout) view.findViewById(R.id.cl_foresthead);
                mJumpToAllforestTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AllForestsActivity.class);
                        startActivity(intent);
                    }
                });
                mCircleForestsRv = (RecyclerView) view.findViewById(R.id.rv_foresthead);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                mCircleForestsRv.setLayoutManager(linearLayoutManager);
            }

            public void bind(int position) {
                mCircleForestsRv.setAdapter(new CircleForestsAdapter());
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView content, created_timestamp, forest_name, follow_num, reply_num, thumbup_num, hole_id, more_2;
            private ImageView background_image_url, is_follow, is_reply, is_thumbup, more, more_1;
            private ConstraintLayout morewhat,thumbup,follow;
            private int position;
            private Boolean thumbupCondition=false,followCondition=false;

            public ViewHolder(View view) {
                super(view);

                thumbup=(ConstraintLayout)view.findViewById(R.id.cl_itemforest_thumbup);
                follow=(ConstraintLayout)view.findViewById(R.id.cl_itemforest_follow);

                content = (TextView) view.findViewById(R.id.tv_itemforest_content);
                created_timestamp = (TextView) view.findViewById(R.id.tv_itemforest_time);
                forest_name = (TextView) view.findViewById(R.id.tv_itemforest_title);
                thumbup_num = (TextView) view.findViewById(R.id.tv_itemforest_thumbupnumber);
                reply_num = (TextView) view.findViewById(R.id.tv_itemforest_replynumber);
                follow_num = (TextView) view.findViewById(R.id.tv_itemforest_follownumber);
                hole_id = (TextView) view.findViewById(R.id.tv_itemforest_holeid);
                background_image_url = (ImageView) view.findViewById(R.id.iv_itemforest_icon);
                is_thumbup = (ImageView) view.findViewById(R.id.iv_itemforest_thumbup);
                is_reply = (ImageView) view.findViewById(R.id.iv_itemforest_reply);
                is_follow = (ImageView) view.findViewById(R.id.iv_itemforest_follow);


                more = (ImageView) view.findViewById(R.id.iv_itemforest_more);
                more_1 = (ImageView) view.findViewById(R.id.iv_itemforest_moreicon);
                more_2 = (TextView) view.findViewById(R.id.tv_itemforest_moretext);
                morewhat = (ConstraintLayout) view.findViewById(R.id.cl_itemforest_morelist);
                morewhat.setVisibility(View.INVISIBLE);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (more_condition == false) {
                            morewhat.setVisibility(View.VISIBLE);
                             mMoreWhatCl = morewhat;
                            more_condition = true;
                        } else {
                             mMoreWhatCl.setVisibility(View.INVISIBLE);
                            morewhat.setVisibility(View.VISIBLE);
                             mMoreWhatCl = morewhat;
                        }


                        if (mOnscrollListener != null) {
                           mJoinedHolesRv.removeOnScrollListener(mOnscrollListener);
                        }
                        mOnscrollListener = new RecyclerView.OnScrollListener() {
                            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                //super.onScrollStateChanged(recyclerView, newState);
                                mMoreWhatCl.setVisibility(View.INVISIBLE);
                                more_condition = false;
                                mJoinedHolesRv.removeOnScrollListener(mOnscrollListener);
                            }
                        };
                        mJoinedHolesRv.addOnScrollListener(mOnscrollListener);


                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RemoveOnScrollListener();
                        if(CheckingToken.IfTokenExist()) {
                            if (mJoinedHolesList.get(position - 1)[9].equals("true")) {
                                new Thread(new Runnable() {//加载纵向列表标题
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.delete_hole(mJoinedHolesList.get(position - 1)[6]);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                mStartingLoadId = 20;
                                                mDeleteCondition = true;
                                                update();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                                                mDeleteCondition = false;
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                CommenRequestManager.ReportRequest(getContext(), request, mJoinedHolesList.get(position - 1)[6], "-1");
                            }
                        }
                        }
                });


                thumbup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if (thumbupCondition==false) {
                                thumbupCondition = true;
                                if (mJoinedHolesList.get(position - 1)[11].equals("false")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + mJoinedHolesList.get(position - 1)[6] + "/-1");//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    is_thumbup.setImageResource(R.mipmap.active);
                                                    mJoinedHolesList.get(position - 1)[11] = "true";
                                                    mJoinedHolesList.get(position - 1)[13] = (Integer.parseInt(mJoinedHolesList.get(position - 1)[13]) + 1) + "";
                                                    thumbupCondition = false;
                                                    thumbup_num.setText(mJoinedHolesList.get(position - 1)[13]);
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    thumbupCondition = false;
                                                    Toast.makeText(getContext(), R.string.network_thumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + mJoinedHolesList.get(position - 1)[6] + "/-1");//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    is_thumbup.setImageResource(R.mipmap.inactive);
                                                    mJoinedHolesList.get(position - 1)[11] = "false";
                                                    mJoinedHolesList.get(position - 1)[13] = (Integer.parseInt(mJoinedHolesList.get(position - 1)[13]) - 1) + "";
                                                    thumbupCondition = false;
                                                    thumbup_num.setText(mJoinedHolesList.get(position - 1)[13]);
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    thumbupCondition = false;
                                                    Toast.makeText(getContext(), R.string.network_notthumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }
                        }
                });
                follow.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {

                            if (followCondition==false) {
                                followCondition = true;
                                if (mJoinedHolesList.get(position - 1)[8].equals("false")) {
                                    new Thread(new Runnable() {//加载纵向列表标题
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/" + mJoinedHolesList.get(position - 1)[6]);//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_follow.setImageResource(R.mipmap.active_3);
                                                        mJoinedHolesList.get(position - 1)[8] = "true";
                                                        mJoinedHolesList.get(position - 1)[3] = (Integer.parseInt(mJoinedHolesList.get(position - 1)[3]) + 1) + "";
                                                        followCondition = false;
                                                        follow_num.setText(mJoinedHolesList.get(position - 1)[3]);
                                                    }else{
                                                        followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.body() != null) {
                                                            try {
                                                                json = response.body().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(getContext(), returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else{
                                                            Toast.makeText(getContext(),"过于频繁请求！",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(getContext(), R.string.network_followfailure_, Toast.LENGTH_SHORT).show();
                                                    followCondition = false;
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/" + mJoinedHolesList.get(position - 1)[6]);//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_follow.setImageResource(R.mipmap.inactive_3);
                                                        mJoinedHolesList.get(position - 1)[8] = "false";
                                                        mJoinedHolesList.get(position - 1)[3] = (Integer.parseInt(mJoinedHolesList.get(position - 1)[3]) - 1) + "";
                                                        followCondition = false;
                                                        follow_num.setText(mJoinedHolesList.get(position - 1)[3]);
                                                    }else{
                                                        followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.body() != null) {
                                                            try {
                                                                json = response.body().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(getContext(), returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }else{
                                                            Toast.makeText(getContext(),"过于频繁请求！",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    followCondition = false;
                                                    Toast.makeText(getContext(), R.string.network_notfollowfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }
                        }

                });


                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RemoveOnScrollListener();
                        Log.d("data[2]1", mJoinedHolesList.get(position - 1)[2]);
                        Intent intent = CommentListActivity.newIntent(getActivity(), mJoinedHolesList.get(position - 1));
                        startActivity(intent);
                    }
                });
                background_image_url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int k = 0; k < mJoinedForestsList.length; k++) {
                            if (mJoinedHolesList.get(position - 1)[5].equals(mJoinedForestsList[k][7])) {
                                Intent intent = DetailForestActivity.newIntent(getContext(), mJoinedForestsList[k]);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }


            public void bind(int position) {
                String[] item=mJoinedHolesList.get(position - 1);
                this.position = position;
                content.setText(item[1]);
                created_timestamp.setText(item[2]);
                forest_name.setText(item[5]);
                follow_num.setText(item[3]);
                reply_num.setText(item[12]);
                thumbup_num.setText(item[13]);
                hole_id.setText("#" + item[6]);
                Log.d("thumbup_num"+"is_thumbup",item[13]+"+"+item[11]);
                if (item[8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                }else{
                    is_follow.setImageResource(R.mipmap.inactive_3);
                }
                if (item[9].equals("true")) {
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }else{
                    more_1.setImageResource(R.mipmap.vector4);
                    more_2.setText("举报");
                }
                if (item[10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }else{
                    is_reply.setImageResource(R.mipmap.inactive_2);
                }
                if (item[11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }else{
                    is_thumbup.setImageResource(R.mipmap.inactive);
                }
                if (item[0].equals("")) {
                    RoundedCorners roundedCorners = new RoundedCorners(16);
                    RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);
                    Glide.with(getActivity())
                            .load(R.mipmap.vector3)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(options1)
                            .into(background_image_url);
                } else {

                    RoundedCorners roundedCorners = new RoundedCorners(16);
                    RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);
                    Glide.with(getActivity())
                            .load(item[0])
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(options1)
                            .into(background_image_url);
                }
            }

        }

        public ForestHoleAdapter() {

        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == ITEM_TYPE_HEADER) {
                return new HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_foresthead, parent, false));
            } else{
                return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_forest, parent, false));
            }
            //return null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);

           /* if(position==0){
                ((ForestHoleAdapter.HeadHolder) holder).bind(position);
            }else{
                ((ForestHoleAdapter.ViewHolder) holder).bind(position);
            }


            */
          if (holder instanceof HeadHolder) {
                ((ForestHoleAdapter.HeadHolder) holder).bind(position);
            } else  {
                ((ForestHoleAdapter.ViewHolder) holder).bind(position);
            }
        if(position== mJoinedHolesList.size()-3&&(mJoinedHolesList.size()%20==0)&&mRefreshConditionRl==null){
            mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
            mPrestrainCondition=true;
            update();
        }

        }

        @Override
        public int getItemCount() {
            if(CheckingToken.IfTokenExist()) {
                return mJoinedHolesList.size() + 1;
            }else{
                return 1;
            }
        }
    }
    private void RemoveOnScrollListener() {
        if (mOnscrollListener != null) {
            mJoinedHolesRv.removeOnScrollListener(mOnscrollListener);
        }
        if(mMoreWhatCl!=null) {
            mMoreWhatCl.setVisibility(View.INVISIBLE);
            more_condition = false;
        }
    }
}

