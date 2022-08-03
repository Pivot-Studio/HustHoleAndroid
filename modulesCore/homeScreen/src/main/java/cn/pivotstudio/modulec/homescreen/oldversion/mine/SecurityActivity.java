package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken;
import cn.pivotstudio.modulec.homescreen.oldversion.network.ErrorMsg;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SecurityActivity extends AppCompatActivity {
    private static final String BASE_URL = RetrofitManager.API;
    private final String TAG = "Security";
    SwitchMaterial isUnderSecurity;
    ImageView back;
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
        isUnderSecurity.setVisibility(View.INVISIBLE);
        back = findViewById(R.id.security_img);
        back.setOnClickListener(v -> finish());
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }
        Log.d(TAG, " in 1");
        setInitMode();
        isUnderSecurity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (CheckingToken.IfTokenExist()) {
                changeSecurityMode(!isChecked);
            } else {
                Toast.makeText(SecurityActivity.this, "认证信息无效，请先登录。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setInitMode() {
        Log.d(TAG, " in setInitMode");
        final Boolean[] isUnder = new Boolean[1];
        new Thread(() -> {
            Call<ResponseBody> call = request.isUnderSecurity();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject mode = new JSONObject(response.body().string());
                        isUnder[0] = mode.getBoolean("is_incognito");
                        isUnderSecurity.setChecked(!isUnder[0]);
                        isUnderSecurity.setVisibility(View.VISIBLE);
                    } catch (IOException | JSONException e) {
                        Log.d(TAG, "in this");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SecurityActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void changeSecurityMode(Boolean turnOn) {
        new Thread(() -> {
            request = retrofit.create(RequestInterface.class);
            Call<ResponseBody> call =
                request.changeSecurityMode(BASE_URL + "auth/update?to_incognito=" + turnOn);
            call.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d(TAG, "code为 ： " + response.code());
                    if (response.code() == 400) {
                        ErrorMsg.getErrorMsg(response, SecurityActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SecurityActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}

