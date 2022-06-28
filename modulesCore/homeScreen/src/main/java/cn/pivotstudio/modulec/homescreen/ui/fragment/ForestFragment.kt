package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import cn.pivotstudio.modulec.homescreen.BuildConfig
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        // 初始化两个RecyclerView
        binding.apply {
            val holeAdapter = ForestHoleAdapter(::navToSpecificHole)
            recyclerViewForestHoles.adapter = holeAdapter
            this@ForestFragment.viewModel.forestHoles.observe(viewLifecycleOwner) {
                holeAdapter.submitList(it)
            }

            val headAdapter = ForestHeadAdapter(::navToDetailForest)
            recyclerViewForestHead.adapter = headAdapter
            this@ForestFragment.viewModel.forestHeads.observe(viewLifecycleOwner) {
                headAdapter.submitList(it.forests)
            }

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

    fun navToDetailForest(forestId: Int) {
        val action = ForestFragmentDirections
            .actionForestFragmentToForestDetailFragment(forestId)
        findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action)
    }

    fun navToSpecificHole(holeId: Int) {
        if (BuildConfig.isRelease) {
            ARouter.getInstance().build("/hole/HoleActivity")
                .withInt(Constant.HOLE_ID, holeId)
                .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                .navigation(requireActivity(), 1)
        }
    }
}