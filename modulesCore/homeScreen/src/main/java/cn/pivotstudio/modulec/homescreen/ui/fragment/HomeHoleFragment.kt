package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.rebase.network.ApiStatus
import cn.pivotstudio.moduleb.rebase.network.model.HoleV2
import cn.pivotstudio.moduleb.rebase.network.util.NetworkConstant
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.constant.ResultCodeConstant
import cn.pivotstudio.moduleb.rebase.lib.util.store.IDeviceWriter
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.ui.custom_view.HomePageOptionBox
import cn.pivotstudio.modulec.homescreen.ui.custom_view.PicGenerator
import cn.pivotstudio.modulec.homescreen.ui.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.ui.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.ui.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomeHoleBinding
import cn.pivotstudio.modulec.homescreen.databinding.HoleShareCardBinding
import cn.pivotstudio.modulec.homescreen.databinding.PpwBottomShareBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.adapter.HomeHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*

@RuntimePermissions
class HomeHoleFragment : BaseFragment(), PicGenerator {
    private lateinit var binding: FragmentHomeHoleBinding
    private val viewModel: HomePageViewModel by viewModels()
    private var etText: EditText? = null   //homePageFragment中的搜索框
    private var tbMode: TabLayout? = null  //父级tabLayout
    private var type = -1
    private var writer: IDeviceWriter? = null //存储文件
    private var ppw = SharePopupWindow()

