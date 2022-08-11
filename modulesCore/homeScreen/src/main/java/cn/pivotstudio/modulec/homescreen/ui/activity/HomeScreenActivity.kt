package cn.pivotstudio.modulec.homescreen.ui.activity

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.UpdateDialog
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.WelcomeDialog
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHsHomescreenBinding
import cn.pivotstudio.modulec.homescreen.network.VersionResponse
import cn.pivotstudio.modulec.homescreen.repository.HomeScreenRepository
import cn.pivotstudio.modulec.homescreen.ui.fragment.HomePageFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.example.libbase.BuildConfig
import com.example.libbase.base.ui.activity.BaseActivity
import com.example.libbase.constant.Constant
import com.githang.statusbar.StatusBarCompat

/**
 * @classname: HomeScreenActivity
 * @description:
 * @date: 2022/4/28 20:58
 * @version:1.0
 * @author:
 */
@Route(path = "/homeScreen/HomeScreenActivity")
class HomeScreenActivity : BaseActivity() {
    private var binding: ActivityHsHomescreenBinding? = null
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hs_homescreen)
        initView()
        checkVersion()
    }

    /**
     * fragment中使用onActivityResult需要在此重写触发，使用navigation后activity的onActivityResult被调用后不会再触发子fragment的onActivityResult，需要手动调用
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mMainNavFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val fragment = mMainNavFragment!!.childFragmentManager.primaryNavigationFragment
        if (fragment is HomePageFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * 视图初始化
     */
    private fun initView() {
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.HH_BandColor_1), true)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, argument ->
            supportActionBar?.title = destination.label

            binding?.apply {
                layoutBottomBar.isVisible =
                    (destination.id != R.id.all_forest_fragment && destination.id != R.id.forest_detail_fragment)
                bottomNavigationView.setupWithNavController(navController)
                bottomNavigationView.setOnItemReselectedListener {
                    when (it.itemId) {
                        R.id.homepage_fragment -> {
                            simulatePullDown()
                        }
                    }
                }
                bottomAppBar.background = null;
            }
        }

    }

    /**
     * 模拟下拉刷新事件
     */
    private fun simulatePullDown() {
        val inst = Instrumentation();
        val downTime = SystemClock.uptimeMillis()
        val mRunnable = Runnable {
            run {
            }
            inst.sendPointerSync(
                MotionEvent.obtain(
                    downTime, downTime,
                    MotionEvent.ACTION_DOWN, 650F, 1150F, 0
                )
            )
            inst.sendPointerSync(
                MotionEvent.obtain(
                    downTime, downTime,
                    MotionEvent.ACTION_MOVE, 650F, 1250F, 0
                )
            )
            inst.sendPointerSync(
                MotionEvent.obtain(
                    downTime, downTime + 20,
                    MotionEvent.ACTION_MOVE, 650F, 1650F, 0
                )
            )
            inst.sendPointerSync(
                MotionEvent.obtain(
                    downTime, downTime + 2000,
                    MotionEvent.ACTION_UP, 650F, 1650F, 0
                )
            )
        }
        Thread(mRunnable).start()
    }

    /**
     * 获取版本内容
     */
    private fun packageName(context: Context): String? {
        val manager = context.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }

    /**
     * 检查版本以及是否第一次使用
     */
    private fun checkVersion() {
        val homeScreenRepository = HomeScreenRepository()
        homeScreenRepository.getVersionMsgForNetwork()
        homeScreenRepository.pHomeScreenVersionMsg.observe(this) { versionResponse: VersionResponse ->
            val oldVersion = packageName(this@HomeScreenActivity)
            val lastVersion = versionResponse.androidversion
            val downloadUrl = versionResponse.androidUpdateUrl
            if (lastVersion != oldVersion) { //如果当前不是新版本
                val updateDialog =
                    UpdateDialog(this@HomeScreenActivity, oldVersion, lastVersion, downloadUrl)
                updateDialog.show()
            } else { //是最新版本
                val mmkvUtil = MMKVUtil.getMMKVUtils(this)
                if (!mmkvUtil.getBoolean(Constant.IS_FIRST_USED)) { //是否第一次使用1037树洞,保证welcomeDialog只在第一使用时显式
                    val welcomeDialog = WelcomeDialog(context)
                    welcomeDialog.show()
                    mmkvUtil.put(Constant.IS_FIRST_USED, true)
                }
            }
        }
    }

    /**
     * 监听点击事件
     */
    fun onClick(v: View) {
        val id = v.id
        if (id == R.id.fab_homescreen_publishhole) {
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/publishHole/PublishHoleActivity").navigation()
            } else {
                showMsg("当前为模块测试阶段")
            }
        }
    }

    private var firstTime: Long = 0

    /**
     * 点击退出键，连点两次退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        navController.currentDestination?.let {
            if (it.id == R.id.all_forest_fragment || it.id == R.id.forest_detail_fragment) {
                return navController.popBackStack()
            }

            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {
                    Toast.makeText(this@HomeScreenActivity, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                    firstTime = secondTime
                    return true
                } else {
                    finish()
                }
            }
        }


        return super.onKeyUp(keyCode, event)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}