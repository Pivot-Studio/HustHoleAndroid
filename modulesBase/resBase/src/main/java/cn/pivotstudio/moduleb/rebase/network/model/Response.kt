package cn.pivotstudio.moduleb.rebase.network.model

import com.squareup.moshi.Json
import java.io.Serializable

data class VersionInfo(
    /** 版本号 **/
    val versionId: String,

    /** 版本名 **/
    val versionName: String,

    /** 更新内容**/
    val updateContent: String?,

    /** 下载链接 **/
    val downloadUrl: String

)

data class HoleV2(
    /** 内容 */
    @Json(name = "content")
    val content: String,

    /** 发布时间 */
    @Json(name = "creatAt")
    var createAt: String = "",

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

    /** 审核状态 */
    @Json(name = "status")
    var status: AuditType? = null,

    var forestAvatarUrl: String? = null,

    var isLatestReply: Boolean = true
) : Hole(), Serializable

data class TokenResponse(
    @Json(name = "token") val token: String
)

data class ReplyWrapper(
    /** 子评论总数 */
    val count: String? = null,

    /** 子评论 */
    val innerList: List<Reply>,

    val self: Reply
): Serializable

data class Reply(
    /** 树洞内容 */
    val content: String? = null,

    /** 发布时间 */
    @Json(name = "creatAt")
    val createAt: String = "",

    val hole: HoleDto? = null,

    /** 树洞id */
    val holeId: String = "",

    /** 点赞数 */
    val likeCount: Long = 0,

    /** 是否我的 */
    var mine: Boolean = false,

    /** 昵称 */
    val nickname: String,

    val replied: ReplyDto? = null,

    /** 评论id */
    val replyId: String,

    /** 回复的评论id */
    val replyTo: String? = null,

    /** 楼内回复的评论id */
    val replyToInner: String? = null,

    /** 是否点赞 */
    val thumb: Boolean,

    /** 审核状态 */
    val status: AuditType? = null
) : Serializable

/**
 * 被回复的评论
 */
data class ReplyNotice(
    val checked: Boolean? = null,

    val content: String? = null,

    @Json(name = "creatAt")
    val createAt: String,

    val holeId: String,

    val nickname: String? = null,

    val replyDeleted: Boolean? = null,

    val replyId: String? = null,

    val replyMsgId: String? = null,

    val type: ReplyType? = null
)

/**
 * HoleDto, 更详细的Hole
 */
data class HoleDto(
    var content: String = "",
    val createAt: String? = null,
    val deleteAt: String? = null,
    val followCount: Long? = null,
    val forestId: String? = null,
    val holeId: String? = null,
    val lastReplyAt: String? = null,
    val likeCount: Long? = null,
    val ownerId: String? = null,
    val participant: Long? = null,
    val replyCount: Long? = null,
    val updateAt: String? = null,
    val status: AuditType?= null
)

/**
 * ReplyDto, 更详细的Reply
 */
data class ReplyDto(
    var content: String = "",
    var createAt: String? = null,
    val deleteAt: String? = null,
    val holeId: String? = null,
    val likeCount: Long? = null,
    val nickname: String = "",
    val repliedUserId: String? = null,
    val replyId: String? = null,
    val replyTo: String? = null,
    val replyToInner: String? = null,
    val updateAt: String? = null,
    val userId: String? = null,
    val status: AuditType? = null
)

enum class ReplyType {
    REPLY_TO_HOLE,
    REPLY_TO_REPLY
}

enum class AuditType {
    UNAUDITED,
    REVIEW_PASSED,
    NOT_APPROVED
}

data class ProFile (
    /**
     * 加入时间
     */
    val days: String? = null,

    /**
     * 收藏数
     */
    val follow: String? = null,

    /**
     * 树洞数
     */
    val hole: String? = null,

    /**
     * 评论数
     */
    val reply: String? = null
) {}

