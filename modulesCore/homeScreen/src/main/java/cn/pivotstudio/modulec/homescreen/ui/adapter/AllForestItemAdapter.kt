package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestCardBinding
import cn.pivotstudio.modulec.homescreen.model.ForestCard

const val TAG = "AllForestItemAdapter"

class AllForestItemAdapter(
    val onItemClick: (Int) -> Unit
) : ListAdapter<ForestCard, AllForestItemAdapter.ForestCardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForestCardViewHolder {
        return ForestCardViewHolder(
            ItemForestCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestCardViewHolder, position: Int) {
        val card = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClick(card.forestId)
        }
        holder.bind(card)
    }

    inner class ForestCardViewHolder(private val binding: ItemForestCardBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(forestCard: ForestCard) {
            binding.forestCard = forestCard
            binding.btnJoinForest.setOnClickListener {
                Log.d(TAG, "bind: Join Forest Btn tapped")
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestCard> =
            object : DiffUtil.ItemCallback<ForestCard>() {
                override fun areItemsTheSame(oldItem: ForestCard, newItem: ForestCard): Boolean {
                    return oldItem.forestId == newItem.forestId
                }

                override fun areContentsTheSame(oldItem: ForestCard, newItem: ForestCard): Boolean {
                    return oldItem.name == newItem.name
                }
            }
    }
}