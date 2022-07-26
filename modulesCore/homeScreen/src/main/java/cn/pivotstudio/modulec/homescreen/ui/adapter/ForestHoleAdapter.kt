package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestBinding
import cn.pivotstudio.modulec.homescreen.model.ForestHole

/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
class ForestHoleAdapter(
    val onContentClick: (Int) -> Unit,
    val onReplyIconClick: (Int) -> Unit,
    val onAvatarClick: (Int) -> Unit,
    val giveALike: (Int) -> Unit,
    val follow: (Int) -> Unit,
    val reportTheHole: (hole: ForestHole) -> Unit,
    val deleteTheHole: (hole: ForestHole) -> Unit
) : ListAdapter<ForestHole, ForestHoleAdapter.ForestHoleViewHolder>(DIFF_CALLBACK) {
    var lastImageMore: ConstraintLayout? = null // 记录上一次点开三个小点界面的引用

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForestHoleViewHolder {
        return ForestHoleViewHolder(
            ItemForestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestHoleViewHolder, position: Int) {
        val forestHole = getItem(position)
        holder.bind(forestHole)
    }

    inner class ForestHoleViewHolder(private val binding: ItemForestBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(forestHole: ForestHole) {
            binding.forestHole = forestHole
            binding.apply {
                layoutItemForestReply.setOnClickListener {
                    onReplyIconClick(forestHole.holeId)
                    notifyItemChanged(adapterPosition)
                }

                textItemForestContent.setOnClickListener {
                    onContentClick(forestHole.holeId)
                }

                imageItemForestAvatar.setOnClickListener {
                    onAvatarClick(forestHole.forestId)
                }

                layoutItemForestThumbsUp.setOnClickListener {
                    giveALike(forestHole.holeId)
                    notifyItemChanged(adapterPosition)
                }

                layoutItemForestFollow.setOnClickListener {
                    follow(forestHole.holeId)
                    notifyItemChanged(adapterPosition)
                }

                // 三个点
                imageItemForestMore.setOnClickListener {
                    layoutItemForestMoreList.visibility = View.VISIBLE
                    if (lastImageMore != layoutItemForestMoreList ) {
                        lastImageMore?.visibility = View.GONE
                    }
                    lastImageMore = layoutItemForestMoreList
                }

                layoutItemForestMoreList.setOnClickListener {
                    if (forestHole.isMine) {
                        deleteTheHole(forestHole)
                    } else {
                        reportTheHole(forestHole)
                    }
                    it.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        const val TAG = "ForestHoleAdapter"
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestHole> =
            object : DiffUtil.ItemCallback<ForestHole>() {
                override fun areItemsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem.liked == newItem.liked &&
                            oldItem.followed == newItem.followed &&
                            oldItem.replyNum == newItem.replyNum
                }
            }
    }
}