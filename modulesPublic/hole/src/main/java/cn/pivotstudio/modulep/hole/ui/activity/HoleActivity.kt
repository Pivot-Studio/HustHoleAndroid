package cn.pivotstudio.modulep.hole.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.databinding.ActivityHoleBinding
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
            bundleOf(KEY_HOLE_ID to holeId.toString())
        )

        navController.addOnDestinationChangedListener { _, destination, argument ->
            supportActionBar?.title = destination.label

            when (destination.id) {
                R.id.firstLevelReplyFragment -> supportActionBar?.title = argument?.getString(KEY_HOLE_ID)
            }
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    }

    /**
     * 监听手机回退键
     *
     * @param keyCode
     * @param event
     * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) { //按下的如果是BACK，同时没有重复
            saveData()
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 保存输入内容以及选择回复的人
     */
    private fun saveData() {

    }
}
