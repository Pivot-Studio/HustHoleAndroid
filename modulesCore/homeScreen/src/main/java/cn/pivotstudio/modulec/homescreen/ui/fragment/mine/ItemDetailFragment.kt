package cn.pivotstudio.modulec.homescreen.ui.fragment.mine

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.*
import cn.pivotstudio.modulec.homescreen.oldversion.model.CheckingToken
import cn.pivotstudio.modulec.homescreen.oldversion.model.EditTextReaction
import cn.pivotstudio.modulec.homescreen.oldversion.mypage.UpdateRecycleViewAdapter
import cn.pivotstudio.modulec.homescreen.ui.activity.HomeScreenActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.ABOUT
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.COMMUNITY_NORM
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.EVALUATION_AND_SUGGESTIONS
import cn.pivotstudio.modulec.homescreen.viewmodel.MineFragmentViewModel.Companion.SHARE
import com.google.android.material.tabs.TabLayoutMediator

/**
 *@classname ItemDetailFragment
 * @description:
 * @date :2022/9/26 17:51
 * @version :1.0
 * @author
 */
class ItemDetailFragment : Fragment() {
    private lateinit var binding: ViewDataBinding
    private val viewModel: MineFragmentViewModel by viewModels()
    private var option: Int = 0
    private var isVerifiedEmail: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
        super.onCreate(savedInstanceState)
        arguments?.let {
            option = it.getInt("fragType")
            isVerifiedEmail = it.getBoolean("isVerified")
        }
    }

    override fun onResume() {
        super.onResume()
        if ((activity as HomeScreenActivity).supportActionBar != null) {
            (activity as HomeScreenActivity).supportActionBar!!.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //依据内容绑定不同的视图
        when (option) {
            COMMUNITY_NORM -> {
                binding = ActivityRulesBinding.inflate(inflater, container, false)
            }
            SHARE -> {
                binding = ActivityShareCardBinding.inflate(inflater, container, false)
            }
            EVALUATION_AND_SUGGESTIONS -> {
                binding = ActivityHoleStarBinding.inflate(inflater, container, false)
            }
            ABOUT -> {
                binding = ActivityAboutBinding.inflate(inflater, container, false)
            }
            R.string.update_log -> {
                binding = ActivityUpdateBinding.inflate(inflater, container, false)
            }
            R.string.campus_email -> {
                if (isVerifiedEmail) {
                    binding = ActivityEmailOkBinding.inflate(inflater, container, false)
                } else if (!isVerifiedEmail) {
                    binding = ActivityEmailVerify1Binding.inflate(inflater, container, false)
                }
            }
            R.string.privacy_security -> {
                binding = ActivitySecurityBinding.inflate(inflater, container, false)
            }
            R.string.keyword_shielding -> {
                binding = ItemLabelBinding.inflate(inflater, container, false)
            }
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when (binding) {
            is ActivityEmailOkBinding -> {
                (binding as ActivityEmailOkBinding).emailOkImg.setOnClickListener {
                    view.findNavController().popBackStack()
                }
            }
            is ActivityEmailVerify1Binding -> {
                (binding as ActivityEmailVerify1Binding).apply {
                    btn1.setOnClickListener {
                        val action = ItemDetailFragmentDirections.actionItemDetailFragment2ToVerifyFragment()
                        view.findNavController().navigate(action)
                    }
                    btn2.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                    emailVerify1Img.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                }
            }
            is ActivitySecurityBinding -> {
                viewModel.checkPrivacyState(binding as ActivitySecurityBinding)
                (binding as ActivitySecurityBinding).apply {
                    securityImg.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                    stSecurity.setOnCheckedChangeListener { _, isChecked ->
                        if (CheckingToken.IfTokenExist()) {
                            viewModel.changePrivacyState(!isChecked)
                        } else {
                            Toast.makeText(context, "认证信息无效，请先登录。", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            is ItemLabelBinding -> {
                (binding as ItemLabelBinding).apply {
                    viewModel.getShieldList(this)
                    constraintLayout2Label.visibility = View.INVISIBLE
                    screenKeywordImg.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                    val hint1 = SpannableString("请尽可能简单地输入您想屏蔽的关键词")
                    EditTextReaction.EditTextSize(etLabel, hint1, R.dimen.sp_14)
                    constraintLayout1Label.setOnClickListener {
                        constraintLayout1Label.visibility = View.INVISIBLE
                        constraintLayout2Label.visibility = View.VISIBLE
                    }
                    tvAddButton.setOnClickListener {
                        viewModel.postShieldWord(this)
                    }
                    labels.setOnLabelClickListener { _, _, pos ->
                        //label是被点击的标签，data是标签所对应的数据，position是标签的位置。
                        onLabelClick(pos)
                    }
                    etLabel.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                        }

                        override fun onTextChanged(
                            p0: CharSequence?,
                            p1: Int,
                            p2: Int,
                            p3: Int
                        ) {
                            if (etLabel.text.toString().length >= 7) {
                                val mView = View.inflate(
                                    requireContext(), R.layout.dialog_screen, null
                                )
                                val dialog = Dialog(context!!)
                                dialog.setContentView(mView)
                                dialog.window!!.setBackgroundDrawableResource(R.drawable.notice)
                                val no =
                                    mView.findViewById<View>(R.id.tv_dialog_screen_notquit) as TextView
                                no.visibility = View.INVISIBLE
                                val yes =
                                    mView.findViewById<View>(R.id.tv_dialog_screen_quit) as TextView
                                val content =
                                    mView.findViewById<View>(R.id.tv_dialog_screen_content) as TextView
                                content.text = "关键词长度不得超过7个字符，请重新添加！"
                                yes.setOnClickListener { dialog.dismiss() }
                                dialog.show()
                            }
                        }

                        override fun afterTextChanged(p0: Editable?) {}
                    })
                }
            }
            is ActivityRulesBinding -> {
                viewModel.initNorm()
                (binding as ActivityRulesBinding).apply {
                    lawContent.text =
                        Html.fromHtml(viewModel.communityNorm.value, Html.FROM_HTML_MODE_LEGACY)
                    rulesImg.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                }
            }
            is ActivityShareCardBinding -> {
                (binding as ActivityShareCardBinding).apply {
                    shareCardBack.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                }
            }
            is ActivityHoleStarBinding -> {
                val frag = this
                (binding as ActivityHoleStarBinding).apply {
                    myImg.setOnClickListener {
                        hideSoftKeyboard(requireContext(), vpHoleStar)
                        view.findNavController().popBackStack()
                    }
                    vpHoleStar.adapter = object : FragmentStateAdapter(frag) {
                        override fun getItemCount(): Int =
                            viewModel.evalAndAdvFragmentList.value!!.size

                        override fun createFragment(position: Int): Fragment {
                            return viewModel.evalAndAdvFragmentList.value!![position]
                        }
                    }
                    TabLayoutMediator(tlHoleStar, vpHoleStar) { tab, position ->
                        tab.text =
                            context?.getString(viewModel.evalAndAdvNameList.value!![position])
                    }.attach()
                }
            }
            is ActivityAboutBinding -> {
                (binding as ActivityAboutBinding).aboutImg.setOnClickListener {
                    view.findNavController().popBackStack()
                }
            }
            is ActivityUpdateBinding -> {
                viewModel.initUpdateLog()
                (binding as ActivityUpdateBinding).apply {
                    updateImg.setOnClickListener {
                        view.findNavController().popBackStack()
                    }
                    val adapter = UpdateRecycleViewAdapter()
                    viewModel.updateLogList.observe(viewLifecycleOwner) {list ->  adapter.submitList(list)}
                    updateRecyclerview.adapter = adapter
                }
            }
        }
    }

//    private fun initPopUpView(
////        binding: ActivityShareCardBinding
//    ) {
//        var isShow = false
//        val shareToView= LayoutInflater.from(context).inflate(R.layout.ppw_share_to, null)
//        val store: LinearLayout = shareToView.findViewById(R.id.store)
//        val ppwShareTo= PopupWindow(shareToView)
//        ppwShareTo.width = ViewGroup.LayoutParams.MATCH_PARENT
//        ppwShareTo.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        ppwShareTo.animationStyle = R.style.Page2Anim
//        ppwShareTo.isOutsideTouchable = true
//
////        binding.shareCardImg.setOnClickListener {
////            isShow = if (!isShow) {
////                ppwShareTo.showAsDropDown(binding.ppwLocation)
////                true
////            } else {
////                ppwShareTo.dismiss()
////                false
////            }
////        }
//        store.setOnClickListener {
//            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show()
//            ppwShareTo.dismiss()
//        }
//        MediaScannerConnection.scanFile(context, arrayOf("path"), arrayOf("image/jpeg")) { path, _ ->
//            Log.i("shareCard", "onScanCompleted$path") }
//
//    }

    private fun onLabelClick(
        pos: Int
    ) {
        val mView =
            View.inflate(context, R.layout.dialog_screen, null)
        val dialog = Dialog(requireContext())
        dialog.setContentView(mView)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.notice)
        val no = mView.findViewById<View>(R.id.tv_dialog_screen_notquit) as TextView
        val yes = mView.findViewById<View>(R.id.tv_dialog_screen_quit) as TextView
        val content = mView.findViewById<View>(R.id.tv_dialog_screen_content) as TextView
        val text = viewModel.shieldWordList.value!![pos]
        content.text = getString(R.string.dialog_shield_delete).format(
            text.substring(0, text.length - 3)
        )
        no.setOnClickListener {
            dialog.dismiss()
        }
        yes.setOnClickListener {
            viewModel.deleteShieldWord(text, dialog, binding as ItemLabelBinding)
        }
        dialog.show()
    }

    private fun hideSoftKeyboard(
        context: Context,
        view: View
    ) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    class UpdateRecycleViewAdapter : ListAdapter<Update,
            RecyclerView.ViewHolder>(DiffCallback) {
        private val ITEM_TYPE_HEAD = 0
        private val ITEM_TYPE_CONTENT = 1

        inner class UpdateContentViewHolder(
            val binding: UpdateItemBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(log: Update) {
                binding.apply {
                    update = log
                    executePendingBindings()
                }
            }
        }
        inner class UpdateHeadViewHolder(
            val binding: ItemUpdateheadBinding
        ) : RecyclerView.ViewHolder(binding.root) {}

        override fun getItemViewType(position: Int): Int {
            return if (position == 0)
                ITEM_TYPE_HEAD
            else
                ITEM_TYPE_CONTENT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if(viewType == ITEM_TYPE_HEAD)
                UpdateHeadViewHolder(ItemUpdateheadBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else
                UpdateContentViewHolder(UpdateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(position > 0) {
                val item = getItem(position - 1)
                (holder as UpdateContentViewHolder).bind(item)
            }
        }

        override fun getItemCount(): Int {
            return super.getItemCount() + 1
        }
        companion object DiffCallback : DiffUtil.ItemCallback<Update>() {
            override fun areItemsTheSame(oldItem: Update, newItem: Update): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: Update, newItem: Update): Boolean {
                return true
            }
        }
    }

    data class Update(
        val version: String,
        val date: String,
        val detail: String
    ){}
}