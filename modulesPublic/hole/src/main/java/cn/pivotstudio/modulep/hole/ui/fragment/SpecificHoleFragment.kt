package cn.pivotstudio.modulep.hole.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.ApiResult
import cn.pivotstudio.husthole.moduleb.network.ApiStatus
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulep.hole.databinding.FragmentSpecificHoleBinding
import cn.pivotstudio.modulep.hole.model.MsgResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity
import cn.pivotstudio.modulep.hole.ui.adapter.EmojiRvAdapter
import cn.pivotstudio.modulep.hole.ui.adapter.RepliesAdapter
import cn.pivotstudio.modulep.hole.viewmodel.SpecificHoleViewModel
import kotlinx.coroutines.flow.collectLatest

class SpecificHoleFragment : BaseFragment() {

    private val args by navArgs<SpecificHoleFragmentArgs>()

    private lateinit var binding: FragmentSpecificHoleBinding
    private val replyViewModel: SpecificHoleViewModel by viewModels {
        SpecificHoleViewModelFactory(args.holeId)
    }

    private val navToInnerReply: (ReplyWrapper) -> Unit = {
        val action =
            SpecificHoleFragmentDirections.actionSpecificHoleFragmentToInnerReplyFragment(it.self)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_specific_hole, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initRefresh()
        initEmojiRv()
        initListener()
        val repliesAdapter = RepliesAdapter(replyViewModel, navToInnerReply)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = replyViewModel
            rvReplies.adapter = repliesAdapter
            rvReplies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    repliesAdapter.lastMoreListCl?.let {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                    }
                }
            })

            layoutHole.apply {
                layoutHoleFrame.setOnClickListener {
                    replyViewModel.replyToOwner()
                }

                clHoleThumbup.setOnClickListener {
                    replyViewModel.giveALikeToTheHole()
                }

                clHoleFollow.setOnClickListener {
                    replyViewModel.followTheHole()
                }

                ivHoleMore.setOnClickListener {
                    clHoleMoreAction.visibility = View.VISIBLE
                }

                clHoleMoreAction.setOnClickListener {
                    if (replyViewModel.hole.value?.isMyHole == true) {
                        replyViewModel.deleteTheHole()
                    }
                    it.visibility = View.GONE
                }
            }

            ivOpenEmoji.setOnClickListener {
                replyViewModel.triggerEmojiPad()
            }

            btnSend.setOnClickListener {
                replyViewModel.sendAComment("${binding.etReplyPost.text}")
                (requireActivity() as HoleActivity).closeKeyBoard()
            }

            etReplyPost.setOnClickListener {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                binding.rvEmoji.visibility = View.VISIBLE
                (requireActivity() as HoleActivity).openKeyBoard(binding.etReplyPost)
                replyViewModel.doneShowingEmojiPad()
            }

            btnFilterOwnerReply.setOnClickListener {
                replyViewModel.filterReplyOfHoleOwner()
            }

        }

        replyViewModel.apply {
            lifecycleScope.launchWhenStarted {
                hole.collectLatest {
                    it?.let { hole ->
                        binding.layoutHole.holeV2 = hole
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                replies.collect {
                    repliesAdapter.submitList(it)
                }
            }

            lifecycleScope.launchWhenStarted {
                loadingState.collectLatest { state ->
                    when (state) {
                        ApiStatus.SUCCESSFUL,
                        ApiStatus.ERROR -> finishRefreshAnim()
                        ApiStatus.LOADING -> {}
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                showEmojiPad.collectLatest { showingEmojiPad ->
                    if (showingEmojiPad) {
                        (requireActivity() as HoleActivity).closeKeyBoard()
                        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                        (binding.rvEmoji.adapter as EmojiRvAdapter).refreshData()
                        binding.rvEmoji.visibility = View.VISIBLE
                    } else {
                        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        binding.rvEmoji.visibility = View.GONE
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                sendingState.collectLatest { state ->
                    when (state) {
                        is ApiResult.Loading -> {
                            binding.btnSend.isClickable = false
                        }
                        is ApiResult.Error -> {
                            showMsg(state.errorMessage)
                        }
                        is ApiResult.Success<*> -> {
                            clearSendingState()
                        }
                    }
                }
            }
        }

        replyViewModel.inputText()
        replyViewModel.usedEmojiList()

        EditTextUtil.ButtonReaction(
            binding.etReplyPost,
            binding.btnSend
        )

    }

    private fun initRefresh() {
        binding.layoutRefresh.apply {
            setRefreshHeader(StandardRefreshHeader(activity))
            setRefreshFooter(StandardRefreshFooter(activity))
            setOnRefreshListener {
                replyViewModel.loadHole()
                binding.rvReplies.isEnabled = false
            }
            setOnLoadMoreListener {    //上拉加载触发
                replyViewModel.loadMoreReplies()
                binding.rvReplies.isEnabled = true
            }
        }
    }

    private fun initObserver() {
        replyViewModel.pInputText.observe(viewLifecycleOwner) { replyResponse: ReplyListResponse.ReplyResponse ->
            binding.etReplyPost.setText(replyResponse.content)
            replyViewModel.answered.set(replyResponse)
        }

        replyViewModel.pClickMsg.observe(viewLifecycleOwner) { msgResponse: MsgResponse ->
            showMsg(msgResponse.msg)
            when (msgResponse.model) {
                "DELETE_HOLE" -> requireActivity().finish()
//                "DELETE_REPLY" -> replyViewModel.getListData(false)
            }
        }

    }

    private fun initEmojiRv() {
        val layoutManager = GridLayoutManager(
            requireActivity(),
            6,
            RecyclerView.VERTICAL,
            false
        )
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0 || position == 7) {
                    6
                } else {
                    1
                }
            }
        }
        binding.rvEmoji.layoutManager = layoutManager
        binding.rvEmoji.adapter =
            EmojiRvAdapter(
                context,
                binding,
                replyViewModel
            )
    }

    private fun initListener() {
        binding.etReplyPost.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.length >= 500) {
                    Toast.makeText(context, "评论不得超过500个字噢~", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        binding.layoutRefresh.finishRefresh() //结束下拉刷新动画
        binding.layoutRefresh.finishLoadMore() //结束上拉加载动画
        binding.rvReplies.isEnabled = true
    }

    /**
     * 处理activity点击事件
     *
     * @param view
     */
    fun onClick(view: View) {
        if (view.id == R.id.btn_send) {
            binding.btnSend.isClickable = false //避免重复发送
//            replyViewModel.sendReply(binding.etReplyPost.text.toString())
            (requireActivity() as HoleActivity).closeKeyBoard()
        } else if (view.id == R.id.btn_filter_owner_reply) {
            val observableField = replyViewModel.is_owner
            observableField.set(!observableField.get()!!)
//            replyViewModel.getListData(false)
        }
    }

    private fun clearSendingState() {
        binding.apply {
            btnSend.isClickable = true
            etReplyPost.text?.clear()
            rvEmoji.visibility = View.GONE
            replyViewModel.doneShowingEmojiPad()
            lifecycleScope.launchWhenStarted {
                replyViewModel.delayLoadReplies()
            }

        }
    }


}

class SpecificHoleViewModelFactory(private val holeId: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpecificHoleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpecificHoleViewModel(holeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}