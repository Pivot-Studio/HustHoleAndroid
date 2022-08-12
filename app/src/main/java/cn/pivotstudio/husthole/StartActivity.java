package cn.pivotstudio.husthole;

import android.os.Bundle;
import android.os.Handler;
import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity;
import cn.pivotstudio.moduleb.libbase.constant.Constant;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

@Route(path = "/app/StartActivity")

public class StartActivity extends BaseActivity {

    MMKVUtil mvUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mvUtil = MMKVUtil.getMMKV(getApplicationContext());
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        setContentView(R.layout.activity_rl_preparation);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mvUtil.getBoolean(Constant.IS_LOGIN)) {
                    ARouter.getInstance().build("/homeScreen/HomeScreenActivity").navigation();
                } else {
                    ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation();
                }
                finish();
            }
        };
        new Handler().postDelayed(runnable, 0);
    }
}