package kr.moare.android.entities

import android.os.Parcelable
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Parcelize
data class UserProfile(
    var username: String,
    var sport: List<String>,
    var name: String,
    var profileImage: String,
    var content: String,
    var place: String,
    val isTeam: Boolean,
    var follower: List<String>,
    var following: List<String>,
    var teamOrMember: List<String>,
    @Transient
    var coilImage: @RawValue ImageRequest? = null
) : Parcelable

@Serializable
data class TeamProfile(
    var host: String,
    var username: String,
    var name: String,
    var profileImage: String,
    var introduction: String,
    var place: String,
    var sport: List<String>,
    var follower: List<String>,
    var following: List<String>,
    var member: List<String>,
    @Transient
    var coilImage: ImageRequest? = null
)