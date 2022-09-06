package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestBriefBinding

const val TAG = "AllForestItemAdapter"

class AllForestItemAdapter(
    val onItemClick: (String) -> Unit
) : ListAdapter<ForestBrief, AllForestItemAdapter.ForestBriefViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForestBriefViewHolder {
        return ForestBriefViewHolder(
            ItemForestBriefBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestBriefViewHolder, position: Int) {
        val card = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(card.forestId)
        }
        holder.bind(card)
    }

    inner class ForestBriefViewHolder(private val binding: ItemForestBriefBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(brief: ForestBrief) {
            binding.forestBrief = brief
            binding.btnJoinForest.setOnClickListener {
                Log.d(TAG, "bind: Join Forest Btn tapped")
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestBrief> =
            object : DiffUtil.ItemCallback<ForestBrief>() {
                override fun areItemsTheSame(oldItem: ForestBrief, newItem: ForestBrief): Boolean {
                    return oldItem.forestId == newItem.forestId
                }

                override fun areContentsTheSame(oldItem: ForestBrief, newItem: ForestBrief): Boolean {
                    return oldItem == newItem
                }
            }
    }
}