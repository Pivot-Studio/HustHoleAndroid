package cn.pivotstudio.modulec.loginandregister.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.BuildConfig
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.loginandregister.R
import cn.pivotstudio.modulec.loginandregister.databinding.ActivityLarBinding
import cn.pivotstudio.modulec.loginandregister.viewmodel.LARViewModel
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
            put(Constant.IS_DARK_MODE, 2)
            if (BuildConfig.isRelease) {
                ARouter.getInstance().build("/homeScreen/HomeScreenActivity")
                    .navigation()
                finish()
            }
        }
    }

}