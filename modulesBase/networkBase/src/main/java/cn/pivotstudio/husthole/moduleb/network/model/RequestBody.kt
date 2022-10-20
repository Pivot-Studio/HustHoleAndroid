package cn.pivotstudio.husthole.moduleb.network.model

sealed interface RequestBody {
    data class User(
        val role: String = "USER",
        var email: String,
        var password: String
    ) : RequestBody

    data class ForestId(
        val forestId: String
    ) : RequestBody

    data class HoleId(
        var holeId: String
    ) : RequestBody

    data class HoleRequest(
        val forestId: String?,
        var content: String
    ) : RequestBody

    data class LikeRequest(
        var holeId: String,
        var replyId: String = ""
    )

    data class Reply(
        val holeId: String,
        val replyId: String
    ) : RequestBody

    data class Comment(
        val holeId: String,
        val repliedId: String?,
        var content: String
    ) : RequestBody

    data class ReportRequest(
        /** null / not null 举报理由 */
        val content: String? = null,

        /** 树洞id */
        val holeId: String,

        /** null / not null 评论id */
        val replyId: String? = null,

        /** 举报类型 */
        val type: ReportType
    ) : RequestBody

    data class ScoreRequest(
        val score: Int
    ) : RequestBody

    data class VerifyRequest(
        /** 验证码 */
        val code: String? = null,

        /** 邮箱 */
        val email: String? = null,

        /** 密码，null/not null */
        val password: String? = null,

        /** 是否重置密码 */
        val resetPassword: Boolean? = null
    ) : RequestBody

    data class SendVerifyCode(
        /** 邮箱 */
        val email: String? = null,

        /** 是否重置密码 */
        val resetPassword: Boolean? = null
    ) : RequestBody

    data class VerifyCode(
        /** 验证码 */
        val code: String? = null,

        /** 邮箱 */
        val email: String? = null,

        /** 是否重置密码 */
        val resetPassword: Boolean? = null
    )

    data class FeedBackRequest (
        val content: String? = null,
        val type: Type? = null
    ) : RequestBody
}

/**
 * 举报类型
 */
enum class ReportType {
    BLOODY_VIOLENCE,
    COPYRIGHT_VIOLATIONS,
    ILLEGAL,
    OTHER,
    PERSONAL_ATTACK,
    PLACE_AD,
    PRON,
    SCAM_GAMBLING,
    TROLL
}

/**
 * 反馈类型
 */
enum class Type(val value: String) {
    Suggestion("Suggestion"),
    Bug("Bug"),
    Other("Other");


    companion object {
            fun fromValue(value: Int): Type = when (value) {
            0        -> Suggestion
            1      -> Bug
            2 -> Other
            else         -> throw IllegalArgumentException()
        }
    }
}