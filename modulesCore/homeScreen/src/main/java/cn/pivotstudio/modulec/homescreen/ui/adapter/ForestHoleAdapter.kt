package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestHoleBinding
import cn.pivotstudio.husthole.moduleb.network.model.ForestHole
import cn.pivotstudio.modulec.homescreen.ui.fragment.ForestFragment

/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
class ForestHoleAdapter(
    private val _context: ForestFragment,
) : ListAdapter<ForestHole, ForestHoleAdapter.ForestHoleViewHolder>(DIFF_CALLBACK) {
    var lastImageMore: ConstraintLayout? = null // 记录上一次点开三个小点界面的引用

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForestHoleViewHolder {
        return ForestHoleViewHolder(
            ItemForestHoleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestHoleViewHolder, position: Int) {
        val forestHole = currentList[position]
        holder.bind(forestHole)
    }

    inner class ForestHoleViewHolder(private val binding: ItemForestHoleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(forestHole: ForestHole) {
            binding.forestHole = forestHole
            binding.apply {
                layoutItemForestReply.setOnClickListener {
                    _context.navToSpecificHoleWithReply(forestHole.holeId)
                }

                imageItemForestAvatar.setOnClickListener {
                    _context.navToSpecificForest(forestHole.forestId)
                }

                layoutItemForestThumbsUp.setOnClickListener {
                    _context.giveALikeToTheHole(forestHole)
                }

                layoutItemForestFollow.setOnClickListener {
                    _context.followTheHole(forestHole)
                }

                // 三个点
                imageItemForestMore.setOnClickListener {
                    layoutItemForestMoreList.visibility = View.VISIBLE
                    if (lastImageMore != layoutItemForestMoreList ) {
                        lastImageMore?.visibility = View.GONE
                    }
                    lastImageMore = layoutItemForestMoreList
                }

                layoutItemForestHole.setOnClickListener {
                    _context.navToSpecificHole(forestHole.holeId)
                }

                layoutItemForestMoreList.setOnClickListener {
                    if (forestHole.isMine) {
                        _context.deleteTheHole(forestHole)
                    } else {
                        _context.reportTheHole(forestHole)
                    }
                    it.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        const val TAG = "ForestHoleAdapter"
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ForestHole> =
            object : DiffUtil.ItemCallback<ForestHole>() {
                override fun areItemsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(oldItem: ForestHole, newItem: ForestHole): Boolean {
                    return oldItem == newItem
                }
            }
    }
}