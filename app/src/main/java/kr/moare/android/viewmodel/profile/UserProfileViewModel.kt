package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.entities.UserProfile
import kr.moare.android.network.ProfileAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.moare.android.entities.Post
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = ProfileAPI()

    val me = encryptedSharedPreferences.getString("username", "") ?: ""

    private val username: String = savedStateHandle["username"] ?: ""

    var profile = MutableStateFlow<UserProfile>(
        UserProfile("", "", listOf(), "", "", "",
            "", false, listOf(), listOf(), listOf())
    )

    var postList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    var allPost = mutableListOf<Post>()
    var num = 0

    init {
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getUserProfile(username)
            }.onSuccess {
                profile.value = it
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    suspend fun getUserPosts() {
        kotlin.runCatching {
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
            Log.d("post", "${postList.value}")
        }.onFailure {
            Log.d("FAIL", "message: $it")
        }
    }
}