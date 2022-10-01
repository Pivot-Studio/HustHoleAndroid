package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMineRecycleviewBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineOthersAdapter
import cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFollowReplyFragment
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.PERSONAL_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHIELD_SETTING
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.UPDATE

/**
 *@classname ItemMineFragment
 * @description:
 * @date :2022/9/24 13:37
 * @version :1.0
 * @author
 */
class ItemMineFragment : Fragment() {
    private lateinit var binding: FragmentMineRecycleviewBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("optionType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineRecycleviewBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myImg.setOnClickListener {
            this.findNavController().popBackStack()
        }
        binding.myTitle.text = context?.getString(viewModel.myNameList.value!![type])
        if (type == PERSONAL_SETTING || type == SHIELD_SETTING || type == UPDATE) {
            val adapter = MineOthersAdapter(MineFragmentViewModel.DETAIL, viewModel, this)
            when (type) {
                PERSONAL_SETTING -> viewModel.mySettingList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
                SHIELD_SETTING -> viewModel.shieldList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
                UPDATE -> viewModel.updateList.observe(viewLifecycleOwner) { list ->
                    adapter.submitList(list)
                }
            }
            binding.rvTitle.apply {
                this.adapter = adapter
                addItemDecoration(MyHoleFollowReplyFragment.SpaceItemDecoration(0, 2))
            }
        } else {
            binding.rvTitle.visibility = GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if((activity as HomeScreenActivity).supportActionBar != null){
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
    }
}