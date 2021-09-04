package cn.pivotstudio.husthole.view.registerandlogin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.pivotstudio.husthole.model.EditTextReaction;
import cn.pivotstudio.husthole.network.ErrorMsg;
import cn.pivotstudio.husthole.network.RequestInterface;
import cn.pivotstudio.husthole.network.RetrofitManager;
import cn.pivotstudio.husthole.network.TokenInterceptor;
import cn.pivotstudio.husthole.R;
import cn.pivotstudio.husthole.network.OkHttpUtil;
import com.githang.statusbar.StatusBarCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SetPasswordActivity extends AppCompatActivity {
    private TextView textView1;
    private EditText editText1;
    private CheckBox checkBox1;
    private Button button1;
    private ImageView back;
   // private String loginemail;
    private Retrofit retrofit;
    private RequestInterface request;
    private static final String TAG = "RegisterActivity";


    private static final String emailkey="key_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpassword);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        textView1=(TextView)findViewById(R.id.tv_setpassword_warn);
        editText1=(EditText)findViewById(R.id.et_setpassword);
        checkBox1=(CheckBox)findViewById(R.id.ck_setpassword_checkBox);
        button1=(Button)findViewById(R.id.btn_setpassword_jumptohomescreen);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        textView1.setVisibility(View.INVISIBLE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.login_password_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
        editText1.setTransformationMethod(new PasswordTransformationMethod());
        button1.setEnabled(false);

        //loginemail=getIntent().getStringExtra(emailkey);

        TokenInterceptor.getContext(SetPasswordActivity.this);
        System.out.println("提交了context");
        retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitManager.API+"auth/")
                .client(OkHttpUtil.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        request = retrofit.create(RequestInterface.class);//创建接口实例
    }
    public void onClick(View v){
        Intent intent;
        String email=getIntent().getStringExtra(emailkey);
        String password=editText1.getText().toString();
        switch (v.getId()) {

            case R.id.ck_setpassword_checkBox://显示/隐藏密码
                if(checkBox1.isChecked()){
                    checkBox1.setText(getString(R.string.login_password_6));
                    editText1.setTransformationMethod(null);
                }else{
                    checkBox1.setText(getString(R.string.login_password_3));
                    editText1.setTransformationMethod(new PasswordTransformationMethod());
                }
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            case R.id.btn_setpassword_jumptohomescreen://加入
                if(true) {//判断密码是否错误，还未添加
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap map = new HashMap();
                            map.put("email", email);
                            map.put("password", password);
                            Call<ResponseBody> call = request.register(map);//进行封装
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
                                        String condition = null;
                                        try {
                                            JSONObject jsonObject = new JSONObject(json);
                                            condition = jsonObject.getString("msg");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                            showResponse(condition);
                                            Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                    }else{
                                        ErrorMsg.getErrorMsg(response,SetPasswordActivity.this);
                                    }

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable tr) {
                                    Toast.makeText(SetPasswordActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                                }

                            });




                        }
                    }).start();




                }else{
                    showResponse("密码格式错误，请重新输入");
                    Log.d("TAG","jjjjjj3");
                    // Toast.makeText(RegisterActivity.this, "学号格式错误，请重新输入", Toast.LENGTH_SHORT).show();
                }




                if(true){//判断密码格式是否正确
                    //正确则进入树洞，并将账户数据上传
                    //intent=new Intent(Login2Activity.this, HomePageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);;
                    //startActivity(intent);
                }else{
                   //给与密码格式错误提示

                }





                break;
            default:
                break;
        }
    }

    public  void showResponse(final String condition){
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(SetPasswordActivity.this, condition, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Intent newIntent(Context packageContext, String email){
        Intent intent = new Intent(packageContext, SetPasswordActivity.class);
        intent.putExtra(emailkey,email);
        return intent;
    }
}
