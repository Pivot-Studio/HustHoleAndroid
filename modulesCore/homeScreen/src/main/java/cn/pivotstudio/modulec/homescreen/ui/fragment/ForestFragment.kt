package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHeadAdapter
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHoleAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestViewModel
import com.example.libbase.base.ui.fragment.BaseFragment

/**
 * @classname: ForestFragment
 * @description:
 * @date: 2022/5/2 22:57
 * @version: 1.0
 * @author:
 */
class ForestFragment : BaseFragment() {
    private lateinit var binding: FragmentForestBinding
    private lateinit var viewModel: ForestViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forest, container, false)
        viewModel = ViewModelProvider(this).get(ForestViewModel::class.java)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化两个RecyclerView
        val holeAdapter = ForestHoleAdapter()
        binding.recyclerViewForestHoles.adapter = holeAdapter
        viewModel.forestHoles.observe(viewLifecycleOwner) {
            holeAdapter.submitList(it)
        }
//        val headAdapter = ForestHeadAdapter()
//        binding.recyclerViewForestHead.adapter = headAdapter
//        headAdapter.submitList(emptyList())
        if (binding.forestFragment == null) {
            binding.forestFragment = this
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
}