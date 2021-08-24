package com.example.hustholetest1.view.homescreen.commentlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.network.CommenRequestManager;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.model.TimeCount;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.network.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.network.OkHttpUtil;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.forest.DetailForestActivity;
import com.example.hustholetest1.view.homescreen.mypage.Update;
import com.example.hustholetest1.view.registerandlogin.activity.LoginActivity;
import com.githang.statusbar.StatusBarCompat;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kotlin.Result;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;



public class CommentListActivity extends AppCompatActivity {
    private static final String key="key_1";
    private TextView title;
    private ConstraintLayout back;
    private EditText mPublishReplyEt;
    private Button mPublishReplyBtn;
    private RecyclerView mCommentlistRv;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray,jsonArray2;
    private ArrayList<String[]> mDetailReplyList=new ArrayList<>();
    private String[] data;
    private String reply_to_who="-1";
    private RefreshLayout refreshlayout1;



    private Retrofit retrofit2;
    private RequestInterface request2;
    private String data_hole_id = null;
    private ReplyAdapter mReplyAdapter;
    private int mStartingLoadId = 0;
    private RefreshLayout mRefreshConditionRl, mLoadMoreCondotionRl;
    private int CONSTANT_STANDARD_LOAD_SIZE = 20;
    private Boolean mPrestrainCondition=false;
    private Boolean mIfFirstLoad=true;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;

    private Boolean more_condition=false,order_condition=false,mOnlyRefreshCondition=false,mIfOnlyCondition=false,mDeleteCondition=false;
    private  ConstraintLayout mMoreWhatCl;
    private RecyclerView.OnScrollListener mOnscrollListener;

    private Button mOnlyMaster;


