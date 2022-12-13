package kr.moare.android.entities

import android.net.Uri
import android.os.Parcelable
import coil.request.ImageRequest
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File

@Serializable
@Parcelize
data class Post(
    var username: String,
    var profileImage: String,
    var createdAt: String,
    var yearAndMonth: String,
    var mediaObj: List<MediaObj>,
    var content: String,
    var sportHashtag: List<String>,
    var place: String,
    var x: String,
    var y: String,
    var like: List<String>? = null,
    val updatedAt: String? = null,
    val deleted: Boolean = false,
    @Transient
    var imageRequest: @RawValue List<ImageRequest>? = null,
    @Transient
    var mediaFile: List<File>? = null
) : Parcelable

@Serializable
@Parcelize
data class MediaObj(
    var type: String,
    var url: String
) : Parcelable

@Serializable
data class LikeObj(
    var username: String,
    var postUsername: String,
    var postCreatedAt: String
)

@Serializable
@Parcelize
data class MediaUrlObj(
    var num: Int,
    var url: String
) : Parcelable

data class SelectedMedia(
    var uri: Uri,
    var type: String
)
