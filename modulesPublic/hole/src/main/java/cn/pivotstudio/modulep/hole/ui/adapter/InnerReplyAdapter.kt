package cn.pivotstudio.modulep.hole.ui.adapter

import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.modulep.hole.databinding.ItemReplyBinding

class InnerReplyAdapter : ListAdapter<Reply, InnerReplyAdapter.ReplyViewHolder>(DIFF_CALLBACK) {
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
        }

        init {
            binding.tvReplyContent.setOnLongClickListener { //重写监听器中的onLongClick()方法
                val cm =
                    BaseApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.text = binding.tvReplyContent.text.toString()
                //                    showMsg("内容已复制至剪切板")
                false
            }
            binding.ivReplyMore.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    binding.clReplyMorelist.visibility = View.VISIBLE
                    if (lastMoreListCl != null && lastMoreListCl !== binding.clReplyMorelist) {
                        lastMoreListCl!!.visibility = View.GONE
                    }
                    lastMoreListCl = binding.clReplyMorelist
                }
            })
            binding.clReplyMorelist.setOnClickListener { v: View? ->
                binding.clReplyMorelist.visibility = View.INVISIBLE
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