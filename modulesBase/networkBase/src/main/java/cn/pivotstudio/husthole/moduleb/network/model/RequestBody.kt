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
}