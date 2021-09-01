package com.example.hustholetest1.view.homescreen.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hustholetest1.model.CheckingToken;
import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.model.HomePageRefreshFooter;
import com.example.hustholetest1.model.MaxHeightRecyclerView;
import com.example.hustholetest1.model.TimeCount;
import com.example.hustholetest1.network.ErrorMsg;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.model.StandardRefreshHeader;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.R;
import com.example.hustholetest1.view.emailverify.EmailVerifyActivity;
import com.example.hustholetest1.view.homescreen.commentlist.CommentListActivity;
import com.example.hustholetest1.view.homescreen.forest.DetailForestActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class HomePageFragment extends Fragment {
    private Button mNewPublishBtn, mNewCommentBtn;
    private EditText mSearchEt;
    private ImageView mTriangleIv;
    private TextView mForestSquareTv;
    private boolean flag=false;
    private PopupWindow mSlideBoxPpw, mDarkScreenPpw;
    private ConstraintLayout mWherePpwCl;
    private MaxHeightRecyclerView mHomePageHolesRv;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private ArrayList<String[]> mHompageHolesList=new ArrayList<>();
    private int mStartingLoadId = 0,mLastLoadId;
    private Boolean mHolesSequenceCondition =true,mPpwExistenceCondition=false;
    private HoleAdapter mHomePageHolesAdpter;
    private RefreshLayout mRefreshConditionRl,mLoadMoreConditionRl;
    private RecyclerView.OnScrollListener mOnscrollListener;


    private int CONSTANT_STANDARD_LOAD_SIZE = 20;
    private Boolean mIfFirstLoad=true;
    private Boolean mPrestrainCondition=false;
    private Boolean mDeleteCondition=false;
    private Boolean more_condition=false;
    private Boolean mHomeScreenUpdate=false;
    private Boolean mSearchCondition=false,mIfSearch=false;
    private  ConstraintLayout mMoreWhatCl;
    private Boolean mSingleHoleCondition=false;

    private ImageView mReturnIsThumbup,mReturnIsReply,mReturnIsFollow;
    private TextView mReturnThumbupNUmber,mReturnReplyNumber,mReturnFollowNumber;
    private int mReturnPosition;
    private int RESULTCODE_COMMENT=1,REQUESTCODE_COMMENT=3;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULTCODE_COMMENT){
            return;
        }
        if(requestCode==REQUESTCODE_COMMENT){
            String thumbupCondition=data.getStringExtra("ThumbupCondition");
            String followCondition=data.getStringExtra("FollowCondition");
            if(thumbupCondition!=null){
                if(thumbupCondition.equals("true")&&  mHompageHolesList.get(mReturnPosition)[11].equals("false")){
                    mReturnIsThumbup.setImageResource(R.mipmap.active);
                    mHompageHolesList.get(mReturnPosition)[11] = "true";
                    mHompageHolesList.get(mReturnPosition)[13] = (Integer.parseInt(mHompageHolesList.get(mReturnPosition)[13]) + 1) + "";
                    mReturnThumbupNUmber.setText(mHompageHolesList.get(mReturnPosition)[13]);
                }else if(thumbupCondition.equals("false")&&  mHompageHolesList.get(mReturnPosition)[11].equals("true")){
                    mReturnIsThumbup.setImageResource(R.mipmap.inactive);
                    mHompageHolesList.get(mReturnPosition)[11] = "false";
                    mHompageHolesList.get(mReturnPosition)[13] = (Integer.parseInt(mHompageHolesList.get(mReturnPosition)[13]) - 1) + "";
                    mReturnThumbupNUmber.setText(mHompageHolesList.get(mReturnPosition)[13]);
                }
            }
            if(followCondition!=null){
                if(followCondition.equals("true")&&  mHompageHolesList.get(mReturnPosition)[8].equals("false")){
                    mReturnIsFollow.setImageResource(R.mipmap.active_3);
                    mHompageHolesList.get(mReturnPosition )[8] = "true";
                    mHompageHolesList.get(mReturnPosition )[3] = (Integer.parseInt(mHompageHolesList.get(mReturnPosition)[3]) + 1) + "";
                    mReturnFollowNumber.setText(mHompageHolesList.get(mReturnPosition)[3]);
                }else if(followCondition.equals("false")&& mHompageHolesList.get(mReturnPosition)[8].equals("true")){
                    mReturnIsFollow.setImageResource(R.mipmap.inactive);
                    mHompageHolesList.get(mReturnPosition )[8] = "false";
                    mHompageHolesList.get(mReturnPosition )[3] = (Integer.parseInt(mHompageHolesList.get(mReturnPosition )[3]) - 1) + "";
                    mReturnFollowNumber.setText(mHompageHolesList.get(mReturnPosition)[3]);
                }
            }
        }
    }

    public static HomePageFragment newInstance() {
        HomePageFragment fragment = new HomePageFragment();
        return fragment;
    }
    private void noSearch(){
        mIfSearch=false;
        if(mSearchEt.getText()!=null){
            mSearchEt.setText("");
        }
    }
    private static Pattern NUMBER_PATTERN = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    public static boolean checkStrIsNum02(String str) {
        String bigStr;
        try {
            /** 先将str转成BigDecimal，然后在转成String */
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            /** 如果转换数字失败，说明该str并非全部为数字 */
            return false;
        }
        Matcher isNum = NUMBER_PATTERN.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
    private void searchUpdate(){
        if(checkStrIsNum02(mSearchEt.getText().toString())){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<ResponseBody> call = request.holes2(RetrofitManager.API+"holes/"+mSearchEt.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            mSearchCondition=false;
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                            if(response.code()==200) {

                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }
                                    mStartingLoadId=0;
                                    mSingleHoleCondition=true;
                                    mHompageHolesList=new ArrayList<>();
                                    JSONObject sonObject = new JSONObject(json);
                                    String[] SingleHole = new String[14];
                                    //detailforest[f][0] = sonObject.getString("background_image_url");
                                    SingleHole[1] = sonObject.getString("content");
                                    SingleHole[2] = sonObject.getString("created_timestamp");
                                    SingleHole[3] = sonObject.getInt("follow_num") + "";
                                    SingleHole[4] = sonObject.getInt("forest_id") + "";
                                    SingleHole[5] = sonObject.getString("forest_name");
                                    SingleHole[6] = sonObject.getInt("hole_id") + "";
                                    //detailforest2[f][1] = sonObject.getString("image");
                                    SingleHole[8] = sonObject.getBoolean("is_follow") + "";
                                    SingleHole[9] = sonObject.getBoolean("is_mine") + "";
                                    SingleHole[10] = sonObject.getBoolean("is_reply") + "";
                                    SingleHole[11] = sonObject.getBoolean("is_thumbup") + "";
                                    SingleHole[12] = sonObject.getInt("reply_num") + "";
                                    SingleHole[13] = sonObject.getInt("thumbup_num") + "";
                                    mHompageHolesList.add(SingleHole);
                                    mHomePageHolesRv.setAdapter(mHomePageHolesAdpter);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                //followCondition = false;
                                String json = "null";
                                if (response.errorBody() != null) {
                                    try {
                                        json = response.errorBody().string();
                                        Toast.makeText(getContext(), json, Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.network_unknownfailture, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            mSearchCondition = false;
                            Toast.makeText(getContext(), R.string.network_loadfailure, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();


        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {


                    Call<ResponseBody> call = request.search_hole(mSearchEt.getText().toString(), mStartingLoadId, CONSTANT_STANDARD_LOAD_SIZE);//进行封装
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                            mSearchCondition = false;
                            if (response.code() == 200) {

                                mSingleHoleCondition=false;
                                mIfSearch = true;
                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }
                                    if (mStartingLoadId != 0 && json.equals("[]")) {
                                        if (mLoadMoreConditionRl != null) {
                                            mLoadMoreConditionRl.finishLoadMore();
                                            mLoadMoreConditionRl = null;
                                            //mHomePageHolesAdpter.notifyDataSetChanged();
                                            if (mPrestrainCondition) {
                                                mPrestrainCondition = false;
                                                // mHomePageHolesAdpter.notifyDataSetChanged();
                                            } else if (mPrestrainCondition) {
                                                mPrestrainCondition = false;
                                            }
                                        }

                                        Toast.makeText(getContext(), "加载到底辣", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (json.equals("[]") || json.equals("null")) {
                                            Toast.makeText(getContext(), "无搜索结果", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (mStartingLoadId == 0) {
                                                mHompageHolesList = new ArrayList<>();
                                            }
                                            jsonArray = new JSONArray(json);
                                            new DownloadTask().execute();


                                            InputMethodManager immm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            immm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                                        }
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                failureAction();
                                ErrorMsg.getErrorMsg(response, getContext());
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            failureAction();
                            Toast.makeText(getContext(), R.string.network_searchfailure_, Toast.LENGTH_SHORT).show();
                        }

                        private void failureAction() {
                            mStartingLoadId = mLastLoadId;
                            mSearchCondition = false;
                            if (mLoadMoreConditionRl != null) {
                                mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                mLoadMoreConditionRl.finishLoadMore();
                                mLoadMoreConditionRl = null;
                                if (mPrestrainCondition == true) {
                                    mPrestrainCondition = false;
                                } else if (mPrestrainCondition == true) {
                                    mStartingLoadId = mStartingLoadId - CONSTANT_STANDARD_LOAD_SIZE;
                                    mPrestrainCondition = false;
                                } else {

                                }
                            }

                        }
                    });

                }
            }).start();
        }
    }
    private void showResponse(){
        Toast.makeText(getContext(), "加载到底了", Toast.LENGTH_SHORT).show();
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homepage, container, false);
        mSearchEt =rootView.findViewById(R.id.et_homepage);
        mTriangleIv = (ImageView)rootView.findViewById(R.id.iv_homepage_triangle);
        mTriangleIv.setImageResource(R.mipmap.triangle);
        mForestSquareTv =(TextView)rootView.findViewById(R.id.tv_homepage_forestsquare);
        mWherePpwCl =(ConstraintLayout) rootView.findViewById(R.id.ll_register);
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.page1fragment_1));
        EditTextReaction.EditTextSize(mSearchEt,string1,12);
        mSearchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveOnScrollListener();
                if(flag==true){
                  EndTriangleAnim();
                }
            }
        });
        mSearchEt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    if(mSearchCondition==false) {
                        mSearchCondition = true;
                        //if (mIfSearch == false) {
                            mLastLoadId = mStartingLoadId;
                            mStartingLoadId = 0;
                        searchUpdate();

                    }

                }
                return false;
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout)rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new HomePageRefreshFooter(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                mRefreshConditionRl =refreshlayout;
                mLastLoadId=mStartingLoadId;
                mStartingLoadId=0;

                update();
                mHomePageHolesRv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return true;
                    }
                });
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                RemoveOnScrollListener();
                if(mIfFirstLoad||mSingleHoleCondition){
                    refreshlayout.finishLoadMore();
                }else{
                    if (mPrestrainCondition == false) {
                        mLoadMoreConditionRl = refreshlayout;
                        mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                        if(mIfSearch) {
                            searchUpdate();
                        }else {
                            update();
                        }
                    } else {
                        mLoadMoreConditionRl = refreshlayout;
                    }
                }
            }
        });


        mHomePageHolesAdpter=new HoleAdapter();
        mHomePageHolesRv =(MaxHeightRecyclerView)rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHomePageHolesRv.setLayoutManager(linearLayoutManager);





        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.ppw_homepage, null);
        View contentView2=LayoutInflater.from(getActivity()).inflate(R.layout.ppw_homepagedarkscreen, null);
        mSlideBoxPpw =new PopupWindow(contentView);
        mDarkScreenPpw =new PopupWindow(contentView2);
        mNewPublishBtn =(Button)contentView.findViewById(R.id.btn_ppwhomepage_newpublish);
        mNewCommentBtn =(Button)contentView.findViewById(R.id.btn_ppwhomepage_newcomment);



        mNewPublishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewPublishBtn.setBackgroundResource(R.drawable.standard_button);
                mNewPublishBtn.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                mNewCommentBtn.setBackgroundResource(R.drawable.standard_button_gray);
                mNewCommentBtn.setTextAppearance(getActivity(),R.style.popupwindow_button);
                mHolesSequenceCondition =true;
                mPpwExistenceCondition=true;

                mLastLoadId=mStartingLoadId;
                mStartingLoadId=0;
                update();
            }
        });
        mNewCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewCommentBtn.setBackgroundResource(R.drawable.standard_button);
                mNewCommentBtn.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                mNewPublishBtn.setBackgroundResource(R.drawable.standard_button_gray);
                mNewPublishBtn.setTextAppearance(getActivity(),R.style.popupwindow_button);
                mHolesSequenceCondition =false;
                mPpwExistenceCondition=true;

                mLastLoadId=mStartingLoadId;
                mStartingLoadId=0;
                update();

            }
        });
        mForestSquareTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoveOnScrollListener();
                if(flag==false){
                    BeginTriangleAnim();
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                  
                }else{
                    EndTriangleAnim();
                }
            }
        });
        contentView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndTriangleAnim();
            }
        });

        mStartingLoadId=0;
        mHompageHolesList=new ArrayList<>();
        update();


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        //EndTriangleAnim();
    }
    private void BeginTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mTriangleIv.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mTriangleIv.setImageResource(R.mipmap.triangle);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=true;
        mDarkScreenPpw.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mDarkScreenPpw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mDarkScreenPpw.setAnimationStyle(R.style.darkScreenAnim);
        mDarkScreenPpw.showAsDropDown(mWherePpwCl);
        mSlideBoxPpw.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mSlideBoxPpw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSlideBoxPpw.setAnimationStyle(R.style.contextMenuAnim);
        mSlideBoxPpw.showAsDropDown(mWherePpwCl);
    }
    private void EndTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        mTriangleIv.startAnimation(rotate);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mTriangleIv.setImageResource(R.mipmap.triangle);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=false;
        mSlideBoxPpw.dismiss();
        mDarkScreenPpw.dismiss();
    }
    public  void homeScreenUpdate(){
        mHomeScreenUpdate=true;
        mSingleHoleCondition=false;
        mLastLoadId=mStartingLoadId;
        mStartingLoadId=0;
        update();

    }
    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TokenInterceptor.getContext(getActivity());
        //TokenInterceptor.getContext(RegisterActivity.this);");
        retrofit= RetrofitManager.getRetrofit();
        request=RetrofitManager.getRequest();
    }
    public  void update( ){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                Call<ResponseBody> call = request.homepageholes(true, mHolesSequenceCondition,mStartingLoadId,CONSTANT_STANDARD_LOAD_SIZE);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200) {
                            mSingleHoleCondition=false;
                            String json = "null";
                            try {
                                if (response.body() != null) {
                                    json = response.body().string();
                                }
                                jsonArray = new JSONArray(json);
                                noSearch();
                                if (mHomeScreenUpdate) {
                                    mHomeScreenUpdate = false;
                                    mHompageHolesList = new ArrayList<>();
                                }
                                if (mRefreshConditionRl != null) {
                                    mHompageHolesList = new ArrayList<>();
                                }
                                if (mDeleteCondition) {
                                    mDeleteCondition = false;
                                    mHompageHolesList = new ArrayList<>();
                                }
                                if (mPpwExistenceCondition != false) {
                                    mHompageHolesList = new ArrayList<>();
                                    mSlideBoxPpw.dismiss();
                                    mDarkScreenPpw.dismiss();
                                    EndTriangleAnim();
                                    mPpwExistenceCondition = false;
                                }
                                new DownloadTask().execute();

                                //}

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            failureAction();
                            ErrorMsg.getErrorMsg(response,getContext());
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable tr) {
                        Toast.makeText(getContext(),R.string.network_loadfailure,Toast.LENGTH_SHORT).show();
                        failureAction();
                    }
                    private void failureAction(){
                        if(mPpwExistenceCondition!=false){
                            mStartingLoadId=mLastLoadId;
                            mPpwExistenceCondition=false;
                        }
                        if(mHomeScreenUpdate){
                            mHomeScreenUpdate=false;
                        }
                        if(mLoadMoreConditionRl!=null){
                            mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                            mLoadMoreConditionRl.finishLoadMore();
                            mLoadMoreConditionRl=null;
                            if(mPrestrainCondition==true){
                                mPrestrainCondition=false;
                            }
                        }else if(mRefreshConditionRl!=null){
                            mStartingLoadId=mLastLoadId;
                            mRefreshConditionRl.finishRefresh();
                            mRefreshConditionRl=null;
                            mHomePageHolesRv.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return false;
                                }
                            });

                            if(mIfFirstLoad){
                                // mHomePageHolesRv.setAdapter(mHomePageHolesAdpter);
                                // mIfFirstLoad=false;
                            }else{
                                // mHomePageHolesAdpter.notifyDataSetChanged();
                            }
                        }else if(mPrestrainCondition==true){
                            mStartingLoadId=mStartingLoadId-CONSTANT_STANDARD_LOAD_SIZE;
                            mPrestrainCondition=false;
                        }else{

                        }
                    }

                });
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {

            if(mLoadMoreConditionRl != null){
                mLoadMoreConditionRl.finishLoadMore();
                mLoadMoreConditionRl = null;
                mHomePageHolesAdpter.notifyDataSetChanged();
                if (mPrestrainCondition == true) {
                    mPrestrainCondition = false;
                }
            }else if(mRefreshConditionRl != null) {
                mRefreshConditionRl.finishRefresh();
                mRefreshConditionRl = null;
                mHomePageHolesRv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                if (mIfFirstLoad) {
                    mHomePageHolesRv.setAdapter(mHomePageHolesAdpter);
                    mIfFirstLoad = false;
                } else {
                    mHomePageHolesAdpter.notifyDataSetChanged();
                }
            }else if(mPrestrainCondition == true) {
                mPrestrainCondition = false;
                mHomePageHolesAdpter.notifyDataSetChanged();

            }else if(mIfSearch){
                if (mIfFirstLoad) {
                    mIfFirstLoad = false;
                }
                mHomePageHolesAdpter.notifyDataSetChanged();
            }
            else{
                mIfFirstLoad = false;
                mHomePageHolesRv.setAdapter(mHomePageHolesAdpter);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                for (int f = 0; f < jsonArray.length(); f++) {
                    JSONObject sonObject = jsonArray.getJSONObject(f);
                    String[] SingleHole = new String[14];
                    //detailforest[f][0] = sonObject.getString("background_image_url");
                    SingleHole[1] = sonObject.getString("content");
                    SingleHole[2] = sonObject.getString("created_timestamp");
                    SingleHole[3] = sonObject.getInt("follow_num") + "";
                    SingleHole[4] = sonObject.getInt("forest_id") + "";
                    SingleHole[5] = sonObject.getString("forest_name");
                    SingleHole[6] = sonObject.getInt("hole_id") + "";
                    //detailforest2[f][1] = sonObject.getString("image");
                    SingleHole[8] = sonObject.getBoolean("is_follow") + "";
                    SingleHole[9] = sonObject.getBoolean("is_mine") + "";
                    SingleHole[10] = sonObject.getBoolean("is_reply") + "";
                    SingleHole[11] = sonObject.getBoolean("is_thumbup") + "";
                    SingleHole[12] = sonObject.getInt("reply_num") + "";
                    SingleHole[13] = sonObject.getInt("thumbup_num") + "";
                    mHompageHolesList.add(SingleHole);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
        private void RemoveOnScrollListener() {
            if (mOnscrollListener != null) {
                mHomePageHolesRv.removeOnScrollListener(mOnscrollListener);
            }
            if(mMoreWhatCl!=null) {
                mMoreWhatCl.setVisibility(View.INVISIBLE);
                more_condition = false;
            }
        }


        public class HoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            //private static List<Event> events;

            public class ViewHolder extends RecyclerView.ViewHolder {
                private TextView content, created_timestamp, follow_num, reply_num, thumbup_num, hole_id, more_2;
                private ImageView background_image_url, is_follow, is_reply, is_thumbup, more, more_1;
                private int position;
                private Button forest_name;
                private ConstraintLayout morewhat, constraintLayout3,thumbup,follow,reply;
                private Boolean thumbupCondition=false,followCondition=false;
                public ViewHolder(View view) {
                    super(view);
                    thumbup=(ConstraintLayout)view.findViewById(R.id.cl_itemhomepage_thumbup);
                    follow=(ConstraintLayout)view.findViewById(R.id.cl_itemhomepage_follow);
                    reply=(ConstraintLayout)view.findViewById(R.id.cl_itemhomepage_reply);
                    content = (TextView) view.findViewById(R.id.tv_itemhomepage_content);
                    created_timestamp = (TextView) view.findViewById(R.id.tv_itemhomepage_time);
                    forest_name = (Button) view.findViewById(R.id.btn_itemhompage_jumptodetailforest);
                    thumbup_num = (TextView) view.findViewById(R.id.tv_itemhomepage_thumbupnumber);
                    reply_num = (TextView) view.findViewById(R.id.tv_itemhomepage_replynumber);
                    follow_num = (TextView) view.findViewById(R.id.tv_itemhomepage_follownumber);
                    hole_id = (TextView) view.findViewById(R.id.tv_itemhomepage_title);
                    is_thumbup = (ImageView) view.findViewById(R.id.iv_itemhomepage_thumbup);
                    is_reply = (ImageView) view.findViewById(R.id.iv_itemhomepage_reply);
                    is_follow = (ImageView) view.findViewById(R.id.iv_itemhomepage_follow);
                    more = (ImageView) view.findViewById(R.id.iv_itemhomepage_more);
                    more_1 = (ImageView) view.findViewById(R.id.iv_itemhomepage_moreicon);
                    more_2 = (TextView) view.findViewById(R.id.tv_itemhomepage_moretext);
                    morewhat = (ConstraintLayout) view.findViewById(R.id.cl_itemhomepage_morelist);
                    constraintLayout3 = (ConstraintLayout) view.findViewById(R.id.cl_itemhomepage_frame);
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
                                mHomePageHolesRv.removeOnScrollListener(mOnscrollListener);
                            }
                            mOnscrollListener = new RecyclerView.OnScrollListener() {
                                //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    //super.onScrollStateChanged(recyclerView, newState);
                                    mMoreWhatCl.setVisibility(View.INVISIBLE);
                                    more_condition = false;
                                    mHomePageHolesRv.removeOnScrollListener(mOnscrollListener);
                                }
                            };
                            mHomePageHolesRv.addOnScrollListener(mOnscrollListener);
                        }
                    });
                    morewhat.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (CheckingToken.IfTokenExist()) {
                                RemoveOnScrollListener();
                                if (mHompageHolesList.get(position)[9].equals("true")) {
                                    new Thread(new Runnable() {//加载纵向列表标题
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.delete_hole( mHompageHolesList.get(position)[6]);//进行封装
                                            call.enqueue(new Callback<ResponseBody>(){


                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if(response.code()==200) {
                                                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                                        mLastLoadId = mStartingLoadId;
                                                        mStartingLoadId = 0;
                                                        mDeleteCondition = true;
                                                        update();
                                                    }else{
                                                        mDeleteCondition = false;
                                                        ErrorMsg.getErrorMsg(response,getContext());
                                                    }
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
                                    new Thread(new Runnable() {//加载纵向列表标题
                                        @Override
                                        public void run() {
                                            Call<ResponseBody> call = request.report_2(RetrofitManager.API+"reports?hole_id=" +mHompageHolesList.get(position)[6] + "&reply_local_id= -1");
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
                                                                Toast.makeText(getContext(), returncondition, Toast.LENGTH_SHORT).show();
                                                            } catch (IOException | JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            Toast.makeText(getContext(), "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        ErrorMsg.getErrorMsg(response,getContext());
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(getContext(), R.string.network_reportfailture, Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }).start();



                                    //CommenRequestManager.ReportRequest(getContext(), request, , "-1");
                                }
                            } else {
                                Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                                startActivity(intent);

                            }


                        }
                    });


                    thumbup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckingToken.IfTokenExist()) {
                                if(thumbupCondition==false){
                                    thumbupCondition=true;
                                    if (mHompageHolesList.get(position)[11].equals("false")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.thumbups(RetrofitManager.API+"thumbups/" + mHompageHolesList.get(position)[6] + "/-1");//进行封装
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code()==200) {
                                                            is_thumbup.setImageResource(R.mipmap.active);
                                                            mHompageHolesList.get(position)[11] = "true";
                                                            mHompageHolesList.get(position)[13] = (Integer.parseInt(mHompageHolesList.get(position)[13]) + 1) + "";
                                                            thumbupCondition = false;
                                                            thumbup_num.setText(mHompageHolesList.get(position)[13]);
                                                        }else{
                                                            thumbupCondition=false;
                                                            ErrorMsg.getErrorMsg(response,getContext());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        thumbupCondition=false;
                                                        Toast.makeText(getContext(), R.string.network_thumbupfailure_, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    } else {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.deletethumbups(RetrofitManager.API+"thumbups/" + mHompageHolesList.get(position)[6] + "/-1");//进行封装
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code()==200) {
                                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                                            mHompageHolesList.get(position)[11] = "false";
                                                            mHompageHolesList.get(position)[13] = (Integer.parseInt(mHompageHolesList.get(position)[13]) - 1) + "";
                                                            thumbupCondition = false;
                                                            thumbup_num.setText(mHompageHolesList.get(position)[13]);
                                                        }else{
                                                            thumbupCondition=false;
                                                            ErrorMsg.getErrorMsg(response,getContext());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        thumbupCondition=false;
                                                        Toast.makeText(getContext(), R.string.network_notthumbupfailure_, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                }
                            } else {
                                Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (CheckingToken.IfTokenExist()) {
                                if(followCondition==false){
                                    followCondition=true;
                                    if (mHompageHolesList.get(position)[8].equals("false")) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.follow(RetrofitManager.API+"follows/" + mHompageHolesList.get(position)[6]);//进行封装
                                                call.enqueue(new Callback<ResponseBody>() {
                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        if(response.code()==200) {
                                                            is_follow.setImageResource(R.mipmap.active_3);
                                                            mHompageHolesList.get(position)[8] = "true";
                                                            mHompageHolesList.get(position)[3] = (Integer.parseInt(mHompageHolesList.get(position)[3]) + 1) + "";
                                                            followCondition = false;
                                                            follow_num.setText(mHompageHolesList.get(position)[3]);
                                                        }else{
                                                            followCondition=false;
                                                            ErrorMsg.getErrorMsg(response,getContext());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        followCondition=false;
                                                        Toast.makeText(getContext(), R.string.network_followfailure_, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    } else {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Call<ResponseBody> call = request.deletefollow(RetrofitManager.API+"follows/" + mHompageHolesList.get(position)[6]);//进行封装
                                                call.enqueue(new Callback<ResponseBody>() {

                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                         //response.errorBody()


                                                        if(response.code()==200) {
                                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                                            mHompageHolesList.get(position)[8] = "false";
                                                            mHompageHolesList.get(position)[3] = (Integer.parseInt(mHompageHolesList.get(position)[3]) - 1) + "";
                                                            followCondition = false;
                                                            follow_num.setText(mHompageHolesList.get(position)[3]);
                                                        }else{
                                                            followCondition=false;
                                                            ErrorMsg.getErrorMsg(response,getContext());
                                                        }
                                                    }
                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        followCondition=false;
                                                        Toast.makeText(getContext(), R.string.network_notfollowfailure_, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                }
                            } else {
                                Intent intent = new Intent(getContext(), EmailVerifyActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RemoveOnScrollListener();
                            mReturnIsThumbup=is_thumbup;
                            mReturnIsReply=is_reply;
                            mReturnIsFollow=is_follow;
                            mReturnThumbupNUmber=thumbup_num;
                            mReturnReplyNumber=reply_num;
                            mReturnFollowNumber=follow_num;
                            mReturnPosition=position;
                            Intent intent = CommentListActivity.newIntent(getActivity(), mHompageHolesList.get(position));
                            intent.putExtra("reply","key_board");
                            startActivityForResult(intent,REQUESTCODE_COMMENT);
                        }
                    });
                    constraintLayout3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RemoveOnScrollListener();
                            mReturnIsThumbup=is_thumbup;
                            mReturnIsReply=is_reply;
                            mReturnIsFollow=is_follow;
                            mReturnThumbupNUmber=thumbup_num;
                            mReturnReplyNumber=reply_num;
                            mReturnFollowNumber=follow_num;
                            mReturnPosition=position;
                            Intent intent = CommentListActivity.newIntent(getActivity(), mHompageHolesList.get(position));
                            startActivityForResult(intent,REQUESTCODE_COMMENT);
                        }
                    });
                    forest_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = DetailForestActivity.newIntent(getContext(), mHompageHolesList.get(position)[4]);
                            startActivity(intent);
                        }
                    });
                }


                public void bind(int position) {
                    this.position = position;
                    content.setText(mHompageHolesList.get(position)[1]);
                    if(mIfSearch){
                        created_timestamp.setText(TimeCount.time(mHompageHolesList.get(position)[2]));
                    }else{
                        created_timestamp.setText(TimeCount.time(mHompageHolesList.get(position)[2]) + (mHolesSequenceCondition == true ? "更新" : "发布"));
                    }
                    if (mHompageHolesList.get(position)[5].equals("")) {
                        forest_name.setVisibility(View.INVISIBLE);
                    } else {
                        forest_name.setVisibility(View.VISIBLE);
                        forest_name.setText("  " + mHompageHolesList.get(position)[5] + "   ");
                    }
                    follow_num.setText(mHompageHolesList.get(position)[3]);
                    reply_num.setText(mHompageHolesList.get(position)[12]);
                    thumbup_num.setText(mHompageHolesList.get(position)[13]);
                    hole_id.setText("#" + mHompageHolesList.get(position)[6]);
                    if (mHompageHolesList.get(position)[8].equals("true")) {
                        is_follow.setImageResource(R.mipmap.active_3);
                    } else {
                        is_follow.setImageResource(R.mipmap.inactive_3);
                    }
                    if (mHompageHolesList.get(position)[9].equals("true")) {
                        more_1.setImageResource(R.mipmap.vector6);
                        more_2.setText("删除");
                    } else {
                        more_1.setImageResource(R.mipmap.vector4);
                        more_2.setText("举报");
                    }

                    if (mHompageHolesList.get(position)[10].equals("true")) {
                        is_reply.setImageResource(R.mipmap.active_2);
                    } else {
                        is_reply.setImageResource(R.mipmap.inactive_2);
                    }
                    if (mHompageHolesList.get(position)[11].equals("true")) {
                        is_thumbup.setImageResource(R.mipmap.active);
                    } else {
                        is_thumbup.setImageResource(R.mipmap.inactive);
                    }
                }

            }

            public HoleAdapter() {

                //this.events=events;
            }


            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new HoleAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_homepage, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                //Event event=events.get(position);
                ((HoleAdapter.ViewHolder) holder).bind(position);
                if (position == mHompageHolesList.size() - 4 && (mHompageHolesList.size() % CONSTANT_STANDARD_LOAD_SIZE == 0)) {
                    mStartingLoadId = mStartingLoadId + CONSTANT_STANDARD_LOAD_SIZE;
                    mPrestrainCondition = true;
                    if(mIfSearch) {
                        searchUpdate();
                    }else{
                        update();
                    }
                }
            }

            @Override
            public int getItemCount() {
                return mHompageHolesList.size();
            }
        }
    }




