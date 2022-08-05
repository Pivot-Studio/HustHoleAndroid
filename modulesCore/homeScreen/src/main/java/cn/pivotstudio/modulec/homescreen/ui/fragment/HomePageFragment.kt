package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentHomepageBinding
import cn.pivotstudio.modulec.homescreen.network.HomepageHoleResponse
import cn.pivotstudio.modulec.homescreen.ui.adapter.HoleRecyclerViewAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel
import cn.pivotstudio.moduleb.libbase.base.model.HoleReturnInfo
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.constant.RequestCodeConstant
import cn.pivotstudio.moduleb.libbase.constant.ResultCodeConstant
import cn.pivotstudio.moduleb.libbase.util.data.CheckStrUtil
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * @classname:HomePageFragment
 * @description:
 * @date:2022/5/2 22:56
 * @version:1.0
 * @author:
 */
class HomePageFragment : BaseFragment() {
    private lateinit var binding: FragmentHomepageBinding
    private val mViewModel: HomePageViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false)
        //
        mViewModel.refreshHoleList(0) //初次加载
        initView()
        initRefresh()
        initObserver()
        return binding?.root
    }

    /**
     * 获取点赞，关注，回复结果反馈的，fragment的onActivityResult在androidx的某个版本不推荐使用了，先暂时用着
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RequestCodeConstant.HOMEPAGE) { //如果不是由homepage这个fragment跳转过去的，返回的结果不接受
            return
        }
        if (resultCode == ResultCodeConstant.Hole) { //如果是由hole返回的数据
            val returnInfo = data!!.getParcelableExtra<HoleReturnInfo>(
                Constant.HOLE_RETURN_INFO)
            mViewModel.pClickDataBean?.is_thumbup = returnInfo!!.is_thumbup
            mViewModel.pClickDataBean?.is_reply = returnInfo.is_reply
            mViewModel.pClickDataBean?.is_follow = returnInfo.is_follow
            mViewModel.pClickDataBean?.thumbup_num = returnInfo.thumbup_num
            mViewModel.pClickDataBean?.reply_num = returnInfo.reply_num
            mViewModel.pClickDataBean?.follow_num = returnInfo.follow_num
        }
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
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val holeRecyclerViewAdapter = HoleRecyclerViewAdapter(mViewModel, context)
        binding.recyclerView.adapter = holeRecyclerViewAdapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                var lastMoreListCl =
                    (binding.recyclerView.adapter as HoleRecyclerViewAdapter?)!!.lastMoreListCl
                if (lastMoreListCl != null) lastMoreListCl.visibility = View.GONE
                lastMoreListCl = null
            }
        })
        binding.clMidBlock.setOptionsListener { v: View -> onClick(v) }
        binding.etHomepage.setOnClickListener { v: View -> onClick(v) }
        binding.etHomepage.imeOptions = EditorInfo.IME_ACTION_SEARCH
        binding.etHomepage.setOnEditorActionListener { v: TextView, actionId: Int, event: KeyEvent? ->
            onEditorListener(
                v,
                actionId,
                event
            )
        }
    }

    /**
     * 初始化刷新框架
     */
    private fun initRefresh() {
        binding.refreshLayout.setRefreshHeader(
            StandardRefreshHeader(
                activity
            )
        ) //设置自定义刷新头
        binding.refreshLayout.setRefreshFooter(
            StandardRefreshFooter(
                activity
            )
        ) //设置自定义刷新底
        binding.refreshLayout.setOnRefreshListener { refreshlayout: RefreshLayout? ->  //下拉刷新触发
            mViewModel.refreshHoleList(0)
            binding.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> true }
        }
        binding!!.refreshLayout.setOnLoadMoreListener { refreshlayout: RefreshLayout? ->  //上拉加载触发
            if (mViewModel!!.pHomePageHoles.value == null) { //特殊情况，首次加载没加载出来又选择上拉加载
                mViewModel!!.refreshHoleList(0)
                binding!!.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> true }
            } else {
                binding!!.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> true }
                if (mViewModel!!.isSearch == true) { //如果当前是搜索状态
                    mViewModel!!.searchHoleList(mViewModel!!.startLoadId?.plus(20) ?: 0)
                } else {
                    mViewModel!!.refreshHoleList(mViewModel!!.startLoadId?.plus(20) ?: 0)
                }
            }
        }
    }

    /**
     * 初始化ViewModel数据观察者
     */
    private fun initObserver() {
        mViewModel.pHomePageHoles.observe(viewLifecycleOwner) { homepageHoleResponse: HomepageHoleResponse ->  //监听列表信息变化
            val length = homepageHoleResponse.data.size
            when (homepageHoleResponse.model) {
                "REFRESH" -> finishRefresh(true)
                "SEARCH_REFRESH" -> finishRefresh(false)
                "LOAD_MORE" -> finishLoadMore(length)
                "SEARCH_LOAD_MORE" -> finishLoadMore(length)
                "SEARCH_HOLE" -> finishRefresh(false)
                "BASE" -> {}
            }
            finishRefreshAnim()
        }
        lifecycleScope.launchWhenStarted {
            mViewModel!!.loadToastMsg.collect {
                showMsg(it)
            }
        }
    }

    /**
     * 点击事件监听
     *
     * @param v
     */
    private fun onClick(v: View) {
        val id = v.id
        if (id == R.id.btn_ppwhomepage_newpublish) {
            if (!mViewModel!!.isDescend!!) {
                mViewModel!!.isDescend = true
                mViewModel!!.refreshHoleList(0)
            }
        } else if (id == R.id.btn_ppwhomepage_newcomment) {
            if (mViewModel!!.isDescend == true) {
                mViewModel!!.isDescend = false
                mViewModel!!.refreshHoleList(0)
            }
        } else if (id == R.id.et_homepage) {
        }
    }

    /**
     * 键盘搜索监听
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    private fun onEditorListener(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
        if ((binding!!.etHomepage.text != null
                    && binding!!.etHomepage.text.toString() != "") && (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)
        ) {
            val et = binding!!.etHomepage.text.toString()
            mViewModel!!.searchKeyword = et
            mViewModel!!.isSearch = true
            if (CheckStrUtil.checkStrIsNum(et) || et[0] == '#') {
                mViewModel!!.searchSingleHole()
            } else {
                mViewModel!!.searchHoleList(0)
            }
        }
        return false
    }

    /**
     * 下拉刷新或搜索的数据更新
     *
     * @param isSearch 决定是否是搜索状态下，非搜索状态下下拉加载需要将状态切换
     */
    private fun finishRefresh(isSearch: Boolean) {
        if (isSearch) mViewModel!!.isSearch = false
        mViewModel!!.startLoadId = 0
    }

    /**
     * 上拉加载的数据更新
     */
    private fun finishLoadMore(length: Int) {
        val lastStartId = mViewModel!!.startLoadId
        if (lastStartId != null) {
            mViewModel!!.startLoadId = lastStartId + length
        }
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        binding!!.etHomepage.setText("")
        binding!!.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding!!.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding!!.recyclerView.setOnTouchListener { v: View?, event: MotionEvent? -> false } //加载结束后允许滑动
    }

    companion object {
        const val TAG = "HomePageFragment"
        fun newInstance(): HomePageFragment {
            return HomePageFragment()
        }
    }
}