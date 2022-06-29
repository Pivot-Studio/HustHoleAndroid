package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestDetailBinding
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole

class ForestDetailAdapter(
    val onContentClick: (Int) -> Unit,
    val onReplyIconClick: (Int) -> Unit,
    val giveALike: (Int) -> Unit,
    val follow: (Int) -> Unit
) : ListAdapter<DetailForestHole, ForestDetailAdapter.DetailViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ForestDetailAdapter.DetailViewHolder {
        return DetailViewHolder(
            ItemForestDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestDetailAdapter.DetailViewHolder, position: Int) {
        val detailHole = getItem(position)
        holder.bind(detailHole)
    }

    inner class DetailViewHolder(private val binding: ItemForestDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(hole: DetailForestHole) {
            binding.hole = hole
            binding.apply {
                textItemForestDetailContent.setOnClickListener {
                    onContentClick(hole.holeId)
                }

                layoutForestDetailReply.setOnClickListener {
                    onReplyIconClick(hole.holeId)
                }

                layoutForestDetailThumbup.setOnClickListener {
                    giveALike(hole.holeId)
                    notifyItemChanged(adapterPosition)
                }

                layoutForestDetailFollow.setOnClickListener {
                    follow(hole.holeId)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DetailForestHole> =
            object : DiffUtil.ItemCallback<DetailForestHole>() {
                override fun areItemsTheSame(
                    oldItem: DetailForestHole,
                    newItem: DetailForestHole
                ): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(
                    oldItem: DetailForestHole,
                    newItem: DetailForestHole
                ): Boolean {
                    return oldItem.content == newItem.content
                }
            }
    }
}