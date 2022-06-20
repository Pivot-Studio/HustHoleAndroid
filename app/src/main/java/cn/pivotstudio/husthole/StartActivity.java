package cn.pivotstudio.husthole;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.libbase.constant.Constant;
import com.example.libbase.base.ui.activity.BaseActivity;




import cn.pivotstudio.moduleb.database.MMKVUtil;


@Route(path="/app/StartActivity")

public class StartActivity extends BaseActivity {


    MMKVUtil mvUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mvUtil=MMKVUtil.getMMKVUtils(getApplicationContext());
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_rl_preparation);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                        if (mvUtil.getBoolean(Constant.IS_LOGIN)) {
                            ARouter.getInstance().build("/homeScreen/HomeScreenActivity").navigation();
                            finish();
                        } else {
                            ARouter.getInstance().build("/loginAndRegister/WelcomeActivity").navigation();
                        }
            }
        };
        new Handler().postDelayed(runnable,0);
    }



}