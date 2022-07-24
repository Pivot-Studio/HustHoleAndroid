package cn.pivotstudio.modulec.homescreen.ui.adapter

import cn.pivotstudio.modulec.homescreen.model.ForestHead
import cn.pivotstudio.modulec.homescreen.ui.adapter.ForestHeadAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestHeadBinding

/**
 * @classname: ForestHeadAdapter
 * @description: 小树林头部"已关注小树林"的 Adapter
 * @date: 2022/6/5 2:13
 * @version: 1.0
 * @author: mhh
 */
class ForestHeadAdapter(val onItemClick: (Int) -> Unit) :
    ListAdapter<ForestHead, ForestHeadAdapter.ForestHoleViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForestHoleViewHolder {
        return ForestHoleViewHolder(
            ItemForestHeadBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestHoleViewHolder, position: Int) {
        val forestHead = getItem(position)
        holder.bind(forestHead)
        holder.itemView.setOnClickListener { onItemClick(forestHead.forestId) }
    }

    inner class ForestHoleViewHolder(private val binding: ItemForestHeadBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(forestHead: ForestHead?) {
            binding.forestHead = forestHead
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestHead> =
            object : DiffUtil.ItemCallback<ForestHead>() {
                override fun areItemsTheSame(oldItem: ForestHead, newItem: ForestHead): Boolean {
                    return false
                }

                override fun areContentsTheSame(oldItem: ForestHead, newItem: ForestHead): Boolean {
                    return false
                }
            }
    }
}