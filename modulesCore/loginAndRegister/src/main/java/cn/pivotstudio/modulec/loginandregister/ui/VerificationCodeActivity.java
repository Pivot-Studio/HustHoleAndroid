package cn.pivotstudio.modulec.loginandregister.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.VerificationCodeRepository;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

/**
 * @classname:VerificationCodeActivity
 * @description:验证界面
 * @date:2022/5/1 22:55
 * @version:1.0
 * @author:
 */
public class VerificationCodeActivity extends BaseActivity {

//    EditText mVCodeEt;
//    Button mVCodeBtn;
//    TextView mTimeTv, mHintEmailTv, mHint1Tv, mHint2Tv;
//
//    public static Intent newIntent(Context context, String id1) {
//        Intent intent1 = new Intent(context, VerificationCodeActivity.class);
//        intent1.putExtra(Constant.EMAIL_HEAD, id1);
//        return intent1;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lr_verificationcode);
//        initView();
//    }
//
//    private void initView() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//        mVCodeEt = findViewById(R.id.et_verify_code);
//        mVCodeBtn = findViewById(R.id.btn_verify_next);
//        mTimeTv = findViewById(R.id.tv_vcode_time);
//        mHintEmailTv = findViewById(R.id.tv_vcode_sendtosomebody);
//        mHint1Tv = findViewById(R.id.tv_text);
//        mHint2Tv = findViewById(R.id.tv_text2);
//
//        mVCodeBtn.setEnabled(false);
//        String emailHead = (String) getIntent().getStringExtra(Constant.EMAIL_HEAD);
//        mHintEmailTv.setText("验证邮箱已发送至" + emailHead + "@hust.edu.cn");
//
//        EditTextUtil.EditTextSize(mVCodeEt,
//            new SpannableString(this.getResources().getString(R.string.retrieve_password_vcode_3)),
//            14);
//        EditTextUtil.ButtonReaction(mVCodeEt, mVCodeBtn);
//        CountDownTimer timer = new CountDownTimer(60000, 1000) {//倒计时
//            public void onTick(long millisUntilFinished) {
//                mTimeTv.setText(millisUntilFinished / 1000 + "s");
//            }
//
//            public void onFinish() {
//                mTimeTv.setVisibility(View.INVISIBLE);
//                mHint1Tv.setVisibility(View.INVISIBLE);
//                mHint2Tv.setVisibility(View.INVISIBLE);
//            }
//        };
//        timer.start();
//    }
//
//    public void onClick(View v) {
//        int id = v.getId();
//        if (id == R.id.btn_verify_next) {//验证成功后进入重新设置密码界面
//
//            VerificationCodeRepository verificationCodeRepository =
//                new VerificationCodeRepository();
//            String emailHead = getIntent().getStringExtra(Constant.EMAIL_HEAD);
//            String vCode = mVCodeEt.getText().toString();
//            verificationCodeRepository.verifyEmailForNetwork(emailHead, vCode);
//            verificationCodeRepository.mVerificationCode.observe(this, verificationCodeResponse -> {
//                showMsg("验证成功");
//                Intent intent =
//                    ModifyPasswordActivity.newIntent(VerificationCodeActivity.this, emailHead,
//                        vCode);
//                startActivity(intent);
//            });
//            verificationCodeRepository.failed.observe(this, this::showMsg);
//        }
//    }
}
