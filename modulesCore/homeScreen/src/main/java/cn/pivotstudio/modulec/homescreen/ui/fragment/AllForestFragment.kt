package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAllFrorestBinding
import cn.pivotstudio.modulec.homescreen.model.ForestCardList
import cn.pivotstudio.modulec.homescreen.ui.adapter.AllForestAdapter
import cn.pivotstudio.modulec.homescreen.ui.adapter.AllForestItemAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.AllForestViewModel
import com.example.libbase.base.ui.fragment.BaseFragment

const val TAG = "AllForestFragmentDebug"
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = AllForestAdapter { navToForestDetail() }
        binding.allForestRecyclerView.adapter = adapter
        viewModel.forestCardsWithOneType.observe(viewLifecycleOwner) {
                adapter.submitList(viewModel.forestCards.toList())
        }

    }

    fun navToForestDetail() {
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
        navController.navigate(R.id.action_all_forest_fragment_to_forest_detail_fragment)
    }
}