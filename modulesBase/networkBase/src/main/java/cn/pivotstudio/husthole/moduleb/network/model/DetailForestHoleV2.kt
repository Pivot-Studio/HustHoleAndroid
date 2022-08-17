package cn.pivotstudio.husthole.moduleb.network.model

import com.squareup.moshi.Json
import retrofit2.http.Query

data class DetailForestHoleV2(
    /**
     * 内容
     */
    @Json(name = "content")
    val content: String,

    /**
     * 发布时间
     */
    @Json(name = "creatAt")
    val createAt: String,

    /**
     * 收藏数
     */
    @Json(name = "follow")
    val followCount: Long,

    /**
     * 小树林id
     */
    @Json(name = "forestId")
    val forestId: String,

    /**
     * 小树林名字
     */
    @Json(name = "forestName")
    val forestName: String,

    /**
     * 树洞id
     */
    @Json(name = "holeId")
    val holeId: String,

    /**
     * 是否收藏
     */
    @Json(name = "isFollow")
    val isFollow: Boolean,

    /**
     * 是否属于自己
     */
    @Json(name = "isMine")
    val isMyHole: Boolean,

    /**
     * 是否评论
     */
    @Json(name = "isReply")
    val isReply: Boolean,

    /**
     * 是否点赞
     */
    @Json(name = "isThumb")
    val liked: Boolean,

    /**
     * 最新评论时间
     */
    val lastReplyAt: String,

    /**
     * 评论数
     */
    @Json(name = "reply")
    val replyCount: Long,

    /**
     * 点赞数
     */
    @Json(name = "thumb")
    val likeCount: Long
): Hole()

data class TokenResponse(
    @Query("token") val token: String
)