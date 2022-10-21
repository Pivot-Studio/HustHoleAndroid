package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.dialog.DeleteDialog
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestDetailBinding
import cn.pivotstudio.modulec.homescreen.repository.ForestDetailHolesLoadStatus
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestDetailAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModelFactory
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.launch

class ForestDetailFragment : BaseFragment() {

    private val _args: ForestDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentForestDetailBinding

    private val viewModel: ForestDetailViewModel by viewModels {
        ForestDetailViewModelFactory(_args.forestBrief)
    }

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
            viewModel.delayLoadHoles()
        }
    }

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

        val adapter = ForestDetailAdapter(this)

        binding.apply {
            recyclerViewForestDetail.adapter = adapter
            recyclerViewForestDetail.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    adapter.lastImageMore?.let {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                    }
                }
            })
            (recyclerViewForestDetail.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false

            fabPublishHoleFromForest.setOnClickListener {
                navToPublishHoleFromDetailForest(_args.forestBrief.forestId)
            }

            detailForestJoinBtn.setOnClickListener {
                joinTheForest()
            }

            detailForestQuitBtn.setOnClickListener {
                quitTheForest()
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.holesV2.collect {
                    adapter.submitList(it)
                }
            }
        }

        viewModel.tip.observe(viewLifecycleOwner) {
            it?.let {
                showMsg(it)
                viewModel.doneShowingTip()
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

    private fun joinTheForest() {
        viewModel.joinTheForest()
    }

    private fun quitTheForest() {
        viewModel.quitTheForest()
    }

    // 点击item跳转到树洞
    fun navToSpecificHole(holeId: String) {
        viewModel.loadHoleLater(holeId)
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId.toInt())
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), ResultCodeConstant.Hole)
        }
    }

    // 点击具体小树林 FloatingActionButton 跳转到发布树洞并填充小树林信息
    fun navToPublishHoleFromDetailForest(forestId: String) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/publishHole/PublishHoleActivity")
                .withBundle(Constant.FROM_DETAIL_FOREST, Bundle().apply {
                    putString(Constant.FOREST_ID, forestId)
                    putString(Constant.FOREST_NAME, viewModel.forestBrief.forestName)
                })
                .navigation()
        }
    }

    // 点击回复图标跳转到树洞后自动打开软键盘
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
        viewModel.giveALikeToTheHole(hole)
    }

    // 关注/收藏
    fun followTheHole(hole: HoleV2) {
        viewModel.followTheHole(hole)
    }

    // 举报树洞交给举报界面处理
    fun reportTheHole(hole: HoleV2) {
        hole.let {
            ARouter.getInstance().build("/report/ReportActivity")
                .withString(Constant.HOLE_ID, it.holeId)
                .withString(Constant.ALIAS, "洞主")
                .navigation()
        }
    }

    // 删除树洞
    fun deleteTheHole(hole: HoleV2) {
        val dialog = DeleteDialog(context)
        dialog.show()
        dialog.setOptionsListener {
            viewModel.deleteTheHole(hole)
        }
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
                if (viewModel.holesV2.value.isEmpty()) { //特殊情况，首次加载没加载出来又选择上拉加载
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