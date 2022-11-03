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
    val username: String,
    val createdAt: String,
    val yearAndMonth: String,
    var mediaUrl: MediaUrl,
    var content: String,
    var sportHashtag: List<String>,
    var place: String,
    var x: String,
    var y: String,
    @Transient
    var imageRequest: @RawValue List<ImageRequest>? = null,
    @Transient
    var mediaFile: List<File>? = null
) : Parcelable

@Serializable
@Parcelize
data class MediaUrl(
    var image: List<MediaUrlObj>,
    var video: List<MediaUrlObj>
) : Parcelable

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
