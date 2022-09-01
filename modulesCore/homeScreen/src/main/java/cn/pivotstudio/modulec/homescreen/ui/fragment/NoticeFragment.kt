package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentNoticeBinding
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.NoticeAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.NoticeViewModel
import com.alibaba.android.arouter.launcher.ARouter

class NoticeFragment : BaseFragment() {

    private lateinit var binding: FragmentNoticeBinding
    private val viewModel: NoticeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notice, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresh()
        val noticeAdapter = NoticeAdapter(this)

        binding.apply {
            rvNotices.adapter = noticeAdapter
        }

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.apply {
            lifecycleScope.launchWhenStarted {
                replies.collect {
                    noticeAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                showPlaceholder.collect {
                    if (it) {
                        binding.noticePlaceholder.visibility = View.VISIBLE
                    } else {
                        binding.noticePlaceholder.visibility = View.GONE
                    }
                }
            }


            state.observe(viewLifecycleOwner) {
                when (it) {
                    LoadStatus.DONE,
                    LoadStatus.ERROR ->
                        finishRefreshAnim()
                    else -> {}
                }
            }

        }

    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false).navigation(requireActivity(), 1)
        }
    }

    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity)) //设置自定义刷新头
            setRefreshFooter(StandardRefreshFooter(activity)) //设置自定义刷新底
            setOnRefreshListener {    //下拉刷新触发
                viewModel.loadReplies()
                binding.rvNotices.isEnabled = false
            }
            setOnLoadMoreListener {  //上拉加载触发
                viewModel.loadMore()
                binding.rvNotices.isEnabled = false
            }
        }
    }

    private fun finishRefreshAnim() {
        binding.apply {
            refreshLayout.finishRefresh() //结束下拉刷新动画
            refreshLayout.finishLoadMore() //结束上拉加载动画
            rvNotices.isEnabled = true //加载结束后允许滑动
        }
    }

}