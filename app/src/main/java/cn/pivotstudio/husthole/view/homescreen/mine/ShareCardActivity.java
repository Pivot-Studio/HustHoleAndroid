package cn.pivotstudio.husthole.view.homescreen.mine;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShareCardActivity extends AppCompatActivity {
    View ShareToView;
    ImageView back, cardImg;
    TextView ppwLocation;
    LinearLayout store;
    Retrofit retrofit;
    RequestInterface request;
    String TAG ="ShareCard";
    PopupWindow ppwShareTo;

    protected void onCreate(Bundle savedInstanceState) {
        String BASE_URL = "http://hustholetest.pivotstudio.cn/api/";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card);

//        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);

        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        back = findViewById(R.id.share_card_back);
        cardImg = findViewById(R.id.share_card_img);
        ppwLocation = findViewById(R.id.ppw_location);


        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);

        getImg();

        ShareToView = LayoutInflater.from(this).inflate(R.layout.ppw_share_to, null);

        store = ShareToView.findViewById(R.id.store);

        ppwShareTo = new PopupWindow(ShareToView);
        ppwShareTo.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        ppwShareTo.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ppwShareTo.setAnimationStyle(R.style.Page2Anim);
        ppwShareTo.setOutsideTouchable(true);

        cardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ppwShareTo.showAsDropDown(ppwLocation);
            }
        });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                ppwShareTo.dismiss();
            }
        });

//        ppwShareTo.showAsDropDown(cardImg);
//        ppwShareTo.setOutsideTouchable(true);

//        store.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
//                ppwShareTo.dismiss();
//            }
//        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void getImg(){
        new Thread(() -> {
            Call<ResponseBody> call = request.getShareImage();
            call.enqueue(new Callback<ResponseBody>() {
                String jsonStr = "null";
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        String urlStr = jsonObject.getString("msg");
                        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
                        String storedStr = pref.getString("img_url",urlStr);
                        if(!storedStr.equals(urlStr)){
                            SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                            editor.putString("img_url",urlStr);
                            editor.apply();
                        }
                        URL url=new URL(urlStr);
                        Glide.with(getApplicationContext()).load(url).into(cardImg);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG,"failure");
                    t.printStackTrace();
                }
            });
        }).start();
    }
}
