package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAllFrorestBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.AllForestAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.AllForestViewModel
import com.example.libbase.base.ui.fragment.BaseFragment

class AllForestFragment : BaseFragment() {
    private lateinit var binding: FragmentAllFrorestBinding
    private val viewModel: AllForestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_all_frorest, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val onCardClick = View.OnClickListener { navToForestDetail() }

        binding.viewModel = viewModel

        // 初始化6个RecyclerView
        val allForestAdapter = AllForestAdapter(onCardClick)
        binding.hotRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.limitedRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.emoRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.campusRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.studyRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.entertainmentRecyclerView.adapter = allForestAdapter
        allForestAdapter.submitList(viewModel.forestCards)
        binding.btnApplyNewForest.setOnClickListener { btn: View? ->
            findNavController(
                requireActivity(),
                R.id.nav_host_fragment
            ).popBackStack()
        }

    }

    fun navToForestDetail() {
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.action_all_forest_fragment_to_forest_detail_fragment)
    }
}