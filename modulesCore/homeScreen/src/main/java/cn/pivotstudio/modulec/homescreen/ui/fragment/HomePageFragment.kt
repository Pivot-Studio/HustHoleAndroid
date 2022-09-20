package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.libbase.base.model.HoleReturnInfo
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.RequestCodeConstant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.moduleb.libbase.util.data.CheckStrUtil
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
import cn.pivotstudio.modulec.homescreen.ui.adapter.HomeHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.scwang.smart.refresh.layout.api.RefreshLayout
import kotlinx.coroutines.flow.collectLatest

/**
 * @classname:HomePageFragment
 * @description:
 * @date:2022/5/2 22:56
 * @version:1.0
 * @author:
 */
class HomePageFragment : BaseFragment() {

    companion object {
        const val TAG = "HomePageFragment"
    }
    
    private lateinit var binding: FragmentHomepageBinding
    private val viewModel: HomePageViewModel by viewModels()

    private val homeHoleAdapter = HomeHoleAdapter(this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false) //
        initView()
        initRefresh()
        initObserver()
        return binding.root
    }

    /**
     * 获取点赞，关注，回复结果反馈的，fragment的onActivityResult在androidx的某个版本不推荐使用了，先暂时用着
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RequestCodeConstant.HOMEPAGE) { //如果不是由homepage这个fragment跳转过去的，返回的结果不接受
            return
        }

        // 如果是具体的一个树洞页返回到首页，涉及到点赞等图标的数据刷新 TODO 提升成更上层的方式，因为 onActivityResult 已经弃用
        if (resultCode == ResultCodeConstant.Hole) {
            val returnInfo = data!!.getParcelableExtra<HoleReturnInfo>(
                Constant.HOLE_RETURN_INFO
            )
            returnInfo?.let {
                viewModel.pClickDataBean?.is_thumbup = it.is_thumbup
                viewModel.pClickDataBean?.is_reply = it.is_reply
                viewModel.pClickDataBean?.is_follow = it.is_follow
                viewModel.pClickDataBean?.thumbup_num = it.thumbup_num
                viewModel.pClickDataBean?.reply_num = it.reply_num
                viewModel.pClickDataBean?.follow_num = it.follow_num
            }
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    /**
     * 视图初始化
     */
    private fun initView() {
        EditTextUtil.EditTextSize(
            binding.etHomepage,
            SpannableString(this.resources.getString(R.string.page1fragment_1)),
            12
        )

        binding.apply {
            recyclerView.adapter = homeHoleAdapter

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
                    (recyclerView.adapter as HomeHoleAdapter).lastImageMore?.let {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                    }
                }
            })
            clMidBlock.setOptionsListener { v: View -> onClick(v) }
            etHomepage.setOnClickListener { v: View -> onClick(v) }
            etHomepage.imeOptions = EditorInfo.IME_ACTION_SEARCH
            etHomepage.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
                onEditorListener(
                    v, actionId, event
                )
            }
        }

    }

    /**
     * 初始化刷新框架
     */
    private fun initRefresh() {
        binding.refreshLayout.setRefreshHeader(
            StandardRefreshHeader(
                activity
            )
        ) //设置自定义刷新头
        binding.refreshLayout.setRefreshFooter(
            StandardRefreshFooter(
                activity
            )
        ) //设置自定义刷新底

        binding.refreshLayout.setOnRefreshListener { refreshlayout: RefreshLayout? ->  //下拉刷新触发
            viewModel.loadHolesV2()
            binding.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> true }
        }

        binding.refreshLayout.setOnLoadMoreListener { refreshlayout: RefreshLayout? ->  //上拉加载触发
            if (viewModel.pHomePageHoles.value == null) { //特殊情况，首次加载没加载出来又选择上拉加载
                viewModel.loadHolesV2()
                binding.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> true }
            } else {
                viewModel.loadHolesV2()
            }
        }
    }

    /**
     * 初始化ViewModel数据观察者
     */
    private fun initObserver() {
        viewModel.apply {
            pHomePageHoles.observe(viewLifecycleOwner) { homepageHoleResponse: HomepageHoleResponse ->  //监听列表信息变化
                val length = homepageHoleResponse.data.size
                when (homepageHoleResponse.model) {
                    "REFRESH" -> finishRefresh(true)
                    "SEARCH_REFRESH" -> finishRefresh(false)
                    "LOAD_MORE" -> finishLoadMore(length)
                    "SEARCH_LOAD_MORE" -> finishLoadMore(length)
                    "SEARCH_HOLE" -> finishRefresh(false)
                    "BASE" -> {}
                }

                finishRefreshAnim()
            }
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }

            lifecycleScope.launchWhenStarted {
                holesV2.collectLatest {
                    homeHoleAdapter.submitList(it)
                }
            }
        }
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    private fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_ppwhomepage_newpublish) {
            if (!viewModel.isDescend!!) {
                viewModel.isDescend = true
                viewModel.refreshHoleList(0)
            }
        } else if (id == R.id.btn_ppwhomepage_newcomment) {
            if (viewModel.isDescend == true) {
                viewModel.isDescend = false
                viewModel.refreshHoleList(0)
            }
        }
    }

    /**
     * 键盘搜索监听
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    private fun onEditorListener(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if ((binding.etHomepage.text != null && binding.etHomepage.text.toString() != "") && (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)) {
            val et = binding.etHomepage.text.toString()
            viewModel.searchKeyword = et
            viewModel.isSearch = true
            if (CheckStrUtil.checkStrIsNum(et) || et[0] == '#') {
                viewModel.searchSingleHole()
            } else {
                viewModel.searchHoleList(0)
            }
        }
        return false
    }

    /**
     * 下拉刷新或搜索的数据更新
     *
     * @param isSearch 决定是否是搜索状态下，非搜索状态下下拉加载需要将状态切换
     */
    private fun finishRefresh(isSearch: Boolean) {
        if (isSearch) viewModel.isSearch = false
        viewModel.startLoadId = 0
    }

    /**
     * 上拉加载的数据更新
     */
    private fun finishLoadMore(length: Int) {
        val lastStartId = viewModel.startLoadId
        if (lastStartId != null) {
            viewModel.startLoadId = lastStartId + length
        }
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        binding.etHomepage.setText("")
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> false } //加载结束后允许滑动
    }

    /**
     * 利用 Navigation 导航到 AllForestFragment
     *
     * 相关类 [AllForestFragment],nav_graph.xml
     */
    fun navToAllForests() {
        Navigation.findNavController(
            requireActivity(), R.id.nav_host_fragment
        ).navigate(R.id.action_forest_fragment_to_all_forest_fragment)
    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: Int) {
        viewModel.loadHoleLater(holeId)
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), ResultCodeConstant.Hole)
        }
    }

    // 点击恢复图标跳转到树洞后自动打开软键盘
    fun navToSpecificHoleWithReply(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, true)
                .navigation(requireActivity(), ResultCodeConstant.Hole)
        }
    }
}