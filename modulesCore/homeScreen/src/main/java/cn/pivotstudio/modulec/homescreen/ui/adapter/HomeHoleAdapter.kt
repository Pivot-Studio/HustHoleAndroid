package cn.pivotstudio.modulec.homescreen.ui.adapter

import cn.pivotstudio.modulec.homescreen.ui.fragment.HomePageFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.Hole
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.modulec.homescreen.databinding.ItemHomepageholeBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.HomePageViewModel

/**
 * @classname:ForestHoleAdapter
 * @description: 小树林界面大 RecyclerView 的 Adapter
 * @date:2022/5/4 0:26
 * @version:1.0
 * @author: mhh
 */
class HomeHoleAdapter(
    private val viewModel: HomePageViewModel
) : ListAdapter<HoleV2, HomeHoleAdapter.HoleViewHolder>(DIFF_CALLBACK) {
    var lastImageMore: ConstraintLayout? = null // 记录上一次点开三个小点界面的引用
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoleViewHolder {
        return HoleViewHolder(
            ItemHomepageholeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HoleViewHolder, position: Int) {
        val hole = getItem(position)
        holder.bind(hole)
    }

    inner class HoleViewHolder(private val binding: ItemHomepageholeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: HoleV2) {
            binding.homePageHole = hole
            binding.apply {

                clItemHomepageReply.setOnClickListener {
                    onItemClickListener?.navigateWithReply(hole.holeId)
                }

                clItemHomepageThumbup.setOnClickListener {
                    viewModel.giveALikeToTheHole(hole)
                }

                clItemHomepageFollow.setOnClickListener {
                    viewModel.followTheHole(hole)
                }

                // 三个点
                ivItemHomepageMore.setOnClickListener {
                    clItemHomepageMorelist.visibility = View.VISIBLE
                    if (lastImageMore != clItemHomepageMorelist) {
                        lastImageMore?.visibility = View.GONE
                    }
                    lastImageMore = clItemHomepageMorelist
                }

                clItemHomepageMorelist.setOnClickListener {
                    if (hole.isMyHole) {
                        onItemClickListener?.deleteHole(hole)
                    } else {
                        onItemClickListener?.reportHole(hole)
                    }
                    it.visibility = View.GONE
                }

                clItemHomepageFrame.setOnClickListener {
                    onItemClickListener?.navigate(hole.holeId)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun navigateWithReply(holeId: String)

        fun navigate(holeId: String)

        fun deleteHole(hole: HoleV2)

        fun reportHole(hole: HoleV2)
    }

    companion object {
        const val TAG = "ForestHoleAdapter"
        val DIFF_CALLBACK: DiffUtil.ItemCallback<HoleV2> =
            object : DiffUtil.ItemCallback<HoleV2>() {
                override fun areItemsTheSame(
                    oldItem: HoleV2,
                    newItem: HoleV2
                ): Boolean {
                    return oldItem.holeId == newItem.holeId
                }

                override fun areContentsTheSame(
                    oldItem: HoleV2,
                    newItem: HoleV2
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}