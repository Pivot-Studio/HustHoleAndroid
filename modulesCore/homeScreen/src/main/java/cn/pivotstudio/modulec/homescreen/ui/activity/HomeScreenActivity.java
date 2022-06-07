package cn.pivotstudio.modulec.homescreen.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import com.example.libbase.BuildConfig;
import com.example.libbase.constant.Constant;
import com.example.libbase.base.ui.activity.BaseActivity;

import com.githang.statusbar.StatusBarCompat;

import cn.pivotstudio.moduleb.database.MMKVUtil;
import cn.pivotstudio.modulec.homescreen.R;
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.UpdateDialog;
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.WelcomeDialog;
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHsHomescreenBinding;
import cn.pivotstudio.modulec.homescreen.repository.HomeScreenRepository;
import cn.pivotstudio.modulec.homescreen.ui.fragment.HomePageFragment;


/**
 * @classname: HomeScreenActivity
 * @description:
 * @date: 2022/4/28 20:58
 * @version:1.0
 * @author:
 */
@Route(path="/homeScreen/HomeScreenActivity")
public class HomeScreenActivity extends BaseActivity{
    private ActivityHsHomescreenBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hs_homescreen);
        initView();
        checkVersion();
    }

    /**
     * fragment中使用onActivityResult需要在此重写触发，使用navigation后activity的onActivityResult被调用后不会再触发子fragment的onActivityResult，需要手动调用
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        Fragment fragment = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        if (fragment instanceof HomePageFragment) {
            ((HomePageFragment)fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 视图初始化
     */
    private void initView() {
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.HH_BandColor_1) , true);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);//用来控制fragment
        binding.bottomNavigation.setOptionsListener(this::onClick);
    }

    /**
     * 获取版本内容
     * @param context
     * @return
     */
    private String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 检查版本以及是否第一次使用
     */
    private void checkVersion(){
        HomeScreenRepository homeScreenRepository=new HomeScreenRepository();
        homeScreenRepository.getVersionMsgForNetwork();
        homeScreenRepository.pHomeScreenVersionMsg.observe(this, versionResponse -> {
            String oldVersion=packageName(HomeScreenActivity.this);
            String lastVersion=versionResponse.getAndroidversion();
            String downloadUrl=versionResponse.getAndroidUpdateUrl();
            if (!lastVersion.equals(oldVersion)) {//如果当前不是新版本
                UpdateDialog updateDialog=new UpdateDialog(HomeScreenActivity.this,oldVersion,lastVersion,downloadUrl);
                updateDialog.show();
            }else{//是最新版本
                MMKVUtil mmkvUtil=MMKVUtil.getMMKVUtils(this);
                if(!mmkvUtil.getBoolean(Constant.IS_FIRST_USED)){//是否第一次使用1037树洞,保证welcomeDialog只在第一使用时显式
                    WelcomeDialog welcomeDialog=new WelcomeDialog(context);
                    welcomeDialog.show();
                    mmkvUtil.put(Constant.IS_FIRST_USED,true);
                }
            }
        });
    }

    /**
     * 监听点击事件
     * @param v
     */
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.fab_homescreen_publishhole) {
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/publishHole/PublishHoleActivity").navigation();
            } else {
                showMsg("当前为模块测试阶段");
            }
        } else if (id == R.id.cl_homescreen_hompage) {
            navController.navigate(R.id.homepage_fragment);
            binding.tvHomescreenTitlebarname.setText("1037树洞");
        } else if (id == R.id.cl_homescreen_forest) {
            navController.navigate(R.id.forest_fragment);
            binding.tvHomescreenTitlebarname.setText("小树林");
        } else if (id == R.id.cl_homescreen_message) {
            navController.navigate(R.id.message_fragment);
            binding.tvHomescreenTitlebarname.setText("通知");
        } else if (id == R.id.cl_homescreen_mine) {
            navController.navigate(R.id.mine_fragment);
            binding.tvHomescreenTitlebarname.setText("我的");
        }

    }
    private long firstTime = 0;

    /**
     * 点击退出键，连点两次退出
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(HomeScreenActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
