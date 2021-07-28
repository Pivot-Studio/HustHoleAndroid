package com.example.hustholetest1.View.RegisterAndLogin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.Model.EditTextReaction;
import com.example.hustholetest1.R;
import com.example.hustholetest1.View.HomePage.Activity.HomePageActivity;
import com.githang.statusbar.StatusBarCompat;

public class Login2Activity extends AppCompatActivity {
    private TextView textView1;
    private EditText editText1;
    private CheckBox checkBox1;
    private Button button1;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_password);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        textView1=(TextView)findViewById(R.id.textView6);
        editText1=(EditText)findViewById(R.id.EditText);
        checkBox1=(CheckBox)findViewById(R.id.checkBox);
        button1=(Button)findViewById(R.id.button4);
        back= (ImageView) findViewById(R.id.backView);
        textView1.setVisibility(View.INVISIBLE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.login_password_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        EditTextReaction.ButtonReaction(editText1,button1);
        editText1.setTransformationMethod(new PasswordTransformationMethod());
        button1.setEnabled(false);
    }
    public void onClick(View v){
        Intent intent;
        switch (v.getId()) {

            case R.id.checkBox://显示/隐藏密码
                if(checkBox1.isChecked()){
                    checkBox1.setText(getString(R.string.login_password_6));
                    editText1.setTransformationMethod(null);
                }else{
                    checkBox1.setText(getString(R.string.login_password_3));
                    editText1.setTransformationMethod(new PasswordTransformationMethod());
                }
                break;
            case R.id.backView://退回键
                finish();
                break;
            case R.id.button4://加入
                if(true){//判断密码格式是否正确
                    //正确则进入树洞，并将账户数据上传
                    intent=new Intent(Login2Activity.this, HomePageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);;
                    startActivity(intent);
                }else{
                   //给与密码格式错误提示
                }
                break;
            default:
                break;
        }
    }
}
