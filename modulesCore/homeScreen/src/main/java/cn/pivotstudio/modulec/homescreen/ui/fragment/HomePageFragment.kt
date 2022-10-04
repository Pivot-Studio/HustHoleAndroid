package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.moduleb.libbase.base.model.HoleReturnInfo
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.RequestCodeConstant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.HomeHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

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
    private val viewModel: HomePageViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initRefresh()
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

        val homeHoleAdapter = HomeHoleAdapter(viewModel, this)

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

        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }

            lifecycleScope.launchWhenStarted {
                holesV2.onEach {
                    finishRefreshAnim()
                }.collectLatest {
                    homeHoleAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                loading.collectLatest { loading ->
                    if (loading.not()) {
                        finishRefreshAnim()
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
            viewModel.loadHolesV2()
            binding.recyclerView.isEnabled = false
        }

        binding.refreshLayout.setOnLoadMoreListener {    //上拉加载触发
            viewModel.loadMoreHoles()
            binding.recyclerView.isEnabled = false
        }
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    private fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_ppwhomepage_latest_reply) {
            viewModel.loadHolesV2(sortMode = NetworkConstant.SortMode.LATEST_REPLY)
        } else if (id == R.id.btn_ppwhomepage_latest_publish) {
            viewModel.loadHolesV2(sortMode = NetworkConstant.SortMode.LATEST)
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
            val queryKey = binding.etHomepage.text.toString()
            viewModel.searchKeyword = queryKey
            viewModel.isSearch = true
            viewModel.searchHolesV2(queryKey)
        }
        return false
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        binding.etHomepage.setText("")
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.recyclerView.isEnabled = true
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