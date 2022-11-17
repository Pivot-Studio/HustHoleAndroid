package cn.pivotstudio.modulep.hole.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.modulep.hole.databinding.ItemFirstLevelReplyBinding
import cn.pivotstudio.modulep.hole.ui.fragment.SpecificHoleFragment
import cn.pivotstudio.modulep.hole.viewmodel.SpecificHoleViewModel

class RepliesAdapter(
    private val viewModel: SpecificHoleViewModel,
    private val report: (Reply) -> Unit,
    private val onItemClick: (ReplyWrapper) -> Unit,
    private val fragment: SpecificHoleFragment
) : ListAdapter<ReplyWrapper, RepliesAdapter.FirstLevelReplyViewHolder>(DIFF_CALLBACK) {

    companion object {
        const val TAG = "RepliesAdapter"
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ReplyWrapper> =
            object : DiffUtil.ItemCallback<ReplyWrapper>() {
                override fun areItemsTheSame(
                    oldItem: ReplyWrapper,
                    newItem: ReplyWrapper
                ): Boolean {
                    return oldItem.self.replyId == newItem.self.replyId
                }

                override fun areContentsTheSame(
                    oldItem: ReplyWrapper,
                    newItem: ReplyWrapper
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    //保证整个recyclerView每次只有一个举报选项显式
    var lastMoreListCl: ConstraintLayout? = null

    inner class FirstLevelReplyViewHolder(
        private val binding: ItemFirstLevelReplyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(replyWrapper: ReplyWrapper) {
            binding.replyWrapper = replyWrapper
            binding.fragment = fragment
            binding.apply {
                // 控制楼中楼视图框显示
                layoutInnerReply.visibility =
                    if (replyWrapper.innerList.isEmpty()) GONE else VISIBLE

                layoutInnerReply.setOnClickListener {
                    onItemClick(replyWrapper)
                }

                tvReplyContent.setOnLongClickListener { //重写监听器中的onLongClick()方法
                    val cm =
                        BaseApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                    cm.text = binding.tvReplyContent.text.toString()
                    //                    showMsg("内容已复制至剪切板")
                    cm.setPrimaryClip(ClipData.newPlainText(null,binding.tvReplyContent.text.toString()))
                    if (cm.hasPrimaryClip()){
                        cm.primaryClip?.getItemAt(0)?.text
                    }
                    Toast.makeText(it.context,"已将内容复制到剪切板！",Toast.LENGTH_SHORT).show()
                    false
                }

                clReply.setOnClickListener {
                    viewModel.replyTo(replyWrapper.self)
                }

                clReplyThumb.setOnClickListener {
                    viewModel.giveALikeToTheReply(replyWrapper.self)
                }

                ivReplyMore.setOnClickListener {
                    clReplyMoreAction.visibility = VISIBLE
                    if (lastMoreListCl != clReplyMoreAction) {
                        lastMoreListCl?.visibility = GONE
                    }
                    lastMoreListCl = clReplyMoreAction
                }

                clReplyMoreAction.setOnClickListener {
                    if (replyWrapper.self.mine) {
                        viewModel.deleteTheReply(replyWrapper.self)
                    } else {
                        report(replyWrapper.self)
                    }
                    it.visibility = GONE
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FirstLevelReplyViewHolder {
        return FirstLevelReplyViewHolder(
            ItemFirstLevelReplyBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FirstLevelReplyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}