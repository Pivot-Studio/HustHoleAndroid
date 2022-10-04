package cn.pivotstudio.modulep.hole.ui.adapter

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.modulep.hole.databinding.ItemFirstLevelReplyBinding
import cn.pivotstudio.modulep.hole.viewmodel.SpecificHoleViewModel

class RepliesAdapter(
    val viewModel: SpecificHoleViewModel,
    val onItemClick: (ReplyWrapper) -> Unit
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
            binding.apply {
                layoutInnerReply.setOnClickListener {
                    onItemClick(replyWrapper)
                }

                clReply.setOnClickListener {
                    viewModel.replyTo(replyWrapper.self)
                }

            }

            binding.layoutInnerReply.visibility =
                if (replyWrapper.innerList.isEmpty()) GONE else VISIBLE
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