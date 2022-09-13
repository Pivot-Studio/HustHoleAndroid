package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineHoleFollowBinding


/**
 *@classname MineRecycleViewAdapter
 * @description:
 * @date :2022/9/12 18:14
 * @version :1.0
 * @author
 */
class MineRecycleViewAdapter :
    ListAdapter<Array<String?>, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HoleAndFollowViewHolder(
        private var binding: ItemMineHoleFollowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: Array<String?>) {
            binding.apply {
                binding.hole = hole
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HoleAndFollowViewHolder(
            ItemMineHoleFollowBinding.inflate(
                LayoutInflater.from(parent.context)
            ))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        holder as HoleAndFollowViewHolder
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Array<String?>>() {
        override fun areItemsTheSame(oldItem: Array<String?>, newItem: Array<String?>): Boolean {
            return false
        }

        override fun areContentsTheSame(oldItem: Array<String?>, newItem: Array<String?>): Boolean {
            return false
        }
    }
}