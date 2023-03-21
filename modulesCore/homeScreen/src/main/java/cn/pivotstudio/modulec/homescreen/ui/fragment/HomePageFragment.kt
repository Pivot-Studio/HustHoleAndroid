package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.HomePageOptionBox
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


/**
 * @classname:HomePageFragment
 * @description:
 * @date:2022/5/2 22:56
 * @version:1.0
 * @author:
 */
class HomePageFragment : BaseFragment() {
    companion object {
        const val TAG = "HomePageFragment"
        val titleList = listOf(
            R.string.page1fragment_2,
            R.string.page1fragment_3,
            R.string.page1fragment_4
        )
    }

    private lateinit var binding: FragmentHomepageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        childFragmentManager.fragments[binding.tbMode.selectedTabPosition]?.onActivityResult(
            requestCode,
            resultCode,
            data
        )
    }

    /**
     * 视图初始化
     */
    private fun initView() {
        EditTextUtil.EditTextSize(
            binding.etHomepage,
            SpannableString(this.resources.getString(R.string.page1fragment_1)),
            12
        )

        binding.apply {
            etHomepage.imeOptions = EditorInfo.IME_ACTION_SEARCH

            vpHomescreenHole.adapter = object : FragmentStateAdapter(this@HomePageFragment) {
                override fun getItemCount(): Int = 3

                override fun createFragment(position: Int): Fragment {
                    val fragment = HomeHoleFragment.newInstance(position + 1)
                    if (position == 0) {
                        etHomepage.setOnClickListener { v -> fragment.onSelectModeClick(v) }
                        etHomepage.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
                            if (tbMode.selectedTabPosition != 0) {
                                tbMode.selectTab(tbMode.getTabAt(0), true)
                            }
                            fragment.onEditorListener(
                                v, actionId, event
                            )
                        }
                        (tbMode.getTabAt(0)?.customView as HomePageOptionBox).setOptionsListener { v: View ->
                            fragment.onSelectModeClick(v)
                        }
                    }
                    return fragment
                }
            }
            TabLayoutMediator(tbMode, vpHomescreenHole) { tab, position ->
                val tabView = if (position != 0) {
                    LayoutInflater.from(context).inflate(R.layout.tab_mine, tbMode, false)
                } else {
                    HomePageOptionBox(context)
                }
                val tv = tabView.findViewById(R.id.title) as TextView
                tv.text = requireContext().getString(titleList[position])
                tab.customView = tabView
            }.attach()
            tbMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.customView is HomePageOptionBox) {
                        val mForestSquareCl =
                            (tab.customView as HomePageOptionBox).findViewById<LinearLayout>(R.id.ll_mid_block)
                        mForestSquareCl.isClickable = true
                        mForestSquareCl.isFocusable = true
                        //mForestSquareCl.setOnClickListener { v: View -> (tab.customView as HomePageOptionBox).onClick(v) }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if (tab?.customView is HomePageOptionBox) {
                        val mForestSquareCl =
                            (tab.customView as HomePageOptionBox).findViewById<LinearLayout>(R.id.ll_mid_block)
                        if ((tab.customView as HomePageOptionBox).mFlag) {
                            (tab.customView as HomePageOptionBox).endTriangleAnim()
                            (tab.customView as HomePageOptionBox).mFlag =
                                !(tab.customView as HomePageOptionBox).mFlag
                        }
                        mForestSquareCl.isClickable = false
                        mForestSquareCl.isFocusable = false
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }

    }
}