package cn.pivotstudio.husthole;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatDelegate;

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
        Runnable runnable = () -> {
            if (mvUtil.getBoolean(Constant.IS_LOGIN)) {
                switch (mvUtil.getInt(Constant.IS_DARK_MODE)) {
                    case 0:
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                        break;
                    case 1:
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
                        break;
                    case 2:
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    default:
                        break;
                }
                ARouter.getInstance().build("/homeScreen/HomeScreenActivity").navigation();
            } else {
                ARouter.getInstance().build("/loginAndRegister/LARActivity").navigation();
            }
            finish();
        };
        new Handler().postDelayed(runnable, 0);
    }
}