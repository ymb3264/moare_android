package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.network.ProfileAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.FollowObj
import kr.moare.android.entities.Post
import kr.moare.android.entities.Profile
import kr.moare.android.entities.ResponseFollowObj
import kr.moare.android.network.FollowAPI
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserInfoDataStore
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences,
    savedStateHandle: SavedStateHandle,
    private val chatClient: ChatClient
) : ViewModel() {
    private val profileAPI = ProfileAPI()
    private val followAPI = FollowAPI()

    private val username: String = savedStateHandle["username"] ?: ""

    val token = encryptedSharedPreferences.getString("token", "") ?: ""

    val usernameFlow = userInfoDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    val accountsFlow = userInfoDataStore.data.map {
        it[PreferencesKey.ACCOUNTS] ?: setOf()
    }
    val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }

    var myUsername = ""

    val userProfile = MutableStateFlow(
        Profile("", "", "", "", listOf(), "", "", "", "", false)
    )

    val postsList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val postLoading = MutableStateFlow(false)
    val followLoading = MutableStateFlow(false)

    init {
        getUserProfile()
        getUserPosts()
        viewModelScope.launch {
            usernameFlow.collect {
                myUsername = it
                coroutineContext.job.cancel()
            }
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                profileAPI.getUserProfile(username)
            }.onSuccess {
                userProfile.value = it
                Log.d("getUserProfile", "$it")
            }.onFailure {
                Log.d("getUserProfile", "message: $it")
            }
        }
    }

    fun getUserPosts() {
        postLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                profileAPI.getUserPosts(username)
            }.onSuccess {
                postsData = it.toMutableList()
                val num = if (postsData.count() <= 6) {
                    postsData.count()
                } else {
                    postNum
                }

                val newPosts = makeImageRequest(postsData.take(num).toMutableList())

                postLoading.value = false
                postsList.value.clear()
                postsList.value.add(newPosts)
                Log.d("getUserPosts", "$it")
            }.onFailure {
                postLoading.value = false
                Log.d("getUserPosts", "message: $it")
            }
        }
    }

    private fun makeImageRequest(posts: MutableList<Post>): MutableList<Post> {
        var newPosts = mutableListOf<Post>()
        for (post in posts) {
            var imageList = mutableListOf<ImageRequest>()
            for (obj in post.mediaObj) {
                if (obj.type == "image") {
                    val request = ImageRequest.Builder(context)
                        .data(obj.url)
                        .crossfade(true)
                        .build()
                    imageList.add(request)
                }
            }
            post.imageRequest = imageList
            newPosts.add(post)
        }
        return newPosts
    }

    fun createChannel(completion: () -> Unit) {
        chatClient.createChannel(
            channelType = "messaging",
            channelId = userProfile.value.username,
            memberIds = listOf(myUsername, userProfile.value.username),
            extraData = mapOf("name" to "test")
        ).enqueue {
            if (it.isSuccess) {
                completion()
            }
        }
    }

    fun follow(myProfile: Profile) {
        followLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val followObj = FollowObj(
                    myUsername, myProfile.createdAt, myProfile.isTeam,
                    username, userProfile.value.email!!, userProfile.value.createdAt, userProfile.value.isTeam)

                followAPI.follow(token, followObj)
            }.onSuccess { response ->
                followLoading.value = false

                userProfile.value.follower = response.targetFollower
                userProfile.value.teamOrMember = response.targetTeamOrMember

                var updatedProfile = myProfile
                updatedProfile.following = response.following
                updatedProfile.teamOrMember = response.teamOrMember
                userInfoDataStore.edit {
                    it[PreferencesKey.PROFILE] = Json.encodeToString(updatedProfile)
                }

                Log.d("follow", "$response")
            }.onFailure {
                followLoading.value = false
                Log.d("follow", "message: $it")
            }
        }
    }

    fun unfollow(myProfile: Profile) {
        followLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val followObj = FollowObj(
                    myUsername, myProfile.createdAt, myProfile.isTeam,
                    username, userProfile.value.email!!, userProfile.value.createdAt, userProfile.value.isTeam)

                followAPI.unfollow(token, followObj)
            }.onSuccess { response ->
                followLoading.value = false

                userProfile.value.follower = response.targetFollower
                userProfile.value.teamOrMember = response.targetTeamOrMember

                var updatedProfile = myProfile
                updatedProfile.following = response.following
                updatedProfile.teamOrMember = response.teamOrMember
                userInfoDataStore.edit {
                    it[PreferencesKey.PROFILE] = Json.encodeToString(updatedProfile)
                }

                Log.d("follow", "$response")
            }.onFailure {
                followLoading.value = false
                Log.d("follow", "message: $it")
            }
        }
    }
}

















