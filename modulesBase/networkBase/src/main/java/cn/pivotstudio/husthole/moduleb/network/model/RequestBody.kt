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

    data class Reply(
        val holeId: String,
        val replyId: String
    ) : RequestBody

    data class Comment(
        val holeId: String,
        val replyId: String?,
        var content: String
    ) : RequestBody
}