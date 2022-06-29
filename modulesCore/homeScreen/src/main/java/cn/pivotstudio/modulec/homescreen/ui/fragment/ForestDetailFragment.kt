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
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestDetailBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestDetailAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModelFactory
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.alibaba.android.arouter.launcher.ARouter
import com.example.libbase.constant.Constant

class ForestDetailFragment : Fragment() {

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

        val adapter = ForestDetailAdapter(
            onContentClick = ::navToSpecificHole,
            onReplyIconClick = ::navToSpecificHoleWithReply,
            giveALike = ::giveALikeToTheHole,
            follow = ::followTheHole
        )
        binding.recyclerViewForestDetail.adapter = adapter
        viewModel.holes.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.overview.observe(viewLifecycleOwner) {
            sharedViewModel.forestHeads.value?.run {
                viewModel.checkIfJoinedTheForest(this.forests)
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

}