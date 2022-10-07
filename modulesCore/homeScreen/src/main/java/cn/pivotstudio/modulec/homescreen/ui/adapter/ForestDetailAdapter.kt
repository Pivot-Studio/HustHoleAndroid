package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.databinding.ItemForestDetailBinding
import cn.pivotstudio.modulec.homescreen.ui.fragment.ForestDetailFragment

class ForestDetailAdapter(
    private val _context: ForestDetailFragment
) : ListAdapter<HoleV2, ForestDetailAdapter.DetailViewHolder>(DIFF_CALLBACK) {
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

        fun bind(hole: HoleV2) {
            binding.hole = hole
            binding.apply {

                layoutDetailForestItem.setOnClickListener {
                    _context.navToSpecificHole(hole.holeId.toInt())
                }

                layoutForestDetailReply.setOnClickListener {
                    _context.navToSpecificHoleWithReply(hole.holeId.toInt())
                }

                layoutForestDetailThumbup.setOnClickListener {
                    Log.e(TAG, "bind: v2接口暂时不支持点赞")
//                    _context.giveALikeToTheHole(hole)
                }

                layoutForestDetailFollow.setOnClickListener {
                    Log.e(TAG, "bind: v2接口暂时不支持收藏")
//                    _context.followTheHole(hole)
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
                    if (hole.isMyHole) {
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
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HoleV2> =
            object : DiffUtil.ItemCallback<HoleV2>() {
                override fun areItemsTheSame(
                    oldItem: HoleV2, newItem: HoleV2
                ): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(
                    oldItem: HoleV2, newItem: HoleV2
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}