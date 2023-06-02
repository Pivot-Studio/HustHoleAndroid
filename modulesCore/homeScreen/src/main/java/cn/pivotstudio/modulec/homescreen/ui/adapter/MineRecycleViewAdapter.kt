package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.husthole.moduleb.network.model.AuditType
import cn.pivotstudio.husthole.moduleb.network.model.HoleV2
import cn.pivotstudio.husthole.moduleb.network.model.Reply
import cn.pivotstudio.modulec.homescreen.R
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineHoleFollowBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemMineReplyBinding
import cn.pivotstudio.modulec.homescreen.viewmodel.HoleFollowReplyViewModel
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_FOLLOW
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_HOLE
import cn.pivotstudio.modulec.homescreen.viewmodel.MyHoleFragmentViewModel.Companion.GET_REPLY


/**
 *@classname MineRecycleViewAdapter
 * @description:
 * @date :2022/9/12 18:14
 * @version :1.0
 * @author
 */
class MineRecycleViewAdapter(
    private val viewModel: HoleFollowReplyViewModel,
    private val type: Int
    ) : ListAdapter<Any, RecyclerView.ViewHolder>(DiffCallback) {

    private lateinit var onItemClickListener: OnItemClickListener

    inner class HoleAndFollowViewHolder(
        val binding: ItemMineHoleFollowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(hole: HoleV2) {
            binding.apply {
                this.hole = hole
                showAuditStatus(hole.status, btAuditStatus)
                executePendingBindings()
            }
        }
    }

    inner class ReplyViewHolder(
        val binding: ItemMineReplyBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reply: Reply) {
            binding.apply {
                this.reply = reply
                showAuditStatus(reply.status, btAuditStatus)
                myReplyMoreWhat.setOnClickListener{
                    if (viewModel.view.value == null) {   //没有删除视图出现
                        myReplyDelete.visibility = View.VISIBLE
                        viewModel.view.value = myReplyDelete
                    } else {  //已经有删除视图出现，去重
                        viewModel.view.value!!.visibility = View.GONE
                        myReplyDelete.visibility = View.VISIBLE
                        viewModel.view.value = myReplyDelete
                    }
                }
                executePendingBindings()
            }


        }
    }

    fun showAuditStatus(
        status: AuditType?,
        button: Button
    ) {
        when (status) {
            AuditType.REVIEW_PASSED -> {
                button.visibility = View.GONE
            }
            AuditType.NOT_APPROVED -> {
                button.text = onItemClickListener.getText(R.string.status_not_approved)
                button.setTextColor(
                    onItemClickListener.getColor(R.color.HH_Reminder_Warning)
                )
            }
            AuditType.UNAUDITED, null -> {
                button.text = onItemClickListener.getText(R.string.status_unaudited)
                button.setTextColor(
                    onItemClickListener.getColor(R.color.HH_BandColor_7)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(type == GET_HOLE || type == GET_FOLLOW)
             HoleAndFollowViewHolder(ItemMineHoleFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else
            ReplyViewHolder(ItemMineReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if(type == GET_HOLE || type == GET_FOLLOW) {
            (holder as HoleAndFollowViewHolder).bind(item as HoleV2)
        } else if (type == GET_REPLY) {
            (holder as ReplyViewHolder).bind(item as Reply)
        }
        onItemClickListener.let {
            if(holder is HoleAndFollowViewHolder) {
                holder.binding.apply {
                    totalView.setOnClickListener {
                        onItemClickListener.navigateToHole((item as HoleV2).holeId)
                    }
                    if(type == GET_HOLE) {
                        textView.text = onItemClickListener.getText(R.string.thumb_follow).format(hole?.likeCount.toString(),hole?.replyCount.toString())
                        totalView.setOnLongClickListener {
                            onItemClickListener.onTotalViewLongClick(item as HoleV2)
                            true
                        }
                    }
                    if(type == GET_FOLLOW)
                        textView.text = onItemClickListener.getText(R.string.reply_follow).format(hole?.replyCount.toString(),hole?.followCount.toString())
                }
            } else {
                (holder as ReplyViewHolder).binding.apply {
                    myReplyTotal.setOnClickListener {
                        onItemClickListener.navigateToHole((item as Reply).holeId)
                        if(viewModel.view.value != null) {
                            viewModel.view.value!!.visibility = View.GONE
                            viewModel.view.value = null
                        }
                    }
                    myReplyDelete.setOnClickListener{
                        val dialog = onItemClickListener.getDialog()
                        val no = dialog.findViewById<View>(R.id.dialog_delete_tv_cancel) as TextView
                        val yes = dialog.findViewById<View>(R.id.dialog_delete_tv_yes) as TextView
                        no.setOnClickListener{
                            myReplyDelete.visibility = View.GONE
                            viewModel.view.value = null
                            dialog.dismiss()
                        }
                        yes.setOnClickListener{
                            reply?.replyId?.let { it1 -> viewModel.deleteReply(it1) }
                            dialog.dismiss()
                            viewModel.view.value = null
                            notifyDataSetChanged()
                        }
                        dialog.show()
                    }
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: MineRecycleViewAdapter.OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun navigateToHole(dest: String)

        fun getDialog(): Dialog

        fun onTotalViewLongClick(hole: HoleV2)

        fun getColor(color: Int): Int

        fun getText(strId: Int): String
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

