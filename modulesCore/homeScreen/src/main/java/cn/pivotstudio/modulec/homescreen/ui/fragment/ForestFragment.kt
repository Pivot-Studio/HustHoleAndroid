package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding
import cn.pivotstudio.modulec.homescreen.model.ForestHole
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.JoinedForestsAdapter
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.alibaba.android.arouter.launcher.ARouter
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRefresh()

        val holeAdapter = ForestHoleAdapter(this)

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
                forestHoles.observe(viewLifecycleOwner) {
                    holeAdapter.submitList(it)
                }

                forestHeads.observe(viewLifecycleOwner) {
                    headAdapter.submitList(it.forests)
                    if (it.forests.isEmpty()) {
                        forestViewModel.showPlaceHolder()
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
                            forestPlaceholder.visibility = VISIBLE
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


    /**
     * 利用 Navigation 导航到 AllForestFragment
     *
     * 相关类 [AllForestFragment],nav_graph.xml
     */
    fun navToAllForests() {
        findNavController(
            requireActivity(), R.id.nav_host_fragment
        ).navigate(R.id.action_forest_fragment_to_all_forest_fragment)
    }

    fun navToSpecificForest(forestId: Int) {
        val action = ForestFragmentDirections.actionForestFragmentToForestDetailFragment(forestId)
        findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action)
    }

    // 点击文字内容跳转到树洞
    fun navToSpecificHole(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false).navigation(requireActivity(), 1)
        }
    }

    // 点击恢复图标跳转到树洞后自动打开软键盘
    fun navToSpecificHoleWithReply(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity").withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, true).navigation()
        }
    }

    // 点赞
    fun giveALikeToTheHole(hole: ForestHole) {
        forestViewModel.giveALikeToTheHole(hole)
    }

    // 关注/收藏
    fun followTheHole(hole: ForestHole) {
        forestViewModel.followTheHole(hole)
    }

    // 举报树洞交给举报界面处理
    fun reportTheHole(hole: ForestHole) {
        ARouter.getInstance().build("/report/ReportActivity").withInt(Constant.HOLE_ID, hole.holeId)
            .withInt(Constant.REPLY_LOCAL_ID, -1).withString(
                Constant.ALIAS, "洞主").navigation()
    }

    // 删除树洞
    fun deleteTheHole(hole: ForestHole) {
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
            setOnLoadMoreListener { refreshlayout -> //上拉加载触发
                if (forestViewModel.forestHoles.value == null || forestViewModel.forestHeads.value == null) { //特殊情况，首次加载没加载出来又选择上拉加载
                    forestViewModel.loadHolesAndHeads()
                } else {
                    forestViewModel.loadMoreForestHoles()
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

    private fun preloadAllForests() {
        val allForestViewModel: AllForestViewModel by activityViewModels()
        allForestViewModel.preload()
    }
}