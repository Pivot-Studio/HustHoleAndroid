package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.HomePageOptionBox
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomeHoleBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.adapter.HomeHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class HomeHoleFragment() : BaseFragment() {
    private lateinit var binding: FragmentHomeHoleBinding
    private val viewModel: HomePageViewModel by viewModels()
    private var job: Job? = null
    private var homeHoleAdapter: HomeHoleAdapter? = null
    private var etText: EditText? = null
    private var tbMode: TabLayout? = null
    private var type = -1

    //深浅色模式转换会重建Fragment，但是调用的是无参构造函数，因此需要借助ViewModel保存type
    constructor(type: Int) : this() {
        this.type = type
    }

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

    override fun onStart() {
        super.onStart()
        job = lifecycleScope.launch {
            viewModel.holesV2.onEach {
                finishRefreshAnim()
            }.collectLatest {
                if (it.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.homepagePlaceholder.visibility = View.VISIBLE
                } else {
                    homeHoleAdapter?.submitList(it)
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.homepagePlaceholder.visibility = View.GONE
                }
            }
        }
    }

    override fun onStop() {
        job?.cancel()
        super.onStop()
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
                val tv = context.findViewById<TextView>(R.id.tv_no_content)
                tv.text = getString(R.string.res_no_myfollow)
            }
        }
    }

    private fun initData() {
        if (type == -1) {
            type = viewModel.type
        } else {
            viewModel.type = type
        }
        if (viewModel.holesV2.value.isEmpty()) {
            when (type) {
                HOLE_LIST -> viewModel.loadHolesV2()
                FOLLOW -> viewModel.getMyFollow()
                RECOMMEND -> viewModel.loadRecHoles(NetworkConstant.SortMode.REC)
            }
        }
    }

    private fun initView() {
        etText = context.findViewById(R.id.et_homepage)
        tbMode = context.findViewById(R.id.tb_mode)
        homeHoleAdapter = HomeHoleAdapter(viewModel)
        homeHoleAdapter?.setOnItemClickListener(object : HomeHoleAdapter.OnItemClickListener {
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
                    viewModel.doneShowingTip()
                }
            }
            if (type == HOLE_LIST) {
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
                    viewModel.getMyFollow()
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
        binding.homepageNestedScrollView.smoothScrollTo(0 , 0)
        binding.refreshLayout.autoRefresh()
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        etText?.setText("")
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
            if (viewModel.sortMode.value != NetworkConstant.SortMode.LATEST_REPLY) {
                viewModel.loadHolesV2(sortMode = NetworkConstant.SortMode.LATEST_REPLY)
            }
        } else if (id == R.id.btn_ppwhomepage_latest_publish) {
            if (viewModel.sortMode.value != NetworkConstant.SortMode.LATEST) {
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
        ARouter.getInstance().build("/report/ReportActivity")
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
                .navigation(requireActivity(), ResultCodeConstant.Hole)
        }
    }

    companion object {
        const val TAG = "HomeHoleFragment"

        @JvmStatic
        fun newInstance(type: Int): HomeHoleFragment {
            return HomeHoleFragment(type)
        }

        const val HOLE_LIST = 1
        const val FOLLOW = 2
        const val RECOMMEND = 3
    }
}