    private String mSendBackDateThumbupCondtion,mSendBackDateFollowCondtion;
    private int RESULTCODE_COMMENT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.srl_commmentlist_loadmore);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(CommentListActivity.this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(CommentListActivity.this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                mRefreshConditionRl =refreshlayout;
                mStartingLoadId=0;

                 if(mIfOnlyCondition) {
                     replyUpdate();
                 }else{
                     hotReplyUpdate();
                 }
               mCommentlistRv.setOnTouchListener(new View.OnTouchListener() {
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
                }else{
                     if(mPrestrainCondition == false) {
                        mLoadMoreCondotionRl = refreshlayout;
                        mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                        if(mIfOnlyCondition) {
                            replyUpdate();

                        }else{
                            hotReplyUpdate();
                        }
                     } else {
                         mLoadMoreCondotionRl = refreshlayout;
                     }
                }
            }
        });

        mAVLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.titlebargreen_AVLoadingIndicatorView);
        mAVLoadingIndicatorView.show();
        mCommentlistRv = (RecyclerView) findViewById(R.id.rv_commentlist);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(CommentListActivity.this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mCommentlistRv.setLayoutManager(linearLayoutManager2);
        mOnlyMaster=(Button)findViewById(R.id.btn_commentlist_master);
        mOnlyMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnlyRefreshCondition==false) {
                    title.setText("加载中...");
                    mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
                    mAVLoadingIndicatorView.show();
                    mOnlyRefreshCondition=true;
                    if (mIfOnlyCondition==false) {
                        mIfOnlyCondition = true;
                        mOnlyMaster.setPadding(30, 5, 6, 6);
                        //mOnlyMaster.setBackground(getDrawable(R.drawable.forest_button));
                        //mOnlyMaster.setText("加入");
                        mOnlyMaster.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                        Drawable homepressed = getResources().getDrawable(R.mipmap.vector8, null);
                        homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                        mOnlyMaster.setCompoundDrawables(homepressed, null, null, null);
                        replyUpdate();
                    } else {
                        //mOnlyMaster.setWidth(64);
                        mIfOnlyCondition = false;
                        mOnlyMaster.setPadding(0, 0, 0, 0);
                        //button.setPadding(-30,-5,-6,-6);
                        //button=(Button)view.findViewById(R.id.rectangle_4);
                        // mOnlyMaster.setBackground(getDrawable(R.drawable.forest_button_white));
                        // mOnlyMaster.setText("已加入");
                        // mOnlyMaster.setGravity(Gravity.CENTER_VERTICAL);
                        mOnlyMaster.setCompoundDrawables(null, null, null, null);
                        mOnlyMaster.setTextColor(getResources().getColor(R.color.GrayScale_50));
                        hotReplyUpdate();
                    }



                }
            }
        });



        mPublishReplyEt = (EditText) findViewById(R.id.et_comment_search);

        SpannableString ss = new SpannableString("评论洞主：");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        mPublishReplyEt.setHint(new SpannedString(ss));





        mPublishReplyBtn = (Button) findViewById(R.id.btn_comment_sendmsg);
        EditTextReaction.ButtonReaction(mPublishReplyEt, mPublishReplyBtn);
        mPublishReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckingToken.IfTokenExist()){
                    RemoveOnScrollListener();
                    new Thread(new Runnable() {//加载纵向列表标题
                        @Override
                        public void run() {
                            Call<ResponseBody> call = request.replies_add("http://hustholetest.pivotstudio.cn/api/replies?hole_id=" + data[6] + "&content=" + mPublishReplyEt.getText().toString() + "&wanted_local_reply_id=" + reply_to_who);//进行封装
                            Log.d("", "http://hustholetest.pivotstudio.cn/api/replies?hole_id=" + data[6] + "&content=" + mPublishReplyEt.getText().toString() + "&wanted_local_reply_id=" + reply_to_who);
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                   if(response.code()==200) {
                                       String json = "null";
                                       try {
                                           if (response.body() != null) {
                                               json = response.body().string();
                                           }
                                           Log.e(TAG, json + "");
                                           InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                           // 隐藏软键盘
                                           imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                           mPublishReplyEt.setText("");
                                           mPublishReplyEt.setHint("评价洞主：");
                                           reply_to_who = "-1";
                                           mStartingLoadId = 0;
                                           mDetailReplyList = new ArrayList<>();
                                           if (mIfOnlyCondition) {
                                               replyUpdate();
                                           } else {
                                               hotReplyUpdate();
                                           }

                                       } catch (IOException e) {
                                           e.printStackTrace();
                                       }
                                   }else{
                                       String json = "null";
                                       String returncondition = null;
                                       if (response.errorBody() != null) {
                                           try {
                                               json = response.errorBody().string();
                                               JSONObject jsonObject = new JSONObject(json);
                                               returncondition = jsonObject.getString("msg");
                                               Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                           } catch (IOException | JSONException e) {
                                               e.printStackTrace();
                                           }
                                       }else{
                                           Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                       }


                                   }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(CommentListActivity.this,R.string.network_sendfailture,Toast.LENGTH_SHORT).show();

                                }


                            });

                        }
                    }).start();
                }else{
                    Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                    startActivity(intent);
            }
            }
        });


        back = (ConstraintLayout) findViewById(R.id.cl_titlebargreen_back);
        title = (TextView) findViewById(R.id.tv_titlebargreen_title);



        TokenInterceptor.getContext(CommentListActivity.this);
        //TokenInterceptor.getContext(RegisterActivity.this);
        //System.out.println("提交了context");
        retrofit= RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);

        data = getIntent().getStringArrayExtra(key);
        if(data == null){
            data_hole_id = getIntent().getStringExtra("data_hole_id");
            if(data_hole_id != null){
                retrofit2 = new Retrofit.Builder()
                        .baseUrl("http://hustholetest.pivotstudio.cn/api/forests/")
                        .client(OkHttpUtil.getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
               // Log.e(TAG, "token99：retrofit2");
                request2 = retrofit2.create(RequestInterface.class);
                data = new String[14];
                getData();
            }
        }
        else {

             if(mIfOnlyCondition) {
                 replyUpdate();
             }else{
                 hotReplyUpdate();
             }
        }







        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                getBack();
                finish();
            }
        });

     mReplyAdapter=new ReplyAdapter();
       //创建接口实例
       // replyUpdate(0);
    }


    @Override
    public void onBackPressed() {
        getBack();
        super.onBackPressed();

        //System.out.println("按下了back键   onBackPressed()");
    }
    private void getBack(){
        Intent data=new Intent();
        data.putExtra("ThumbupCondition",mSendBackDateThumbupCondtion);
        data.putExtra("FollowCondition",mSendBackDateFollowCondtion);
        setResult(RESULTCODE_COMMENT,data);
    }



   private void hotReplyUpdate(){
       new Thread(new Runnable() {//加载纵向列表标题
           @Override
           public void run() {
               //SharedPreferences editor = getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
               //String order = editor.getString("order", "true");
               Call<ResponseBody> call = request.hotReply(data[6],0,3);//进行封装
               call.enqueue(new Callback<ResponseBody>() {
                   @Override
                   public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                       if(response.code()==200) {
                           String json = "null";
                           try {
                               if (response.body() != null) {
                                   json = response.body().string();
                               }
                               JSONObject jsonObject = new JSONObject(json);
                               jsonArray2 = jsonObject.getJSONArray("msg");
                               replyUpdate();
                           } catch (IOException | JSONException e) {
                               e.printStackTrace();
                           }
                       }else{
                           FailureAction();
                           String json = "null";
                           String returncondition = null;
                           if (response.errorBody() != null) {
                               try {
                                   json = response.errorBody().string();
                                   JSONObject jsonObject = new JSONObject(json);
                                   returncondition = jsonObject.getString("msg");
                                   Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                               } catch (IOException | JSONException e) {
                                   e.printStackTrace();
                               }

                           }else{
                               Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                           }
                       }
                   }

                   @Override
                   public void onFailure(Call<ResponseBody> call, Throwable tr) {
                       Toast.makeText(CommentListActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                       FailureAction();
                   }
                   private void FailureAction(){
                       mAVLoadingIndicatorView.hide();
                       mAVLoadingIndicatorView.setVisibility(View.GONE);
                       title.setText("加载失败");
                       if(order_condition){
                           order_condition=false;

                       }
                       if(mDeleteCondition){
                           mDeleteCondition=false;
                       }
                       if(mRefreshConditionRl !=null){
                           mRefreshConditionRl.finishRefresh();
                           mRefreshConditionRl =null;
                           mCommentlistRv.setOnTouchListener(new View.OnTouchListener() {
                               @Override
                               public boolean onTouch(View v, MotionEvent event) {
                                   return false;
                               }
                           });
                           if(mIfFirstLoad) {}else{}
                       }else if(mLoadMoreCondotionRl!=null){
                           mLoadMoreCondotionRl.finishLoadMore();
                           mLoadMoreCondotionRl=null;
                           mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                           //mReplyAdapter.notifyDataSetChanged();
                           if(mPrestrainCondition==true){
                               mPrestrainCondition=false;
                           }
                       }else if(mPrestrainCondition==true){
                           mPrestrainCondition=false;
                           mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                       }else{
                       }

                   }

               });

           }
       }).start();
   }





    private void replyUpdate() {
        SpannableString ss = new SpannableString("评论洞主：");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        mPublishReplyEt.setHint(new SpannedString(ss));
    new Thread(new Runnable() {//加载纵向列表标题
        @Override
        public void run() {

            SharedPreferences editor = getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
            String order = editor.getString("order", "true");
            Call<ResponseBody> call;
            if(mIfOnlyCondition) {
                 call = request.owner(data[6] ,mStartingLoadId,CONSTANT_STANDARD_LOAD_SIZE, order);
                 jsonArray2=new JSONArray();
            }else{
                 call = request.replies("http://hustholetest.pivotstudio.cn/api/replies?hole_id=" + data[6] + "&is_descend=" + order + "&start_id=" + mStartingLoadId + "&list_size=" + CONSTANT_STANDARD_LOAD_SIZE);
            }//进行封装
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.code()==200) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }
                            Log.e(TAG, json + "");
                            if (mRefreshConditionRl != null) {
                                mDetailReplyList = new ArrayList<>();
                            }
                            if (order_condition) {
                                order_condition = false;
                                mAVLoadingIndicatorView.setVisibility(View.GONE);
                                mAVLoadingIndicatorView.hide();
                                //title.setText("#"+data[6]);
                                mDetailReplyList = new ArrayList<>();
                            }
                            if (mOnlyRefreshCondition) {
                                mOnlyRefreshCondition = false;
                                mAVLoadingIndicatorView.setVisibility(View.GONE);
                                mAVLoadingIndicatorView.hide();
                                mDetailReplyList = new ArrayList<>();
                            }
                            if (mDeleteCondition) {
                                mDeleteCondition = false;
                                mAVLoadingIndicatorView.setVisibility(View.GONE);
                                mAVLoadingIndicatorView.hide();
                                mDetailReplyList = new ArrayList<>();
                            }
                            JSONObject jsonObject = new JSONObject(json);

                            if (json.equals("{\"msg\":[]}") && mLoadMoreCondotionRl != null) {
                                Toast.makeText(CommentListActivity.this, "加载到底辣", Toast.LENGTH_SHORT).show();
                                mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                //if(mLoadMoreCondotionRl!=null){
                                mLoadMoreCondotionRl.finishLoadMore();
                                mLoadMoreCondotionRl = null;
                                //}
                            } else {
                                jsonArray = jsonObject.getJSONArray("msg");
                                new DownloadTask().execute();

                            }
                            mAVLoadingIndicatorView.hide();
                            mAVLoadingIndicatorView.setVisibility(View.GONE);
                            //mDetailReplyList = new String[jsonArray.length()][12];
                            // if(a==1){
                            //    refreshlayout1.finishRefresh();
                            //}
                            //new DownloadTask().execute();
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        String json = "null";
                        String returncondition = null;
                        if (response.errorBody() != null) {
                            try {
                                json = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(json);
                                returncondition = jsonObject.getString("msg");
                                Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                            FailureAction();
                        }else{
                            Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                    Toast.makeText(CommentListActivity.this,R.string.network_loadfailure,Toast.LENGTH_SHORT).show();
                    FailureAction();
                }
              private void FailureAction(){
                  mAVLoadingIndicatorView.hide();
                  mAVLoadingIndicatorView.setVisibility(View.GONE);
                  title.setText("加载失败");
                  if(mIfOnlyCondition&&mOnlyRefreshCondition) {
                      mOnlyRefreshCondition=false;
                      mIfOnlyCondition=false;
                      mIfOnlyCondition = false;
                      mOnlyMaster.setPadding(0, 0, 0, 0);
                      mOnlyMaster.setCompoundDrawables(null, null, null, null);
                      mOnlyMaster.setTextColor(getResources().getColor(R.color.GrayScale_50));
                  }else if(mIfOnlyCondition==false&&mOnlyRefreshCondition){
                      mOnlyRefreshCondition=false;
                      mIfOnlyCondition = true;
                      mOnlyMaster.setPadding(30, 5, 6, 6);
                      mOnlyMaster.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
                      Drawable homepressed = getResources().getDrawable(R.mipmap.vector8, null);
                      homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                      mOnlyMaster.setCompoundDrawables(homepressed, null, null, null);
                  }
                  if(order_condition){
                      order_condition=false;

                  }
                  if(mDeleteCondition){
                      mDeleteCondition=false;
                  }
                  if(mRefreshConditionRl !=null){
                      mRefreshConditionRl.finishRefresh();
                      mRefreshConditionRl =null;
                      mCommentlistRv.setOnTouchListener(new View.OnTouchListener() {
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
                      }
                  }else if(mPrestrainCondition==true){
                      mPrestrainCondition=false;
                      mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                  }else{
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

                    mCommentlistRv.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                   if(mIfFirstLoad) {
                       mCommentlistRv.setAdapter(mReplyAdapter);
                       mIfFirstLoad=false;
                   }else{
                       mReplyAdapter.notifyDataSetChanged();
                   }
                } else if(mLoadMoreCondotionRl!=null){
                    mLoadMoreCondotionRl.finishLoadMore();
                    mLoadMoreCondotionRl=null;
                    mReplyAdapter.notifyDataSetChanged();
                    if(mPrestrainCondition==true){
                        mPrestrainCondition=false;
                    }
                }else if(mPrestrainCondition==true){
                    mPrestrainCondition=false;
                    mReplyAdapter.notifyDataSetChanged();
                }else{
                    mIfFirstLoad=false;
                    //mReplyAdapter=new ReplyAdapter();
                   mCommentlistRv.setAdapter(mReplyAdapter);
                }


                //mCommentlistRv.setAdapter(new ReplyAdapter());
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    for(int f=0;f<jsonArray2.length();f++) {
                        JSONObject sonObject2 = jsonArray2.getJSONObject(f);
                        String[] singleReply2=new String[12];
                        singleReply2[0] = sonObject2.getString("alias");
                        singleReply2[1] = sonObject2.getString("content");
                        singleReply2[2] = sonObject2.getString("created_timestamp");
                        singleReply2[3] = sonObject2.getInt("hole_id")+"";
                        singleReply2[4] = sonObject2.getInt("id")+"";
                        singleReply2[5] = sonObject2.getBoolean("is_mine")+"";
                        singleReply2[6] =sonObject2.getBoolean("is_thumbup")+"";
                        singleReply2[7] = sonObject2.getInt("reply_local_id")+"";
                        singleReply2[8] = sonObject2.getInt("reply_to")+"";
                        singleReply2[9] = sonObject2.getString("reply_to_alias");
                        singleReply2[10] = sonObject2.getString("reply_to_content");
                        singleReply2[11] = sonObject2.getInt("thumbup_num")+"";
                        mDetailReplyList.add(singleReply2);
                    }

                    for(int f=0;f<jsonArray.length();f++) {
                        JSONObject sonObject = jsonArray.getJSONObject(f);
                        String[] singleReply=new String[12];
                       singleReply[0] = sonObject.getString("alias");
                       singleReply[1] = sonObject.getString("content");//
                       singleReply[2] = sonObject.getString("created_timestamp");//
                       singleReply[3] = sonObject.getInt("hole_id")+"";
                       singleReply[4] = sonObject.getInt("id")+"";
                       singleReply[5] = sonObject.getBoolean("is_mine")+"";
                       singleReply[6] =sonObject.getBoolean("is_thumbup")+"";//
                       singleReply[7] = sonObject.getInt("reply_local_id")+"";//
                       singleReply[8] = sonObject.getInt("reply_to")+"";//
                       singleReply[9] = sonObject.getString("reply_to_alias");//
                       singleReply[10] = sonObject.getString("reply_to_content");//
                       singleReply[11] = sonObject.getInt("thumbup_num")+"";//
                       mDetailReplyList.add(singleReply);
                    }




                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                return null;
            }
        }

   
    public static Intent newIntent(Context packageContext, String[] data){
       // Log.d("data[2]2",data[2]);
        Intent intent = new Intent(packageContext, CommentListActivity.class);
        intent.putExtra(key,data);
        return intent;
    }


    public class ReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_NOMESSAGE = 2;
        private int mHeaderCount=1;//头部View个数
        //private Boolean more_condition=false;
       // private  ConstraintLayout morewhat0;
        @Override
        public int getItemViewType(int position) {
            if (mHeaderCount != 0 && position < mHeaderCount) {
                return ITEM_TYPE_HEADER;
            }else if(position!=0&&mDetailReplyList.size()!=0){
                return ITEM_TYPE_CONTENT;
            }else{
                return ITEM_TYPE_NOMESSAGE;
            }
        }

        public class HeadHolder extends RecyclerView.ViewHolder{
            private Button forest_name;
            private TextView created_timestamp,content,thumbup_num,reply_num,follow_num,more_2;
            private ConstraintLayout thumbup,reply,follow;
            private ImageView is_thumbup,is_reply,is_follow;
            private ImageView orders,more,more_1;
            private ConstraintLayout morewhat,orderCl;
            private Boolean thumbupCondition=false,followCondition=false;
            public HeadHolder(View view){
                super(view);
                thumbup=(ConstraintLayout)view.findViewById(R.id.cl_commenthead_thumbup);
                reply=(ConstraintLayout)view.findViewById(R.id.cl_commenthead_reply);
                follow=(ConstraintLayout)view.findViewById(R.id.cl_commenthead_follow);
                orderCl=(ConstraintLayout)view.findViewById(R.id.cl_commenthead_changesequence);
                forest_name = (Button) view.findViewById(R.id.btn_commenthead_jumptodetailforest);
                created_timestamp = (TextView) view.findViewById(R.id.tv_commenthead_time);
                content = (TextView) view.findViewById(R.id.tv_commenthead_content);
                thumbup_num = (TextView) view.findViewById(R.id.tv_commenthead_thumbupnum);
                reply_num = (TextView) view.findViewById(R.id.tv_commenthead_replynum);
                follow_num = (TextView) view.findViewById(R.id.tv_commenthead_follownum);
                is_thumbup = (ImageView) view.findViewById(R.id.iv_commenthead_thumbup);
                is_reply = (ImageView) view.findViewById(R.id.iv_commenthead_reply);
                is_follow = (ImageView) view.findViewById(R.id.iv_commenthead_follow);
                orders=(ImageView)view.findViewById(R.id.iv_commenthead_changesequence);
                more=(ImageView)view.findViewById(R.id.iv_commenthead_more);
                more_1=(ImageView)view.findViewById(R.id.iv_commenthead_moreicon);
                more_2=(TextView)view.findViewById(R.id.tv_commenthead_moretext);
                morewhat=(ConstraintLayout)view.findViewById(R.id.cl_commenthead_morelist);
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

                        if (mOnscrollListener != null) {
                            mCommentlistRv.removeOnScrollListener(mOnscrollListener);
                        }
                        mOnscrollListener = new RecyclerView.OnScrollListener() {
                            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                //super.onScrollStateChanged(recyclerView, newState);
                                mMoreWhatCl.setVisibility(View.INVISIBLE);
                                more_condition = false;
                                mCommentlistRv.removeOnScrollListener(mOnscrollListener);
                            }
                        };
                        mCommentlistRv.addOnScrollListener(mOnscrollListener);


                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        if(CheckingToken.IfTokenExist()){
                            RemoveOnScrollListener();
                            if (data[9].equals("true")) {
                                new Thread(new Runnable() {//加载纵向列表标题
                                    @Override
                                    public void run() {
                                        //Call<ResponseBody> call = request.delete_hole("http://hustholetest.pivotstudio.cn/api/holes/" + holenumber);//进行封装
                                        Call<ResponseBody> call = request.delete_hole(data[6]);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.code() == 200) {
                                                    String json = "null";
                                                    String returncondition = null;
                                                    getBack();
                                                    finish();
                                                    if (response.body() != null) {
                                                        try {
                                                            json = response.body().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(CommentListActivity.this, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.errorBody() != null) {
                                                        try {
                                                            json = response.errorBody().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //FailureAction();
                                                    } else {
                                                        Toast.makeText(CommentListActivity.this, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(CommentListActivity.this, R.string.network_deletefailture, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {//加载纵向列表标题
                                    @Override
                                    public void run() {
                                        //HashMap map = new HashMap();
                                        //map.put("hole_id", data[6]);
                                       // map.put("reply_local_id", -1);
                                        Call<ResponseBody> call = request.report_2("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + data[6] + "&reply_local_id= -1");
                                        //Call<ResponseBody> call = request.report(map);//进行封装
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if(response.code()==200) {
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.body() != null) {
                                                        try {
                                                            json = response.body().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(CommentListActivity.this, "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    //followCondition = false;
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.errorBody() != null) {
                                                        try {
                                                            json = response.errorBody().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //FailureAction();
                                                    }else{
                                                        Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(CommentListActivity.this, R.string.network_reportfailture, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).start();
                            }
                        }else{
                            Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });

                forest_name.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = DetailForestActivity.newIntent(CommentListActivity.this, data[4]);
                        startActivity(intent);
                    }
                });

                orderCl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(order_condition==false) {
                            order_condition=true;
                            SharedPreferences editor = getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
                            String order = editor.getString("order", "true");
                            order = order.equals("false") ? "true" : "false";
                            SharedPreferences.Editor editor2 = getSharedPreferences("Depository2", Context.MODE_PRIVATE).edit();//获取编辑器
                            editor2.putString("order", order);
                            editor2.commit();//提交

                            mAVLoadingIndicatorView.setVisibility(View.VISIBLE);
                            mAVLoadingIndicatorView.show();
                            title.setText("加载中...");

                            mStartingLoadId = 0;
                             if(mIfOnlyCondition) {
                            replyUpdate();

                        }else{
                            hotReplyUpdate();
                        }
                        }

                    }
                });


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RemoveOnScrollListener();
                        SpannableString ss = new SpannableString("评论洞主：");
                        // 新建一个属性对象,设置文字的大小
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
                        // 附加属性到文本
                        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 设置hint
                        mPublishReplyEt.setHint(new SpannedString(ss));
                        reply_to_who="-1";
                    }
                });



                thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            //thumbupCondition=true;
                            if(thumbupCondition==false) {
                                thumbupCondition = true;
                                if (data[11].equals("false")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + data[6] + "/-1");//进行封装
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_thumbup.setImageResource(R.mipmap.active);
                                                        data[11] = "true";
                                                        data[13] = (Integer.parseInt(data[13]) + 1) + "";
                                                        thumbupCondition = false;
                                                        thumbup_num.setText(data[13]);
                                                        mSendBackDateThumbupCondtion=data[11];
                                                    }else{
                                                        FailureAction();
                                                        String json = "null";
                                                        String returncondition = null;
                                                         if (response.errorBody() != null) {
                                                             try {
                                                                 json = response.errorBody().string();
                                                                 JSONObject jsonObject = new JSONObject(json);
                                                                 returncondition = jsonObject.getString("msg");
                                                                 Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                             } catch (IOException | JSONException e) {
                                                                 e.printStackTrace();
                                                             }
                                                        }else{
                                                                Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(CommentListActivity.this, R.string.network_thumbupfailure_, Toast.LENGTH_SHORT).show();
                                                    FailureAction();
                                                }
                                                private void FailureAction(){
                                                    thumbupCondition=false;
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + data[6] + "/-1");//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_thumbup.setImageResource(R.mipmap.inactive);
                                                        data[11] = "false";
                                                        data[13] = (Integer.parseInt(data[13]) - 1) + "";
                                                        thumbupCondition = false;
                                                        thumbup_num.setText(data[13]);
                                                        mSendBackDateThumbupCondtion=data[11];
                                                    }else{
                                                        FailureAction();
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                json = response.errorBody().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }else{
                                                            Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    FailureAction();
                                                    Toast.makeText(CommentListActivity.this, R.string.network_notthumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                                private void FailureAction(){
                                                    thumbupCondition=false;
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }else{
                            Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });
                follow.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick (View v){
                            if(CheckingToken.IfTokenExist()){
                                if(followCondition==false) {
                                    followCondition=true;
                                    if (data[8].equals("false")) {
                                        new Thread(new Runnable() {//加载纵向列表标题
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/" + data[6]);//进行封装Log.e(TAG, "token2：");
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code()==200) {
                                                            is_follow.setImageResource(R.mipmap.active_3);
                                                            data[8] = "true";
                                                            data[3] = (Integer.parseInt(data[3]) + 1) + "";
                                                            followCondition = false;
                                                            follow_num.setText(data[3]);
                                                            mSendBackDateFollowCondtion=data[8];
                                                        }else{
                                                            FailureAction();
                                                            //followCondition = false;
                                                            String json = "null";
                                                            String returncondition = null;
                                                            if (response.errorBody() != null) {
                                                                try {
                                                                    json = response.errorBody().string();
                                                                    JSONObject jsonObject = new JSONObject(json);
                                                                    returncondition = jsonObject.getString("msg");
                                                                    Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                                } catch (IOException | JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }else{
                                                                Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        FailureAction();
                                                        Toast.makeText(CommentListActivity.this, R.string.network_followfailure_, Toast.LENGTH_SHORT).show();

                                                    }
                                                    private void FailureAction(){
                                                        followCondition=false;
                                                    }
                                                });
                                            }
                                        }).start();
                                    } else {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/" + data[6]);//进行封装
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code()==200) {
                                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                                            data[8] = "false";
                                                            data[3] = (Integer.parseInt(data[3]) - 1) + "";
                                                            followCondition = false;
                                                            follow_num.setText(data[3]);
                                                            mSendBackDateFollowCondtion=data[8];
                                                        }else{
                                                            FailureAction();
                                                            //followCondition = false;
                                                            String json = "null";
                                                            String returncondition = null;
                                                            if (response.errorBody() != null) {
                                                                try {
                                                                    json = response.errorBody().string();
                                                                    JSONObject jsonObject = new JSONObject(json);
                                                                    returncondition = jsonObject.getString("msg");
                                                                    Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                                } catch (IOException | JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }else{
                                                                Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                       FailureAction();
                                                        Toast.makeText(CommentListActivity.this, R.string.network_notfollowfailure_, Toast.LENGTH_SHORT).show();
                                                    }
                                                    private void FailureAction(){
                                                        followCondition=false;
                                                    }

                                                });
                                            }
                                        }).start();
                                    }
                                }
                            }else{
                                Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                                startActivity(intent);
                            }
                        }

                });



            }
            public void bind(int position){
                SharedPreferences editor =getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
                String  order = editor.getString("order", "true");
                if(order.equals("true")){
                    orders.setImageResource(R.mipmap.group111);
                }else{
                    orders.setImageResource(R.mipmap.group112);
                }

                if (data[5].equals("")) {
                    forest_name.setVisibility(View.INVISIBLE);
                } else {
                    forest_name.setVisibility(View.VISIBLE);
                    forest_name.setText("  " + data[5] + "   ");
                }
                created_timestamp.setText(TimeCount.time(data[2]));
                content.setText(data[1]);
                thumbup_num.setText(data[13]);
                reply_num.setText(data[12]);
                follow_num.setText(data[3]);

                title.setText("#"+data[6]);
                Log.d("title",data[6]+"");
                if (data[8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                }else{
                    is_follow.setImageResource(R.mipmap.inactive_3);
                }
                if(data[9].equals("true")){
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }else{
                    more_1.setImageResource(R.mipmap.vector4);
                    more_2.setText("举报");
                }
                if (data[10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }else{
                    is_reply.setImageResource(R.mipmap.inactive_2);
                }
                if (data[11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }else{
                    is_thumbup.setImageResource(R.mipmap.inactive);
                }
            }
        }
        public class MessageHolder extends RecyclerView.ViewHolder{
            ImageView nomessage;
            TextView nomessageTv;
            public MessageHolder(View view) {
                super(view);
                nomessage=(ImageView)view.findViewById(R.id.iv_nomessage);
                nomessageTv=(TextView)view.findViewById(R.id.tv_nomessage);
            }
            public void bind(int position){
               // Toast.makeText(CommentListActivity.this,""+mDetailReplyList.size(),Toast.LENGTH_SHORT).show();
                if(position==1&&mDetailReplyList.size()==0) {
                    nomessage.setImageResource(R.mipmap.nomessage);
                    nomessageTv.setText("还没有评论哦~");
                }else{
                    nomessageTv.setText("");
                }
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView alias_me,content, created_timestamp,thumbup_num,reply_tosomebody,more_2,Hot;
            private ImageView  is_thumbup,more,more_1;
            private ConstraintLayout linearLayout,morewhat,thumbup;
            private Button line;
            private int position;
            private Boolean thumbupCondition=false,followCondition=false;

            public ViewHolder(View view) {
                super(view);

                thumbup=(ConstraintLayout)view.findViewById(R.id.cl_commentreply_thumbup);

                alias_me=(TextView)view.findViewById(R.id.tv_commentreply_title);
                content=(TextView)view.findViewById(R.id.tv_commentreply_content);
                created_timestamp=(TextView)view.findViewById(R.id.tv_commentreply_time);
                thumbup_num=(TextView)view.findViewById(R.id.tv_commentreply_thumbupnumber);
                is_thumbup=(ImageView)view.findViewById(R.id.iv_commentreply_thumbup);
                reply_tosomebody=(TextView)view.findViewById(R.id.tv_commentreply_detailreplycontent);
                linearLayout=(ConstraintLayout) view.findViewById(R.id.ll_commentreply_replycontent);
                line=(Button)view.findViewById(R.id.textView72);
                Hot=(TextView)view.findViewById(R.id.tv_commentreply_ifhot);
                more=(ImageView)view.findViewById(R.id.iv_commentreply_more);
                more_1=(ImageView)view.findViewById(R.id.iv_commentreply_moreicon);
                more_2=(TextView)view.findViewById(R.id.tv_commentreply_moretext);
                morewhat=(ConstraintLayout)view.findViewById(R.id.cl_commentreply_more);
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
                        if (mOnscrollListener != null) {
                            mCommentlistRv.removeOnScrollListener(mOnscrollListener);
                        }
                        mOnscrollListener = new RecyclerView.OnScrollListener() {
                            //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                //super.onScrollStateChanged(recyclerView, newState);
                                mMoreWhatCl.setVisibility(View.INVISIBLE);
                                more_condition = false;
                                mCommentlistRv.removeOnScrollListener(mOnscrollListener);
                            }
                        };
                        mCommentlistRv.addOnScrollListener(mOnscrollListener);
                    }
                });
                morewhat.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v){
                        if(CheckingToken.IfTokenExist()){
                            RemoveOnScrollListener();
                           // morewhat.setVisibility(View.INVISIBLE);
                           // more_condition = false;
                            if (mDetailReplyList.get(position)[5].equals("true")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.delete_hole_2("http://hustholetest.pivotstudio.cn/api/replies/" + data[6] + "/" + mDetailReplyList.get(position)[7]);//进行封装
                                        Log.e(TAG, "http://hustholetest.pivotstudio.cn/api/replies/" + data[6] + "/" + mDetailReplyList.get(position)[7]);
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if(response.code()==200) {
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.body() != null) {
                                                        try {
                                                            json = response.body().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            mDeleteCondition = true;
                                                            mStartingLoadId = 0;
                                                            if (mIfOnlyCondition) {
                                                                replyUpdate();
                                                            } else {
                                                                hotReplyUpdate();
                                                            }
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(CommentListActivity.this, "删除失败，超过可删除的时间范围", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    //followCondition = false;
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.errorBody() != null) {
                                                        try {
                                                            json = response.errorBody().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                                    e.printStackTrace();
                                                        }
                                                                //FailureAction();
                                                    }else{
                                                        Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(CommentListActivity.this, R.string.network_deletefailture, Toast.LENGTH_SHORT).show();
                                               // mDeleteCondition=false;
                                            }
                                        });
                                    }
                                }).start();
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Call<ResponseBody> call = request.report_2("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + data[6] + "&reply_local_id=" + mDetailReplyList.get(position)[7]);//进行封装
                                        Log.e(TAG, "http://hustholetest.pivotstudio.cn/api/replies/" + data[6] + "/" + mDetailReplyList.get(position)[7]);
                                        call.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if(response.code()==200) {
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.body() != null) {
                                                        try {
                                                            json = response.body().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        Toast.makeText(CommentListActivity.this, "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报", Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    //followCondition = false;
                                                    String json = "null";
                                                    String returncondition = null;
                                                    if (response.errorBody() != null) {
                                                        try {
                                                            json = response.errorBody().string();
                                                            JSONObject jsonObject = new JSONObject(json);
                                                            returncondition = jsonObject.getString("msg");
                                                            Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                        } catch (IOException | JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                                //FailureAction();
                                                    }else{
                                                        Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                Toast.makeText(CommentListActivity.this, R.string.network_reportfailture, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }).start();
                            }
                        }else{
                            Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });





                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RemoveOnScrollListener();
                        SpannableString ss = new SpannableString("回复@"+ mDetailReplyList.get(position)[0]+(mDetailReplyList.get(position)[5].equals("true")?"(我)":"")+"：");
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
                        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        mPublishReplyEt.setHint(new SpannedString(ss));
                        mPublishReplyEt.requestFocus();
                        InputMethodManager imm = (InputMethodManager) mPublishReplyEt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        reply_to_who= mDetailReplyList.get(position)[7];
                    }
                });



                thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(CheckingToken.IfTokenExist()) {
                            if(thumbupCondition==false) {
                                thumbupCondition=true;
                                if (mDetailReplyList.get(position)[6].equals("false")) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + data[6] + "/" + mDetailReplyList.get(position)[7]);//进行封装
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_thumbup.setImageResource(R.mipmap.active);
                                                        mDetailReplyList.get(position)[6] = "true";
                                                        mDetailReplyList.get(position)[11] = (Integer.parseInt(mDetailReplyList.get(position)[11]) + 1) + "";
                                                        thumbupCondition = false;
                                                        thumbup_num.setText(mDetailReplyList.get(position)[11]);
                                                        if(position<(jsonArray2.length())){
                                                            for(int a=position+1;a<mDetailReplyList.size();a++){
                                                               if( mDetailReplyList.get(a)[7].equals(mDetailReplyList.get(position)[7])){
                                                                   mDetailReplyList.get(a)[6] = "true";
                                                                   mDetailReplyList.get(a)[11] = (Integer.parseInt(mDetailReplyList.get(a)[11]) + 1) + "";
                                                                   mReplyAdapter.notifyItemChanged(a+1,mDetailReplyList.size()+1);
                                                               }
                                                            }
                                                        }else{
                                                            for(int a=0;a<jsonArray2.length();a++){
                                                                if( mDetailReplyList.get(a)[7].equals(mDetailReplyList.get(position)[7])){
                                                                    mDetailReplyList.get(a)[6] = "true";
                                                                    mDetailReplyList.get(a)[11] = (Integer.parseInt(mDetailReplyList.get(a)[11]) + 1) + "";
                                                                    mReplyAdapter.notifyItemChanged(a+1,mDetailReplyList.size()+1);
                                                                }
                                                            }
                                                        }
                                                    }else{
                                                        FailureAction();
                                                       // followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                json = response.errorBody().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }else{
                                                            Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    FailureAction();
                                                    Toast.makeText(CommentListActivity.this, R.string.network_thumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                                private void FailureAction(){
                                                    thumbupCondition=false;
                                                }
                                            });
                                        }
                                    }).start();
                                }else{
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/" + data[6] + "/" + mDetailReplyList.get(position)[6]);//进行封装
                                            Log.e(TAG, "token2：");
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        is_thumbup.setImageResource(R.mipmap.inactive);
                                                        mDetailReplyList.get(position)[6] = "false";
                                                        mDetailReplyList.get(position)[11] = (Integer.parseInt(mDetailReplyList.get(position)[11]) - 1) + "";
                                                        thumbupCondition = false;
                                                        thumbup_num.setText(mDetailReplyList.get(position)[11]);
                                                        if(position<(jsonArray2.length())){
                                                            for(int a=position+1;a<mDetailReplyList.size();a++){
                                                                if( mDetailReplyList.get(a)[7].equals(mDetailReplyList.get(position)[7])){
                                                                    mDetailReplyList.get(a)[6] = "false";
                                                                    mDetailReplyList.get(a)[11] = (Integer.parseInt(mDetailReplyList.get(a)[11]) - 1) + "";
                                                                    mReplyAdapter.notifyItemChanged(a+1,mDetailReplyList.size()+1);
                                                                }
                                                            }
                                                        }else{
                                                            for(int a=0;a<jsonArray2.length();a++){
                                                                if( mDetailReplyList.get(a)[7].equals(mDetailReplyList.get(position)[7])){
                                                                    mDetailReplyList.get(a)[6] = "true";
                                                                    mDetailReplyList.get(a)[11] = (Integer.parseInt(mDetailReplyList.get(a)[11]) + 1) + "";
                                                                    mReplyAdapter.notifyItemChanged(a+1,mDetailReplyList.size()+1);
                                                                }
                                                            }
                                                        }
                                                    }else{
                                                       // followCondition = false;
                                                        String json = "null";
                                                        String returncondition = null;
                                                        if (response.errorBody() != null) {
                                                            try {
                                                                json = response.errorBody().string();
                                                                JSONObject jsonObject = new JSONObject(json);
                                                                returncondition = jsonObject.getString("msg");
                                                                Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            FailureAction();
                                                        }else{
                                                            Toast.makeText(CommentListActivity.this,R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    FailureAction();
                                                    Toast.makeText(CommentListActivity.this, R.string.network_notthumbupfailure_, Toast.LENGTH_SHORT).show();
                                                }
                                                private void FailureAction(){
                                                    thumbupCondition=false;
                                                }
                                            });
                                        }
                                    }).start();
                                }
                            }
                        }else{
                            Intent intent=new Intent(CommentListActivity.this, EmailVerifyActivity.class);
                            startActivity(intent);
                        }
                    }
                });




            }
            public void bind(int position){
                if(position<(jsonArray2.length())){
                    Hot.setVisibility(View.VISIBLE);
                }else{
                    Hot.setVisibility(View.INVISIBLE);
                }
                this.position=position;
                 alias_me.setText(mDetailReplyList.get(position)[0]+(mDetailReplyList.get(position)[5].equals("true")?"(我)":""));
                 content.setText(mDetailReplyList.get(position)[1]);
                 created_timestamp.setText(TimeCount.time( mDetailReplyList.get(position)[2]));
                 thumbup_num.setText(mDetailReplyList.get(position)[11]);
                if (mDetailReplyList.get(position)[6].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }else{
                    is_thumbup.setImageResource(R.mipmap.inactive);
                }
                if(mDetailReplyList.get(position)[8].equals("-1")){
                    //linearLayout.setVisibility(View.INVISIBLE);
                    ConstraintLayout.LayoutParams linearParams0 =(ConstraintLayout.LayoutParams) reply_tosomebody.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
                    linearParams0.height =0;
                    linearParams0.width=0;
                    linearParams0.topMargin=0;
                    linearParams0.bottomMargin=0;// 控件的宽强制设成30
                    reply_tosomebody.setLayoutParams(linearParams0);
                   ConstraintLayout.LayoutParams linearParams =(ConstraintLayout.LayoutParams) linearLayout.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
                    linearParams.height =0;
                    linearParams.width=0;
                    linearLayout.setLayoutParams(linearParams);

                }else {
                    Log.d("", mDetailReplyList.get(position)[0]+ mDetailReplyList.get(position)[1]+ mDetailReplyList.get(position)[2]+ mDetailReplyList.get(position)[8]);
                    //linearLayout.setVisibility(View.VISIBLE);
                    SpannableStringBuilder builder = new SpannableStringBuilder(""+ mDetailReplyList.get(position)[9]+":"+ mDetailReplyList.get(position)[10]);
//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.GrayScale_0));
                    //ForegroundColorSpan whiteSpan = new ForegroundColorSpan(getResources().getColor(R.color.GrayScale_80));
                    builder.setSpan(redSpan,0, mDetailReplyList.get(position)[9].length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //builder.setSpan(whiteSpan, detailreply[position][9].length(), 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    reply_tosomebody.setText(builder);
                }
                if(mDetailReplyList.get(position)[5].equals("true")){
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }else{
                    more_1.setImageResource(R.mipmap.vector4);
                    more_2.setText("举报");
                }
                line.setHeight(getViewHeight(content,true));
            }
        }
        public ReplyAdapter(){

        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            if (viewType ==ITEM_TYPE_HEADER) {
                return new ReplyAdapter.HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_commenthead,parent,false));
            } else if (viewType==ITEM_TYPE_CONTENT) {
                return new ReplyAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_commentreply,parent,false));
            }else if(viewType==ITEM_TYPE_NOMESSAGE){
                return new ReplyAdapter.MessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_commentnomessage,parent,false));
            }
            return null;

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);

            if (holder instanceof ReplyAdapter.HeadHolder) {
                ((ReplyAdapter.HeadHolder)holder).bind(position);
            } else if (holder instanceof ReplyAdapter.ViewHolder) {
                ((ReplyAdapter.ViewHolder) holder).bind(position-1);
            }else if (holder instanceof ReplyAdapter.MessageHolder) {
                ((ReplyAdapter.MessageHolder) holder).bind(position);
            }

            if(position== mDetailReplyList.size()-3&&(mDetailReplyList.size()%20==0)){
                mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                mPrestrainCondition=true;
                 if(mIfOnlyCondition) {
                            replyUpdate();

                        }else{
                            hotReplyUpdate();
                        }
            }


            }
        @Override
        public int getItemCount() {
            if(mDetailReplyList.size()==0){
                return 2;
            }
            return mDetailReplyList.size()+1;

        }
    }

    public static int getViewHeight(View view, boolean isHeight){
        int result;
        if(view==null)return 0;
        if(isHeight){
            int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            view.measure(h,0);
            result =view.getMeasuredHeight();
        }else{
            int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            view.measure(0,w);
            result =view.getMeasuredWidth();
        }
        return result;
    }

    //点击通知到树洞，先获取data
    public void getData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call<ResponseBody> call = request2.holes2("http://hustholetest.pivotstudio.cn/api/holes/"+data_hole_id);
                Log.e("tag", "token2：");
                Log.d("tag", "run: request2");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200) {
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                JSONObject jsonObject = new JSONObject(json);
                                //data[0]=jsonObject.getString("background_image_url");
                                data[1] = jsonObject.getString("content");
                                data[2] = jsonObject.getString("created_timestamp");
                                data[3] = jsonObject.getInt("follow_num") + "";
                                data[4] = jsonObject.getInt("forest_id") + "";
                                data[5] = jsonObject.getString("forest_name");
                                data[6] = jsonObject.getInt("hole_id") + "";
                                data[8] = jsonObject.getBoolean("is_follow") + "";
                                data[9] = jsonObject.getBoolean("is_mine") + "";
                                data[10] = jsonObject.getBoolean("is_reply") + "";
                                data[11] = jsonObject.getBoolean("is_thumbup") + "";
                                data[12] = jsonObject.getInt("reply_num") + "";
                                data[13] = jsonObject.getInt("thumbup_num") + "";
                                hotReplyUpdate();
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            //followCondition = false;
                            String json = "null";
                            String returncondition = null;
                            if (response.errorBody() != null) {
                                try {
                                    json = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(json);
                                    returncondition = jsonObject.getString("msg");
                                    Toast.makeText(CommentListActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(CommentListActivity.this, R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        Toast.makeText(CommentListActivity.this, R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
    private void RemoveOnScrollListener() {
        if (mOnscrollListener != null) {
            mCommentlistRv.removeOnScrollListener(mOnscrollListener);
        }
        if(mMoreWhatCl!=null) {
            mMoreWhatCl.setVisibility(View.INVISIBLE);
            more_condition = false;
        }
    }
}
