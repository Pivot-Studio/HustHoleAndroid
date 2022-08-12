package cn.pivotstudio.modulec.loginandregister.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.ForgetPasswordRepository;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

/**
 * @classname:ForgetPasswordActivity
 * @description:忘记密码界面
 * @date:2022/5/1 22:10
 * @version:1.0
 * @author:
 */
public class ForgetPasswordActivity extends BaseActivity {
//    TextView mWarningTv;
//    Button mForgetPasswordBtn;
//    EditText mEmailHeadEt;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lr_forgetpassword);
//        initView();
//    }
//
//    private void initView() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//        mWarningTv = findViewById(R.id.tv_forget_warn);
//        mForgetPasswordBtn = findViewById(R.id.btn_forget_get_code);
//        mEmailHeadEt = findViewById(R.id.et_forget_email);
//
//        mForgetPasswordBtn.setEnabled(false);
//        mWarningTv.setVisibility(View.INVISIBLE);
//        //Et与Bt绑定，动态改变button背景
//        EditTextUtil.EditTextSize(mEmailHeadEt,
//            new SpannableString(this.getResources().getString(R.string.retrieve_password_forget_2)),
//            14);
//        EditTextUtil.ButtonReaction(mEmailHeadEt, mForgetPasswordBtn);
//    }
//
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_forget_get_code) {//进入验证码验证界面
//            ForgetPasswordRepository forgetPasswordRepository = new ForgetPasswordRepository();
//            forgetPasswordRepository.sendVerifyCode(mEmailHeadEt.getText().toString());
//            forgetPasswordRepository.mForgetPassword.observe(this, forgetPasswordResponse -> {
//                showMsg(forgetPasswordResponse.getMsg());
//                Intent intent = VerificationCodeActivity.newIntent(ForgetPasswordActivity.this,
//                    mEmailHeadEt.getText().toString());
//                startActivity(intent);
//            });
//            forgetPasswordRepository.failed.observe(this, this::showMsg);
//        }
//    }
}
