package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.libbase.base.ui.activity.BaseActivity;
import com.example.libbase.constant.Constant;
import com.example.libbase.util.ui.EditTextUtil;

import cn.pivotstudio.modulec.loginandregister.R;
import cn.pivotstudio.modulec.loginandregister.repository.ModifyPasswordRepository;


/**
 * @classname:ModifyPasswordActivity
 * @description:重写设置密码界面
 * @date:2022/5/2 0:53
 * @version:1.0
 * @author:
 */
public class ModifyPasswordActivity extends BaseActivity {
    EditText mPasswordEt;
    TextView mWarningTv;
    ImageView mVisibleIv;
    Boolean mVisibleFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_modifypassword);
        initView();
        mVisibleFlag=false;
    }
    private void initView(){
        mPasswordEt=findViewById(R.id.et_modify_email);
        mWarningTv=findViewById(R.id.tv_modify_warn);
        mVisibleIv=findViewById(R.id.iv_modify_visible);
        Button mModifyPasswordBtn=findViewById(R.id.btn_modify_jumptohomescreen);

        mPasswordEt.setTransformationMethod(new PasswordTransformationMethod());
        mWarningTv.setVisibility(View.INVISIBLE);


        EditTextUtil.EditTextSize(mPasswordEt,new SpannableString(this.getResources().getString(R.string.retrieve_password_modify_2)),14);
        EditTextUtil.ButtonReaction(mPasswordEt,mModifyPasswordBtn);

    }
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.iv_modify_visible) {//隐藏/显示密码
            if (mVisibleFlag) {
                mVisibleIv.setImageResource(R.drawable.checkbox_false);
                mPasswordEt.setTransformationMethod(new PasswordTransformationMethod());
            } else {
                mVisibleIv.setImageResource(R.drawable.checkbox_true);
                mPasswordEt.setTransformationMethod(null);
            }
            mVisibleFlag= !mVisibleFlag;
        } else if (id == R.id.btn_modify_jumptohomescreen) {//重新设置完密码后进入登录界面，并且将之前的活动清除
            ModifyPasswordRepository modifyPasswordRepository=new ModifyPasswordRepository();
            modifyPasswordRepository.modifyPasswordForNetwork(getIntent().getStringExtra(Constant.EMAIL_HEAD),getIntent().getStringExtra(Constant.VERIFY_CODE),mPasswordEt.getText().toString());
            modifyPasswordRepository.mModifyPassword.observe(this,modifyPasswordResponse -> {
                showMsg(modifyPasswordResponse.getMsg());
                Intent intent = new Intent(ModifyPasswordActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                //清除前面所有活动
                startActivity(intent);
            });
            modifyPasswordRepository.failed.observe(this, this::showMsg);
        } else if (id == R.id.iv_titlebarwhite_back) {//退回键
            finish();
        }
    }
    public static Intent newIntent(Context context, String id1, String vertify){
        Intent intent1=new Intent(context,ModifyPasswordActivity.class);
        intent1.putExtra(Constant.EMAIL_HEAD,id1);
        intent1.putExtra(Constant.VERIFY_CODE,vertify);
        return intent1;
    }
}
