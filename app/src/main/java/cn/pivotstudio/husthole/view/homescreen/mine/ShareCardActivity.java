package cn.pivotstudio.husthole.view.homescreen.mine;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Objects;

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
    Boolean isShow=false;

    protected void onCreate(Bundle savedInstanceState) {
        String BASE_URL = RetrofitManager.API;

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

//        MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "", "");

        isShow = false;
        ppwShareTo = new PopupWindow(ShareToView);
        ppwShareTo.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        ppwShareTo.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        ppwShareTo.setAnimationStyle(R.style.Page2Anim);
        ppwShareTo.setOutsideTouchable(true);

//        cardImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!isShow){
//                    ppwShareTo.showAtLocation(Objects.requireNonNull(getActivity()).getWindow().getDecorView(), Gravity.BOTTOM,0,0);
//                    isShow = true;
//                }else {
//                    ppwShareTo.dismiss();
//                    isShow = false;
//                }
//            }
//        });
        cardImg.setOnClickListener(this::onClick);

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                ppwShareTo.dismiss();
            }
        });
        MediaScannerConnection.scanFile(this,
                new String[]{"path"},
                new String[]{"image/jpeg"},
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i(TAG,"onScanCompleted"+path);
                    }
                });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                public static Uri saveAlbum(Context context, Bitmap bitmap, Bitmap.CompressFormat format, int quality, boolean recycle) {
                cardImg.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(cardImg.getDrawingCache());
                cardImg.setDrawingCacheEnabled(false);
                if(bitmap != null){
//                    Uri uri = SaveImgUtil.saveAlbum(getParent(),Bitmap.createBitmap(convertViewToBitmap(cardImg)), Bitmap.CompressFormat.PNG,3,true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
                    Log.d(TAG,"ok");

                }else {
                    Log.d(TAG,"is null");
                }
                Toast.makeText(getApplicationContext(),"保存成功",Toast.LENGTH_SHORT).show();
                ppwShareTo.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    public Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    private void onClick(View view) {
        if(R.id.share_card_img == view.getId())
            if(!isShow){
                ppwShareTo.showAsDropDown(ppwLocation);
//                ppwShareTo.showAtLocation(getParent().getWindow().getDecorView(), Gravity.BOTTOM,0,0);
                isShow = true;
            }else {
                ppwShareTo.dismiss();
                isShow = false;
            }
    }



    private void getImg(){
        new Thread(() -> {
            Call<ResponseBody> call = request.getShareImage();
            call.enqueue(new Callback<ResponseBody>() {
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
