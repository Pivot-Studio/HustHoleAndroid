package cn.pivotstudio.modulec.homescreen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.pivotstudio.modulec.homescreen.databinding.ItemNoticeBinding
import cn.pivotstudio.modulec.homescreen.databinding.ItemNoticeHeaderBinding
import cn.pivotstudio.modulec.homescreen.model.Notice
import cn.pivotstudio.modulec.homescreen.ui.fragment.NoticeFragment

class NoticeAdapter(private val context: NoticeFragment) :
    ListAdapter<Notice, RecyclerView.ViewHolder>(Notice.DIFF_CALLBACK) {

    companion object {
        const val TYPE_NOTICE_HEADER = 0
        const val TYPE_NOTICE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_NOTICE_HEADER
            else -> TYPE_NOTICE_CONTENT
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount().plus(1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NOTICE_HEADER -> HeaderViewHolder(
                ItemNoticeHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> ContentViewHolder(
                ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> {
                if (position > 0) {
                    holder.bind(getItem(position - 1))
                }
            }
        }
    }

    //内容 ViewHolder
    inner class ContentViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.notice = notice
            binding.layoutNotice.setOnClickListener { context.navToSpecificHole(notice.holeId.toInt()) }
        }
    }

    inner class HeaderViewHolder(private val binding: ItemNoticeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}