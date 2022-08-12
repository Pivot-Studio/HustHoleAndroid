package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.SaveImg;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.bumptech.glide.Glide;
import com.githang.statusbar.StatusBarCompat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.IOException;
import java.net.URL;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
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
    String TAG = "ShareCard";
    PopupWindow ppwShareTo;
    Boolean isShow = false;
    String imgurl = "";

    protected void onCreate(Bundle savedInstanceState) {
        String BASE_URL = RetrofitManager.API;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_card);

        //        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
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
                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                ppwShareTo.dismiss();
            }
        });
        MediaScannerConnection.scanFile(this, new String[] { "path" },
            new String[] { "image/jpeg" }, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.i(TAG, "onScanCompleted" + path);
                }
            });

        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (imgurl.equals("")) {
                    Toast.makeText(ShareCardActivity.this, "最新分享图片加载失败", Toast.LENGTH_SHORT).show();
                } else {
                    Picasso.get().load(imgurl).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.i(TAG, "load from==>" + from);
                            boolean hasSaved =
                                SaveImg.saveImg(bitmap, "分享卡片" + ".png", ShareCardActivity.this,
                                    ShareCardActivity.this);
                            if (hasSaved) {
                                Toast.makeText(ShareCardActivity.this, "已保存至相册", Toast.LENGTH_SHORT)
                                    .show();
                            } else {
                                Toast.makeText(ShareCardActivity.this, "保存失败", Toast.LENGTH_SHORT)
                                    .show();
                            }
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Toast.makeText(ShareCardActivity.this, "保存失败", Toast.LENGTH_SHORT)
                                .show();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }
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

    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    private void onClick(View view) {
        if (R.id.share_card_img == view.getId()) {
            if (!isShow) {
                ppwShareTo.showAsDropDown(ppwLocation);
                //                ppwShareTo.showAtLocation(getParent().getWindow().getDecorView(), Gravity.BOTTOM,0,0);
                isShow = true;
            } else {
                ppwShareTo.dismiss();
                isShow = false;
            }
        }
    }

    private void getImg() {
        new Thread(() -> {
            Call<ResponseBody> call = request.checkupdate();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("sadfgdadgffgf", jsonObject + "");
                        String urlStr = jsonObject.getString("AndroidLatestShareImage");
                        if (urlStr.equals("")) {
                            Toast.makeText(ShareCardActivity.this, "获取的图片链接为空", Toast.LENGTH_SHORT)
                                .show();
                        }
                        imgurl = urlStr;
                        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
                        String storedStr = pref.getString("img_url", urlStr);
                        if (!storedStr.equals(urlStr)) {
                            SharedPreferences.Editor editor =
                                getSharedPreferences("data", MODE_PRIVATE).edit();
                            editor.putString("img_url", urlStr);
                            editor.apply();
                        }
                        URL url = new URL(urlStr);
                        Glide.with(getApplicationContext()).load(url).into(cardImg);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "failure");
                    t.printStackTrace();
                }
            });
        }).start();
    }
}
