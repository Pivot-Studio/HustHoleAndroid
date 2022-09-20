package cn.pivotstudio.husthole.moduleb.network.model

import com.squareup.moshi.Json

data class HoleV2(
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
    var followCount: Long,

    /**
     * 小树林id
     */
    @Json(name = "forestId")
    val forestId: String,

    /**
     * 小树林名字
     */
    @Json(name = "forestName")
    var forestName: String? = null,

    /**
     * 树洞id
     */
    @Json(name = "holeId")
    val holeId: String,

    /**
     * 是否收藏
     */
    @Json(name = "isFollow")
    var isFollow: Boolean,

    /**
     * 是否属于自己
     */
    @Json(name = "isMine")
    val isMyHole: Boolean,

    /**
     * 是否评论
     */
    @Json(name = "isReply")
    var isReply: Boolean,

    /**
     * 是否点赞
     */
    @Json(name = "isThumb")
    var liked: Boolean,

    /**
     * 最新评论时间
     */
    @Json(name = "lastReplyAt")
    val lastReplyAt: String,

    /**
     * 评论数
     */
    @Json(name = "reply")
    var replyCount: Long,

    /**
     * 点赞数
     */
    @Json(name = "thumb")
    var likeCount: Long,

    var forestAvatarUrl: String? = null
): Hole()

data class TokenResponse(
    @Json(name = "token") val token: String
)