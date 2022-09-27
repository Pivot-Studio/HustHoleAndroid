package cn.pivotstudio.modulep.hole.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.ui.fragment.BaseFragment
import cn.pivotstudio.moduleb.libbase.util.ui.EditTextUtil
import cn.pivotstudio.modulep.hole.BuildConfig
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.custom_view.refresh.StandardRefreshHeader
import cn.pivotstudio.modulep.hole.databinding.FragmentSpecificHoleBinding
import cn.pivotstudio.modulep.hole.model.MsgResponse
import cn.pivotstudio.modulep.hole.model.ReplyListResponse
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity
import cn.pivotstudio.modulep.hole.ui.adapter.EmojiRvAdapter
import cn.pivotstudio.modulep.hole.ui.adapter.RepliesAdapter
import cn.pivotstudio.modulep.hole.viewmodel.SpecificHoleViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
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
        findNavController().currentDestination?.label = args.holeId
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initRefresh()
        initEmojiRv()
        val repliesAdapter = RepliesAdapter(navToInnerReply)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = replyViewModel
            rvReplies.adapter = repliesAdapter
            rvReplies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    var lastMoreListCl =
                        (binding.rvReplies.adapter as RepliesAdapter?)!!.lastMoreListCl
                    if (lastMoreListCl != null) lastMoreListCl.visibility = View.GONE
                    lastMoreListCl = null
                }
            })

            ivOpenEmoji.setOnClickListener {
                replyViewModel.triggerEmojiPad()
            }
        }

        replyViewModel.apply {
            lifecycleScope.launchWhenStarted {
                hole.collectLatest {
                    it?.let { hole ->
                        repliesAdapter.notifyHoleChanged(hole)
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                replies.collectLatest {
                    repliesAdapter.submitList(it)
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
        }

        replyViewModel.inputText
        replyViewModel.usedEmojiList

        EditTextUtil.ButtonReaction(
            binding.etReplyPost,
            binding.btnSend
        )

    }

    private fun initRefresh() {
        binding.layoutRefresh.setRefreshHeader(StandardRefreshHeader(requireActivity())) //设置自定义刷新头
        binding.layoutRefresh.setRefreshFooter(ClassicsFooter(requireActivity())) //设置自定义刷新底
        binding.layoutRefresh.setOnRefreshListener {    //下拉刷新触发
            replyViewModel.loadHole()
            binding.rvReplies.isEnabled = false //加载时静止滑动
        }
        binding.layoutRefresh.setOnLoadMoreListener {    //上拉加载触发
            replyViewModel.loadMoreReplies()
            binding.rvReplies.isEnabled = true
        }
    }

    private fun initObserver() {
        replyViewModel.pInputText.observe(viewLifecycleOwner) { replyResponse: ReplyListResponse.ReplyResponse ->
            binding.etReplyPost.setText(replyResponse.content)
            replyViewModel.answered.set(replyResponse)
        }
        replyViewModel.pSendReply.observe((context as HoleActivity)) { msgResponse: MsgResponse ->
            binding.btnSend.isClickable = true //发送成功运行点击，以免重复发送
            showMsg(msgResponse.msg)
            binding.etReplyPost.setText("") //将输入框内容清空

            //将表情包栏关掉
            binding.rvEmoji.visibility = View.GONE
            // setVisibility(View.GONE);
            val isOpened = replyViewModel.is_emoji
            isOpened.set(false)
            replyViewModel.getListData(false) //重新刷新数据
        }
        replyViewModel.pClickMsg.observe(viewLifecycleOwner) { msgResponse: MsgResponse ->
            showMsg(msgResponse.msg)
            when (msgResponse.model) {
                "DELETE_HOLE" -> requireActivity().finish()
                "DELETE_REPLY" -> replyViewModel.getListData(false)
            }
        }
        replyViewModel.failed.observe(viewLifecycleOwner) { s: String? ->
            binding.btnSend.isClickable = true
            showMsg(s)
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
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
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
            replyViewModel.sendReply(binding.etReplyPost.text.toString())
            (requireActivity() as HoleActivity).closeKeyBoard()
        } else if (view.id == R.id.btn_replylist_owner) {
            val observableField = replyViewModel.is_owner
            observableField.set(!observableField.get()!!)
            replyViewModel.getListData(false)
        } else if (view.id == R.id.cl_titlebargreen_back) {
            if (BuildConfig.isRelease) {
                requireActivity().finish() //关闭活动
                (requireActivity() as HoleActivity).closeKeyBoard() //关闭键盘
            } else {
                showMsg("当前处于模块测试阶段")
            }
        } else if (view.id == R.id.iv_open_emoji) {
            val isOpened = replyViewModel.is_emoji
            if (!isOpened.get()!!) {
                (requireActivity() as HoleActivity).closeKeyBoard()
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                (binding.rvEmoji.adapter as EmojiRvAdapter).refreshData()
                binding.rvEmoji.visibility = View.VISIBLE
                isOpened.set(true)
            } else {
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                binding.rvEmoji.visibility = View.GONE
                // setVisibility(View.GONE);
                isOpened.set(false)
            }
        } else if (view.id == R.id.et_reply_post) {
            val isOpened = replyViewModel.is_emoji
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            binding.rvEmoji.visibility = View.VISIBLE
            (requireActivity() as HoleActivity).openKeyBoard(binding.etReplyPost)
            isOpened.set(false)
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