package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineHoleFollowBinding
import cn.pivotstudio.modulec.homescreen.oldversion.mine.HoleStarReplyActivity
import cn.pivotstudio.modulec.homescreen.viewmodel.GET_FOLLOW
import cn.pivotstudio.modulec.homescreen.viewmodel.GET_HOLE
import com.alibaba.android.arouter.launcher.ARouter


/**
 *@classname MineRecycleViewAdapter
 * @description:
 * @date :2022/9/12 18:14
 * @version :1.0
 * @author
 */
class MineRecycleViewAdapter(val type: Int) :
    ListAdapter<Array<String?>, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HoleAndFollowViewHolder(
        private var binding: ItemMineHoleFollowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: Array<String?>) {
            binding.apply {
                binding.hole = hole
                binding.totalView.setOnClickListener {
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(
                            Constant.HOLE_ID,
                            Integer.valueOf(hole[4].toString())
                        )
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation(it.context as HoleStarReplyActivity, 2)
                }
                if(type == GET_HOLE)
                    binding.textView.text = R.string.thumb_follow.toString().format(hole[8],hole[7])
                if(type == GET_FOLLOW)
                    binding.textView.text = R.string.reply_follow.toString().format(hole[7],hole[3])
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
            return oldItem[1] == newItem[1]
        }

        override fun areContentsTheSame(oldItem: Array<String?>, newItem: Array<String?>): Boolean {
            return oldItem[3] == newItem[3] || oldItem[7] == newItem[7] || oldItem[8] == newItem[8]
        }
    }
}