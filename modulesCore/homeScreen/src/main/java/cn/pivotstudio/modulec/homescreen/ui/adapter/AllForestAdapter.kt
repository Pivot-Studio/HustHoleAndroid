package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemAllForestBinding
import cn.pivotstudio.modulec.homescreen.model.ForestCardList

class AllForestAdapter(
    val onItemClick: (Int) -> Unit
) : ListAdapter<Pair<String, ForestCardList>, AllForestAdapter.AllForestViewHolder>(DiffCallback) {

    inner class AllForestViewHolder(
        private var binding: ItemAllForestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forestCardList: Pair<String, ForestCardList>) {
            val adapter = AllForestItemAdapter(onItemClick)
            binding.itemRecyclerView.adapter = adapter
            binding.type = forestCardList.first
            adapter.submitList(forestCardList.second.forests)
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Pair<String, ForestCardList>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, ForestCardList>,
            newItem: Pair<String, ForestCardList>
        ): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, ForestCardList>,
            newItem: Pair<String, ForestCardList>
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
        val cardList = getItem(position)
        holder.bind(cardList)
    }

}