package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ActivityHoleStarBinding
import com.google.android.material.tabs.TabLayout
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
    private var listener: ModeListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityHoleStarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.vpHoleStar.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = titleList.size

            override fun createFragment(position: Int): Fragment {
                val bundle = Bundle()
                bundle.putInt("type", position + 1)
                return MyHoleFollowReplyFragment.newInstance(bundle)
            }
        }
        binding.tlHoleStar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if(tab?.position == 1) {
                    listener?.changeMode()
                }
            }
        })
        TabLayoutMediator(binding.tlHoleStar, binding.vpHoleStar) { tab, position ->
            val tabView = LayoutInflater.from(context).inflate(R.layout.tab_mine, binding.tlHoleStar, false)
            val tv = tabView.findViewById(R.id.title) as TextView
            tv.text = requireContext().getString(titleList[position])
            tab.customView = tabView
        }.attach()
        //用post延迟切换
        binding.vpHoleStar.post {
            binding.vpHoleStar.setCurrentItem(args.fragType - 1, true)
        }
    }

    fun setModeListener(listener: ModeListener) {
        this.listener = listener
    }

    interface ModeListener {
        fun changeMode() {}
    }
    companion object {
        val titleList = listOf(
        R.string.tv_myHoles,
        R.string.tv_myFollows,
        R.string.tv_myReply
        )
    }
}