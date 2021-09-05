package cn.pivotstudio.husthole.view.homescreen.mine;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.model.CheckingToken;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SecurityActivity extends AppCompatActivity {
    SwitchMaterial isUnderSecurity;
    ImageView back;
    private static final String BASE_URL = RetrofitManager.API;
    private final String TAG = "insecurity";
    Retrofit retrofit;
    RequestInterface request;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        RetrofitManager.RetrofitBuilder(BASE_URL);
        retrofit = RetrofitManager.getRetrofit();
        request = retrofit.create(RequestInterface.class);
        isUnderSecurity = findViewById(R.id.st_security);
        back = findViewById(R.id.security_img);
        back.setOnClickListener(v -> finish());
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        if(getSupportActionBar()!=null){//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        setInitMode();
        isUnderSecurity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(CheckingToken.IfTokenExist())
                changeSecurityMode(isChecked);
            else
                Toast.makeText(SecurityActivity.this, "认证信息无效，请先登录。", Toast.LENGTH_SHORT).show();
        });


    }
    private void  setInitMode(){
        final Boolean[] isUnder = new Boolean[1];
        new Thread(() -> {
            Call<ResponseBody> call = request.isUnderSecurity();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                   try {
                       if(response.body() != null){
                           JSONObject mode = new JSONObject(response.body().string());
                           Log.d(TAG,response.body().string());
                           isUnder[0] = mode.getBoolean("is_incognito");
                           Log.d(TAG,"现在处于安全模式？" + isUnder[0]);
                       }
                    } catch (IOException | JSONException e) {
                       Log.d(TAG,"in this");
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });
        }).start();
        isUnderSecurity.setChecked(isUnder[0] != null && isUnder[0]);
    }
    private void changeSecurityMode(Boolean turnOn){
        new Thread(() -> {
            request = retrofit.create(RequestInterface.class);
            HashMap map = new HashMap();
            map.put("to_incognito",turnOn);
            Call<ResponseBody> call = request.changeSecurityMode(map);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG,"切换成功，现在是打开？" + response.body());
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });
        }).start();
    }
}

