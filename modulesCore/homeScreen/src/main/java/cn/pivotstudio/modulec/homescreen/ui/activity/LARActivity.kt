package cn.pivotstudio.modulec.homescreen.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cn.pivotstudio.moduleb.rebase.database.MMKVUtil
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.resbase.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ActivityLarBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = "/loginAndRegister/LARActivity")
class LARActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLarBinding
    private lateinit var navController: NavController
    private lateinit var mmkvUtil: MMKVUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lar)
        mmkvUtil = MMKVUtil.getMMKV(this)
        checkNightMode()
        checkHasTokenAlready()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        findViewById<Toolbar>(R.id.lar_top_appbar).let {
            setSupportActionBar(it)
            it.setupWithNavController(
                navController,
                AppBarConfiguration(setOf(R.id.welcomeFragment))
            )
        }

        navController.addOnDestinationChangedListener { _, destination, argument ->
            // ActionBar显示情况特判
            supportActionBar?.let {
                when (destination.id) {
                    R.id.welcomeFragment,
                    R.id.privacyFragment -> {
                        supportActionBar?.hide()
                    }
                    else -> supportActionBar?.show()
                }
            }
        }

        supportActionBar?.setDisplayShowTitleEnabled(false)

    }

    fun loginWithUseToken(token: String) {
        mmkvUtil.apply {
            put(Constant.USER_TOKEN_V2, token)
            put(Constant.IS_LOGIN, true)
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/homeScreen/HomeScreenActivity")
                    .navigation()
                finish()
            }
        }
    }

    private fun checkHasTokenAlready() {
        if (mmkvUtil.getBoolean(Constant.IS_LOGIN)) {
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/homeScreen/HomeScreenActivity")
                    .navigation()
                finish()
            }
        }
    }

    private fun checkNightMode() {

        when (mmkvUtil.getInt(Constant.IS_DARK_MODE)) {
            0 -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )

            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

}