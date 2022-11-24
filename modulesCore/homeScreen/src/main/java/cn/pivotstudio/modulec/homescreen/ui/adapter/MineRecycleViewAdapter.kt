package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.moduleb.libbase.base.app.BaseApplication.Companion.context
import cn.pivotstudio.moduleb.libbase.constant.Constant
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.FragmentMyholeBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineHoleFollowBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineReplyBinding
import cn.pivotstudio.modulec.homescreen.ui.fragment.MyHoleFollowReplyFragment
import cn.pivotstudio.modulec.homescreen.viewmodel.HoleFollowReplyViewModel
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
    val viewModel: HoleFollowReplyViewModel,
    val frag: MyHoleFollowReplyFragment,
    val fragBinding: FragmentMyholeBinding
    ) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    var view: View? = null

    inner class HoleAndFollowViewHolder(
        private val binding: ItemMineHoleFollowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: HoleV2) {
            binding.apply {
                this.hole = hole
                totalView.setOnClickListener {
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(
                            Constant.HOLE_ID,
                            Integer.valueOf(hole.holeId)
                        )
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation(frag.requireActivity(), 2)
                }

                if(type == GET_HOLE) {
                    textView.text = frag.getString(R.string.thumb_follow).format(hole.likeCount.toString(),hole.replyCount.toString())
                    totalView.setOnLongClickListener {
                        val mView = View.inflate(frag.context, R.layout.dialog_delete, null)
                        val dialog = Dialog(frag.requireContext())
                        dialog.setContentView(mView)
                        dialog.window!!.setBackgroundDrawableResource(R.drawable.notice)
                        val no = mView.findViewById<View>(R.id.dialog_delete_tv_cancel) as TextView
                        val yes = mView.findViewById<View>(R.id.dialog_delete_tv_yes) as TextView
                        no.setOnClickListener {
                            dialog.dismiss()
                        }
                        yes.setOnClickListener {
                            viewModel.deleteTheHole(hole)
                            dialog.dismiss()
                        }
                        dialog.show()
                        false
                    }
                }
                
                if(type == GET_FOLLOW)
                    textView.text = frag.getString(R.string.reply_follow).format(hole.replyCount.toString(),hole.followCount.toString())
                executePendingBindings()
            }
        }
    }

    inner class ReplyViewHolder(
        private val binding: ItemMineReplyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reply: Reply) {
            binding.apply {
                this.reply = reply
                myReplyTotal.setOnClickListener{
                    ARouter.getInstance()
                        .build("/hole/HoleActivity")
                        .withInt(
                            Constant.HOLE_ID,
                            Integer.valueOf(reply.holeId)
                        )
                        .withBoolean(Constant.IF_OPEN_KEYBOARD, false)
                        .navigation(frag.requireActivity(), 2)
                    if(view != null) {
                        view!!.visibility = View.GONE
                        view = null
                    }
                }
                myReplyMoreWhat.setOnClickListener{
                    if (view == null) {   //没有删除视图出现
                        myReplyDelete.visibility = View.VISIBLE
                        view = myReplyDelete
                    } else {  //已经有删除视图出现，去重
                        view!!.visibility = View.GONE
                        myReplyDelete.visibility = View.VISIBLE
                        view = myReplyDelete
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
                        view = null
                        dialog.dismiss()
                    }
                    yes.setOnClickListener{
//                        viewModel.deleteHole(dialog, binding, frag.context, layoutPosition)
                        viewModel.deleteReply(reply.replyId)
                        dialog.dismiss()
                        view = null
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
                    view = null
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
            (holder as HoleAndFollowViewHolder).bind(item as HoleV2)
        } else if (type == GET_REPLY) {
            (holder as ReplyViewHolder).bind(item as Reply)
        }
    }



    companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if(oldItem is HoleV2 && newItem is HoleV2)
                 oldItem.holeId == newItem.holeId
            else
                (oldItem as Reply).holeId == (newItem as Reply).holeId
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if(oldItem is HoleV2 && newItem is HoleV2)
                oldItem.lastReplyAt == newItem.lastReplyAt || oldItem.likeCount == newItem.likeCount || oldItem.followCount == newItem.followCount || oldItem.replyCount == newItem.replyCount
            else
                true
        }
    }
}

