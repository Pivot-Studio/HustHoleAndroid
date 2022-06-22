package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemAllForestBinding
import cn.pivotstudio.modulec.homescreen.model.ForestCard
import cn.pivotstudio.modulec.homescreen.model.ForestCardList

class AllForestAdapter(
    private val onItemClick: (View) -> Unit
) : ListAdapter<ForestCardList, AllForestAdapter.AllForestViewHolder>(DiffCallback) {

    inner class AllForestViewHolder(
        private var binding: ItemAllForestBinding,
        private val onItemClick: (View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forestCardList: ForestCardList) {
            val adapter =
                AllForestItemAdapter {
                    onItemClick
                }
            binding.itemRecyclerView.adapter = adapter
            adapter.submitList(forestCardList.forests)
        }

    }

    companion object DiffCallback: DiffUtil.ItemCallback<ForestCardList>() {
        override fun areItemsTheSame(oldItem: ForestCardList, newItem: ForestCardList): Boolean {
            return oldItem.forests.last().forestId == newItem.forests.last().forestId
        }

        override fun areContentsTheSame(oldItem: ForestCardList, newItem: ForestCardList): Boolean {
            return oldItem.forests == newItem.forests
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllForestViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AllForestViewHolder(
            ItemAllForestBinding.inflate(layoutInflater, parent, false),
            onItemClick
        )
    }

    override fun onBindViewHolder(holder: AllForestViewHolder, position: Int) {
        val cardList = getItem(position)
        holder.bind(cardList)
    }

    fun submit(dataItems: HashMap<String, List<ForestCard>>) {
        val allTypesOfForestCards = listOf<ForestCardList>().toMutableList()
        dataItems.keys.forEach { type ->
            dataItems[type]?.let { allTypesOfForestCards.add(ForestCardList(it)) }
        }
        this.submitList(allTypesOfForestCards)
    }
}