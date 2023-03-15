package cn.pivotstudio.modulep.hole.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.databinding.ActivityHoleBinding
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.githang.statusbar.StatusBarCompat

/**
 * @classname:HoleActivity
 * @description:
 * @date:2022/5/8 13:24
 * @version:1.0
 * @author:
 */
@Route(path = "/hole/HoleActivity")
class HoleActivity : BaseActivity() {
    @JvmField
    @Autowired(name = Constant.HOLE_ID)
    var holeId = 0

    @JvmField
    @Autowired(name = Constant.IF_OPEN_KEYBOARD)
    var ifOpenKeyboard = false

    private lateinit var binding: ActivityHoleBinding
    private lateinit var navController: NavController

    companion object {
        const val KEY_HOLE_ID = "holeId"
        const val KEY_OPEN_KEYBOARD = "openingKeyboard"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this) //初始化@Autowired
        initView()
    }


    private fun initView() {
        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.HH_BandColor_1), true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hole)

        setSupportActionBar(binding.topAppbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.setGraph(
            R.navigation.hole_nav_graph,
            bundleOf(
                KEY_HOLE_ID to holeId.toString(),
                KEY_OPEN_KEYBOARD to ifOpenKeyboard
            )
        )

        binding.topAppbar.setupWithNavController(
            navController,
            AppBarConfiguration(setOf()) {
                navController.currentDestination?.let {
                    if (it.id == R.id.specificHoleFragment && navController.previousBackStackEntry == null) {
                        finish()
                    }
                }
                false
            }
        )
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            navController.currentDestination?.let {
                if ((it.id == R.id.innerReplyFragment || it.id == R.id.specificHoleFragment) && navController.previousBackStackEntry != null) {
                    return navController.popBackStack()
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

    fun saveResultData(hole: HoleV2) {

        val intent = Intent()
            .putExtra(Constant.HOLE_LIKED, hole.liked)
            .putExtra(Constant.HOLE_LIKE_COUNT, hole.likeCount)
            .putExtra(Constant.HOLE_REPLIED, hole.isReply)
            .putExtra(Constant.HOLE_REPLY_COUNT, hole.replyCount)
            .putExtra(Constant.HOLE_FOLLOWED, hole.isFollow)
            .putExtra(Constant.HOLE_FOLLOW_COUNT, hole.followCount)

        setResult(
            ResultCodeConstant.Hole,
            intent
        )

    }

}
