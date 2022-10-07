package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.ForestBrief
import cn.pivotstudio.modulec.homescreen.databinding.ItemAllForestBinding

class AllForestAdapter(
    val onItemClick: (String) -> Unit
) : ListAdapter<Pair<String, List<ForestBrief>>, AllForestAdapter.AllForestViewHolder>(DiffCallback) {

    inner class AllForestViewHolder(
        private var binding: ItemAllForestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forestBriefList: Pair<String, List<ForestBrief>>) {
            val adapter = AllForestItemAdapter(onItemClick)
            binding.itemRecyclerView.adapter = adapter
            binding.type = forestBriefList.first
            adapter.submitList(forestBriefList.second)
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Pair<String, List<ForestBrief>>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, List<ForestBrief>>,
            newItem: Pair<String, List<ForestBrief>>
        ): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, List<ForestBrief>>,
            newItem: Pair<String, List<ForestBrief>>
        ): Boolean {
            return false
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllForestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AllForestViewHolder(
            ItemAllForestBinding.inflate(layoutInflater, parent, false),
        )
    }

    override fun onBindViewHolder(holder: AllForestViewHolder, position: Int) {
        val briefs = getItem(position)
        holder.bind(briefs)
    }

}