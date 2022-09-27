package cn.pivotstudio.modulep.hole.ui.adapter

import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.ReplyWrapper
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication
import cn.pivotstudio.modulep.hole.R
import cn.pivotstudio.modulep.hole.databinding.ItemFirstLevelReplyBinding
import cn.pivotstudio.modulep.hole.databinding.ItemHoleBinding

class RepliesAdapter(
    val onItemClick: (ReplyWrapper) -> Unit
) : ListAdapter<ReplyWrapper, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val ITEM_TYPE_HOLE = 0
        private const val ITEM_TYPE_REPLY = 1
        private const val ITEM_TYPE_NO_REPLY = 2
        private const val ITEM_TYPE_HOLE_LOADING = 3
        const val TAG = "ReplyListRecyclerViewAdapter"
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ReplyWrapper> =
            object : DiffUtil.ItemCallback<ReplyWrapper>() {
                override fun areItemsTheSame(
                    oldItem: ReplyWrapper,
                    newItem: ReplyWrapper
                ): Boolean {
                    return oldItem.self.holeId == newItem.self.holeId
                }

                override fun areContentsTheSame(
                    oldItem: ReplyWrapper,
                    newItem: ReplyWrapper
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    private var _hole: HoleV2? = null

    //保证整个recyclerView每次只有一个举报选项显式
    var lastMoreListCl: ConstraintLayout? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TYPE_HOLE
        } else if (currentList.isEmpty()) {
            ITEM_TYPE_NO_REPLY
        } else {
            ITEM_TYPE_REPLY
        }
    }

    inner class FirstLevelReplyViewHolder(
        val binding: ItemFirstLevelReplyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(replyWrapper: ReplyWrapper) {
            binding.replyWrapper = replyWrapper
            binding.layoutInnerReply.setOnClickListener {
                onItemClick(replyWrapper)
            }
        }
    }

    inner class HoleViewHolder(
        var binding: ItemHoleBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hole: HoleV2) {
            binding.holeV2 = hole
        }

        init {
            binding.tvHoleContent.setOnLongClickListener(
                OnLongClickListener { //重写监听器中的onLongClick()方法
                    val cm =
                        BaseApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.text = binding.tvHoleContent.text.toString()
//                    showMsg("内容已复制至剪切板")
                    false
                })
            binding.ivHoleMore.setOnClickListener {
                binding.clHoleMoreAction.visibility = View.VISIBLE
                if (lastMoreListCl != null && lastMoreListCl !== binding.clHoleMoreAction) {
                    lastMoreListCl!!.visibility = View.GONE
                }
                lastMoreListCl = binding.clHoleMoreAction
            }
            binding.clHoleMoreAction.setOnClickListener { v: View? ->
                binding.clHoleMoreAction.visibility = View.INVISIBLE
            }
        }
    }

    inner class NoReplyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun getItemCount(): Int {
        val replyLength = currentList.size
        val holeLength = 1
        return replyLength + holeLength
    }

    fun notifyHoleChanged(hole: HoleV2) {
        _hole = hole
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_TYPE_HOLE -> {
                return HoleViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_hole,
                        parent,
                        false
                    )
                )
            }
            ITEM_TYPE_REPLY -> {
                val itemReplyBinding = DataBindingUtil.inflate<ItemFirstLevelReplyBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_first_level_reply,
                    parent,
                    false
                )
                return FirstLevelReplyViewHolder(itemReplyBinding)
            }
            ITEM_TYPE_NO_REPLY -> {
                return NoReplyHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_noreply, parent, false)
                )
            }
            ITEM_TYPE_HOLE_LOADING -> {
                return NoReplyHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_holeloading, parent, false)
                )
            }
            else -> {
                return NoReplyHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_replyloading, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FirstLevelReplyViewHolder) {
            val reply = currentList[position - 1]
            holder.bind(reply)
        } else if (holder is HoleViewHolder) {
            _hole?.let {
                holder.bind(it)
            }
        }
    }
}