package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class JoinAccount(
    var email: String,
    var createdAt: String,
    var password: String,
    var username: String,
    var sportHashtag: MutableList<String> = mutableListOf()
)

@Serializable
data class EmailCode(
    val serverCode: Int
)