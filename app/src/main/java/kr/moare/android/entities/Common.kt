package kr.moare.android.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    val message: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val userID: String
)

@Serializable
data class SportHashtagList(
    var sportList: List<String>
)