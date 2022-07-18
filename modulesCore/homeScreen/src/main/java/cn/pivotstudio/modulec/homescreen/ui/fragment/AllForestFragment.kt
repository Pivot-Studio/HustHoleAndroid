package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentAllFrorestBinding
import cn.pivotstudio.modulec.homescreen.ui.adapter.AllForestAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.AllForestViewModel
import com.example.libbase.base.ui.fragment.BaseFragment
import kotlinx.coroutines.delay

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

        val adapter = AllForestAdapter(::navToForestDetail)
        binding.allForestRecyclerView.adapter = adapter
        viewModel.forestCardsWithOneType.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.forestCards.toList())
        }
    }

    fun navToForestDetail(forestId: Int) {
        val action = AllForestFragmentDirections
            .actionAllForestFragmentToForestDetailFragment(forestId)
        Log.d(TAG, "navToForestDetail: forest id : $forestId")
        findNavController(requireActivity(), R.id.nav_host_fragment)
            .navigate(action)
    }
}