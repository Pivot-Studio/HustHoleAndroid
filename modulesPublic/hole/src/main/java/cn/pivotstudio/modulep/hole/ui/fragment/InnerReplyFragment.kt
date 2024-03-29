package cn.pivotstudio.modulep.hole.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.rebase.network.ApiResult
import cn.pivotstudio.moduleb.rebase.network.ApiStatus
import cn.pivotstudio.moduleb.rebase.network.model.Reply
import cn.pivotstudio.moduleb.rebase.lib.base.app.BaseApplication
import cn.pivotstudio.moduleb.rebase.lib.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.rebase.lib.constant.Constant
import cn.pivotstudio.moduleb.rebase.lib.util.ui.EditTextUtil
import cn.pivotstudio.moduleb.rebase.lib.util.ui.SoftKeyBoardUtil
import cn.pivotstudio.modulep.hole.ui.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulep.hole.ui.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulep.hole.databinding.FragmentInnerReplyBinding
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity
import cn.pivotstudio.modulep.hole.ui.adapter.EmojiRvAdapter
import cn.pivotstudio.modulep.hole.ui.adapter.InnerReplyAdapter
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel
import cn.pivotstudio.modulep.hole.viewmodel.InnerReplyViewModel
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import cn.pivotstudio.modulep.hole.R


class InnerReplyFragment : BaseFragment() {

    private val args by navArgs<InnerReplyFragmentArgs>()
    private lateinit var binding: FragmentInnerReplyBinding
    private lateinit var mActionBar: ActionBar
    private val innerReplyViewModel: InnerReplyViewModel by viewModels {
        InnerReplyViewModelFactory(args.reply)
    }
    private var stackPosition: Int = 0

    private val baseReply: Reply
        get() = innerReplyViewModel.reply.value

    private val report: (Reply) -> Unit = {
        ARouter.getInstance().build("/hole/ReportActivity")
            .withString(Constant.HOLE_ID, it.holeId)
            .withString(Constant.REPLY_ID, it.replyId)
            .withString(Constant.ALIAS, it.nickname)
            .navigation()
    }

    private val innerReplyAdapter by lazy { InnerReplyAdapter(innerReplyViewModel, report) }


    // 二级评论页的点赞可以直接通过ViewModel反馈给一级评论页
    private val sharedViewModel: HoleViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActionBar = (requireActivity() as HoleActivity).supportActionBar!!
        sharedViewModel.fragmentStack.push(this)
        stackPosition = sharedViewModel.fragmentStack.size
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_inner_reply, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEmojiRv()
        initListener()
        initObserver()
        initRefresh()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = innerReplyViewModel
        binding.apply {
            type = "ReplyToHole"
            rvReplies.adapter = innerReplyAdapter
            rvReplies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        mActionBar.hide()
                        SoftKeyBoardUtil.hideKeyboard(requireActivity())
                        innerReplyAdapter.lastMoreListCl?.let {
                            if (it.isVisible) {
                                it.visibility = View.GONE
                            }
                        }
                    }
                    else
                        mActionBar.show()
                }
            })

            etReplyPost.setOnClickListener {
                innerReplyViewModel.doneShowingEmojiPad()
            }

            btnSend.setOnClickListener {
                innerReplyViewModel.sendAInnerComment("${binding.etReplyPost.text}")
                SoftKeyBoardUtil.hideKeyboard(requireActivity())
            }

            layoutReplyFrame.setOnClickListener {
                innerReplyViewModel.replyToOwner()
            }
            tvHoleContent.setOnClickListener {
                innerReplyViewModel.replyToOwner()
            }

            tvHoleContent.setOnLongClickListener {
                val cm =
                    BaseApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(
                    ClipData.newPlainText(
                        null,
                        tvHoleContent.text.toString()
                    )
                )
                if (cm.hasPrimaryClip()) {
                    cm.primaryClip?.getItemAt(0)?.text
                }
                Toast.makeText(it.context, "已将内容复制到剪切板！", Toast.LENGTH_SHORT).show()
                true
            }

            ivBaseReplyMore.setOnClickListener {
                clHoleMoreAction.visibility = View.VISIBLE
            }

            clHoleMoreAction.setOnClickListener {
                if (innerReplyViewModel.reply.value.mine) {
                    innerReplyViewModel.deleteTheReply(innerReplyViewModel.reply.value)
                } else {
                    report(innerReplyViewModel.reply.value)
                }
                it.visibility = View.GONE
            }

            ivOpenEmoji.setOnClickListener {
                innerReplyViewModel.triggerEmojiPad()
            }

            clHoleThumb.setOnClickListener {
                innerReplyViewModel.giveALikeTo(baseReply)
            }
        }

        EditTextUtil.ButtonReaction(
            binding.etReplyPost,
            binding.btnSend
        )

    }

    override fun onResume() {
        super.onResume()
        mActionBar.title = '#' + args.reply.holeId
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.fragmentStack.pop()
    }
    private fun initObserver() {
        innerReplyViewModel.apply {
            tip.observe(viewLifecycleOwner, Observer<String?> {
                it?.let {
                    showMsg(it)
                    doneShowingTip()
                }
            })
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    reply.collectLatest {
                        sharedViewModel.fragmentStack.apply {
                            (this[stackPosition - 2] as SpecificHoleFragment).replyViewModel.refreshTheReply(it)
                        }
                    }
                }
            }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    innerReplies.collectLatest {
                        innerReplyAdapter.submitList(it)
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                showEmojiPad.collectLatest { showingEmojiPad ->
                    if (showingEmojiPad) {
                        SoftKeyBoardUtil.hideKeyboard(requireActivity())
                        (binding.rvEmoji.adapter as EmojiRvAdapter).refreshData()
                        binding.rvEmoji.visibility = View.VISIBLE
                    } else {
                        binding.rvEmoji.visibility = View.GONE
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                loadingState.collectLatest { state ->
                    when (state) {
                        ApiStatus.SUCCESSFUL -> {
                            finishRefreshAnim()
                        }
                        ApiStatus.ERROR -> finishRefreshAnim()
                        ApiStatus.LOADING -> {}
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
                            showMsg(getString(R.string.hole_sending_successfully))
                            clearSendingState()
                        }
                    }
                }
            }
        }
    }

    private fun clearSendingState() {
        binding.apply {
            btnSend.isClickable = true
            etReplyPost.text?.clear()
            rvEmoji.visibility = View.GONE
            lifecycleScope.launchWhenStarted {
                innerReplyViewModel.doneShowingEmojiPad()
                innerReplyViewModel.delayLoadReplies()
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
                binding.etReplyPost,
                sharedViewModel
            )
    }

    private fun initRefresh() {
        binding.layoutRefresh.apply {
            setRefreshHeader(StandardRefreshHeader(activity))
            setRefreshFooter(StandardRefreshFooter(activity))
            setOnRefreshListener {
                innerReplyViewModel.loadSecondLvReplies()
                binding.rvReplies.isEnabled = false
            }
            setOnLoadMoreListener {    //上拉加载触发
                innerReplyViewModel.loadMoreReplies()
                binding.rvReplies.isEnabled = true
            }
        }
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

    private fun finishRefreshAnim() {
        binding.layoutRefresh.finishRefresh() //结束下拉刷新动画
        binding.layoutRefresh.finishLoadMore() //结束上拉加载动画
        binding.rvReplies.isEnabled = true
    }
}


class InnerReplyViewModelFactory(private val reply: Reply) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InnerReplyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InnerReplyViewModel(reply) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}