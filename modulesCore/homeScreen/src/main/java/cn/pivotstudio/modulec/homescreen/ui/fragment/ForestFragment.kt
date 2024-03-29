package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.ui.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.ui.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.ui.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding
import cn.pivotstudio.moduleb.rebase.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.JoinedForestsAdapter
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.constant.ResultCodeConstant
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.AllForestViewModel

/**
 * @classname: ForestFragment
 * @description:
 * @date: 2022/5/2 22:57
 * @version: 1.0
 * @author:
 */
class ForestFragment : BaseFragment() {
    private lateinit var binding: FragmentForestBinding
    private val forestViewModel: ForestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forest, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == ResultCodeConstant.Hole) {
            data?.extras?.let { bundle ->
                bundle.apply {
                    forestViewModel.refreshLoadLaterHole(
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
            forestViewModel.delayLoadHoles()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRefresh()

        val holeAdapter = ForestHoleAdapter(
            this
        )

        val headAdapter = JoinedForestsAdapter(
            onItemClick = ::navToSpecificForest,
            navToAllForest = ::navToAllForests
        )

        // 初始化两个RecyclerView和liveData监听器
        binding.apply {
            recyclerViewForestHoles.apply {
                adapter = holeAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        (adapter as ForestHoleAdapter).lastImageMore?.let {
                            if (it.isVisible) {
                                it.visibility = GONE
                            }
                        }
                    }
                })
            }

            recyclerViewForestHead.adapter = headAdapter
            viewModel = forestViewModel.apply {
                lifecycleScope.launchWhenStarted {
                    holesV2.collect {
                        holeAdapter.submitList(it)
                    }
                }

                lifecycleScope.launchWhenStarted {
                    forestsV2.collect {
                        headAdapter.submitList(it)
                    }
                }

                holesLoadState.observe(viewLifecycleOwner) {
                    when (it) {
                        LoadStatus.DONE -> {
                            recyclerViewForestHoles.visibility = VISIBLE
                            forestPlaceholder.visibility = GONE
                            finishRefreshAnim()
                        }
                        LoadStatus.ERROR -> {
                            holesV2.value.takeIf { holes ->
                                holes.isEmpty()
                            }?.let {
                                forestPlaceholder.visibility = VISIBLE
                            }
                            finishRefreshAnim()
                        }
                        LoadStatus.LOADING -> {
                            forestPlaceholder.visibility =
                                if (recyclerViewForestHoles.isVisible) GONE else VISIBLE
                        }
                        else -> {
                            recyclerViewForestHoles.visibility = GONE
                            forestPlaceholder.visibility = VISIBLE
                            finishRefreshAnim()
                        }
                    }
                }

                tip.observe(viewLifecycleOwner) {
                    it?.let {
                        showMsg(it)
                        doneShowingTip()
                    }
                }
            }

            if (forestFragment == null) {
                this.forestFragment = this@ForestFragment
            }

        }

        preloadAllForests()
    }

    override fun onResume() {
        super.onResume()
        forestViewModel.tryLoadNewHeader()
    }


    /**
     * 利用 Navigation 导航到 AllForestFragment
     *
     * 相关类 [AllForestFragment],nav_graph.xml
     */
    fun navToAllForests() {
        forestViewModel.loadHeaderLater()
        findNavController(
            requireActivity(), R.id.nav_host_fragment
        ).navigate(R.id.action_forest_fragment_to_all_forest_fragment)
    }

    fun navToSpecificForest(forestId: String) {
        forestViewModel.loadHeaderLater()
        val action = ForestFragmentDirections.actionForestFragmentToForestDetailFragment(
            forestViewModel.getForestById(forestId)
        )
        findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action)
    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: String) {
        forestViewModel.loadHoleLater(holeId)
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId.toInt())
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), ResultCodeConstant.Hole)
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

    // 点赞
    fun giveALikeToTheHole(hole: HoleV2) {
        forestViewModel.giveALikeToTheHole(hole)
    }

    // 关注/收藏
    fun followTheHole(hole: HoleV2) {
        forestViewModel.followTheHole(hole)
    }

    // 举报树洞交给举报界面处理
    fun reportTheHole(hole: HoleV2) {
        ARouter.getInstance().build("/hole/ReportActivity")
            .withString(Constant.HOLE_ID, hole.holeId)
            .withString(Constant.ALIAS, "洞主")
            .navigation()
    }

    // 删除树洞
    fun deleteTheHole(hole: HoleV2) {
        val dialog = DeleteDialog(context)
        dialog.show()
        dialog.setOptionsListener {
            forestViewModel.deleteTheHole(hole)
        }
    }

    // 基本上从HomePageFragment复制过来的代码，没有深入研究
    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity)) //设置自定义刷新头
            setRefreshFooter(StandardRefreshFooter(activity)) //设置自定义刷新底
            setOnRefreshListener {    //下拉刷新触发
                forestViewModel.loadHolesAndHeads()
                binding.recyclerViewForestHoles.isEnabled = false
            }
            setOnLoadMoreListener {   //上拉加载触发
                if (forestViewModel.holesV2.value.isEmpty() || forestViewModel.holesV2.value.isEmpty()) { //特殊情况，首次加载没加载出来又选择上拉加载
                    forestViewModel.loadHolesAndHeads()
                } else {
                    forestViewModel.loadMoreForestHoles()
                }
                binding.recyclerViewForestHoles.isEnabled = false
            }

        }

        (activity as HomeScreenActivity).setOnBottomBarItemReselectedListener {
            binding.refreshLayout.autoRefresh()
            binding.forestNestedScrollView.scrollTo(0, 0)
        }
    }

    private fun finishRefreshAnim() {
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.recyclerViewForestHoles.isEnabled = true //加载结束后允许滑动
    }

    private fun preloadAllForests() {
        val allForestViewModel: AllForestViewModel by activityViewModels()
        allForestViewModel.preload()
    }
}