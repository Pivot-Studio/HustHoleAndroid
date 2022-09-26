package cn.pivotstudio.husthole.moduleb.network.model

sealed interface RequestBody {
    data class User(
        val role: String = "USER",
        var email: String,
        var password: String
    ) : RequestBody

    data class ForestId(
        var forestId: String
    ) : RequestBody

    data class HoleId(var holeId: String) : RequestBody

    data class HoleRequest(
        var content: String,
        var forestId: String
    ) : RequestBody

    data class Reply(
        var holeId: String,
        var replyId: String
    )
}