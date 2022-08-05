package cn.pivotstudio.modulec.homescreen.ui.adapter

import cn.pivotstudio.modulec.homescreen.model.ForestHead
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cn.pivotstudio.modulec.homescreen.databinding.ItemDiscoverMoreBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestHeadBinding

/**
 * @classname: ForestHeadAdapter
 * @description: 小树林头部"已关注小树林"的 Adapter
 * @date: 2022/6/5 2:13
 * @version: 1.0
 * @author: mhh
 */
class JoinedForestsAdapter(
    val onItemClick: (Int) -> Unit,
    val navToAllForest: () -> Unit
) :
    ListAdapter<ForestHead, ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount -> TYPE_DISCOVER_MORE
            else -> TYPE_JOINED_FOREST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_JOINED_FOREST -> JoinedForestViewHolder(
                ItemForestHeadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> DiscoverMoreViewHolder(
                ItemDiscoverMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            itemCount -> (holder as DiscoverMoreViewHolder).itemView.setOnClickListener { }
            else -> {
                val forestHead = getItem(position)
                (holder as JoinedForestViewHolder).apply {
                    bind(forestHead)
                    itemView.setOnClickListener { onItemClick(forestHead.forestId) }
                }
            }
        }

    }

    inner class JoinedForestViewHolder(
        private val binding: ItemForestHeadBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(forestHead: ForestHead?) {
            binding.forestHead = forestHead
        }
    }

    inner class DiscoverMoreViewHolder(
        binding: ItemDiscoverMoreBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val TYPE_DISCOVER_MORE = 1
        const val TYPE_JOINED_FOREST = 0
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestHead> =
            object : DiffUtil.ItemCallback<ForestHead>() {
                override fun areItemsTheSame(oldItem: ForestHead, newItem: ForestHead): Boolean {
                    return oldItem.forestId == newItem.forestId
                }

                override fun areContentsTheSame(oldItem: ForestHead, newItem: ForestHead): Boolean {
                    return oldItem == newItem
                }
            }
    }
}