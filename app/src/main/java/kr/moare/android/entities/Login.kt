package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class LoginAccount(
    var email: String,
    var password: String
)