package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
    val navToAHoleById: (Int) -> Unit
) : ListAdapter<ForestHole, ForestHoleAdapter.ForestHoleViewHolder>(DIFF_CALLBACK) {
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
        holder.itemView.setOnClickListener { navToAHoleById(forestHole.holeId) }
    }

    inner class ForestHoleViewHolder(private val binding: ItemForestBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bind(forestHole: ForestHole?) {
            binding.forestHole = forestHole
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestHole> =
            object : DiffUtil.ItemCallback<ForestHole>() {
                override fun areItemsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem.content == newItem.content
                }
            }
    }
}