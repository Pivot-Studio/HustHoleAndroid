package cn.pivotstudio.modulec.homescreen.ui.activity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import cn.pivotstudio.moduleb.rebase.network.model.VersionInfo
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil
import cn.pivotstudio.moduleb.rebase.lib.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.constant.ResultCodeConstant
import cn.pivotstudio.moduleb.resbase.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.ui.custom_view.dialog.UpdateDialog
import cn.pivotstudio.modulec.homescreen.ui.custom_view.dialog.WelcomeDialog
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHsHomescreenBinding
import cn.pivotstudio.modulec.homescreen.network.ApkDownload
import cn.pivotstudio.modulec.homescreen.ui.adapter.TAG
import cn.pivotstudio.modulec.homescreen.ui.fragment.ForestDetailFragment
import cn.pivotstudio.modulec.homescreen.ui.fragment.ForestFragment
import cn.pivotstudio.modulec.homescreen.ui.fragment.HomePageFragment
import cn.pivotstudio.modulec.homescreen.viewmodel.HomeScreenActivityViewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.*
import java.io.File
import java.util.*


/**
 * @classname: HomeScreenActivity
 * @description:
 * @date: 2022/4/28 20:58
 * @version:1.0
 * @author:
 */
@Route(path = "/homeScreen/HomeScreenActivity")
class HomeScreenActivity : BaseActivity() {
    private lateinit var binding: ActivityHsHomescreenBinding
    private val viewModel: HomeScreenActivityViewModel by viewModels()
    private lateinit var navController: NavController
    private val fragmentList = listOf(
        R.id.all_forest_fragment,
        R.id.forest_detail_fragment,
        R.id.holeFollowReplyFragment,
        R.id.itemMineFragment,
        R.id.itemDetailFragment2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hs_homescreen)
        registerInstallBroadcast()
        checkVersion()
        initView()
    }


    /**
     * fragment中使用onActivityResult需要在此重写触发，使用navigation后activity的onActivityResult被调用后不会再触发子fragment的onActivityResult，需要手动调用
     */
    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val mMainNavFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        when (val fragment = mMainNavFragment!!.childFragmentManager.primaryNavigationFragment) {
            is HomePageFragment,
            is ForestDetailFragment,
            is ForestFragment -> {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    /**
     * 视图初始化
     */
    private fun initView() {
        viewModel.tip.observe(this) {
            it?.let {
                showMsg(it)
                viewModel.doneShowingTip()
            }
        }
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.homeScreenToolbar.let {
            setSupportActionBar(it)
            it.setupWithNavController(
                navController,
                AppBarConfiguration(
                    setOf(
                        R.id.homepage_fragment,
                        R.id.forest_fragment,
                        R.id.notice_fragment,
                        R.id.mine_fragment,
                        R.id.forest_detail_fragment
                    )
                )
            )
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label

            // BottomNavigationBar显示情况特判
            binding.apply {
                layoutBottomBar.isVisible =
                    !fragmentList.any { it == destination.id }

                bottomNavigationView.setupWithNavController(navController)
                bottomNavigationView.background = null
            }

            // ActionBar显示情况特判
            supportActionBar?.let {
                if (destination.id == R.id.forest_detail_fragment) {
                    it.hide()
                } else {
                    it.show()
                }
            }

        }
    }

    private fun registerInstallBroadcast() {
        val intentFilter = IntentFilter()
        // 监听安装广播
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        // 监听自定义广播
        intentFilter.addAction("NOT_UPDATE")
        // 注册应用安装需要设置这个参数
        intentFilter.addDataScheme("package")
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!Objects.equals(intent.action, "NOT_UPDATE")) {
                    val file =
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath?.let {
                            File(it)
                        }
                    file?.apply {
                        // 获取下载目录下的所有文件
                        this.listFiles()?.apply {
                            for (item in this) {
                                if (item.name.endsWith(".apk")) {
                                    item.delete()
                                    Log.d(TAG, "APK is deleted")
                                    // showMsg("已删除安装包")
                                }
                            }
                        }
                    }
                }
                context.unregisterReceiver(this)
            }
        }
        registerReceiver(receiver, intentFilter)
    }

    /**
     * 检查版本以及是否第一次使用
     */
    private fun checkVersion() {
        val mmkvUtil = MMKVUtil.getMMKV(this)
        //是否第一次使用1037树洞,保证welcomeDialog只在第一使用时显式
        if (!mmkvUtil.getBoolean(Constant.IS_FIRST_USED)) {
            val welcomeDialog = WelcomeDialog(context)
            welcomeDialog.show()
            mmkvUtil.put(Constant.IS_FIRST_USED, true)
        }
        // 获取旧版本信息
        var oldCode = 0L
        var oldName: String? = null
        try {
            val mInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(
                        packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    packageManager.getPackageInfo(packageName, 0)
                }
            oldCode = mInfo.longVersionCode
            oldName = mInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "don't have this package")
            e.printStackTrace()
        }

        lifecycleScope.launch {
            viewModel.versionInfo.collect {
                it?.let {
                    // token缓存过期会请求失败
                    if (it.versionId == "403") {
                        showMsg("登录过期！")
                        delay(300L)
                        // 回退到登录界面
                        if (BuildConfig.isRelease) {
                            mmkvUtil.put(Constant.IS_LOGIN, false)
                            ARouter.getInstance().build("/loginAndRegister/LARActivity")
                                .navigation()
                            finish()
                        }
                    } else {
                        // 检测到新版本信息
                        if (it.versionId.toLong() > oldCode || !Objects.equals(
                                it.versionName,
                                oldName
                            )
                        ) {
                            createDownloadWork(it)
                        } else {
                            // 没有新版本就直接注销BroadcastReceiver
                            context.sendBroadcast(Intent("NOT_UPDATE"))
                        }
                    }
                }
            }
        }
    }

    private fun createDownloadWork(infos: VersionInfo) {
        // 设置相关约束（只在WIFI且内存充足环境下执行）
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresStorageNotLow(true)
            .build()
        // 配置工作请求
        val downloadRequest = OneTimeWorkRequestBuilder<ApkDownload>()
            .addTag("download")
            .setConstraints(constraints)
            .setInputData(workDataOf("baseUrl" to infos.downloadUrl))
            .build()
        // 将 WorkRequest 提交到 WorkManager。
        WorkManager.getInstance(context).enqueueUniqueWork(
            "download",
            ExistingWorkPolicy.KEEP,
            downloadRequest
        )
        // 监察任务是否完成
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(downloadRequest.id)
            .observe(this@HomeScreenActivity) { info ->
                //Toast.makeText(context, info?.state.toString(), Toast.LENGTH_SHORT).show()
                if (info?.state == WorkInfo.State.SUCCEEDED) {
                    val fileName = infos.downloadUrl.let {
                        it.substring(
                            it.lastIndexOf("/") + 1,
                            it.length
                        )
                    }
                    // 获取通知
                    val notification = createNotification(fileName)
                    // 通知栏显示通知
                    notification?.let {
                        val notificationManager: NotificationManager =
                            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(1, it)
                    }
                    // 显示更新Dialog
                    UpdateDialog(context, infos) {
                        getInstallIntent(fileName)
                    }.show()
                }
            }
    }

    /**
     * 创建更新的通知
     */
    private fun createNotification(fileName: String): Notification? {
        val channelId = "INSTALL"
        if (checkNotification()) {
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(this, channelId)
            //创建并设置NotificationChannel
            val channel = NotificationChannel(
                channelId,
                "待办消息",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            //设置notification的点击事件
            val contentIntent: PendingIntent = PendingIntent.getActivity(
                this,
                0,
                getInstallIntent(fileName),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )

            notification.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.icon) //设置通知的小图标(有些手机设置Icon图标不管用，默认图标就是Manifest.xml里的图标)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.icon
                    )
                ) //设置通知的大图标
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE) //设置通知的提醒方式： 呼吸灯
                .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                .setChannelId(channelId)
                .setContentTitle("安装包下载完毕，点击安装...")
                .setAutoCancel(false) //设置通知被点击一次是否自动取消
            return notification.build()
        }
        return null
    }


    /**
     * 检查是否有通知权限
     */
    private fun checkNotification(): Boolean =
        NotificationManagerCompat.from(this).areNotificationsEnabled()

    /**
     * 返回安装页面的intent
     * @param fileName 要安装apk的文件名
     * @return
     */
    private fun getInstallIntent(fileName: String): Intent? {
        try {
            val file = File(
                this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath,
                fileName
            )
            if (!file.exists()) {
                return null
            }
            val uri =
                FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            return Intent(Intent.ACTION_VIEW).setDataAndType(
                uri,
                "application/vnd.android.package-archive"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

        } catch (e: NullPointerException) {
            Log.e(TAG, "file is not exists!")
            e.printStackTrace()
        } catch (e: java.lang.IllegalArgumentException) {
            Log.e(TAG, "file can't be accessed!")
            e.printStackTrace()
        }
        return null
    }

    /**
     * 监听点击事件
     */
    fun jumpToPublishHoleByARouter(v: View) {
        val id = v.id

        if (id == R.id.fab_homescreen_publishhole) {
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/hole/PublishHoleActivity")
                    .navigation(this, ResultCodeConstant.PUBLISH_HOLE)
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
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            navController.currentDestination?.let { navDestination ->
                if (fragmentList.any { it == navDestination.id } || supportFragmentManager.backStackEntryCount > 0) {
                    return navController.popBackStack()
                } else {
                    val secondTime = System.currentTimeMillis()
                    if (secondTime - firstTime > 2000L) {
                        Toast.makeText(this@HomeScreenActivity, "再按一次退出程序", Toast.LENGTH_SHORT)
                            .show()
                        firstTime = secondTime
                        return true
                    } else {
                        finish()
                    }
                }
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    fun setOnBottomBarItemReselectedListener(listener: NavigationBarView.OnItemReselectedListener?) {
        binding.bottomNavigationView.setOnItemReselectedListener(listener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp() || super.onNavigateUp()
    }
}