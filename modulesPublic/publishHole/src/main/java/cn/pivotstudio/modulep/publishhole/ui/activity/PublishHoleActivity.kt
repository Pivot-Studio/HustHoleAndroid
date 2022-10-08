package cn.pivotstudio.modulep.publishhole.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.moduleb.database.MMKVUtil
import cn.pivotstudio.moduleb.libbase.base.ui.activity.BaseActivity
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.moduleb.libbase.util.ui.SoftKeyBoardUtil
import cn.pivotstudio.modulep.publishhole.BuildConfig
import cn.pivotstudio.modulep.publishhole.R
import cn.pivotstudio.modulep.publishhole.custom_view.ForestsPopupWindow
import cn.pivotstudio.modulep.publishhole.databinding.ActivityPublishholeBinding
import cn.pivotstudio.modulep.publishhole.viewmodel.PublishHoleViewModel
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.githang.statusbar.StatusBarCompat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @classname PublishHoleActivity
 * @description: 发布树洞界面
 * @date 2022/5/5 22:43
 * @version:1.0
 * @author:
 */
@Route(path = "/publishHole/PublishHoleActivity")
class PublishHoleActivity : BaseActivity() {
    @JvmField
    @Autowired(name = Constant.FROM_DETAIL_FOREST)
    var args: Bundle? = null

    private var forestName: String? = null
    private var forestId: String = ""
    private val viewModel: PublishHoleViewModel by viewModels()
    private lateinit var binding: ActivityPublishholeBinding

    private val forestPopupWindow: ForestsPopupWindow by lazy { ForestsPopupWindow(this) }

    private var mmkvUtil: MMKVUtil = MMKVUtil.getMMKV(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        if (args != null) {
            forestId = args!!.getString(Constant.FOREST_ID)!!
            forestName = args!!.getString(Constant.FOREST_NAME)
        }
        initView()
        initListener()
        initObserver()
    }

    /**
     * 初始化view
     */
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publishhole)
        if (args != null) {
            viewModel.forestName.value = forestName
            viewModel.forestId = forestId
        } else {
            viewModel.forestName.setValue("未选择小树林")
        }

        binding.clTitlebargreenBack.setOnClickListener { view: View -> onClick(view) }
        binding.titlebargreenAVLoadingIndicatorView.hide()
        binding.tvTitlebargreenTitle.text = "发树洞"
        binding.titlebargreenAVLoadingIndicatorView.visibility = View.GONE

        supportActionBar?.hide()

        StatusBarCompat.setStatusBarColor(
            this,
            resources.getColor(R.color.HH_BandColor_1, null),
            true
        )

        EditTextUtil.ButtonReaction(
            binding.etPublishhole,
            binding.btnPublishHole
        )

        EditTextUtil.EditTextSize(
            binding.etPublishhole,
            SpannableString(this.resources.getString(R.string.publishhole_4)),
            14
        )

        if (mmkvUtil.getString(Constant.HOLE_TEXT) != null) {
            val lastText = mmkvUtil.getString(Constant.HOLE_TEXT)
            binding.etPublishhole.setText(lastText)
            binding.tvPublishholeTextnumber.text = lastText.length.toString() + "/1037"
        }
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        binding.etPublishhole.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                binding.tvPublishholeTextnumber.text = s.length.toString() + "/1037"
                if (s.length >= 1037) {
                    Toast.makeText(this@PublishHoleActivity, "输入内容过长", Toast.LENGTH_SHORT).show()
                }
            }
        })
        SoftKeyBoardUtil.setListener(this, object : SoftKeyBoardUtil.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                changeEtHeight()
            }

            override fun keyBoardHide(height: Int) {
                changeEtHeight()
            }

            fun changeEtHeight() {
                val locationHead = IntArray(2)
                binding.btnPublishholeLine.getLocationOnScreen(locationHead)
                val locationBottom = IntArray(2)
                binding.tvPublishholeTextnumber.getLocationOnScreen(locationBottom)
                binding.etPublishhole.maxHeight = locationBottom[1] - locationHead[1] - 20
            }
        })
    }

    /**
     * 初始化数据监听
     */
    private fun initObserver() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorTip.collectLatest {
                    showMsg(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collectLatest {
                    it?.let { state ->
                        when (state) {
                            ApiStatus.SUCCESSFUL -> {
                                mmkvUtil.put(Constant.HOLE_TEXT, "")
                                showMsg(getString(R.string.publish_hole_successfully))
                                finish()
                            }
                            ApiStatus.ERROR -> {
                                binding.btnPublishHole.isClickable = true
                            }
                            ApiStatus.LOADING -> {
                                binding.btnPublishHole.isClickable = false
                            }
                        }
                    }
                }
            }
        }

        viewModel.joinedForest.observe(this) { joinedForests ->
            forestPopupWindow.setJoinedForests(joinedForests)
        }

        viewModel.typeForestList.observe(this) { forestsWithType ->
            forestPopupWindow.setAllForests(forestsWithType)
        }

        viewModel.forestName.observe(this) { s: String? ->
            binding.tvPublishholeForestname.text = s
        }

    }

    private var publishEnable = forestId.isNotBlank()

    private fun showPopupWindow() {
        forestPopupWindow.showAtLocation(
            binding.clPublishhole,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            0
        ) //在activity的底部展示。
        forestPopupWindow.show()
        publishEnable = true
    }


    /**
     * 相关点击事件
     *
     * @param view
     */
    fun onClick(view: View) {
        closeKeyBoard()
        when (view.id) {
            R.id.tv_publishhole_forestname -> {
                showPopupWindow()
            }

            R.id.btn_publish_hole -> {
                if (publishEnable) {
                    val content = binding.etPublishhole.text.toString()
                    if (content.length > 15) {
                        viewModel.publishAHole(content = content)
                    } else {
                        showMsg(getString(R.string.publish_hole_fifteen_words_at_least))
                    }
                } else {
                    showPopupWindow()
                    showMsg(getString(R.string.publish_hole_choose_a_forest_first))
                }
            }

            R.id.cl_titlebargreen_back -> {
                if (BuildConfig.isRelease) {
                    mmkvUtil.put(Constant.HOLE_TEXT, binding.etPublishhole.text.toString())
                    finish()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) { //按下的如果是BACK，同时没有重复
            mmkvUtil.put(Constant.HOLE_TEXT, binding.etPublishhole.text.toString())
        }
        return super.onKeyDown(keyCode, event)
    }
}