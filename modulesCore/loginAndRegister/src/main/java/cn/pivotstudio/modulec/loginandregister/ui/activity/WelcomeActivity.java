package cn.pivotstudio.modulec.loginandregister.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import cn.pivotstudio.modulec.loginandregister.R;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.libbase.base.ui.activity.BaseActivity;

/**
 * @author
 * @version :1.0
 * @classname WelcomeActivity
 * @description:欢迎界面
 * @date :2022/4/25 19:43
 */
@Route(path = "/loginAndRegister/WelcomeActivity")
public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lr_welcome);
    }

    /**
     * 点击事件
     */
    public void onClick(View view) {
        Intent intent;
        int id = view.getId();
        if (id == R.id.btn_welcome_jumptologin) {//注册
            intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    }
}
