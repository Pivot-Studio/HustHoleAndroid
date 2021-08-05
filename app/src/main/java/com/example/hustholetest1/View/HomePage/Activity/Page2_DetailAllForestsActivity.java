package com.example.hustholetest1.View.HomePage.Activity;

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
import com.example.hustholetest1.Model.BlurTransformation;
import com.example.hustholetest1.Model.GlideRoundTransform;
import com.example.hustholetest1.Model.RequestInterface;
import com.example.hustholetest1.Model.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.fragment.Page2Fragment;
import com.example.hustholetest1.View.RegisterAndLogin.Activity.Login2Activity;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class Page2_DetailAllForestsActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private ImageView back,head,head_2;
    private TextView title,hole_and_number,content;
    private Button button;
    private JSONArray jsonArray2;
    private String[][] detailforest;
   // private TextView title;
    private String[] data;
    private RequestInterface request;
    private RecyclerView recyclerView;
    private static final String key="key_1";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page2_detailforest);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.GrayScale_100), true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        back = (ImageView) findViewById(R.id.imageView21);
        title=(TextView)findViewById(R.id.textView42);
        hole_and_number=(TextView)findViewById(R.id.textView43);
        content=(TextView)findViewById(R.id.textView44);
        button=(Button)findViewById(R.id.rectangle_3);
        head=(ImageView)findViewById(R.id.imageView19);
        head_2=(ImageView)findViewById(R.id.imageView22);
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

        data=getIntent().getStringArrayExtra(key);
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
            button=(Button)findViewById(R.id.rectangle_3);
            button.setBackground(getDrawable(R.drawable.forest_button_white));
            button.setPadding(0,0,0,0);
            button.setText("已加入");
            button.setTextColor(getResources().getColor(R.color.HH_BandColor_3));
            // button.setApp(Page2_AllForestsActivity.this,R.style.button_2);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data[5].equals("true")){
                    View mView = View.inflate(getApplicationContext(), R.layout.forest_quit_notice, null);
                    // mView.setBackgroundResource(R.drawable.homepage_notice);
                    //设置自定义的布局
                    //mBuilder.setView(mView);
                    Dialog dialog = new Dialog(Page2_DetailAllForestsActivity.this);
                    dialog.setContentView(mView);
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

                                    Call<ResponseBody> call2 = request.delete( "http://hustholetest.pivotstudio.cn/api/forests/quit/"+data[3]);//进行封装
                                    call2.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            Toast.makeText(Page2_DetailAllForestsActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(Page2_DetailAllForestsActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(Page2_DetailAllForestsActivity.this, "加入成功", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(Page2_DetailAllForestsActivity.this, "加入失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }

            }
        });
        RoundedCorners roundedCorners = new RoundedCorners(16);
        RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(Page2_DetailAllForestsActivity.this)
                .load(data[0])
                .apply(options1)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(head);
        Glide.with(Page2_DetailAllForestsActivity.this)
                .load(data[1])
                .transform(new BlurTransformation(Page2_DetailAllForestsActivity.this,20))
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(head_2);

        TokenInterceptor.getContext(Page2_DetailAllForestsActivity.this);
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


        new Thread(new Runnable() {//加载纵向列表标题
            @Override
            public void run() {
                Call<ResponseBody> call = request.detailholes("http://hustholetest.pivotstudio.cn/api/forests/"+data[3]+"/holes?start_id=0&list_size=5&is_last_active=true");//进行封装
                Log.e(TAG, "token2：");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        String json = "null";
                        try {
                            if (response.body() != null) {
                                json = response.body().string();
                            }

                            jsonArray2 = new JSONArray(json);
                            //jsonArray =response.body();
                            detailforest=new String[jsonArray2.length()][14];
                            new DownloadTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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

        recyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Page2_DetailAllForestsActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            recyclerView.setAdapter(new ForestHoleAdapter());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                for(int f=0;f<jsonArray2.length();f++) {
                    JSONObject sonObject = jsonArray2.getJSONObject(f);
                    //detailforest[f][0] = sonObject.getString("background_image_url");
                    detailforest[f][1] = sonObject.getString("content");
                    detailforest[f][2] = sonObject.getString("created_timestamp");
                    detailforest[f][3] = sonObject.getInt("follow_num")+"";
                    //detailforest[f][4] = sonObject.getInt("forest_id")+"";
                    detailforest[f][5] = data[7];
                    detailforest[f][6] = sonObject.getInt("hole_id")+"";
                    //detailforest2[f][1] = sonObject.getString("image");
                    detailforest[f][8] = sonObject.getBoolean("is_follow")+"";
                    //detailforest2[f][9] = sonObject.getBoolean("is_mine")+"";
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

    public class ForestHoleAdapter extends RecyclerView.Adapter<ForestHoleAdapter.ViewHolder>{
        //private static List<Event> events;



       public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content,created_timestamp,follow_num,reply_num,thumbup_num,hole_id;
            private ImageView is_follow,is_reply,is_thumbup;
            ConstraintLayout next;
            private int position;
            public ViewHolder(View view) {
                super(view);
                content=(TextView)view.findViewById(R.id.textView37);
                created_timestamp=(TextView)view.findViewById(R.id.textView33);
               // forest_name=(TextView)view.findViewById(R.id.textView32);
                thumbup_num=(TextView)view.findViewById(R.id.textView34);
                reply_num=(TextView)view.findViewById(R.id.textView35);
                follow_num =(TextView)view.findViewById(R.id.textView36);
                hole_id=(TextView)view.findViewById(R.id.textView32);
               // background_image_url=(ImageView)view.findViewById(R.id.imageView12);
                is_thumbup =(ImageView)view.findViewById(R.id.imageView13);
                is_reply=(ImageView)view.findViewById(R.id.imageView14);
                is_follow=(ImageView)view.findViewById(R.id.imageView15);
                //forestname = (TextView) view.findViewById(R.id.textView28);
                //forestphoto=(ImageView)view.findViewById(R.id.circleImageView);
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent= CommentActivity.newIntent(Page2_DetailAllForestsActivity.this,detailforest[position]);
                        startActivity(intent);
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
                                            Toast.makeText(Page2_DetailAllForestsActivity.this,"点赞失败,请检查网络状况",Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(Page2_DetailAllForestsActivity.this,"取消点赞失败,请检查网络状况",Toast.LENGTH_SHORT).show();
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
                                    Call<ResponseBody> call = request.thumbups("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest[position][6]);//进行封装
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
                                            Toast.makeText(Page2_DetailAllForestsActivity.this,"关注失败,请检查网络状况",Toast.LENGTH_SHORT).show();
                                            Log.d("","取消点赞失败");
                                        }
                                    });
                                }
                            }).start();
                        }else{
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Call<ResponseBody> call = request.deletethumbups("http://hustholetest.pivotstudio.cn/api/follows/"+detailforest[position][6]);//进行封装
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
                                            Toast.makeText(Page2_DetailAllForestsActivity.this,"取消关注失败,请检查网络状况",Toast.LENGTH_SHORT).show();
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
                content.setText(detailforest[position][1]);
                created_timestamp.setText(detailforest[position][2]);
                follow_num.setText(detailforest[position][3]);
                reply_num.setText(detailforest[position][12]);
                thumbup_num.setText(detailforest[position][13]);
                hole_id.setText("#" + detailforest[position][6]);
                Log.d(TAG,detailforest[position][8]);
                Log.d(TAG,detailforest[position][10]);
                Log.d(TAG,detailforest[position][11]);

                if (detailforest[position][8].equals("true")) {
                    is_follow.setImageResource(R.mipmap.active_3);
                    //is_follow.setImageDrawable(R.drawable.hole_active_1);
                }

                if (detailforest[position][10].equals("true")) {
                    is_reply.setImageResource(R.mipmap.active_2);
                }
                if (detailforest[position][11].equals("true")) {
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
        public ForestHoleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.foresthole_2_model,parent,false );
            Log.d(TAG,"已经创建适配器布局");
           ForestHoleAdapter.ViewHolder holder=new ForestHoleAdapter.ViewHolder(view);
            return holder;
        }
        @Override
        public void onBindViewHolder(ForestHoleAdapter.ViewHolder holder, int position) {
            //Event event=events.get(position);
            Log.d(TAG,"已经设置单个信息了"+position);
            holder.bind(position);

        }
        @Override
        public int getItemCount() {
            return jsonArray2.length();
        }
    }



    public static Intent newIntent(Context packageContext, String[] data){
        Intent intent = new Intent(packageContext, Page2_DetailAllForestsActivity.class);
        intent.putExtra(key,data);
        return intent;
    }
}
