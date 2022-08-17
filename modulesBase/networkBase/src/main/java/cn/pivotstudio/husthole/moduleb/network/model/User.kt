package cn.pivotstudio.husthole.moduleb.network.model

data class User(
    val role: String = "USER",
    var email: String,
    var password: String
)