package kr.moare.android.entities

import kotlinx.serialization.Serializable

@Serializable
data class RequestFollowObj(
    var userObj: UserFollowObj,
    var targetObj: UserFollowObj,
    var userIsTeam: Boolean,
    var targetIsTeam: Boolean
)

// 하나로 통합
@Serializable
data class UserFollowObj(
    var userID: String,
    var createdAt: String,
    var profileImage: String,
    var username: String
)

@Serializable
data class ResponseFollowObj(
    var following: List<FollowObj>,
    var teamOrMember: List<FollowObj>?,
    var targetFollower: List<FollowObj>,
    var targetTeamOrMember: List<FollowObj>?
)