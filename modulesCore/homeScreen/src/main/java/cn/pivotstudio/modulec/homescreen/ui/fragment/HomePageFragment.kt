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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.HomePageOptionBox
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding
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
    private var listener: EditActionListener? = null
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

    @Deprecated("Deprecated in Java")
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
            etHomepage.setOnClickListener {}
            etHomepage.imeOptions = EditorInfo.IME_ACTION_SEARCH

            vpHomescreenHole.adapter = object : FragmentStateAdapter(this@HomePageFragment) {
                override fun getItemCount(): Int = 3

                override fun createFragment(position: Int): Fragment =
                    HomeHoleFragment.newInstance(position + 1)
            }
            TabLayoutMediator(tbMode, vpHomescreenHole) { tab, position ->
                val tabView = if (position != 0) {
                    LayoutInflater.from(context).inflate(R.layout.tab_mine, tbMode, false)
                } else {
                    HomePageOptionBox(context, tbMode)
                }
                val tv = tabView.findViewById(R.id.title) as TextView
                tv.text = requireContext().getString(titleList[position])
                tab.customView = tabView
            }.attach()
            binding.vpHomescreenHole.post {
                binding.vpHomescreenHole.setCurrentItem(0, true)
            }
            tbMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab?.customView is HomePageOptionBox) {
                        val mForestSquareCl =
                            (tab.customView as HomePageOptionBox).findViewById<LinearLayout>(R.id.ll_mid_block)
                        //恢复上层（子）视图点击事件
                        mForestSquareCl.isClickable = true
                        mForestSquareCl.isFocusable = true
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    if (tab?.customView is HomePageOptionBox) {
                        val view = tab.customView as HomePageOptionBox
                        val mForestSquareCl: LinearLayout =
                            view.findViewById(R.id.ll_mid_block)
                        //如果ppw是打开的，跳转其他tab会收起ppw
                        if (view.mFlag) {
                            view.endTriangleAnim()
                            view.mFlag = !view.mFlag
                        }
                        //解决按键事件冲突
                        mForestSquareCl.isClickable = false
                        mForestSquareCl.isFocusable = false
                    }
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })

            etHomepage.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
                //在其他tab点击搜索时会回到树洞列表tab里面
                if (tbMode.selectedTabPosition != 0) {
                    tbMode.selectTab(tbMode.getTabAt(0), true)
                }
                // 这里用接口回调的原因是因为viewPager内的Fragment可能会被销毁重建
                // 使用接口可以保证新的Fragment实例实现绑定了点击事件，避免点击时新的Fragment没有设置处理点击事件而无反应或者闪退
                if (tbMode.selectedTabPosition == 0) {
                    if (!v.text.isNullOrEmpty() && (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)) {
                        listener!!.onSend(v.text.toString())
                    }
                }
                false
            }
        }
    }

    fun setEditActionListener(listener: EditActionListener) {
        this.listener = listener
    }

    interface EditActionListener {
        fun onSend(text: String)
    }
}