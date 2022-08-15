package cn.pivotstudio.modulec.homescreen.model

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName

data class Notice(
    @field:SerializedName("alias") val alias: String,
    @field:SerializedName("content") val content: String,
    @field:SerializedName("created_timestamp") val timeStamp: String,
    @field:SerializedName("hole_id") var holeId: String,
    @field:SerializedName("id") val id: String,
    @field:SerializedName("is_read") var isRead: String,
    @field:SerializedName("reply_local_id") val replyLocalId: String,
    @field:SerializedName("type") val type: String,
) {
    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Notice> =
            object : DiffUtil.ItemCallback<Notice>() {
                override fun areItemsTheSame(
                    oldItem: Notice, newItem: Notice
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Notice, newItem: Notice
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

data class NoticeResponse(
    @field:SerializedName("msg") val notices: List<Notice>?
)