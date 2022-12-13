package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class FollowObj(
    var username: String,
    var createdAt: String,
    var isTeam: Boolean,
    var targetUsername: String,
    var targetEmail: String,
    var targetCreatedAt: String,
    var targetIsTeam: Boolean
)

@Serializable
data class ResponseFollowObj(
    var following: List<String>,
    var teamOrMember: List<String>,
    var targetFollower: List<String>,
    var targetTeamOrMember: List<String>
)