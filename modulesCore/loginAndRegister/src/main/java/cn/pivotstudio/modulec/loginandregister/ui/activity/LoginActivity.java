package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.LoginRepository;
import com.alibaba.android.arouter.launcher.ARouter;
import cn.pivotstudio.moduleb.libbase.BuildConfig;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

/**
 * @classname:LoginActivity
 * @description:登录界面
 * @date:2022/4/26 15:19
 * @version:1.0
 * @author:
 */
public class LoginActivity extends BaseActivity {
    EditText mPasswordEt, mIdEt;
    TextView mWarningTv;
    ImageView mVisibleIv;
    Boolean mVisibleFlag;
    MMKVUtil mmkvUtil;
    LoginRepository loginRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_login);
        mVisibleFlag = false;
        loginRepository = new LoginRepository();
        mmkvUtil = MMKVUtil.getMMKVUtils(getApplicationContext());
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        mIdEt = findViewById(R.id.et_login_emailhead);
        mPasswordEt = findViewById(R.id.et_login_password);
        Button mLoginBt = findViewById(R.id.btn_login_jumptohomescreen);
        mWarningTv = findViewById(R.id.tv_login_warn);
        mVisibleIv = findViewById(R.id.iv_login_visible);

        mPasswordEt.setTransformationMethod(new PasswordTransformationMethod());//密码初始设置为星号
        mWarningTv.setVisibility(View.INVISIBLE);//输入错误警告设置为不可见
        mLoginBt.setEnabled(false);//按钮未输入内容设置为不可点击

        //Et与Bt绑定，动态改变button背景
        EditTextUtil.EditTextSize(mIdEt,
            new SpannableString(this.getResources().getString(R.string.register_2)), 14);
        EditTextUtil.EditTextSize(mPasswordEt,
            new SpannableString(this.getResources().getString(R.string.register_4)), 14);
        EditTextUtil.ButtonReaction(mIdEt, mLoginBt);
    }

    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.iv_login_visible) {//隐藏/显示密码
            if (mVisibleFlag) {
                mVisibleIv.setImageResource(R.drawable.checkbox_true);
                mPasswordEt.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                mVisibleIv.setImageResource(R.drawable.checkbox_false);
                mPasswordEt.setTransformationMethod(null);
            }
            mVisibleFlag = !mVisibleFlag;
        } else if (id == R.id.tv_login_jumptocheckemail) {//注册
            intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.tv_login_jumptoforgetpassword) {//忘记密码
            intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.iv_titlebarwhite_back) {//退回键
            finish();
        } else if (id == R.id.btn_login_jumptohomescreen) {//登录
            loginRepository.getLoginForNetwork(mIdEt.getText().toString(),
                mPasswordEt.getText().toString());

            loginRepository.login.observe(this, loginResponse -> {
                showMsg(loginResponse.getMsg());
                mmkvUtil.put(Constant.USER_TOKEN, loginResponse.getToken());
                mmkvUtil.put(Constant.IS_LOGIN, true);
                if (BuildConfig.isRelease) {
                    ARouter.getInstance().build("/homeScreen/HomeScreenActivity").navigation();
                } else {
                    showMsg("当前为模块测试阶段");
                }
            });
            loginRepository.failed.observe(this, this::showMsg);
        }
    }
}
