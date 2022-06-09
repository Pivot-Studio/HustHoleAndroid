package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentForestDetailBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModel
import com.example.libbase.base.ui.fragment.BaseFragment

class ForestDetailFragment : BaseFragment() {

    private lateinit var binding: FragmentForestDetailBinding

    private val viewModel: ForestDetailViewModel = ViewModelProvider(requireActivity()).get(ForestDetailViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate<FragmentForestDetailBinding>(inflater, R.layout.fragment_forest_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel

        // 用于绑定 LiveData
        binding.lifecycleOwner = viewLifecycleOwner
    }

}