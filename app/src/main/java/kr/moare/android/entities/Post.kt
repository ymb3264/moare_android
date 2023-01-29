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
    var userID: String,
    var postCreatedAt: String,
    var userCreatedAt: String,
    var username: String,
    var profileImage: String,
    var yearAndMonth: String,
    var mediaObj: List<MediaObj>,
    var content: String,
    var sportHashtag: List<String>,
    var place: String,
    var x: String,
    var y: String,
    var like: List<String> = listOf(),
    val updatedAt: String? = null,
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
data class CreatePost(
    var postCreatedAt: String,
    var userCreatedAt: String,
    var username: String,
    var profileImage: String,
    var yearAndMonth: String,
    var mediaObj: List<MediaObj>,
    var content: String,
    var sportHashtag: List<String>,
    var place: String,
    var x: String,
    var y: String,
    var userHashtag: List<String> = mutableListOf()
)

@Serializable
data class UpdatePost(
    var postCreatedAt: String,
    var updatedAt: String,
    var content: String,
    var sportHashtag: List<String>,
    var place: String,
    var x: String,
    var y: String,
    var userHashtag: MutableList<String> = mutableListOf()
)

data class PostListObj(
    var postList: MutableList<Post>,
    var isLoaded: Boolean
)

data class SelectedMedia(
    var uri: Uri,
    var type: String,
    // index for preselected attachment
    var index: Int? = null
)

@Serializable
data class LikeObj(
    var userID: String,
    var userCreatedAt: String,
    var username: String,
    var postUserID: String,
    var postCreatedAt: String
)
