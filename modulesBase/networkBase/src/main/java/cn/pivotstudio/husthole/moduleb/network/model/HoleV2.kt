package cn.pivotstudio.husthole.moduleb.network.model

import com.squareup.moshi.Json
import java.io.Serializable

data class HoleV2(
    /** 内容 */
    @Json(name = "content")
    val content: String,

    /** 发布时间 */
    @Json(name = "creatAt")
    val createAt: String,

    /** 收藏数 */
    @Json(name = "follow")
    var followCount: Long,

    /** 小树林id */
    @Json(name = "forestId")
    val forestId: String,

    /** 小树林名字 */
    @Json(name = "forestName")
    var forestName: String? = null,

    /** 树洞id */
    @Json(name = "holeId")
    val holeId: String,

    /** 是否收藏 */
    @Json(name = "isFollow")
    var isFollow: Boolean,

    /** 是否属于自己 */
    @Json(name = "isMine")
    val isMyHole: Boolean,

    /** 是否评论 */
    @Json(name = "isReply")
    var isReply: Boolean,

    /** 是否点赞 */
    @Json(name = "isThumb")
    var liked: Boolean,

    /** 最新评论时间 */
    @Json(name = "lastReplyAt")
    val lastReplyAt: String,

    /** 评论数 */
    @Json(name = "reply")
    var replyCount: Long,

    /** 点赞数 */
    @Json(name = "thumb")
    var likeCount: Long,

    var forestAvatarUrl: String? = null,

    var isLatestReply: Boolean = true
) : Hole(), Serializable

data class TokenResponse(
    @Json(name = "token") val token: String
)

data class ReplyWrapper (
    /** 子评论总数 */
    val count: String? = null,

    /** 子评论 */
    val innerList: List<Reply>? = null,

    val self: Reply
)

data class Reply (
    /** 树洞内容 */
    val content: String? = null,

    /** 发布时间 */
    @Json(name = "creatAt")
    val createAt: String? = null,

    val hole: HoleV2? = null,

    /** 树洞id */
    val holeId: String? = null,

    /** 点赞数 */
    val likeCount: Long? = null,

    /** 是否我的 */
    val mine: Boolean? = null,

    /** 昵称 */
    val nickname: String,

    val replied: Replied? = null,

    /** 评论id */
    val replyId: String,

    /** 回复的评论id */
    val replyTo: String? = null,

    /** 楼内回复的评论id */
    val replyToInner: String? = null,

    /** 是否点赞 */
    val thumb: Boolean
) : Serializable

/**
 * 被回复的评论
 */
data class Replied(
    val checked: Boolean? = null,

    val content: String? = null,

    val createAt: String,

    val holeId: String,

    val nickname: String? = null,

    val replyDeleted: Boolean? = null,

    val replyId: String? = null,

    val replyMsgId: String? = null,

    val type: ReplyType? = null
)

enum class ReplyType {
    REPLY_TO_HOLE,
    REPLY_TO_REPLY
}