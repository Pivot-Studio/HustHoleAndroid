package cn.pivotstudio.modulec.homescreen.ui.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMyholeBinding
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshFooter
import cn.pivotstudio.modulec.homescreen.oldversion.model.StandardRefreshHeader
import cn.pivotstudio.modulec.homescreen.ui.adapter.MineRecycleViewAdapter
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_FOLLOW
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_HOLE
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_REPLY

/**
 *@classname MyHoleFollowReplyFragment
 * @description:
 * @date :2022/9/20 21:46
 * @version :1.0
 * @author
 */

class MyHoleFollowReplyFragment(val type: Int) : Fragment() {
    private val viewModel: MyHoleFragmentViewModel by viewModels()
    private lateinit var binding: FragmentMyholeBinding

//    private var isRefresh: Boolean = false     //表示是否处于刷新状态
//    private var isFinishRefresh: Boolean = false  //表示是否完成刷新
//    private var isOnLoadMore: Boolean = false    //表示是否处于加载更多状态
//    private var isFinishOnLoadMore: Boolean = false    //表示是否加载完成

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
        val adapter = MineRecycleViewAdapter(type, viewModel, context, binding)
        when (type) {
            GET_HOLE -> viewModel.myHolesList.observe(viewLifecycleOwner) { list ->
                adapter.submitList(
                    list
                )
            }
            GET_FOLLOW -> viewModel.myFollowList.observe(viewLifecycleOwner) { list ->
                adapter.submitList(
                    list
                )
            }
            GET_REPLY -> viewModel.myReplyList.observe(viewLifecycleOwner) { list ->
                adapter.submitList(
                    list
                )
            }
        }
        binding.myHoleRecyclerView.apply {
            this.adapter = adapter
            addItemDecoration(SpaceItemDecoration(0, 20))
        }

        initRefresh()
    }

    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(StandardRefreshHeader(activity))
            setRefreshFooter(StandardRefreshFooter(activity))
            setEnableLoadMore(true)
            setEnableRefresh(true)
            setOnRefreshListener {
                when (type) {
                    GET_HOLE -> {
                        viewModel.initMyHoleRefresh()
                        viewModel.getMyHoleList()
                    }
                    GET_FOLLOW -> {
                        viewModel.initMyFollowRefresh()
                        viewModel.getMyFollowList()
                    }
                    GET_REPLY -> {
                        viewModel.initMyReplyRefresh()
                        viewModel.getMyReplyList()
                    }
                }
                finishRefresh()
            }
            setOnLoadMoreListener {
                when (type) {
                    GET_HOLE -> viewModel.getMyHoleList()
                    GET_FOLLOW -> viewModel.getMyFollowList()
                    GET_REPLY -> viewModel.getMyReplyList()
                }
                finishLoadMore()
            }
        }
    }

    /**
     * @description:自定义设置item间距
     */
    class SpaceItemDecoration(
        private val leftRight: Int,
        private val topBottom: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
            if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                //最后一项需要 bottom
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.bottom = topBottom;
                }
                outRect.top = topBottom;
                outRect.left = leftRight;
                outRect.right = leftRight;
            } else {
                //最后一项需要right
                if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                    outRect.right = leftRight;
                }
                outRect.top = topBottom;
                outRect.left = leftRight;
                outRect.bottom = topBottom;
            }
        }
    }

    companion object {
        const val TAG = "MyHoleFollowReplyFragment"
        @JvmStatic
        fun newInstance(type: Int): MyHoleFollowReplyFragment {
            return MyHoleFollowReplyFragment(type)
        }
    }
}

