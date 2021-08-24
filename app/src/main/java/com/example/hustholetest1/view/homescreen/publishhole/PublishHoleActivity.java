package com.example.hustholetest1.view.homescreen.publishhole;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.network.RequestInterface;
import com.example.hustholetest1.model.SoftKeyBoardListener;
import com.example.hustholetest1.network.RetrofitManager;
import com.example.hustholetest1.network.TokenInterceptor;
import com.example.hustholetest1.R;
import com.example.hustholetest1.view.homescreen.forest.DetailForestActivity;
import com.githang.statusbar.StatusBarCompat;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class PublishHoleActivity extends AppCompatActivity {//发树洞
    private static final String key="key_1";
    private TextView text,limit,title;
    private ImageView back,X;
    private EditText editText;
    private Button button0;
    private PopupWindow  popWindow,popWindow2;
    private Retrofit retrofit;
    private RequestInterface request;
    private JSONArray jsonArray,jsonArray2;
    private String[][] detailforest,detailforest2;
    private RecyclerView recyclerView;
    private int number=0;
    private String what="0";
    private ConstraintLayout linearLayout;
    private TextView length;
    private AVLoadingIndicatorView mAVLoadingIndicatorView;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishhole);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
         Button button8=(Button)findViewById(R.id.btn_publishhole_line);
         title=(TextView)findViewById(R.id.tv_titlebargreen_title);
         title.setText(R.string.publishhole_1);
         mAVLoadingIndicatorView=(AVLoadingIndicatorView)findViewById(R.id.titlebargreen_AVLoadingIndicatorView);
         mAVLoadingIndicatorView.hide();
         mAVLoadingIndicatorView.setVisibility(View.GONE);

        SoftKeyBoardListener.setListener(this,new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //Toast.makeText(PublishHoleActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();
                int[] location=new int[2];
                button8.getLocationOnScreen(location);
                int[] location2=new int[2];
                length.getLocationOnScreen(location2);
                editText.setMaxHeight(location2[1]-location[1]-20);

            }

            @Override
            public void keyBoardHide(int height) {
               // Toast.makeText(PublishHoleActivity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
                //textView.setText("高度："+String.valueOf(height));
                int[] location=new int[2];
                button8.getLocationOnScreen(location);
                int[] location2=new int[2];
                length.getLocationOnScreen(location2);
                editText.setMaxHeight(location2[1]-location[1]-20);

            }
        });




        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1), true);
        if (getSupportActionBar()!= null) {
            getSupportActionBar().hide();
        }

        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.publishhole_4));

        TokenInterceptor.getContext(PublishHoleActivity.this);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);//创建接口实;//创建接口实
        length=(TextView)findViewById(R.id.tv_publishhole_textnumber);


        back=(ImageView)findViewById(R.id.iv_titlebargreen_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                finish();
            }
        });
        button0=(Button)findViewById(R.id.btn_publishhole_send);


        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content=editText.getText().toString();
                if(content.length()>15) {
                    new Thread(new Runnable() {//加载纵向列表标题
                        @Override
                        public void run() {
                            Call<ResponseBody> call = request.holes("http://hustholetest.pivotstudio.cn/api/holes?content="+content+"&forest_id="+what);//进行封装
                            Log.e(TAG, "token2：");
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    Toast.makeText(PublishHoleActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                    finish();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    // 隐藏软键盘
                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(PublishHoleActivity.this,"发布失败，请检查网络",Toast.LENGTH_SHORT).show();
                                }


                            });
                        }
                    }).start();
                }else{
                    Toast.makeText(PublishHoleActivity.this,"输入内容至少需要15字",Toast.LENGTH_SHORT).show();
                }
            }
        });
        text=(TextView)findViewById(R.id.tv_publishhole_forestname);
        String forest=(String) getIntent()
                .getStringExtra(key);
        if(forest.equals("1")){

        }else{
            text.setText(forest);
            what=forest;
        }



        limit=(TextView)findViewById(R.id.tv_publishhole_pwlocation);
        editText=(EditText)findViewById(R.id.et_publishhole);
        EditTextReaction.ButtonReaction(editText,button0);
        EditTextReaction.EditTextSize(editText,string1,14);
        linearLayout=(ConstraintLayout)findViewById(R.id.include);
        TextView limit2=(TextView) findViewById(R.id.limit2);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                length.setText(s.length()+"/1037");
                if (s.length() >=1037) {
                    //editText.setFocusable(false);
                    Toast.makeText(PublishHoleActivity.this,"输入内容过长",Toast.LENGTH_SHORT).show();
                    //textInputLayout.setErrorEnabled(true);
                    //textInputLayout.setError("输入超长");
                } else {
                   // editText.setFocusable(true);
                }
            }
        });



        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // 隐藏软键盘
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                View contentView = LayoutInflater.from(PublishHoleActivity.this).inflate(R.layout.ppw_publishhole, null);
                View contentView2 = LayoutInflater.from(PublishHoleActivity.this).inflate(R.layout.ppw_homepagedarkscreen, null);
                //关闭掉对话框,拿到对话框的对象

                int[] location=new int[2];
                limit.getLocationOnScreen(location);
                int[] location2=new int[2];
                limit2.getLocationOnScreen(location2);

                popWindow2=new PopupWindow(contentView2);
                popWindow2.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popWindow2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                popWindow2.setAnimationStyle(R.style.darkScreenAnim);
                popWindow2.showAsDropDown(linearLayout);

                popWindow=new PopupWindow(contentView);
                popWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popWindow.setHeight(location2[1]-location[1]);
                Toast.makeText(PublishHoleActivity.this,location2[1]-location[1]+"",Toast.LENGTH_SHORT).show();
                popWindow.setAnimationStyle(R.style.Page2Anim);
                //popWindow.setClippingEnabled(false);
                popWindow.showAsDropDown(limit);


                contentView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popWindow2.dismiss();
                        popWindow.dismiss();
                    }
                });
                X=(ImageView)contentView.findViewById(R.id.iv_ppwpublishhole_finish);
                X.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popWindow2.dismiss();
                        popWindow.dismiss();
                    }
                });
                recyclerView = (RecyclerView)contentView.findViewById(R.id.rv_ppwpublishhole);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PublishHoleActivity.this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);


                new Thread(new Runnable() {//加载纵向列表标题
                    @Override
                    public void run() {
                        number=0;
                        Call<ResponseBody> call = request.joined(30,0);//进行封装
                        Log.e(TAG, "token2：");
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }
                                    JSONObject jsonObject = new JSONObject(json);
                                    //读取
                                    jsonArray=jsonObject.getJSONArray("forests");
                                    detailforest=new String[jsonArray.length()][8];
                                    new DownloadTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    //}

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {

                            }


                        });
                        Call<ResponseBody> call2 = request.getHotForest(0,10);//进行封装
                        call2.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }

                                    JSONObject jsonObject=new JSONObject(json);
                                    jsonArray2=jsonObject.getJSONArray("forests");
                                    detailforest2=new String[jsonArray2.length()][8];
                                    new DownloadTask2().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();
                                }
                                // Log.e(TAG, "URLSASFSDGS"+background_image_url_list[0]);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable tr) {

                            }
                        });



                    }
                }).start();




                //popupWindow2=new PopupWindow(contentView2);
                //button=(Button)contentView.findViewById(R.id.button);
                //text=(TextView)contentView.findViewById(R.id.textView60);
            }
        });



    }






    public static Intent newIntent(Context packageContext, String data){
        Intent intent = new Intent(packageContext, PublishHoleActivity.class);
        intent.putExtra(key,data);
        return intent;
    }




    private class DownloadTask extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            number++;
            if(number==2) {
                recyclerView.setAdapter(new ForestHoleAdapter());
            }
            // number1++;
            // if(jsonArray.length()==number1){

            //}
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //int allnumber=voids[0];
            try {
                for(int f=0;f<jsonArray.length();f++) {
                    JSONObject sonObject = jsonArray.getJSONObject(f);
                    detailforest[f][0] = sonObject.getString("background_image_url");
                    detailforest[f][1] = sonObject.getString("cover_url");
                    detailforest[f][2] = sonObject.getString("description");
                    detailforest[f][3] = sonObject.getInt("forest_id") + "";
                    detailforest[f][4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                    detailforest[f][5] = "true";
                    detailforest[f][6] = sonObject.getString("last_active_time");
                    detailforest[f][7] = sonObject.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }





    private class DownloadTask2 extends AsyncTask<Void, Void, Void> {//用于加载图片

        @Override
        protected void onPostExecute(Void unused) {
            number++;
            if(number==2) {
                recyclerView.setAdapter(new ForestHoleAdapter());
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //int allnumber=voids[0];
            try {
                for(int f=0;f<jsonArray2.length();f++) {
                    JSONObject sonObject = jsonArray2.getJSONObject(f);
                    detailforest2[f][0] = sonObject.getString("background_image_url");
                    detailforest2[f][1] = sonObject.getString("cover_url");
                    detailforest2[f][2] = sonObject.getString("description");
                    detailforest2[f][3] = sonObject.getInt("forest_id") + "";
                    detailforest2[f][4] = sonObject.getInt("hole_number") + "Huster . " + sonObject.getInt("joined_number") + "树洞";
                    detailforest2[f][5] = "true";
                    detailforest2[f][6] = sonObject.getString("last_active_time");
                    detailforest2[f][7] = sonObject.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




    public class ForestHoleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        //private static List<Event> events;
        public static final int ITEM_TYPE_HEADER = 0;
        public static final int ITEM_TYPE_CONTENT = 1;
        public static final int ITEM_TYPE_BOTTOM = 2;
        private int mHeaderCount=1;//头部View个数

        @Override
        public int getItemViewType(int position) {
            if (position ==1||position==2+jsonArray.length()) {
                return ITEM_TYPE_HEADER;
            } else {
                return ITEM_TYPE_CONTENT;
            }
        }

        public class HeadHolder extends RecyclerView.ViewHolder{



            private TextView textView;
            public HeadHolder(View view){
                super(view);
                textView=(TextView)view.findViewById(R.id.tv_publishholeforestlisttitle_title);
            }
            public void bind(int position){
                if(position==1) {
                    textView.setText("加入的小树林");
                }else if(position==2+jsonArray.length()){
                    textView.setText("热门的小树林");
                }
            }
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView content;
            private ImageView background_image_url;
            private Button button;
            ConstraintLayout next;
            private int position;
            public ViewHolder(View view) {
                super(view);
                content=(TextView)view.findViewById(R.id.tv_publishholeforestlist_name);
                background_image_url=(ImageView)view.findViewById(R.id.iv_publishholeforestlist_icon);
                button=(Button)view.findViewById(R.id.btn_publishholeforestlist_chooseforest);
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        popWindow.dismiss();
                        popWindow2.dismiss();
                        if (position > 2 + jsonArray.length()) {
                            text.setText(detailforest2[position-3-jsonArray.length()][7]);
                            what=detailforest2[position-3-jsonArray.length()][3];
                        } else if (position>1&&position<2+jsonArray.length()) {
                            text.setText(detailforest[position-2][7]);
                            what=detailforest[position-2][3];
                        }else if(position==0){
                            text.setText("未选择加入小树林");
                            what="0";
                        }
                    }
                });
                view.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if (position > 2 + jsonArray.length()) {
                            Log.d("data[2]1", detailforest2[position-3-jsonArray.length()][2]);
                            Intent intent = DetailForestActivity.newIntent(PublishHoleActivity.this, detailforest2[position-3-jsonArray.length()]);
                            startActivity(intent);
                        } else if (position>1&&position<2+jsonArray.length()) {
                            Intent intent = DetailForestActivity.newIntent(PublishHoleActivity.this, detailforest[position-2]);
                            startActivity(intent);
                        }
                    }
                });
            }


            public void bind(int position){
                Log.d("position",position+"");
                this.position=position;
                RoundedCorners roundedCorners = new RoundedCorners(16);
                RequestOptions options1 = RequestOptions.bitmapTransform(roundedCorners);
                if(position==0) {
                    Glide.with(PublishHoleActivity.this)
                            .load(R.mipmap.vector3)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(options1)
                            .into(background_image_url);
                    content.setText("未选择加入小树林");
                }else if (position > 2 + jsonArray.length()) {
                    content.setText(detailforest2[position-3-jsonArray.length()][7]);
                    Glide.with(PublishHoleActivity.this)
                            .load(detailforest2[position-3-jsonArray.length()][0])
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(options1)
                            .into(background_image_url);
                } else if (position>1&&position<2+jsonArray.length()) {
                    content.setText(detailforest[position-2][7]);
                    Glide.with(PublishHoleActivity.this)
                            .load(detailforest[position-2][0])
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .apply(options1)
                            .into(background_image_url);
                }

            }
        }



        public ForestHoleAdapter(){
            Log.d(TAG,"数据传入了");
            //this.events=events;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {


            if (viewType ==ITEM_TYPE_HEADER) {
                return new PublishHoleActivity.ForestHoleAdapter.HeadHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_publishholeforestlisttitle,parent,false ));
            } else if (viewType == ITEM_TYPE_CONTENT) {
                return new PublishHoleActivity.ForestHoleAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_publishholeforestlist,parent,false ));
            }
            return null;

        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //Event event=events.get(position);

            if (holder instanceof PublishHoleActivity.ForestHoleAdapter.HeadHolder) {
                ((PublishHoleActivity.ForestHoleAdapter.HeadHolder)holder).bind(position);
            } else if (holder instanceof PublishHoleActivity.ForestHoleAdapter.ViewHolder) {
                ((PublishHoleActivity.ForestHoleAdapter.ViewHolder) holder).bind(position);
            }

            Log.d(TAG,"已经设置单个信息了"+position);


        }
        @Override
        public int getItemCount() {
            return 3+jsonArray.length()+jsonArray2.length();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(popWindow!=null){
        popWindow.dismiss();
        popWindow2.dismiss();
        }
    }
    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }
    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }
}
