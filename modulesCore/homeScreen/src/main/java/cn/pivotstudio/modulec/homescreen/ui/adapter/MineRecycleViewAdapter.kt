package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMyholeBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineHoleFollowBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineReplyBinding
import cn.pivotstudio.modulec.homescreen.oldversion.mine.HoleStarReplyActivity
import cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFollowReplyFragment
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_FOLLOW
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_HOLE
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_REPLY
import com.alibaba.android.arouter.launcher.ARouter


/**
 *@classname MineRecycleViewAdapter
 * @description:
 * @date :2022/9/12 18:14
 * @version :1.0
 * @author
 */
class MineRecycleViewAdapter(
    val type: Int,
    val viewModel: MyHoleFragmentViewModel,
    val frag: MyHoleFollowReplyFragment,
    val fragBinding: FragmentMyholeBinding
    ) : ListAdapter<Array<String?>, RecyclerView.ViewHolder>(DiffCallback) {

    inner class HoleAndFollowViewHolder(
        private val binding: ItemMineHoleFollowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: Array<String?>) {
            binding.apply {
                this.hole = hole
                totalView.setOnClickListener {
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(
                            Constant.HOLE_ID,
                            Integer.valueOf(hole[3].toString())
                        )
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation(frag.requireActivity(), 2)
                }
                if(type == GET_HOLE)
                    textView.text = R.string.thumb_follow.toString().format(hole[6],hole[5])
                if(type == GET_FOLLOW)
                    textView.text = R.string.reply_follow.toString().format(hole[5],hole[4])
                executePendingBindings()
            }
        }
    }

    inner class ReplyViewHolder(
        private val binding: ItemMineReplyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var moreCondition: Boolean = false

        fun bind(hole: Array<String?>) {
            binding.apply {
                this.hole = hole
                myReplyTotal.setOnClickListener{
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(
                            Constant.HOLE_ID,
                            Integer.valueOf(hole[3].toString())
                        )
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation(frag.requireActivity(), 2)
                }
                myReplyMoreWhat.setOnClickListener{
                    if (!moreCondition) {
                        myReplyDelete.visibility = View.VISIBLE
                        moreCondition = true
                    } else {
                        myReplyDelete.visibility = View.GONE
                        moreCondition = false
                    }
                }
                myReplyDelete.setOnClickListener{
                    val mView = View.inflate(frag.context, R.layout.dialog_delete, null)
                    val dialog = Dialog(frag.requireContext())
                    dialog.setContentView(mView)
                    dialog.window!!.setBackgroundDrawableResource(R.drawable.notice)
                    val no = mView.findViewById<View>(R.id.dialog_delete_tv_cancel) as TextView
                    val yes = mView.findViewById<View>(R.id.dialog_delete_tv_yes) as TextView
                    no.setOnClickListener{
                        binding.myReplyDelete.visibility = View.GONE
                        moreCondition = false
                        dialog.dismiss()
                    }
                    yes.setOnClickListener{
                        viewModel.deleteHole(dialog, binding, frag.context, layoutPosition)
                        moreCondition = false
                        notifyDataSetChanged()
                    }
                    dialog.show()
                }
                executePendingBindings()
            }
            fragBinding.myHoleRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    binding.myReplyDelete.visibility = View.GONE
                    moreCondition = false
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(type == GET_HOLE || type == GET_FOLLOW)
             HoleAndFollowViewHolder(ItemMineHoleFollowBinding.inflate(LayoutInflater.from(parent.context)))
        else
            ReplyViewHolder(ItemMineReplyBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if(type == GET_HOLE || type == GET_FOLLOW) {
            holder as HoleAndFollowViewHolder
            holder.bind(item)
        } else if (type == GET_REPLY) {
            holder as ReplyViewHolder
            holder.bind(item)
        }
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

