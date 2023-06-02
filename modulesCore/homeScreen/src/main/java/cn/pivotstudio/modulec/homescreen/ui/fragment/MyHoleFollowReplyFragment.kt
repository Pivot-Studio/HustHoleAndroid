package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.util.NetworkConstant
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMyholeBinding
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineRecycleViewAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.HoleFollowReplyViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_FOLLOW
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_HOLE
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_REPLY
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 *@classname MyHoleFollowReplyFragment
 * @description:
 * @date :2022/9/20 21:46
 * @version :1.0
 * @author yuruop
 */

class MyHoleFollowReplyFragment : BaseFragment() {
    private val viewModel: HoleFollowReplyViewModel by viewModels()
    private lateinit var binding: FragmentMyholeBinding
    private var type: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyholeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initRefresh()
    }

    override fun onResume() {
        if((requireActivity() as HomeScreenActivity).supportActionBar != null){
            (requireActivity() as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onResume()
    }

    private fun initData() {
        type = requireArguments().getInt("type")
        when(type) {
            GET_HOLE -> viewModel.getMyHole()
            GET_FOLLOW -> viewModel.getMyFollow()
            GET_REPLY -> viewModel.getMyReply()
        }
    }

    private fun initView() {
        val adapter = MineRecycleViewAdapter(viewModel, type)
        adapter.setOnItemClickListener(object : MineRecycleViewAdapter.OnItemClickListener {
            override fun navigateToHole(dest: String) {
                ARouter.getInstance()
                    .build("/hole/HoleActivity")
                    .withInt(
                        Constant.HOLE_ID,
                        Integer.valueOf(dest)
                    )
                    .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                    .navigation(requireActivity(), 2)
            }

            override fun getDialog(): Dialog = createDialog()

            override fun onTotalViewLongClick(hole: HoleV2) {
                val dialog = createDialog()
                val no = dialog.findViewById<View>(R.id.dialog_delete_tv_cancel) as TextView
                val yes = dialog.findViewById<View>(R.id.dialog_delete_tv_yes) as TextView
                no.setOnClickListener {
                    dialog.dismiss()
                }
                yes.setOnClickListener {
                    viewModel.deleteTheHole(hole)
                    dialog.dismiss()
                }
                dialog.show()
            }

            override fun getColor(color: Int) = requireContext().getColor(color)

            override fun getText(strId: Int): String = requireContext().getString(strId)
        })
        binding.myHoleRecyclerView.apply {
            this.adapter = adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    viewModel.view.value?.let {
                        it.visibility = View.GONE
                        viewModel.view.value = null
                    }
                }
            })
        }

        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    showMsg(it)
                    viewModel.doneShowingTip()
                }
            }

            if(type == GET_FOLLOW) {
                (parentFragment as HoleFollowReplyFragment).setModeListener(object : HoleFollowReplyFragment.ModeListener {
                    override fun changeMode() {
                        if(sortMode.value == NetworkConstant.SortMode.LATEST) {
                            viewModel.getMyFollow(NetworkConstant.SortMode.ASC)
                        }else {
                            viewModel.getMyFollow(NetworkConstant.SortMode.LATEST)
                        }
                    }
                })
            }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    //依据页面的不同提交相应的List
                    val list: StateFlow<List<*>>? = when (type) {
                        GET_HOLE -> myHole
                        GET_FOLLOW -> myFollow
                        GET_REPLY -> myReply
                        else -> null
                    }
                    list?.onEach {
                        finishRefreshAnim()
                    }?.collectLatest {
                        adapter.submitList(it)
                        if (it.isEmpty()) {
                            binding.myHoleRecyclerView.visibility = View.GONE
                            binding.minePlaceholder.visibility = View.VISIBLE
                        } else {
                            binding.myHoleRecyclerView.visibility = View.VISIBLE
                            binding.minePlaceholder.visibility = View.GONE
                        }
                    }
                }
            }
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    //对不同网络状态处理
                    loadingState.collectLatest { state ->
                        when (state) {
                            ApiStatus.SUCCESSFUL,
                            ApiStatus.ERROR -> {
                                finishRefreshAnim()
                            }
                            ApiStatus.LOADING -> {}
                        }
                    }
                }
            }
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    //展示异常时相应的视图
                    showingPlaceholder.collectLatest {
                        it?.let { placeholderType ->
                            showPlaceHolderBy(placeholderType)
                        }
                    }
                }
            }
        }
    }

    private fun createDialog(): Dialog{
        val mView = View.inflate(context, R.layout.dialog_delete, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(mView)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.notice)
        return dialog
    }

    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity))
            setRefreshFooter(StandardRefreshFooter(activity))
            setEnableLoadMore(true)
            setEnableRefresh(true)
            setOnRefreshListener {
                when (type) {
                    GET_HOLE -> viewModel.getMyHole()
                    GET_FOLLOW -> viewModel.getMyFollow()
                    GET_REPLY -> viewModel.getMyReply()
                }
                binding.myHoleRecyclerView.isEnabled = false
            }
            setOnLoadMoreListener {
                when (type) {
                    GET_HOLE -> viewModel.loadMoreHole()
                    GET_FOLLOW -> viewModel.loadMoreFollow()
                    GET_REPLY -> viewModel.loadMoreReply()
                }
                binding.myHoleRecyclerView.isEnabled = false
            }
        }
    }

    private fun finishRefreshAnim() {
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.myHoleRecyclerView.isEnabled = true
    }

    private fun showPlaceHolderBy(placeholderType: HoleFollowReplyViewModel.PlaceholderType) {
        when (placeholderType) {
            HoleFollowReplyViewModel.PlaceholderType.PLACEHOLDER_NETWORK_ERROR -> {
                binding.placeholderHomeNetError.visibility = View.VISIBLE
                binding.placeholderHomeNoContent.visibility = View.GONE
                viewModel.repository.tip.value = getString(R.string.network_loadfailure)
            }

            HoleFollowReplyViewModel.PlaceholderType.PLACEHOLDER_NO_CONTENT -> {
                binding.placeholderHomeNoContent.visibility = View.VISIBLE
                binding.placeholderHomeNetError.visibility = View.GONE
                val tv = requireView().findViewById<TextView>(R.id.tv_no_content)
                tv.text = when (type) {
                    GET_HOLE -> getString(R.string.res_no_myhole)
                    GET_FOLLOW -> getString(R.string.res_no_myfollow)
                    GET_REPLY -> getString(R.string.res_no_myreply)
                    else -> "null"
                }
            }
        }
    }
    companion object {
        const val TAG = "MyHoleFollowReplyFragment"

        @JvmStatic
        fun newInstance(args: Bundle): MyHoleFollowReplyFragment {
            val newFragment = MyHoleFollowReplyFragment()
            newFragment.arguments = args
            return newFragment
        }
    }
}

