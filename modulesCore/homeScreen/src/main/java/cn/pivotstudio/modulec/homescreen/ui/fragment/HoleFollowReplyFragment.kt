package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHoleStarBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator

/**
 *@classname HoleFollowReply
 * @description:
 * @date :2022/9/20 21:58
 * @version :1.0
 * @author
 */
class HoleFollowReplyFragment : Fragment() {
    lateinit var binding: ActivityHoleStarBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private var type = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        if((activity as HomeScreenActivity).supportActionBar != null){
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getInt("fragType")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityHoleStarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.myImg.setOnClickListener{
            this.findNavController().popBackStack()
        }

        binding.vpHoleStar.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = viewModel.myFragmentList.value!!.size

            override fun createFragment(position: Int): Fragment {
                return viewModel.myFragmentList.value!![position]
            }
        }

        TabLayoutMediator(binding.tlHoleStar, binding.vpHoleStar) { tab, position ->
            tab.text = context?.getString(viewModel.myTabTitle.value!![position])
        }.attach()
        //用post延迟切换
        binding.vpHoleStar.post {
            binding.vpHoleStar.setCurrentItem(type - 1, true)
        }
    }


}