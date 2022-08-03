package cn.pivotstudio.modulec.homescreen.oldversion.mine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.oldversion.model.EditTextReaction;
import cn.pivotstudio.modulec.homescreen.oldversion.network.OkHttpUtil;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RequestInterface;
import cn.pivotstudio.modulec.homescreen.oldversion.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;
import java.io.IOException;
import java.util.HashMap;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VerifyActivity extends AppCompatActivity {
    private static final String ACTIVITY_IDENTIFY = "email";
    Button btn_verify;
    Boolean match = false;
    EditText et;
    TextView tv_again, tv_howTo, tv_time, tv_not;
    ImageView img;
    LinearLayout ll_retry;
    Retrofit retrofit;
    Boolean mIfResend = false;
    private RequestInterface request;

    public static Intent newIntent(Context context, String useremail) {
        Intent intent = new Intent(context, VerifyActivity.class);
        intent.putExtra(ACTIVITY_IDENTIFY, useremail);
        //intent1.putExtra(ACTIVITY_IDENTIFY2,vertify);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify2);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.HH_BandColor_1),
            true);
        if (getSupportActionBar() != null) {//隐藏上方ActionBar
            getSupportActionBar().hide();
        }

        et = findViewById(R.id.et_verify);
        btn_verify = findViewById(R.id.btn_verify);
        tv_again = findViewById(R.id.tv_again);
        tv_howTo = findViewById(R.id.tv_howTo);
        img = findViewById(R.id.email_verify2_img);
        ll_retry = findViewById(R.id.ll_emailverify2);
        ll_retry.setVisibility(View.INVISIBLE);
        tv_time = findViewById(R.id.tv_emailverify_time);
        tv_not = findViewById(R.id.tv_not);
        btn_verify.setOnClickListener(this::onClick);
        tv_again.setOnClickListener(this::onClick);
        tv_howTo.setOnClickListener(this::onClick);
        img.setOnClickListener(this::onClick);
        retrofit = RetrofitManager.getRetrofit();
        request = RetrofitManager.getRequest();
        SpannableString string1 = new SpannableString("输入验证码");
        EditTextReaction.EditTextSize(et, string1, 14);
        btn_verify.setEnabled(false);
        EditTextReaction.ButtonReaction(et, btn_verify);
    }

    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.btn_verify) {
            SharedPreferences editor =
                VerifyActivity.this.getSharedPreferences("Depository", Context.MODE_PRIVATE);//
            String emailcode = editor.getString("email", "");
            Log.d("VerifyActivity ", emailcode + "+" + et.getText().toString() + "+");
            //                if(request == null){Log.d("xxx","ttt");}
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map = new HashMap();
                    retrofit = new Retrofit.Builder().baseUrl(RetrofitManager.API + "auth/")
                        .client(OkHttpUtil.getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                    request = retrofit.create(RequestInterface.class);//创建接口实例
                    // map.put("email", emailcode);
                    map.put("verify_code", et.getText().toString());
                    Call<ResponseBody> call = request.verifyCodeMatch(
                        RetrofitManager.API + "auth/activation?verify_code=" + et.getText()
                            .toString());
                    //String vertify=et.getText().toString();
                    //Call<ResponseBody> call = request.verifyCodeMatch(RetrofitManager.API+"auth/activation?email="+emailcode+"&verify_code="+ et.getText().toString());//进行封装
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(VerifyActivity.this, json, Toast.LENGTH_SHORT)
                                    .show();
                                //                                Intent intent = new Intent(VerifyActivity.this, HomeScreenActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                //                                startActivity(intent);
                            } else {
                                String json = "null";
                                String returncondition = null;
                                if (response.errorBody() != null) {
                                    try {
                                        json = response.errorBody().string();
                                        JSONObject jsonObject = new JSONObject(json);
                                        returncondition = jsonObject.getString("msg");
                                        Toast.makeText(VerifyActivity.this, returncondition,
                                            Toast.LENGTH_SHORT).show();
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(VerifyActivity.this,
                                            R.string.network_unknownfailture, Toast.LENGTH_SHORT)
                                        .show();
                                }
                                //ErrorMsg.getErrorMsg(response,VerifyActivity.this);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            Toast.makeText(VerifyActivity.this, R.string.network_failure,
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        } else if (id == R.id.tv_again) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Retrofit retrofit2 =
                        new Retrofit.Builder().baseUrl(RetrofitManager.API + "auth/")
                            .client(OkHttpUtil.getOkHttpClient2())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RequestInterface request2 = retrofit2.create(RequestInterface.class);
                    HashMap map = new HashMap();
                    SharedPreferences editor =
                        VerifyActivity.this.getSharedPreferences("Depository",
                            Context.MODE_PRIVATE);//
                    String condition = editor.getString("email", "");
                    map.put("email", condition);
                    map.put("isResetPassword", false);
                    Call<ResponseBody> call = request2.sendVerifyCode(RetrofitManager.API
                        + "auth/sendVerifyCode?email="
                        + condition
                        + "&isResetPassword=false");
                    // Call<ResponseBody> call = request2.sendVerifyCode(map);//进行封装
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call,
                                               Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                ll_retry.setVisibility(View.VISIBLE);
                                tv_again.setVisibility(View.INVISIBLE);
                                tv_not.setVisibility(View.INVISIBLE);
                                CountDownTimer timer = new CountDownTimer(60000, 1000) {//倒计时
                                    public void onTick(long millisUntilFinished) {
                                        tv_time.setText(millisUntilFinished / 1000 + "s");
                                    }

                                    public void onFinish() {
                                        ll_retry.setVisibility(View.INVISIBLE);
                                        tv_again.setVisibility(View.VISIBLE);
                                        tv_howTo.setVisibility(View.VISIBLE);
                                        //textView2.setVisibility(View.INVISIBLE);
                                        //textView3.setVisibility(View.INVISIBLE);
                                    }
                                };
                                timer.start();

                                String json = "null";
                                try {
                                    if (response.body() != null) {
                                        json = response.body().string();
                                        JSONObject jsonObject = new JSONObject(json);
                                        String returncondition = jsonObject.getString("msg");
                                        Toast.makeText(VerifyActivity.this, returncondition,
                                            Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String json = "null";
                                String returncondition = null;
                                if (response.errorBody() != null) {
                                    try {
                                        json = response.errorBody().string();
                                        //JSONObject jsonObject = new JSONObject(json);
                                        //returncondition = jsonObject.getString("msg");
                                        Toast.makeText(VerifyActivity.this, json,
                                            Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(VerifyActivity.this,
                                            R.string.network_unknownfailture, Toast.LENGTH_SHORT)
                                        .show();
                                }
                                // ErrorMsg.getErrorMsg(response,VerifyActivity.this);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable tr) {
                            Toast.makeText(VerifyActivity.this, R.string.network_loginfailure,
                                Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        } else if (id == R.id.tv_howTo) {
            intent = new Intent(getApplicationContext(), EmailHowToActivity.class);
            startActivity(intent);
        } else if (id == R.id.email_verify2_img) {
            finish();
        }
    }
}