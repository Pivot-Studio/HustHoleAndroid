package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil;

public class RegisterActivity extends BaseActivity {
    EditText mRegisterEmailHeadEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_register);
        initView();
    }

    /**
     * View相关初始化
     */
    private void initView() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        TextView mRegisterWarnTv = findViewById(R.id.tv_register_warn);
        Button mRegisterJumpToSetPasswordBtn = findViewById(R.id.btn_register_jumptosetpassword);
        mRegisterEmailHeadEt = findViewById(R.id.et_register_emailhead);

        mRegisterWarnTv.setVisibility(View.INVISIBLE);//错误警告设置为不可见
        mRegisterJumpToSetPasswordBtn.setEnabled(false);//button设置为点击无效
        EditTextUtil.EditTextSize(mRegisterEmailHeadEt,
            new SpannableString(this.getResources().getString(R.string.login_id_2)), 14);
        EditTextUtil.ButtonReaction(mRegisterEmailHeadEt, mRegisterJumpToSetPasswordBtn);
    }

    /**
     * 点击事件初始化
     */
    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.btn_register_jumptosetpassword) {//下一步设置密码
            String mEmailHead = mRegisterEmailHeadEt.getText().toString();
            if (Character.isUpperCase(mEmailHead.charAt(0)) || Character.isLowerCase(
                mEmailHead.charAt(0))) {//判断学号是否正确
                intent = SetPasswordActivity.newIntent(RegisterActivity.this, mEmailHead);
                startActivity(intent);
            } else {//如果不正确，给予提示
                showMsg("邮箱格式错误，请重新输入");
            }
        } else if (id == R.id.tv_register_jumptologin) {//已经注册
            intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.iv_titlebarwhite_back) {//退回键
            finish();
        } else if (id == R.id.tv_register_jumptoprivacy) {
            intent = new Intent(RegisterActivity.this, PrivacyActivity.class);
            startActivity(intent);
        }
    }
}
