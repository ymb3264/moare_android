package kr.moare.android.entities

import android.os.Parcelable
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// response해주는 객체의 파라미터는 다있어야한다 -> x
// response에 없지만 request에 있는경우 기본값이 있으면 response 가능
@Serializable
@Parcelize
data class Profile(
    var userID: String? = null,
    var createdAt: String,
    var chatToken: String? = null,
    var username: String,
    var sportHashtag: List<String>? = null,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    var isTeam: Boolean,
    var follower: List<FollowObj> = listOf(),
    var following: List<FollowObj> = listOf(),
    var teamOrMember: List<FollowObj> = listOf(),
    var likePost: List<String>? = null,
    var blockedUser: List<String>? = null,
    var blockedBy: List<String>? = null,
    @Transient
    var coilImage: @RawValue ImageRequest? = null,
    // var for controlling chat
    var chatID: String? = null
) : Parcelable

@Serializable
@Parcelize
data class FollowObj(
    var userID: String,
    var createdAt: String,
    var profileImage: String,
    var username: String
) : Parcelable

@Serializable
data class RequestUpdateProfile(
    var newProfile: UpdateProfile,
    var beforeProfile: Profile
)

@Serializable
data class UpdateProfile(
    var createdAt: String,
    var username: String,
    var sportHashtag: List<String>?,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    var userHashtag: List<String> = mutableListOf(),
    var shouldUpdateDefaultImage: Boolean = false
)

@Serializable
data class CreateTeamProfile(
    var createdAt: String,
    var username: String,
    var sportHashtag: List<String>?,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    var follow: FollowObj?,
    var isTeam: Boolean = true,
    var userHashtag: List<String> = mutableListOf()
)

@Serializable
data class BlockUserObj(
    var targetUserID: String,
    var targetCreatedAt: String,
    var userProfile: Profile
)