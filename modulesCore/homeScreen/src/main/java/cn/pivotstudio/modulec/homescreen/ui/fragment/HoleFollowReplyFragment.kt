package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.modulec.homescreen.R
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
class HoleFollowReplyFragment : BaseFragment() {
    private val args by navArgs<HoleFollowReplyFragmentArgs>()
    lateinit var binding: ActivityHoleStarBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if((requireActivity() as HomeScreenActivity).supportActionBar != null){
            (requireActivity() as HomeScreenActivity).supportActionBar!!.hide()
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
            override fun getItemCount(): Int = titleList.size

            override fun createFragment(position: Int): Fragment {
                return MyHoleFollowReplyFragment.newInstance(position + 1)
            }
        }
        TabLayoutMediator(binding.tlHoleStar, binding.vpHoleStar) { tab, position ->
            val tabView = LayoutInflater.from(context).inflate(R.layout.tab_mine, null)
            val tv = tabView.findViewById(R.id.title) as TextView
            tv.text = requireContext().getString(titleList[position])
            tab.customView = tabView
        }.attach()
        //用post延迟切换
        binding.vpHoleStar.post {
            binding.vpHoleStar.setCurrentItem(args.fragType - 1, true)
        }
    }


    companion object {
        val titleList = listOf(
        R.string.tv_myHoles,
        R.string.tv_myFollows,
        R.string.tv_myReply
        )
    }

}