package kr.moare.android.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class TokenResponse(
    val token: String,
    val username: String
)

@Serializable
data class SportHashtagList(
    var sportList: List<String>
)