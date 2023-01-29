package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class SearchUserObj(
    var userID: String,
    var createdAt: String,
    var username: String
)