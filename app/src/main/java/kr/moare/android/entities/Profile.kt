package kr.moare.android.entities

import android.os.Parcelable
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// response해주는 객체의 파라미터는 다있어야한다
// response에 없지만 request에 있는경우 기본값이 있으면 response 가능
@Serializable
@Parcelize
data class Profile(
    var email: String? = null,
    var createdAt: String,
    var chatToken: String? = null,
    var username: String,
    var sportHashtag: List<String>? = null,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    var isTeam: Boolean,
    var follower: List<String>? = null,
    var following: List<String>? = null,
    var teamOrMember: List<String>? = null,
    @Transient
    var coilImage: @RawValue ImageRequest? = null
) : Parcelable

@Serializable
data class UpdateProfile(
    var createdAt: String,
    var username: String,
    var sportHashtag: List<String>?,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String
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
    var isTeam: Boolean = true,
    var follower: List<String> = listOf(),
    var following: List<String> = listOf(),
    var teamOrMember: List<String> = listOf(),
    var userCreatedAt: String
)