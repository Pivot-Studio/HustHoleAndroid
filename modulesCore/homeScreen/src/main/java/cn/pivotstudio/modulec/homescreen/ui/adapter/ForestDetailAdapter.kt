package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestDetailBinding
import cn.pivotstudio.modulec.homescreen.model.DetailForestHole
import cn.pivotstudio.modulec.homescreen.ui.fragment.ForestDetailFragment
import cn.pivotstudio.modulec.homescreen.viewmodel.ForestDetailViewModel

class ForestDetailAdapter(
    private val _context: ForestDetailFragment
) : ListAdapter<DetailForestHole, ForestDetailAdapter.DetailViewHolder>(DIFF_CALLBACK) {
    var lastImageMore: ConstraintLayout? = null // 记录上一次点开三个小点界面的引用

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ForestDetailAdapter.DetailViewHolder {
        return DetailViewHolder(
            ItemForestDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ForestDetailAdapter.DetailViewHolder, position: Int) {
        val detailHole = getItem(position)
        holder.bind(detailHole)
    }

    inner class DetailViewHolder(private val binding: ItemForestDetailBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {

        fun bind(hole: DetailForestHole) {
            binding.hole = hole
            binding.apply {
                textItemForestDetailContent.setOnClickListener {
                    _context.navToSpecificHole(hole.holeId)
                }

                layoutForestDetailReply.setOnClickListener {
                    _context.navToSpecificHoleWithReply(hole.holeId)
                }

                layoutForestDetailThumbup.setOnClickListener {
                    _context.giveALikeToTheHole(hole)
                }

                layoutForestDetailFollow.setOnClickListener {
                    _context.followTheHole(hole)
                }

                // 三个点
                imageItemForestDetailMore.setOnClickListener {
                    layoutItemForestDetailMorelist.visibility = View.VISIBLE
                    if (lastImageMore != layoutItemForestDetailMorelist) {
                        lastImageMore?.visibility = View.GONE
                    }
                    lastImageMore = layoutItemForestDetailMorelist
                }

                layoutItemForestDetailMorelist.setOnClickListener {
                    if (hole.isMine) {
                        _context.deleteTheHole(hole)
                    } else {
                        _context.reportTheHole(hole)
                    }
                    it.visibility = View.GONE
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DetailForestHole> =
            object : DiffUtil.ItemCallback<DetailForestHole>() {
                override fun areItemsTheSame(
                    oldItem: DetailForestHole, newItem: DetailForestHole
                ): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(
                    oldItem: DetailForestHole, newItem: DetailForestHole
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}