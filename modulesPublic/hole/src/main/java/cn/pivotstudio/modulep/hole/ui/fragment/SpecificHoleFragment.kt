package cn.pivotstudio.modulep.hole.ui.fragment

import android.R.attr.startY
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
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
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.moduleb.libbase.util.ui.SoftKeyBoardUtil
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshFooter
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulep.hole.databinding.FragmentSpecificHoleBinding
import cn.pivotstudio.modulep.hole.model.ReplyListResponse
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity
import cn.pivotstudio.modulep.hole.ui.adapter.EmojiRvAdapter
import cn.pivotstudio.modulep.hole.ui.adapter.RepliesAdapter
import cn.pivotstudio.modulep.hole.viewmodel.HoleViewModel
import cn.pivotstudio.modulep.hole.viewmodel.SpecificHoleViewModel
import com.alibaba.android.arouter.launcher.ARouter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking


class SpecificHoleFragment : BaseFragment() {

    private val args by navArgs<SpecificHoleFragmentArgs>()

    private lateinit var binding: FragmentSpecificHoleBinding
    private lateinit var mActionBar: ActionBar
     val replyViewModel: SpecificHoleViewModel by viewModels {
        SpecificHoleViewModelFactory(args.holeId, end)
    }
    private val sharedViewModel: HoleViewModel by activityViewModels()

    private val navToInnerReply: (ReplyWrapper) -> Unit = {
        val action =
            SpecificHoleFragmentDirections.actionSpecificHoleFragmentToInnerReplyFragment(it.self)
        findNavController().navigate(action)
    }

    private val report: (Reply?) -> Unit = {
        ARouter.getInstance().build("/report/ReportActivity")
            .withString(Constant.HOLE_ID, it?.holeId)
            .withString(Constant.REPLY_ID, it?.replyId)
            .withString(Constant.ALIAS, it?.nickname)
            .navigation()
    }

    private val end: () -> Unit = {
        this.findNavController().popBackStack()
    }

    private val repliesAdapter by lazy { RepliesAdapter(replyViewModel, report, navToInnerReply) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActionBar = (requireActivity() as HoleActivity).supportActionBar!!
        mActionBar.title = '#' + args.holeId
        sharedViewModel.fragmentStack.push(this)
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
        binding.apply {
            layoutHole.type = "HoleToHole"
            lifecycleOwner = viewLifecycleOwner
            viewModel = replyViewModel
            rvReplies.apply {
                this.adapter = repliesAdapter
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                            SoftKeyBoardUtil.hideKeyboard(requireActivity())
                            mActionBar.hide()
                        }
                        else
                            mActionBar.show()
                    }
                })
            }
            layoutHole.apply {
                layoutHoleFrame.setOnClickListener {
                    replyViewModel.replyToOwner()
                }
                tvHoleContent.setOnClickListener {
                    replyViewModel.replyToOwner()
                }
                tvHoleContent.setOnLongClickListener {
                    copyToClipboard(tvHoleContent)
                    true
                }

                clHoleThumb.setOnClickListener {
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
                    }else {
                        replyViewModel.reportTheHole()
                    }
                    it.visibility = View.GONE
                }
            }

            ivOpenEmoji.setOnClickListener {
                replyViewModel.triggerEmojiPad()
            }

            btnSend.setOnClickListener {
                replyViewModel.sendAComment("${binding.etReplyPost.text}")
                SoftKeyBoardUtil.hideKeyboard(requireActivity())
            }

            etReplyPost.setOnClickListener {
                SoftKeyBoardUtil.showKeyboard(requireActivity(), etReplyPost)
                replyViewModel.doneShowingEmojiPad()
            }
        }

        replyViewModel.inputText()
        sharedViewModel.usedEmojiList()

        EditTextUtil.ButtonReaction(
            binding.etReplyPost,
            binding.btnSend
        )

        initObserver()
        initRefresh()
        initEmojiRv()
        initListener()

        if (args.openingKeyboard) {
            SoftKeyBoardUtil.showKeyboard(requireActivity(), binding.etReplyPost)
        }
    }


    override fun onResume() {
        super.onResume()
        mActionBar.title = '#' + args.holeId
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedViewModel.fragmentStack.pop()
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
        }

        replyViewModel.apply {
            lifecycleScope.launchWhenStarted {
                hole.collectLatest {
                    it?.let { hole ->
                        binding.layoutHole.holeV2 = hole
                        savaDate(hole)
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
                        SoftKeyBoardUtil.hideKeyboard(requireActivity())
                        (binding.rvEmoji.adapter as EmojiRvAdapter).refreshData()
                        binding.rvEmoji.visibility = View.VISIBLE
                    } else {
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
                            showMsg(getString(R.string.hole_sending_successfully))
                            clearSendingState()
                        }
                    }
                }
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

    private fun initListener() {

        binding.apply {
            etReplyPost.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    if (s.length >= 500) {
                        Toast.makeText(context, "评论不得超过500个字噢~", Toast.LENGTH_SHORT).show()
                    }
                }
            })

            replyNestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY + 12) {
                    btnFilterOwnerReply.hide()
                }

                if (scrollY < oldScrollY - 12) {
                    btnFilterOwnerReply.show()
                }

                if (scrollY == 0) {
                    btnFilterOwnerReply.show()
                }
            }

            rvReplies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    binding.layoutHole.clHoleMoreAction.visibility = View.GONE
                    repliesAdapter.lastMoreListCl?.let {
                        if (it.isVisible) {
                            it.visibility = View.GONE
                        }
                    }
                }
            })
        }

    }

    /**
     * 刷新结束后动画的流程
     */
    private fun finishRefreshAnim() {
        binding.layoutRefresh.finishRefresh() //结束下拉刷新动画
        binding.layoutRefresh.finishLoadMore() //结束上拉加载动画
        binding.rvReplies.isEnabled = true
    }

    private fun clearSendingState() {
        binding.apply {
            btnSend.isClickable = true
            etReplyPost.text?.clear()
            rvEmoji.visibility = View.GONE

            lifecycleScope.launchWhenStarted {
                replyViewModel.doneShowingEmojiPad()
                replyViewModel.delayLoadReplies()
            }
        }
    }

    private fun savaDate(hole: HoleV2) {
        (requireActivity() as HoleActivity).saveResultData(hole)
    }

    private fun copyToClipboard(view: TextView) {
        val cm =
            BaseApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(
            ClipData.newPlainText(
                null,
                view.text.toString()
            )
        )
        if (cm.hasPrimaryClip()) {
            cm.primaryClip?.getItemAt(0)?.text
        }
        Toast.makeText(context, "已将内容复制到剪切板！", Toast.LENGTH_SHORT).show()
    }
}

class SpecificHoleViewModelFactory(
    private val holeId: String,
    private val finish: () -> Unit
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpecificHoleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpecificHoleViewModel(holeId, finish) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}