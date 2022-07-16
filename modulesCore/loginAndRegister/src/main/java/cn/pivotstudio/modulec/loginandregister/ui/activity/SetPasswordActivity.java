package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.libbase.base.ui.activity.BaseActivity;
import com.example.libbase.constant.Constant;
import com.example.libbase.util.ui.EditTextUtil;

import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.SetPasswordRepository;

/**
 * @classname:SetPasswordActivity
 * @description:注册时设置密码
 * @date:2022/4/27 14:35
 * @version:1.0
 * @author:
 */
public class SetPasswordActivity extends BaseActivity {
    CheckBox mVisibleCb;
    EditText mSetPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_setpassword);
        initView();

    }
    private void initView(){
        TextView mWarningTv=findViewById(R.id.tv_setpassword_warn);
        Button mSetPasswordBtn=findViewById(R.id.btn_setpassword_jumptohomescreen);
        mSetPasswordEt=findViewById(R.id.et_setpassword);
        mVisibleCb=findViewById(R.id.ck_setpassword_checkBox);
        mWarningTv.setVisibility(View.INVISIBLE);//错误警告设置为不可见
        mSetPasswordBtn.setEnabled(false);//button设置为点击无效
        EditTextUtil.EditTextSize(mSetPasswordEt,new SpannableString("输入密码"),14);
        EditTextUtil.ButtonReaction(mSetPasswordEt, mSetPasswordBtn);
    }
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ck_setpassword_checkBox) {//隐藏/显示密码
            if (mVisibleCb.isChecked()) {
                mVisibleCb.setText(getString(R.string.login_password_6));
                mSetPasswordEt.setTransformationMethod(null);
            } else {
                mVisibleCb.setText(getString(R.string.login_password_3));
                mSetPasswordEt.setTransformationMethod(new PasswordTransformationMethod());
            }
        } else if (id == R.id.btn_setpassword_jumptohomescreen) {//设置密码
            SetPasswordRepository setPasswordRepository=new SetPasswordRepository();
            setPasswordRepository.createAccountForNetwork(getIntent().getStringExtra(Constant.EMAIL_HEAD),mSetPasswordEt.getText().toString());
            setPasswordRepository.mSetPassword.observe(this,setPasswordResponse->{
                showMsg(setPasswordResponse.getMsg());
                Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            setPasswordRepository.failed.observe(this, this::showMsg);
        }else if (id == R.id.iv_titlebarwhite_back) {//退回键
            finish();
        }
    }
    /**
     * 用于界面跳转
     * @param packageContext 发生跳转的Activity的context
     * @param email 满足要求的email
     * @return intent
     */
    public static Intent newIntent(Context packageContext, String email){
        Intent intent = new Intent(packageContext, SetPasswordActivity.class);
        intent.putExtra(Constant.EMAIL_HEAD,email);
        return intent;
    }
}