    //深浅色模式转换会重建Fragment，但是调用的是无参构造函数！

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeHoleBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        initData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRefresh()
    }

    /**
     * 获取点赞，关注，回复结果反馈的，fragment的onActivityResult在androidx的某个版本不推荐使用了，先暂时用着
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == ResultCodeConstant.Hole) {
            data?.extras?.let { bundle ->
                bundle.apply {
                    viewModel.refreshLoadLaterHole(
                        isThumb = getBoolean(Constant.HOLE_LIKED),
                        replied = getBoolean(Constant.HOLE_REPLIED),
                        followed = getBoolean(Constant.HOLE_FOLLOWED),
                        thumbNum = getLong(Constant.HOLE_LIKE_COUNT),
                        replyNum = getLong(Constant.HOLE_REPLY_COUNT),
                        followNum = getLong(Constant.HOLE_FOLLOW_COUNT),
                    )
                }
            }
            return
        }
        if (resultCode == ResultCodeConstant.PUBLISH_HOLE) {
            lifecycleScope.launchWhenStarted {
                delay(2000)
                autoRefreshAndScrollToTop()
            }
        }
    }

    private fun showPlaceHolderBy(type: HomePageViewModel.PlaceholderType) {
        when (type) {
            HomePageViewModel.PlaceholderType.PLACEHOLDER_NETWORK_ERROR -> {
                binding.placeholderHomeNetError.visibility = View.VISIBLE
                binding.placeholderHomeNoResult.visibility = View.GONE
                binding.placeholderHomeNoContent.visibility = View.GONE
            }

            HomePageViewModel.PlaceholderType.PLACEHOLDER_NO_SEARCH_RESULT -> {
                binding.placeholderHomeNoResult.visibility = View.VISIBLE
                binding.placeholderHomeNetError.visibility = View.GONE
                binding.placeholderHomeNoContent.visibility = View.GONE
            }

            HomePageViewModel.PlaceholderType.PLACEHOLDER_NO_CONTENT -> {
                binding.placeholderHomeNoContent.visibility = View.VISIBLE
                binding.placeholderHomeNetError.visibility = View.GONE
                binding.placeholderHomeNoResult.visibility = View.GONE
                val tv: TextView? = requireView().findViewById(R.id.tv_no_content)
                tv?.text = getString(R.string.res_no_myfollow)
            }
        }
    }

    private fun initData() {
        type = requireArguments().getInt("type")
        writer = requireArguments().getSerializable("writer") as IDeviceWriter
        if (viewModel.holesV2.value.isEmpty()) {
            when (type) {
                HOLE_LIST -> viewModel.loadHolesV2()
                FOLLOW -> viewModel.getMyFollow(NetworkConstant.SortMode.LATEST)
                RECOMMEND -> viewModel.loadRecHoles(NetworkConstant.SortMode.REC)
            }
        }
    }

    private fun initView() {
        etText = context.findViewById(R.id.et_homepage)
        tbMode = context.findViewById(R.id.tb_mode)
        val homeHoleAdapter = HomeHoleAdapter(viewModel)
        homeHoleAdapter.setOnItemClickListener(object : HomeHoleAdapter.OnItemClickListener {
            override fun navigateWithReply(holeId: String) {
                navToSpecificHoleWithReply(holeId)
            }

            override fun navigate(holeId: String) {
                navToSpecificHole(holeId)
            }

            override fun deleteHole(hole: HoleV2) {
                deleteTheHole(hole)
            }

            override fun reportHole(hole: HoleV2) {
                reportTheHole(hole)
            }

            override fun generate(hole: HoleV2) {
                ppw.initView(hole)
                ppw.initAction(hole)
            }
        })
        binding.apply {
            recyclerView.adapter = homeHoleAdapter
            recyclerView.setHasFixedSize(true)
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
                    (recyclerView.adapter as HomeHoleAdapter).lastImageMore?.let {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                    }
                }
            })
        }
        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    doneShowingTip()
                }
            }
            when (type) {
                HOLE_LIST -> {
                    (parentFragment as HomePageFragment).setEditActionListener(object :
                        HomePageFragment.EditActionListener {
                        override fun onSend(text: String) {
                            viewModel.searchKeyword = text
                            viewModel.isSearch = true
                            viewModel.searchHolesV2(text)
                        }
                    })
                    (tbMode?.getTabAt(0)?.customView as HomePageOptionBox).setOptionsListener { v: View ->
                        onSelectModeClick(v)
                    }
                }
                FOLLOW -> {
                    (parentFragment as HomePageFragment).setTabSelectListener(object : HomePageFragment.TabSelectListener {
                        override fun alterMode() {
                            if (viewModel.sortModes == NetworkConstant.SortMode.LATEST) {
                                viewModel.getMyFollow(NetworkConstant.SortMode.ASC)
                            } else {
                                viewModel.getMyFollow(NetworkConstant.SortMode.LATEST)
                            }
                        }

                    })
                }
            }

            lifecycleScope.launch {
                holesV2.onEach {
                    finishRefreshAnim()
                }.collectLatest {
                    if (it.isEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.homepagePlaceholder.visibility = View.VISIBLE
                    } else {
                        homeHoleAdapter.submitList(it)
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.homepagePlaceholder.visibility = View.GONE
                    }
                }
            }

            lifecycleScope.launch {
                loadingState.collectLatest { state ->
                    when (state) {
                        ApiStatus.SUCCESSFUL,
                        ApiStatus.ERROR -> {
                            finishRefreshAnim()
                        }
                        ApiStatus.LOADING -> {}
                    }
                }
            }

            lifecycleScope.launch {
                showingPlaceholder.collectLatest {
                    it?.let { placeholderType ->
                        showPlaceHolderBy(placeholderType)
                    }
                }
            }
        }
    }

    /**
     * 初始化刷新框架
     */
    private fun initRefresh() {
        binding.refreshLayout.setRefreshHeader(StandardRefreshHeader(activity)) //设置自定义刷新头
        binding.refreshLayout.setRefreshFooter(StandardRefreshFooter(activity)) //设置自定义刷新底
        binding.refreshLayout.setOnRefreshListener { //下拉刷新触发
            when (type) {
                HOLE_LIST -> {
                    viewModel.loadHolesV2()
                }
                FOLLOW -> {
                    viewModel.getMyFollow(NetworkConstant.SortMode.LATEST)
                }
                RECOMMEND -> {
                    viewModel.loadRecHoles(NetworkConstant.SortMode.REC)
                }
            }
            binding.recyclerView.isEnabled = false
        }

        binding.refreshLayout.setOnLoadMoreListener {    //上拉加载触发
            when (type) {
                HOLE_LIST -> {
                    viewModel.loadMoreHoles()
                }
                FOLLOW -> {
                    viewModel.loadMoreFollow()
                }
                RECOMMEND -> {
                    viewModel.loadMoreRecHoles(NetworkConstant.SortMode.REC)
                }
            }
            binding.recyclerView.isEnabled = false
        }

        (activity as HomeScreenActivity).setOnBottomBarItemReselectedListener {
            autoRefreshAndScrollToTop()
        }
    }

    private fun autoRefreshAndScrollToTop() {
        binding.homepageNestedScrollView.smoothScrollTo(0, 0)
        binding.refreshLayout.autoRefresh()
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        etText?.let {
            if(it.text.isNotEmpty()) {
                it.setText("")
            }
        }
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.recyclerView.isEnabled = true
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    private fun onSelectModeClick(v: View) {
        val id = v.id
        if (id == R.id.btn_ppwhomepage_latest_reply) {
            if (viewModel.sortModes != NetworkConstant.SortMode.LATEST_REPLY) {
                viewModel.loadHolesV2(sortMode = NetworkConstant.SortMode.LATEST_REPLY)
            }
        } else if (id == R.id.btn_ppwhomepage_latest_publish) {
            if (viewModel.sortModes != NetworkConstant.SortMode.LATEST) {
                viewModel.loadHolesV2(sortMode = NetworkConstant.SortMode.LATEST)
            }
        }
    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: String) {
        viewModel.loadHoleLater(holeId)
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId.toInt())
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), ResultCodeConstant.Hole)
        }
    }

    // 举报树洞交给举报界面处理
    fun reportTheHole(hole: HoleV2) {
        ARouter.getInstance().build("/hole/ReportActivity")
            .withString(Constant.HOLE_ID, hole.holeId)
            .withString(Constant.ALIAS, "洞主")
            .navigation()
    }

    fun deleteTheHole(hole: HoleV2) {
        val dialog = DeleteDialog(context)
        dialog.show()
        dialog.setOptionsListener {
            viewModel.deleteTheHole(hole)
        }
    }

    // 点击恢复图标跳转到树洞后自动打开软键盘
    fun navToSpecificHoleWithReply(holeId: String) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId.toInt())
                .withBoolean(Constant.IF_OPEN_KEYBOARD, true)
                .navigation(context, ResultCodeConstant.Hole)
        }
    }

    private fun cancelDarkBackGround() {
        val lp = requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        requireActivity().window.attributes = lp
    }   //取消暗背景

    override suspend fun generate(view: View): Bitmap? {
        val bitmap: Bitmap?
        try {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Toast.makeText(context, "图片生成失败！", Toast.LENGTH_SHORT).show()
            return null
        }
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun getPermission() {
        ppw.onDownloadClick()
    }

    override suspend fun save(photo: Bitmap, fileName: String) {
        writer?.saveToDevice(photo, context, "hustHole", fileName)
    }

    override suspend fun generateQRCode(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_SQUARE
            val matrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 720, 720, hints)
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK
                    } else {
                        pixels[y * width + x] = Color.WHITE
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap?.recycle()
            return null
        }
    }

    companion object {
        const val TAG = "HomeHoleFragment"

        @JvmStatic
        fun newInstance(args: Bundle): HomeHoleFragment {
            val newFragment = HomeHoleFragment()
            newFragment.arguments = args
            return newFragment
        }

        const val HOLE_LIST = 1
        const val FOLLOW = 2
        const val RECOMMEND = 3
    }

    inner class SharePopupWindow {
        private var shareBind: HoleShareCardBinding? = null
        private var funcBind: PpwBottomShareBinding? = null
        private var ppwShare: PopupWindow? = null
        private var ppwFunc: PopupWindow? = null
        private var bitmap: Bitmap? = null
        fun initView(data: HoleV2) {
            funcBind = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.ppw_bottom_share,
                null,
                false
            )
            shareBind = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.hole_share_card,
                null,
                false
            )

            viewModel.viewModelScope.launch {
                bitmap = generateQRCode("https://husthole.com/#/holeDetail/" + data.holeId)
                shareBind!!.QRCode.setImageBitmap(bitmap)
            }
            ppwShare = PopupWindow(shareBind!!.root)
            ppwFunc = PopupWindow(funcBind!!.root)
            val window = requireActivity().window

            with(ppwShare!!) {
                setAttribute(this)
                isOutsideTouchable = true
                isFocusable = true
                width = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            with(ppwFunc!!) {
                setAttribute(this)
                isFocusable = false
                isOutsideTouchable = false
                width = ViewGroup.LayoutParams.MATCH_PARENT
            }

            //背景亮度降低
            window.attributes.alpha = 0.6f
            window.setWindowAnimations(R.style.darkScreenAnim)
            ppwShare!!.showAtLocation(
                window.decorView, Gravity.CENTER,
                0, 0
            )
            ppwFunc!!.showAtLocation(
                window.decorView, Gravity.BOTTOM,
                0, 0
            )
        }

        fun initAction(data: HoleV2) {
            ppwShare?.setOnDismissListener {
                ppwFunc?.dismiss()
                cancelDarkBackGround()
            }
            shareBind?.apply {
                this.hole = data
                createAt.text =
                    getString(R.string.created_at).format(data.createAt.substring(0, 10))
            }
            funcBind?.download?.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getPermission()
                } else {
                    getPermissionWithPermissionCheck()
                }
            }
            /*funcBind.shareToQq.setOnClickListener {
                val params = Bundle()
                params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
                params.putString(QQShare.SHARE_TO_QQ_TITLE, '#' + data.holeId)
                params.putString(QQShare.SHARE_TO_QQ_SUMMARY, if(data.content.length <= 40) data.content else data.content.substring(0..40))
                params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://husthole.com/#/holeDetail/1" + data.holeId)
                params.putString(
                    QQShare.SHARE_TO_QQ_IMAGE_URL,
                    "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif"
                )
                params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "1037树洞")
                val mTencent = Tencent.createInstance("jjj", this.requireActivity().applicationContext, "${this.requireActivity().applicationContext.packageName}.fileprovider")
                mTencent.shareToQQ(this.requireActivity(), params, object : IUiListener {
                    override fun onComplete(p0: Any?) {
                        TODO("Not yet implemented")
                    }

                    override fun onError(p0: UiError?) {
                        TODO("Not yet implemented")
                    }

                    override fun onCancel() {
                        TODO("Not yet implemented")
                    }
                    override fun onWarning(p0: Int) {
                        TODO("Not yet implemented")
                    }
                })
                ppwShare.dismiss()
            }*/
        }

        fun onDownloadClick() {
            viewModel.viewModelScope.launch {
                shareBind?.apply {
                    val now = Date()
                    val ft2 = SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA)
                    generate(this.mainContent)?.let {
                        save(it, hole?.holeId + ft2.format(now))
                        bitmap?.recycle()
                        ppwShare?.dismiss()
                    }
                }
            }
        }

        private fun setAttribute(ppw: PopupWindow) {
            with(ppw) {
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = R.style.Page2Anim
            }
        }
    }

    override fun onResume() {
        (activity as HomeScreenActivity).setOnBottomBarItemReselectedListener {
            autoRefreshAndScrollToTop()
        }
        super.onResume()
    }

    override fun onStop() {
        // 解决底部按键冲突问题
        (activity as HomeScreenActivity).setOnBottomBarItemReselectedListener(null)
        super.onStop()
    }
}