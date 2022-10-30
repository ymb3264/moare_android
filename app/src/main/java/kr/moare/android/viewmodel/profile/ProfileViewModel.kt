package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.entities.*
import kr.moare.android.network.ProfileAPI
import kr.moare.android.utils.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = ProfileAPI()
    val storageHelper = StorageHelper()

    val username = encryptedSharedPreferences.getString("username", "") ?: ""

    var profile = MutableStateFlow<UserProfile>(
        UserProfile("", listOf(), "", "", "",
            "", false, listOf(), listOf(), listOf())
    )

    var userProfile = MutableStateFlow<UserProfile>(
        UserProfile("", listOf(), "", "", "",
            "", false, listOf(), listOf(), listOf())
    )

    var teamProfile = MutableStateFlow<TeamProfile>(
        TeamProfile("", "", "", "", "",
    "", listOf(), listOf(), listOf(), listOf())
    )

    var newTeamProfile = TeamProfile("", "", "", "", "",
            "", listOf(), listOf(), listOf(), listOf())

    var attachments = MutableStateFlow<List<Attachment>>(mutableStateListOf())
    var selectedImage = MutableStateFlow<SelectedMedia?>(null)
    var croppedImage = MutableStateFlow<Uri?>(null)

    var postList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    val isRefreshing = MutableStateFlow(false)
    var allPost = mutableListOf<Post>()
    var num = 0

    init {
        viewModelScope.launch {
            getAllPost()
        }
        getUserProfile()
        loadAttachments()
    }

    fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                val username = encryptedSharedPreferences.getString("username", "") ?: ""
                api.getUserProfile(username)
            }.onSuccess {
                profile.value = it
                teamProfile.value.host = it.username
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun makeTeamProfile(profile: TeamProfile) {
        viewModelScope.launch {
            kotlin.runCatching {
                api.makeTeamProfile(profile)
            }.onSuccess {
                teamProfile.value = it
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun loadAttachments() {
        val images = storageHelper.getMediaAttachments(context, true)
        this.attachments.value = images
    }

    fun selectProfileImage(
//        index: Int,
        attachment: Attachment,
        cropImage: (Uri) -> Unit
    ) {
//        val dataSet = attachments.value
//        val newFiles = dataSet.toMutableList()
//        val newItem: Attachment
        val seletedMedia = SelectedMedia(attachment.uri!!, attachment.type!!)

//        if (selectedImage.value == seletedMedia) {
//            selectedImage.value = null
//            newItem = dataSet[index].copy(
//                isSelected = !newFiles[index].isSelected
//            )
//        } else {
            selectedImage.value = seletedMedia
//            newItem = dataSet[index].copy(
//                isSelected = !newFiles[index].isSelected
//            )
//        }

        selectedImage.value?.let { cropImage(it.uri) }

//        newFiles.removeAt(index)
//        newFiles.add(index, newItem)

//        attachments.value = newFiles
    }

    suspend fun getAllPost() {
        kotlin.runCatching {
            isRefreshing.value = true
            postList.value.clear()
            api.getUserPosts(username)
        }.onSuccess {
            allPost = it as MutableList<Post>
            var firstList = mutableListOf<Post>()
            allPost.forEachIndexed { index, post ->
                val image = post.mediaUrl.image
                if (index < 9 && image.isNotEmpty()) {
                    var imageList = mutableListOf<ImageRequest>()
                    for (element in image) {
                        val request = ImageRequest.Builder(context)
                            .data(element.url)
                            .crossfade(true)
                            .build()
                        imageList.add(request)
                    }
                    post.imageRequest = imageList
                    firstList.add(post)
                }
            }
            Log.d("success", "$it")
            postList.value.add(firstList)
            num += 9
            isRefreshing.value = false
            Log.d("post", "${postList.value}")
        }.onFailure {
            isRefreshing.value = false
            Log.d("FAIL", "message: $it")
        }
    }
}