package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestDetailBinding
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.repository.LoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestDetailAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModelFactory
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.example.libbase.base.ui.fragment.BaseFragment
import com.example.libbase.constant.Constant

class ForestDetailFragment : BaseFragment() {

    private val _args: ForestDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentForestDetailBinding

    private val viewModel: ForestDetailViewModel by viewModels {
        ForestDetailViewModelFactory(_args.forestId)
    }

    // 只有 ForestViewModel 实例存放了所有关注了的小树林的列表
    // 所以需要从这里拿到这个列表进行状态判断
    // 决定小树林"是否加入" 的显示状态
    private val sharedViewModel: ForestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_forest_detail, container, false)
        // 用于绑定 LiveData
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        initRefresh()

        val adapter = ForestDetailAdapter(
            onContentClick = ::navToSpecificHole,
            onReplyIconClick = ::navToSpecificHoleWithReply,
            giveALike = ::giveALikeToTheHole,
            follow = ::followTheHole
        )

        binding.apply {
            recyclerViewForestDetail.adapter = adapter

            (recyclerViewForestDetail.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            fabPublishHoleFromForest.setOnClickListener {
                navToPublishHoleFromDetailForest(_args.forestId)
            }
        }

        viewModel.holes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.overview.observe(viewLifecycleOwner) {
            sharedViewModel.forestHeads.value?.run {
                viewModel.checkIfJoinedTheForest(this.forests)
            }
        }

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                ForestDetailHolesLoadStatus.DONE,
                ForestDetailHolesLoadStatus.ERROR ->
                    finishRefreshAnim()
                else -> {}
            }
        }



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

    // 点击具体小树林 FloatingActionButton 跳转到发布树洞并填充小树林信息
    private fun navToPublishHoleFromDetailForest(forestId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/publishHole/PublishHoleActivity")
                .withBundle(Constant.FROM_DETAIL_FOREST, Bundle().apply {
                    putInt(Constant.FOREST_ID, forestId)
                    putString(Constant.FOREST_NAME, viewModel.overview.value!!.name)
                })
                .navigation()
        } else {
            showMsg("当前为模块测试阶段")
        }
    }

    // 点击回复图标跳转到树洞后自动打开软键盘
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

    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity)) //设置自定义刷新头
            setRefreshFooter(StandardRefreshFooter(activity)) //设置自定义刷新底
            setOnRefreshListener {    //下拉刷新触发
                viewModel.loadHoles()
                binding.recyclerViewForestDetail.isEnabled = false
            }
            setOnLoadMoreListener { refreshlayout ->  //上拉加载触发
                if (viewModel.holes.value == null) { //特殊情况，首次加载没加载出来又选择上拉加载
                    viewModel.loadHoles()
                } else {
                    viewModel.loadMore()
                }
                binding.recyclerViewForestDetail.isEnabled = false
            }
        }
    }

    private fun finishRefreshAnim() {
        binding.apply {
            refreshLayout.finishRefresh() //结束下拉刷新动画
            refreshLayout.finishLoadMore() //结束上拉加载动画
            recyclerViewForestDetail.isEnabled = true //加载结束后允许滑动
        }
    }

}