package com.example.hustholetest1.view.registerandlogin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hustholetest1.model.EditTextReaction;
import com.example.hustholetest1.R;
import com.githang.statusbar.StatusBarCompat;



public class RegisterActivity extends AppCompatActivity {
    EditText editText1;
    Button button1;
    TextView textView1,textView2;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.GrayScale_100) , true);

        editText1=(EditText)findViewById(R.id.et_register_email);
        button1=(Button)findViewById(R.id.btn_register_jumptohomescreen);
        textView1=(TextView)findViewById(R.id.tv_register_jumptoforgetpassword);
        textView2=(TextView)findViewById(R.id.tv_register_warn);
        back= (ImageView) findViewById(R.id.iv_titlebarwhite_back);
        textView2.setVisibility(View.INVISIBLE);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
        SpannableString string1 = new SpannableString(this.getResources().getString(R.string.login_id_2));
        EditTextReaction.EditTextSize(editText1,string1,14);
        button1.setEnabled(false);
        EditTextReaction.ButtonReaction(editText1,button1);
    }
    public void onClick(View v){
        Intent intent;
        String email=editText1.getText().toString();
        switch (v.getId()) {
            case R.id.btn_register_jumptohomescreen://下一步设置密码
                if(Character.isUpperCase(email.charAt(0))||Character.isLowerCase(email.charAt(0))){//判断学号是否正确

                     intent= SetPasswordActivity.newIntent(RegisterActivity.this,email);
                     startActivity(intent);
                }else{
                    Toast.makeText(RegisterActivity.this," 邮箱格式错误，请重新输入", Toast.LENGTH_SHORT).show();
                }//如果不正确，给予提示
                break;
            case R.id.tv_register_jumptoforgetpassword://已经注册
                intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_titlebarwhite_back://退回键
                finish();
                break;
            default:
                break;
        }
    }
}
