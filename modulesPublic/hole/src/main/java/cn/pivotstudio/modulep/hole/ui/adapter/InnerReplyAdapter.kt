package cn.pivotstudio.modulep.hole.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.modulep.hole.databinding.ItemReplyBinding
import cn.pivotstudio.modulep.hole.ui.activity.HoleActivity
import cn.pivotstudio.modulep.hole.ui.fragment.InnerReplyFragment
import cn.pivotstudio.modulep.hole.viewmodel.InnerReplyViewModel

class InnerReplyAdapter(
    private val viewModel: InnerReplyViewModel,
    private val report: (Reply) -> Unit
) : ListAdapter<Reply, InnerReplyAdapter.ReplyViewHolder>(DIFF_CALLBACK) {
    var lastMoreListCl: ConstraintLayout? = null // 记录上一次点开三个小点界面的引用

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): InnerReplyAdapter.ReplyViewHolder {
        return ReplyViewHolder(
            ItemReplyBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: InnerReplyAdapter.ReplyViewHolder, position: Int) {
        val detailHole = getItem(position)
        holder.bind(detailHole)
    }

    inner class ReplyViewHolder(var binding: ItemReplyBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(reply: Reply) {
            binding.reply = reply
            binding.type = "ReplyToHole"
            binding.apply {
                clReply.setOnClickListener {
                    viewModel.replyTo(reply)
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
                    Toast.makeText(it.context,"已将内容复制到剪切板！", Toast.LENGTH_SHORT).show()
                    false
                }

                ivReplyMore.setOnClickListener {
                    binding.clReplyMoreAction.visibility = View.VISIBLE
                    if (lastMoreListCl != null && lastMoreListCl !== binding.clReplyMoreAction) {
                        lastMoreListCl!!.visibility = View.GONE
                    }
                    lastMoreListCl = binding.clReplyMoreAction
                }

                clReplyMoreAction.setOnClickListener {
                    if (reply.mine) {
                        viewModel.deleteTheReply(reply)
                    } else {
                        report(reply)
                    }
                    binding.clReplyMoreAction.visibility = View.INVISIBLE
                }

                clReplyThumb.setOnClickListener {
                    viewModel.giveALikeTo(reply)
                }

            }

        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Reply> =
            object : DiffUtil.ItemCallback<Reply>() {
                override fun areItemsTheSame(
                    oldItem: Reply, newItem: Reply
                ): Boolean {
                    return oldItem.replyId == newItem.replyId
                }

                override fun areContentsTheSame(
                    oldItem: Reply, newItem: Reply
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}