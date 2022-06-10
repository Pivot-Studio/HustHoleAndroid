package cn.pivotstudio.modulec.homescreen.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.UpdateDialog
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.WelcomeDialog
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHsHomescreenBinding
import cn.pivotstudio.modulec.homescreen.model.VersionResponse
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
        binding!!.bottomNavigation.setOptionsListener { v: View -> onClick(v) }
        setupActionBarWithNavController(navController)

        binding!!.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_home -> {
                    navController.navigate(R.id.homepage_fragment)
                    true
                }

                R.id.page_forest -> {
                    navController.navigate(R.id.forest_fragment)
                    true
                }

                R.id.page_msg -> {
                    navController.navigate(R.id.message_fragment)
                    true
                }

                R.id.page_mine -> {
                    navController.navigate(R.id.mine_fragment)
                    true
                }
                else -> false
            }
        }
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
        } else if (id == R.id.cl_homescreen_hompage) {
            navController!!.navigate(R.id.homepage_fragment)
            binding!!.tvHomescreenTitlebarname.text = "1037树洞"
        } else if (id == R.id.cl_homescreen_forest) {
            navController!!.navigate(R.id.forest_fragment)
            binding!!.tvHomescreenTitlebarname.text = "小树林"
        } else if (id == R.id.cl_homescreen_message) {
            navController!!.navigate(R.id.message_fragment)
            binding!!.tvHomescreenTitlebarname.text = "通知"
        } else if (id == R.id.cl_homescreen_mine) {
            navController!!.navigate(R.id.mine_fragment)
            binding!!.tvHomescreenTitlebarname.text = "我的"
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
        return super.onKeyUp(keyCode, event)
    }

    /**
     * 提供给 NavHostFragment 管理的 Fragment 设置底部导航栏能见性
     *
     * @param visibility true：可见 / false 不可见
     */
    fun setBottomNavVisibility(visibility: Boolean) {
        binding?.run {
            if (!visibility) {
                ivHomescreenOptionbox.visibility = View.GONE
                bottomNavigation.visibility = View.GONE
                fabHomescreenPublishhole.visibility = View.GONE
                iconFabHomeScreen.visibility = View.GONE
            } else {
                ivHomescreenOptionbox.visibility = View.VISIBLE
                bottomNavigation.visibility = View.VISIBLE
                fabHomescreenPublishhole.visibility = View.VISIBLE
                iconFabHomeScreen.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}