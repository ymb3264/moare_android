package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class JoinAccount(
    var email: String,
//    val ph: String,
    var createdAt: String,
    var username: String,
    var password: String,
    var sportHashtag: MutableList<String>
)

@Serializable
data class EmailCode(
    val serverCode: Int
)

data class SportItem(
    var sport: String,
    var selected: Boolean = false
)