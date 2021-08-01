package com.example.hustholetest1.View.HomePage.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.Model.RequestInterface;
import com.example.hustholetest1.Model.StandardRefreshHeader;
import com.example.hustholetest1.Model.TimeCount;
import com.example.hustholetest1.Model.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.fragment.Page2Fragment;
import com.githang.statusbar.StatusBarCompat;
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

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class CommentActivity extends AppCompatActivity {
    private static final String key="key_1";
    private TextView title;
    private ImageView back;
    private EditText editText;
    private Button button;
    private RecyclerView recyclerView2;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray;
    private String[][] detailreply;
    private String[] data;
    private String reply_to_who="-1";
    private RefreshLayout refreshlayout1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.GrayScale_100), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        RefreshLayout refreshLayout = (RefreshLayout)findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new StandardRefreshHeader(CommentActivity.this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(CommentActivity.this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout0) {
                refreshlayout1=refreshlayout0;
                replyUpdate(1);
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

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(CommentActivity.this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(linearLayoutManager2);
        editText = (EditText) findViewById(R.id.editText4);

        SpannableString ss = new SpannableString("评论洞主：");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        editText.setHint(new SpannedString(ss));





        button = (Button) findViewById(R.id.button);
        EditTextReaction.ButtonReaction(editText, button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {//加载纵向列表标题
                    @Override
                    public void run() {

                        Call<ResponseBody> call = request.replies_add("http://hustholetest.pivotstudio.cn/api/replies?hole_id="+data[6]+"&content="+editText.getText().toString()+"&wanted_local_reply_id="+reply_to_who);//进行封装
                       Log.d("", "http://hustholetest.pivotstudio.cn/api/replies?hole_id="+data[6]+"&content="+editText.getText().toString()+"&wanted_local_reply_id="+reply_to_who);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }
                                    Log.e(TAG, json + "");
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    // 隐藏软键盘
                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                    editText.setText("");
                                    editText.setHint("评价洞主：");
                                    reply_to_who="-1";
                                    replyUpdate(0);

                                } catch (IOException e) {
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
        });


        back = (ImageView) findViewById(R.id.backView);
        title = (TextView) findViewById(R.id.title);


        data = getIntent().getStringArrayExtra(key);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        TokenInterceptor.getContext(CommentActivity.this);
        //TokenInterceptor.getContext(RegisterActivity.this);
        System.out.println("提交了context");
        retrofit = new Retrofit.Builder()
                .baseUrl("http://hustholetest.pivotstudio.cn/")
                .client(com.example.hustholetest1.Model.OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.e(TAG, "token99：");
        request = retrofit.create(RequestInterface.class);//创建接口实例
        replyUpdate(0);
    }

    private void replyUpdate(int a) {
        SpannableString ss = new SpannableString("评论洞主：");
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 设置hint
        editText.setHint(new SpannedString(ss));
    new Thread(new Runnable() {//加载纵向列表标题
        @Override
        public void run() {

            SharedPreferences editor = getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
            String order = editor.getString("order", "false");
            Call<ResponseBody> call = request.replies("http://hustholetest.pivotstudio.cn/api/replies?hole_id=" + data[6] + "&is_descend=" + order + "&start_id=0&list_size=10");//进行封装

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    String json = "null";
                    try {
                        if (response.body() != null) {
                            json = response.body().string();
                        }
                        Log.e(TAG, json + "");
                        JSONObject jsonObject = new JSONObject(json);
                        //读取
                        jsonArray = jsonObject.getJSONArray("msg");
                        Log.e(TAG, jsonArray.length() + "");
                        detailreply = new String[jsonArray.length()][12];


                        if(a==1){
                            refreshlayout1.finishRefresh();
                        }
                        new DownloadTask().execute();
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
                recyclerView2.setAdapter(new ReplyAdapter());
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    for(int f=0;f<jsonArray.length();f++) {
                        JSONObject sonObject = jsonArray.getJSONObject(f);
                        detailreply[f][0] = sonObject.getString("alias");
                        detailreply[f][1] = sonObject.getString("content");
                        detailreply[f][2] = sonObject.getString("created_timestamp");
                        detailreply[f][3] = sonObject.getInt("hole_id")+"";
                        detailreply[f][4] = sonObject.getInt("id")+"";
                        detailreply[f][5] = sonObject.getBoolean("is_mine")+"";
                        detailreply[f][6] =sonObject.getBoolean("is_thumbup")+"";
                        detailreply[f][7] = sonObject.getInt("reply_local_id")+"";
                        detailreply[f][8] = sonObject.getInt("reply_to")+"";
                        detailreply[f][9] = sonObject.getString("reply_to_alias");
                        detailreply[f][10] = sonObject.getString("reply_to_content");
                        detailreply[f][11] = sonObject.getInt("thumbup_num")+"";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                return null;
            }
        }

   
    public static Intent newIntent(Context packageContext, String[] data){
        Log.d("data[2]2",data[2]);
        Intent intent = new Intent(packageContext, CommentActivity.class);
        intent.putExtra(key,data);
        return intent;
    }


    public class ReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_NOMESSAGE = 2;
        private int mHeaderCount=1;//头部View个数
        private Boolean more_condition=false;
        private  ConstraintLayout morewhat0;
        @Override
        public int getItemViewType(int position) {
            if (mHeaderCount != 0 && position < mHeaderCount) {
//头部View
                return ITEM_TYPE_HEADER;

            }else if(position==1&&jsonArray.length()==0){
                return ITEM_TYPE_NOMESSAGE;
            }else {
                return ITEM_TYPE_CONTENT;
            }
        }

        public class HeadHolder extends RecyclerView.ViewHolder{
            private Button forest_name;
            private TextView created_timestamp,content,thumbup_num,reply_num,follow_num,more_2;
            private ImageView is_thumbup,is_reply,is_follow,orders,more,more_1;
            private ConstraintLayout morewhat,constraintLayout3;
            public HeadHolder(View view){
                super(view);
                forest_name = (Button) view.findViewById(R.id.button13);
                created_timestamp = (TextView) view.findViewById(R.id.textView38);
                content = (TextView) view.findViewById(R.id.textView61);
                thumbup_num = (TextView) view.findViewById(R.id.textView62);
                reply_num = (TextView) view.findViewById(R.id.textView63);
                follow_num = (TextView) view.findViewById(R.id.textView64);
                is_thumbup = (ImageView) view.findViewById(R.id.imageView23);
                is_reply = (ImageView) view.findViewById(R.id.imageView24);
                is_follow = (ImageView) view.findViewById(R.id.imageView25);
                orders=(ImageView)view.findViewById(R.id.imageView30);


                more=(ImageView)view.findViewById(R.id.imageView26);
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
                        if(data[9].equals("true")){

                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.delete_hole("http://hustholetest.pivotstudio.cn/api/holes/" + data[6]);//进行封装
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
                                                    Toast.makeText(CommentActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                    //replyUpdate(0);
                                                    finish();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(CommentActivity.this, "删除失败，超过可删除的时间范围", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this, "删除失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }else {
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.report("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + data[6] + "&reply_local_id=-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String json = "null";
                                            String returncondition = null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(CommentActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(CommentActivity.this, "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this, "举报失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }

                    }
                });

                forest_name.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = Page2_DetailAllForestsActivity.newIntent(CommentActivity.this, data[4]);
                        startActivity(intent);
                    }
                });

                orders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences editor =getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
                        String  order = editor.getString("order", "false");
                        order=order.equals("false")?"true":"false";
                        SharedPreferences.Editor editor2 = getSharedPreferences("Depository2", Context.MODE_PRIVATE).edit();//获取编辑器
                        editor2.putString("order",order);
                        editor2.commit();//提交
                        replyUpdate(0);


                    }
                });


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SpannableString ss = new SpannableString("评论洞主：");
                        // 新建一个属性对象,设置文字的大小
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
                        // 附加属性到文本
                        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 设置hint
                        editText.setHint(new SpannedString(ss));
                        reply_to_who="-1";
                    }
                });



                is_thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(data[11].equals("false")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+data[6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.active);
                                           data[11]="true";
                                           data[13]=(Integer.parseInt(data[13])+1)+"";

                                            thumbup_num.setText(data[13]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"点赞失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+data[6]+"/-1");//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                           data[11]="false";
                                           data[13]=(Integer.parseInt(data[13])-1)+"";

                                            thumbup_num.setText(data[13]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"取消点赞失败",Toast.LENGTH_SHORT).show();
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
                        if(data[8].equals("false")){
                            new Thread(new Runnable() {//加载纵向列表标题
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/follows/"+data[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.active_3);
                                           data[8]="true";
                                           data[3]=(Integer.parseInt(data[3])+1)+"";

                                            follow_num.setText(data[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"点赞失败",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消点赞失败");
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/follows/"+data[6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_follow.setImageResource(R.mipmap.inactive_3);
                                           data[8]="false";
                                           data[3]=(Integer.parseInt(data[3])-1)+"";

                                            follow_num.setText(data[3]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"取消关注失败",Toast.LENGTH_SHORT).show();
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
                SharedPreferences editor =getSharedPreferences("Depository2", Context.MODE_PRIVATE);//
                String  order = editor.getString("order", "false");
                if(order.equals("false")){
                    orders.setImageResource(R.mipmap.group111);
                }else{
                    orders.setImageResource(R.mipmap.group112);
                }


                Log.d("data[2]3", data[2]);
                if (data[5].equals("")) {
                    forest_name.setVisibility(View.INVISIBLE);
                } else {
                    forest_name.setText("  " + data[5] + "  ");
                }
              // Log.d("????????????",data[2].substring(0,4)+data[2].substring(5,7)+data[2].substring(8,10)+data[2].substring(11,13)+data[2].substring(14,16)+data[2].substring(14,16));
                created_timestamp.setText(TimeCount.time(data[2]));
                content.setText(data[1]);
                Log.d("content",data[1]+"");
                thumbup_num.setText(data[13]);
                reply_num.setText(data[12]);
                follow_num.setText(data[3]);
                title.setText("#" + data[6]);

                if (data[8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                }
                if(data[9].equals("true")){
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }
                if (data[10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }
                if (data[11].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }
            }
        }
        public class MessageHolder extends RecyclerView.ViewHolder{
            ImageView nomessage;
            public MessageHolder(View view) {
                super(view);
                nomessage=(ImageView)view.findViewById(R.id.imageView37);
            }
            public void bind(int position){
               nomessage.setImageResource(R.mipmap.nomessage);
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView alias_me,content, created_timestamp,thumbup_num,reply_tosomebody,more_2;
            private ImageView  is_thumbup,more,more_1;
            private ConstraintLayout linearLayout,constraintLayout3,morewhat;
            private Button line;
            private int position;

            public ViewHolder(View view) {
                super(view);
                alias_me=(TextView)view.findViewById(R.id.textView65);
                content=(TextView)view.findViewById(R.id.textView69);
                created_timestamp=(TextView)view.findViewById(R.id.textView68);
                thumbup_num=(TextView)view.findViewById(R.id.textView70);
                is_thumbup=(ImageView)view.findViewById(R.id.imageView27);
                reply_tosomebody=(TextView)view.findViewById(R.id.textView99);
                linearLayout=(ConstraintLayout) view.findViewById(R.id.linearLayout);
                line=(Button)view.findViewById(R.id.textView72);

                more=(ImageView)view.findViewById(R.id.imageView28);
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
                        if(detailreply[position][5].equals("true")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.delete_hole_2("http://hustholetest.pivotstudio.cn/api/replies/"+data[6]+"/" + detailreply[position][7]);//进行封装
                                    Log.e(TAG, "http://hustholetest.pivotstudio.cn/api/replies/"+data[6]+"/" + detailreply[position][7]);
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String json = "null";

                                            String returncondition=null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    Log.d("json", response.body().string());
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(CommentActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                    replyUpdate(0);
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(CommentActivity.this, "删除失败，超过可删除的时间范围", Toast.LENGTH_SHORT).show();
                                            }


                                        }


                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this, "删除失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.report_2("http://hustholetest.pivotstudio.cn/api/reports?hole_id=" + data[6] + "&reply_local_id="+detailreply[position][7]);//进行封装
                                    Log.e(TAG, "http://hustholetest.pivotstudio.cn/api/replies/"+data[6]+"/" + detailreply[position][7]);
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            String json = "null";
                                            String returncondition = null;
                                            if (response.body() != null) {
                                                try {
                                                    json = response.body().string();
                                                    JSONObject jsonObject = new JSONObject(json);
                                                    returncondition = jsonObject.getString("msg");
                                                    Toast.makeText(CommentActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                                } catch (IOException | JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }else{
                                                Toast.makeText(CommentActivity.this, "您已经举报过该树洞,我们会尽快处理，请不要过于频繁的举报", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this, "举报失败", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }).start();
                        }

                    }
                });





                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SpannableString ss = new SpannableString("回复@"+detailreply[position][0]+(detailreply[position][5].equals("true")?"(我)":"")+"：");
                        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(14,true);
                        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        editText.setHint(new SpannedString(ss));
                        editText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                        reply_to_who=detailreply[position][7];
                    }
                });



                is_thumbup.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(detailreply[position][6].equals("false")){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+data[6]+"/"+detailreply[position][6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.active);
                                            detailreply[position][6]="true";
                                            detailreply[position][11]=(Integer.parseInt(detailreply[position][11])+1)+"";

                                            thumbup_num.setText(detailreply[position][11]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"点赞失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/thumbups/"+data[6]+"/"+detailreply[position][6]);//进行封装
                                    Log.e(TAG, "token2：");
                                    call.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            is_thumbup.setImageResource(R.mipmap.inactive);
                                            detailreply[position][6]="false";
                                            detailreply[position][11]=(Integer.parseInt(detailreply[position][11])-1)+"";

                                            thumbup_num.setText(detailreply[position][11]);
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Toast.makeText(CommentActivity.this,"取消点赞失败",Toast.LENGTH_SHORT).show();
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
                 alias_me.setText(detailreply[position][0]+(detailreply[position][5].equals("true")?"(我)":""));
                 content.setText(detailreply[position][1]);
                 created_timestamp.setText(TimeCount.time( detailreply[position][2]));
                 thumbup_num.setText(detailreply[position][11]);
                if (detailreply[position][6].equals("true")) {
                    is_thumbup.setImageResource(R.mipmap.active);
                }
                if(detailreply[position][8].equals("-1")){
                    linearLayout.setVisibility(View.INVISIBLE);
                }else {
                    Log.d("",detailreply[position][0]+detailreply[position][1]+detailreply[position][2]+detailreply[position][8]);
                    //linearLayout.setVisibility(View.VISIBLE);
                    SpannableStringBuilder builder = new SpannableStringBuilder(""+detailreply[position][9]+":"+detailreply[position][10]);
//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                    ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.GrayScale_0));
                    //ForegroundColorSpan whiteSpan = new ForegroundColorSpan(getResources().getColor(R.color.GrayScale_80));
                    builder.setSpan(redSpan,0, detailreply[position][9].length()+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //builder.setSpan(whiteSpan, detailreply[position][9].length(), 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    reply_tosomebody.setText(builder);
                }
                if(detailreply[position][5].equals("true")){
                    more_1.setImageResource(R.mipmap.vector6);
                    more_2.setText("删除");
                }
                line.setHeight(getViewHeight(content,true));
                /*
                detailreply[f][0] = sonObject.getString("alias");
                detailreply[f][1] = sonObject.getString("content");
                detailreply[f][2] = sonObject.getString("created_timestamp");
                detailreply[f][3] = sonObject.getInt("hole_id")+"";
                detailreply[f][4] = sonObject.getInt("id")+"";
                detailreply[f][5] = sonObject.getBoolean("is_mine")+"";
                detailreply[f][6] =sonObject.getBoolean("is_thumbup")+"";
                detailreply[f][7] = sonObject.getInt("reply_local_id")+"";
                detailreply[f][8] = sonObject.getInt("reply_to")+"";
                detailreply[f][9] = sonObject.getString("reply_to_alias");
                detailreply[f][10] = sonObject.getString("reply_to_content");
                detailreply[f][11] = sonObject.getInt("thumbup_num")+"";
*/

            }
        }
        public ReplyAdapter(){
            Log.d(TAG,"数据传入了");
            //this.events=events;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            if (viewType ==ITEM_TYPE_HEADER) {
                return new ReplyAdapter.HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_head,parent,false));
            } else if (viewType == ITEM_TYPE_CONTENT) {
                return new ReplyAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply,parent,false));
            }else if(viewType==ITEM_TYPE_NOMESSAGE){
                return new ReplyAdapter.MessageHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_nomessage,parent,false));
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
                ((ReplyAdapter.MessageHolder) holder).bind(position - 1);
            }

            }
        @Override
        public int getItemCount() {
            if(jsonArray.length()==0){
                return 2;
            }
            return jsonArray.length()+1;

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

    
}
