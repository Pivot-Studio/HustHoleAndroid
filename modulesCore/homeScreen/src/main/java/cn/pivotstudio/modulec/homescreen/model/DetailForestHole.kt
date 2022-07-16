package cn.pivotstudio.modulec.homescreen.model

import com.google.gson.annotations.SerializedName

data class DetailForestHole(
    @field:SerializedName("content") val content: String,
    @field:SerializedName("created_timestamp") val createdTimestamp: String,
    @field:SerializedName("follow_num") val followNum: Int,
    @field:SerializedName("hole_id") val holeId: Int,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("is_follow") val isFollow: Boolean,
    @field:SerializedName("is_mine") val isMine: Boolean,
    @field:SerializedName("is_reply") val replied: Boolean,
    @field:SerializedName("is_thumbup") val liked: Boolean,
    @field:SerializedName("reply_num") val replyNum: Int,
    @field:SerializedName("thumbup_num") val likeNum: Int
)

