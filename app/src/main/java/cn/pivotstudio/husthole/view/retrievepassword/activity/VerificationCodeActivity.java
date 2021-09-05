package cn.pivotstudio.husthole.view.retrievepassword.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.husthole.model.EditTextReaction;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.network.ErrorMsg;
import cn.pivotstudio.husthole.network.OkHttpUtil;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.network.RetrofitManager;
import com.githang.statusbar.StatusBarCompat;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VerificationCodeActivity extends AppCompatActivity {
    private EditText editText1;
    private Button button1;
    private TextView time1,hint1,textView2,textView3;
    private ImageView back;
    private static final String ACTIVITY_IDENTIFY="identify";
    private String id;
    private RequestInterface request;
    private Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrievepasswordvcode);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);
        editText1=(EditText)findViewById(R.id.et_vcode_email);
        button1=(Button)findViewById(R.id.btn_vcode_jumptologin);
        time1=(TextView)findViewById(R.id.tv_vcode_time);
        hint1=(TextView)findViewById(R.id.tv_vcode_sendtosomebody);
        textView2=(TextView)findViewById(R.id.tv_detailforest_new);
        textView3=(TextView)findViewById(R.id.textView2);
        button1=(Button)findViewById(R.id.btn_vcode_jumptologin);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        button1.setEnabled(false);
        //hint1.setVisibility(View.INVISIBLE);
        editText1=(EditText)findViewById(R.id.et_vcode_email);
         id=(String) getIntent()
                .getStringExtra(ACTIVITY_IDENTIFY);
        hint1.setText("验证邮箱已发送至"+id+"@hust.edu.cn");

        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.retrieve_password_vcode_3));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
        CountDownTimer timer = new CountDownTimer(60000, 1000) {//倒计时
            public void onTick(long millisUntilFinished) {
                time1.setText( millisUntilFinished / 1000 + "s");
            }
            public void onFinish() {
                time1.setVisibility(View.INVISIBLE);
                textView2.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
            }
        };
        timer.start();
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitManager.API+"auth/")
                .client(OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        request = retrofit.create(RequestInterface.class);//创建接口实例
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_vcode_jumptologin://验证成功后进入重新设置密码界面

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            String vertify=editText1.getText().toString();
                            map.put("email", id);
                            map.put("verify_code",vertify);

                            Call<ResponseBody> call = request.verifyCodeMatch(RetrofitManager.API+"auth/verifyCodeMatch?email="+id+"&verify_code="+vertify);//进行封装
                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if(response.code()==200) {
                                        String json = "null";
                                        try {
                                            if (response.body() != null) {
                                                json = response.body().string();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent=ModifyPasswordActivity.newIntent(VerificationCodeActivity.this,id,vertify);
                                        startActivity(intent);
                                    }else{
                                        String json = "null";
                                        String returncondition = null;
                                        if (response.errorBody() != null) {
                                            try {
                                                json = response.errorBody().string();
                                                JSONObject jsonObject = new JSONObject(json);
                                                returncondition = jsonObject.getString("is_match");
                                                Toast.makeText(VerificationCodeActivity.this, returncondition, Toast.LENGTH_SHORT).show();
                                            } catch (IOException | JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }else{
                                            Toast.makeText(VerificationCodeActivity.this, R.string.network_unknownfailture,Toast.LENGTH_SHORT).show();
                                        }
                                        //ErrorMsg.getErrorMsg(response,VerificationCodeActivity.this);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(VerificationCodeActivity.this, R.string.network_loginfailure, Toast.LENGTH_SHORT).show();
                                }

                            });
                        }
                    }).start();

                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            default:
                break;
        }
    }
    public static Intent newIntent(Context context, String id1){
        Intent intent1=new Intent(context,VerificationCodeActivity.class);
        intent1.putExtra(ACTIVITY_IDENTIFY,id1);
        return intent1;
    }
}
