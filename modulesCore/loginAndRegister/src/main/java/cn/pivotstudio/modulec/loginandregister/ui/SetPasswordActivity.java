package cn.pivotstudio.modulec.loginandregister.ui;

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
import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.SetPasswordRepository;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

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

    /**
     * 用于界面跳转
     *
     * @param packageContext 发生跳转的Activity的context
     * @param email 满足要求的email
     * @return intent
     */
    public static Intent newIntent(Context packageContext, String email) {
        Intent intent = new Intent(packageContext, SetPasswordActivity.class);
        intent.putExtra(Constant.EMAIL_HEAD, email);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_setpassword);
        initView();
    }

    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        TextView mWarningTv = findViewById(R.id.tv_set_password_warn);
        Button mSetPasswordBtn = findViewById(R.id.btn_join_in);
        mSetPasswordEt = findViewById(R.id.et_set_password);
        mVisibleCb = findViewById(R.id.ck_set_password_checkBox);
        mWarningTv.setVisibility(View.INVISIBLE);//错误警告设置为不可见
        mSetPasswordBtn.setEnabled(false);//button设置为点击无效
        EditTextUtil.EditTextSize(mSetPasswordEt, new SpannableString("输入密码"), 14);
        EditTextUtil.ButtonReaction(mSetPasswordEt, mSetPasswordBtn);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ck_set_password_checkBox) {//隐藏/显示密码
            if (mVisibleCb.isChecked()) {
                mVisibleCb.setText(getString(R.string.login_password_6));
                mSetPasswordEt.setTransformationMethod(null);
            } else {
                mVisibleCb.setText(getString(R.string.login_password_3));
                mSetPasswordEt.setTransformationMethod(new PasswordTransformationMethod());
            }
        } else if (id == R.id.btn_join_in) {//设置密码
            SetPasswordRepository setPasswordRepository = new SetPasswordRepository();
            setPasswordRepository.createAccountForNetwork(
                getIntent().getStringExtra(Constant.EMAIL_HEAD),
                mSetPasswordEt.getText().toString());
            setPasswordRepository.mSetPassword.observe(this, setPasswordResponse -> {
                showMsg(setPasswordResponse.getMsg());
                Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class).setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            setPasswordRepository.failed.observe(this, this::showMsg);
        }
    }
}
