package cn.pivotstudio.husthole.moduleb.network.model

import cn.pivotstudio.husthole.moduleb.network.model.Hole
import com.google.gson.annotations.SerializedName

data class DetailForestHole(
    @field:SerializedName("content") var content: String,
    @field:SerializedName("created_timestamp") val createdTimestamp: String,
    @field:SerializedName("follow_num") var followNum: Int,
    @field:SerializedName("hole_id") var holeId: Int,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("is_follow") var followed: Boolean,
    @field:SerializedName("is_mine") val isMine: Boolean,
    @field:SerializedName("is_reply") var replied: Boolean,
    @field:SerializedName("is_thumbup") var liked: Boolean,
    @field:SerializedName("reply_num") var replyNum: Int,
    @field:SerializedName("thumbup_num") var likeNum: Int
) : Hole()

