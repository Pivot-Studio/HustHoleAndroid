package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ReplyNotice
import cn.pivotstudio.modulec.homescreen.databinding.ItemNoticeBinding
import cn.pivotstudio.modulec.homescreen.ui.fragment.NoticeFragment

class NoticeAdapter(private val context: NoticeFragment) :
    ListAdapter<ReplyNotice, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ReplyNotice> =
            object : DiffUtil.ItemCallback<ReplyNotice>() {
                override fun areItemsTheSame(
                    oldItem: ReplyNotice, newItem: ReplyNotice
                ): Boolean {
                    return oldItem.replyId == newItem.replyId
                }

                override fun areContentsTheSame(
                    oldItem: ReplyNotice, newItem: ReplyNotice
                ): Boolean {
                    return false
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContentViewHolder(
            ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    //内容 ViewHolder
    inner class ContentViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: ReplyNotice) {
            binding.reply = reply
            binding.layoutReply.setOnClickListener { context.navToSpecificHole(reply.holeId.toInt()) }
        }
    }

}