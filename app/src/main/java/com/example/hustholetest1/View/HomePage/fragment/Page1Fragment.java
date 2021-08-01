package com.example.hustholetest1.View.HomePage.fragment;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.Model.RequestInterface;
import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.Model.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.CommentActivity;
import com.example.hustholetest1.View.HomePage.Activity.Page2_AllForestsActivity;
import com.example.hustholetest1.View.HomePage.Activity.Page2_DetailAllForestsActivity;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Page1Fragment extends Fragment {
    private Button button1,button2;
    private EditText editText1;
    private ImageView imageView;
    private TextView textView;
    private boolean flag=false;
    private PopupWindow popWindow,popupWindow2,popupWindow3;
    private ConstraintLayout constraintLayout;
    private RecyclerView recyclerView;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private String[][] detailforest;
    private Boolean condition=false;
    private RefreshLayout refreshlayout1;



    public static Page1Fragment newInstance() {
        Page1Fragment fragment = new Page1Fragment();
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page1fragment, container, false);
        editText1=rootView.findViewById(R.id.editText);
        imageView= (ImageView)rootView.findViewById(R.id.imageView);
        imageView.setImageResource(R.mipmap.triangle);
        textView=(TextView)rootView.findViewById(R.id.textView);
        constraintLayout=(ConstraintLayout) rootView.findViewById(R.id.linearLayout);
        //recyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.page1fragment_1));
        EditTextReaction.EditTextSize(editText1,string1,12);
        editText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==true){
                  EndTriangleAnim();
                }
            }
        });
        editText1.setImeOptions(EditorInfo.IME_ACTION_SEND);
        editText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                Log.d("输入","点击");
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    new Thread(new Runnable() {//加载纵向列表标题
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            // 隐藏软键盘
                            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                            Call<ResponseBody> call = request.search("http://hustholetest.pivotstudio.cn//api/search/hole?keyword="+editText1.getText().toString()+"&start_id=0&list_size=10");//进行封装
                            Log.e(TAG, "token2：");
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    String json = "null";
                                    try {
                                        if (response.body() != null) {
                                            json = response.body().string();
                                        }
                                        // JSONObject jsonObject = new JSONObject(json);
                                        //读取
                                        Log.d("",json);
                                        if(json.equals("null")) {
                                            Toast.makeText(getContext(),"无搜索结果",Toast.LENGTH_SHORT).show();
                                        }else{
                                            jsonArray = new JSONArray(json);

                                            new DownloadTask().execute();
                                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            // 隐藏软键盘
                                            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
                                        }

                                        //}

                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                   Toast.makeText(getContext(),"无搜索结果",Toast.LENGTH_SHORT).show();
                                }


                            });
                        }
                    }).start();

                    //处理事件
                }
                return false;
            }
        });
        RefreshLayout refreshLayout = (RefreshLayout)rootView.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout0) {
               refreshlayout1=refreshlayout0;

                update(2);

//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(false/*,false*/);


                //传入false表示加载失败
            }
        });



        recyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rootView.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);





        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.page1fragment_popupwindow1, null);
        View contentView2=LayoutInflater.from(getActivity()).inflate(R.layout.screen, null);
        popWindow=new PopupWindow(contentView);
        popupWindow2=new PopupWindow(contentView2);
        button1=(Button)contentView.findViewById(R.id.button5);
        button2=(Button)contentView.findViewById(R.id.button7);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button1.setBackgroundResource(R.drawable.standard_button);
                button1.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                button2.setBackgroundResource(R.drawable.standard_button_gray);
                button2.setTextAppearance(getActivity(),R.style.popupwindow_button);
                condition=false;
                update(1);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button2.setBackgroundResource(R.drawable.standard_button);
                button2.setTextAppearance(getActivity(),R.style.popupwindow_button_click);
                button1.setBackgroundResource(R.drawable.standard_button_gray);
                button1.setTextAppearance(getActivity(),R.style.popupwindow_button);
                condition=true;
                update(1);

            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag==false){
                    Log.d("wsccsf","1");
                    BeginTriangleAnim();
                    InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                    if (manager != null) {
                        manager.hideSoftInputFromWindow(rootView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                   // recyclerView.setBackgroundColor(getResources().getColor(R.color.GrayScale_50));
                }else{
                    Log.d("wsccsf","3");
                    EndTriangleAnim();
                    //recyclerView.setBackgroundColor(getResources().getColor(R.color.GrayScale_100));
                }
            }
        });
        contentView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndTriangleAnim();
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        EndTriangleAnim();


    }
    private void BeginTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);
        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(R.mipmap.triangle);
                Log.d("wsccsf","2");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=true;
        popupWindow2.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow2.showAsDropDown(constraintLayout);
        popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWindow.setAnimationStyle(R.style.contextMenuAnim);
        popWindow.showAsDropDown(constraintLayout);
    }
    private void EndTriangleAnim(){
        RotateAnimation rotate;
        rotate =new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        imageView.startAnimation(rotate);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageResource(R.mipmap.triangle);
                Log.d("wsccsf","4");
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flag=false;
        popWindow.dismiss();
        popupWindow2.dismiss();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
         update(0);
    }
    public void update(int a){
        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                Call<ResponseBody> call = request.holes2("http://hustholetest.pivotstudio.cn/api/holes?is_descend=true&is_last_reply="+condition+"&start_id=0&list_size=10");//进行封装
                Log.e(TAG, "token2：");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }
                           // JSONObject jsonObject = new JSONObject(json);
                            //读取
                            jsonArray=new JSONArray(json);
                           //jsonArray = jsonObject.getJSONArray("holes");
                            detailforest=new String[jsonArray.length()][14];

                            //bitmapss=new Bitmap[jsonArray.length()][2];
                            //for(int f=0;f<jsonArray.length();f++) {
                            new DownloadTask().execute();

                            if(a==1){
                                popWindow.dismiss();
                                popupWindow2.dismiss();
                                EndTriangleAnim();
                            }
                            if(a==2){
                                refreshlayout1.finishRefresh();
                            }
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
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        update(0);
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {

                recyclerView.setAdapter(new HoleAdapter());

        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                for(int f=0;f<jsonArray.length();f++) {
                    JSONObject sonObject = jsonArray.getJSONObject(f);
                    //detailforest[f][0] = sonObject.getString("background_image_url");
                    detailforest[f][1] = sonObject.getString("content");
                    detailforest[f][2] = sonObject.getString("created_timestamp");
                    detailforest[f][3] = sonObject.getInt("follow_num")+"";
                    detailforest[f][4] = sonObject.getInt("forest_id")+"";
                    detailforest[f][5] = sonObject.getString("forest_name");
                    detailforest[f][6] = sonObject.getInt("hole_id")+"";
                    //detailforest2[f][1] = sonObject.getString("image");
                    detailforest[f][8] = sonObject.getBoolean("is_follow")+"";
                    detailforest[f][9] = sonObject.getBoolean("is_mine")+"";
                    detailforest[f][10] = sonObject.getBoolean("is_reply")+"";
                    detailforest[f][11] = sonObject.getBoolean("is_thumbup")+"";
                    detailforest[f][12] = sonObject.getInt("reply_num")+"";
                    detailforest[f][13] = sonObject.getInt("thumbup_num")+"";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class HoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
        private Boolean more_condition=false;
        private  ConstraintLayout morewhat0;
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content,created_timestamp,follow_num,reply_num,thumbup_num,hole_id,more_2;
            private ImageView background_image_url,is_follow,is_reply,is_thumbup,more,more_1;
            private int position;
            private Button forest_name;
            private ConstraintLayout morewhat,constraintLayout3;
            public ViewHolder(View view) {
                super(view);
                content=(TextView)view.findViewById(R.id.textView37);
                created_timestamp=(TextView)view.findViewById(R.id.textView33);
                forest_name=(Button)view.findViewById(R.id.button13);
                thumbup_num =(TextView)view.findViewById(R.id.textView34);
                reply_num=(TextView)view.findViewById(R.id.textView35);
                follow_num =(TextView)view.findViewById(R.id.textView36);
                hole_id=(TextView)view.findViewById(R.id.textView32);
                is_thumbup=(ImageView)view.findViewById(R.id.imageView13);
                is_reply=(ImageView)view.findViewById(R.id.imageView14);
                is_follow=(ImageView)view.findViewById(R.id.imageView15);
                more=(ImageView)view.findViewById(R.id.imageView17);
                more_1=(ImageView)view.findViewById(R.id.imageView34);
                more_2=(TextView)view.findViewById(R.id.textView76);
                morewhat=(ConstraintLayout)view.findViewById(R.id.morewhat);
                constraintLayout3=(ConstraintLayout)view.findViewById(R.id.constraintLayout3);
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
                        if(detailforest[position][9].equals("true")){

                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.delete_hole("http://hustholetest.pivotstudio.cn/api/holes/" + detailforest[position][6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String json = "null";
                                            String returncondition=null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(getContext(), returncondition, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

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
                                    Call<ResponseBody> call = request.report("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + detailforest[position][6]+"&reply_local_id=-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String json = "null";
                                            String returncondition=null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(getContext(), returncondition, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
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
                        if(detailforest[position][11].equals("false")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+detailforest[position][6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.active);
                                            detailforest[position][11]="true";
                                            detailforest[position][13]=(Integer.parseInt( detailforest[position][13])+1)+"";

                                            thumbup_num.setText(detailforest[position][13]);
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
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+detailforest[position][6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                            detailforest[position][11]="false";
                                            detailforest[position][13]=(Integer.parseInt( detailforest[position][13])-1)+"";

                                            thumbup_num.setText(detailforest[position][13]);
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
                        if(detailforest[position][8].equals("false")){
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.follow("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest[position][6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.active_3);
                                            detailforest[position][8]="true";
                                            detailforest[position][3]=(Integer.parseInt(detailforest[position][3])+1)+"";

                                            follow_num.setText(detailforest[position][3]);
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
                                    Call<ResponseBody> call = request.deletefollow("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest[position][6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                            detailforest[position][8]="false";
                                            detailforest[position][3]=(Integer.parseInt( detailforest[position][3])-1)+"";

                                            follow_num.setText(detailforest[position][3]);
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
                constraintLayout3.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d("data[2]1",detailforest[position][2]);
                        Intent intent= CommentActivity.newIntent(getActivity(),detailforest[position]);
                        startActivity(intent);
                    }
                });
                forest_name.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                                Intent intent = Page2_DetailAllForestsActivity.newIntent(getContext(), detailforest[position][4]);
                                startActivity(intent);
                    }
                });
            }


            public void bind(int position){
                this.position=position;
                content.setText(detailforest[position][1]);
                created_timestamp.setText(detailforest[position][2]);
                Log.d("??????",detailforest[position][5]);
                if(detailforest[position][5].equals("")){
                    forest_name.setVisibility(View.INVISIBLE);
                }else {
                    forest_name.setText("  "+detailforest[position][5]+"  ");
                }
                follow_num.setText(detailforest[position][3]);
                reply_num.setText(detailforest[position][12]);
                thumbup_num.setText(detailforest[position][13]);
                hole_id.setText("#" + detailforest[position][6]);
                if (detailforest[position][8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                }
                if(detailforest[position][9].equals("true")){
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }

                if (detailforest[position][10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }
                if (detailforest[position][11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }
            }

        }

        public HoleAdapter(){

            //this.events=events;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {


                return new HoleAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.page1fragment_model,parent,false ));

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);
                ((HoleAdapter.ViewHolder) holder).bind(position);
        }
        @Override
        public int getItemCount() {
            return 10;
        }
    }

}
