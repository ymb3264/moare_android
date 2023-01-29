package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoginAccount(
    var userID: String,
    var password: String
)

@Serializable
data class ResponseForNewPwd(
    var createdAt: String,
    var serverCode: Int
)

@Serializable
data class NewPwdObj(
    var userID: String,
    var createdAt: String,
    var password: String
)