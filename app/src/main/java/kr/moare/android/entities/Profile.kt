package kr.moare.android.entities

import android.os.Parcelable
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// response해주는 객체의 파라미터는 다있어야한다
// response에 없지만 request에 있는경우 기본값이 있으면 가능
@Serializable
@Parcelize
data class UserProfile(
    var createdAt: String,
    var username: String,
    var sportHashtag: List<String>,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    var isTeam: Boolean,
    var follower: List<String>,
    var following: List<String>,
    var teamOrMember: List<String>,
    @Transient
    var coilImage: @RawValue ImageRequest? = null
) : Parcelable

@Serializable
data class UpdateProfile(
    var createdAt: String,
    var username: String,
    var sportHashtag: List<String>,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
)