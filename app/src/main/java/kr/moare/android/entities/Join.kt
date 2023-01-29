package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class JoinAccount(
    var userID: String,
    var createdAt: String,
    var password: String,
    var username: String,
    var sportHashtag: MutableList<String> = mutableListOf(),
    var allTermsAgreed: Boolean
)

@Serializable
data class EmailCode(
    val serverCode: Int
)