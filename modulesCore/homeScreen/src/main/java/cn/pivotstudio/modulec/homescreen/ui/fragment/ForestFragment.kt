package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHeadAdapter
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.example.libbase.base.ui.fragment.BaseFragment
import com.example.libbase.constant.Constant

/**
 * @classname: ForestFragment
 * @description:
 * @date: 2022/5/2 22:57
 * @version: 1.0
 * @author:
 */
class ForestFragment : BaseFragment() {
    private lateinit var binding: FragmentForestBinding
    private val viewModel: ForestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_forest, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRefresh()

        val holeAdapter = ForestHoleAdapter(
            onContentClick = ::navToSpecificHole,
            onReplyIconClick = ::navToSpecificHoleWithReply,
            onAvatarClick = ::navToSpecificForest,
            giveALike = ::giveALikeToTheHole,
            follow = ::followTheHole
        )

        val headAdapter = ForestHeadAdapter(onItemClick = ::navToSpecificForest)

        // 初始化两个RecyclerView
        binding.apply {

            recyclerViewForestHoles.adapter = holeAdapter
            recyclerViewForestHead.adapter = headAdapter
            viewModel = this@ForestFragment.viewModel.apply {
                forestHoles.observe(viewLifecycleOwner) {
                    holeAdapter.submitList(it)
                }

                forestHeads.observe(viewLifecycleOwner) {
                    headAdapter.submitList(it.forests)
                }

                loadState.observe(viewLifecycleOwner) {
                    when (it) {
                        LoadStatus.DONE,
                        LoadStatus.ERROR ->
                            finishRefreshAnim()
                        else -> {}
                    }
                }
            }

            (recyclerViewForestHoles.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            if (forestFragment == null) {
                this.forestFragment = this@ForestFragment
            }
        }
    }


    /**
     * 利用 Navigation 导航到 AllForestFragment
     *
     * 相关类 [AllForestFragment],nav_graph.xml
     */
    fun navToAllForests() {
        findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(R.id.action_forest_fragment_to_all_forest_fragment)
    }

    private fun navToSpecificForest(forestId: Int) {
        val action = ForestFragmentDirections
            .actionForestFragmentToForestDetailFragment(forestId)
        findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action)
    }

    // 点击文字内容跳转到树洞
    private fun navToSpecificHole(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), 1)
        }
    }

    // 点击恢复图标跳转到树洞后自动打开软键盘
    private fun navToSpecificHoleWithReply(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, true)
                .navigation()
        }
    }

    // 点赞
    private fun giveALikeToTheHole(holeId: Int) {
        viewModel.giveALikeToTheHole(holeId)
    }

    // 关注/收藏
    private fun followTheHole(holeId: Int) {
        viewModel.followTheHole(holeId)
    }

    // 基本上从HomePageFragment复制过来的代码，没有深入研究
    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity)) //设置自定义刷新头
            setRefreshFooter(StandardRefreshFooter(activity)) //设置自定义刷新底
            setOnRefreshListener {    //下拉刷新触发
                viewModel.loadHolesAndHeads()
                binding.recyclerViewForestHoles.isEnabled = false
            }
            setOnLoadMoreListener { refreshlayout ->  //上拉加载触发
                if (viewModel.forestHoles.value == null || viewModel.forestHeads.value == null) { //特殊情况，首次加载没加载出来又选择上拉加载
                    viewModel.loadHolesAndHeads()
                } else {
                    viewModel.loadMoreForestHoles()
                }
                binding.recyclerViewForestHoles.isEnabled = false

            }
        }
    }

    private fun finishRefreshAnim() {
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.recyclerViewForestHoles.isEnabled = true //加载结束后允许滑动
    }
}