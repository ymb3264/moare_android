package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.network.PostAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.util.Identity.decode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import kr.moare.android.utils.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = PostAPI()

    val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""

    private val usernameFlow = userIdUsernameDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }

    var username = ""
    var myProfile = MutableStateFlow<Profile>(
        Profile(createdAt = "", username = "", sportHashtag = listOf(), name = "", profileImage = "", content = "", place = "", isTeam = false)
    )

    val showSearchView = MutableStateFlow(false)

    val currentLocationFlow = locationDataStore.data.map {
        it[PreferencesKey.CURRENTLOCATION] ?: ""
    }
    private val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }

    val postsList = MutableStateFlow<MutableList<MutableList<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val postLike = MutableStateFlow(listOf<String>())

    val noPost = MutableStateFlow(false)
    val loading = MutableStateFlow(false)
    val shouldSetLocation = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            usernameFlow.collect {
                username = it
            }
        }
        viewModelScope.launch {
            profileFlow.collect {
                if (it.isNotEmpty()) {
                    val profile = Json.decodeFromString<Profile>(it)
                    myProfile.value = profile
                }
            }
        }
        viewModelScope.launch {
            currentLocationFlow.collect { currentLocation ->
                if (currentLocation.isNotEmpty()) {
                    shouldSetLocation.value = false
                    loading.value = true
                    getPosts(Json.decodeFromString(currentLocation))
                } else {
                    // 처음 화면 나타날때 currentLocation이 잠깐동안 empty라 지역설정 메시지가 잠깐 나오는걸 방지하기 위해
                    shouldSetLocation.value = true
                }
            }
        }
    }

    private suspend fun getPosts(currentLocation: UserDefaultLocation) {
        noPost.value = false
        postNum = 6

        val yearMonthFormatter = SimpleDateFormat("yyyy-MM", Locale.KOREA)
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -1)
        val oneDayBeforeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val oneDayBefore = oneDayBeforeFormatter.format(cal.time)

            kotlin.runCatching {
                api.getPosts(token, yearMonthFormatter.format(Date()), currentLocation, username, oneDayBefore)
            }.onSuccess { response ->
                if (response.isEmpty()) {
                    noPost.value = true
                    return
                }

                // 차단한 사용자 게시물 삭제
                var mutableList = response.toMutableList()
                var newPostsData = response.toMutableList()
                myProfile.value.blockedBy?.let { blockedBy ->
                    for (post in mutableList) {
                        val blockedUser = post.userID + "+" + post.userCreatedAt
                        if (blockedBy.contains(blockedUser)) {
                            newPostsData.remove(post)
                        }
                    }
                }

                postsData = newPostsData

                val num = if (postsData.count() <= 6) {
                    postsData.count()
                } else {
                    postNum
                }

//                val newPosts = makeImageRequest(postsData.take(num).toMutableList())

                loading.value = false
                postsList.value.clear()
                postsList.value.add(postsData.take(num).toMutableList())
                Log.d("success", "$response")
            }.onFailure {
                loading.value = false
                Log.d("FAIL", "message: $it")
            }
    }

    private fun loadMorePost() {
        var newPosts = mutableListOf<Post>()
        val count = postsData.count()

        if (count % 60 == 0 && postNum == count) {
            getMorePost()
        } else {
            if (postNum < count && count < postNum+6) {
                newPosts = postsData.subList(postNum, count)
            } else if (count >= postNum+6) {
                newPosts = postsData.subList(postNum, postNum+6)
            } else {
                return
            }

            postsList.value.add(newPosts)
            postNum += 6
        }
    }

    fun getMorePost() {
        viewModelScope.launch {
            currentLocationFlow.collect { currentLocation ->
                kotlin.runCatching {
                    val lastPost = postsData.last()
                    api.getMorePosts(
                        lastPost.yearAndMonth,
                        Json.decodeFromString(currentLocation),
                        lastPost.postCreatedAt)
                }.onSuccess {
                    postsData = (postsData + it).toMutableList()
                    loadMorePost()
                    Log.d("getMorePost", "$it")
                }.onFailure {
                    Log.d("getMorePost", "message: $it")
                }
                coroutineContext.job.cancel()
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

    fun like(listIndex: Int, postIndex: Int, post: Post) {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", username, post.userID, post.postCreatedAt)
                api.like(like)
            }.onSuccess {
                postLike.value = it
                postsList.value[listIndex][postIndex].like = it

                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun unlike(listIndex: Int, postIndex: Int, post: Post) {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", username, post.userID, post.postCreatedAt)
                api.unlike(like)
            }.onSuccess {
                postLike.value = it
                postsList.value[listIndex][postIndex].like = it

                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun reportPost(post: Post, cb: () -> Unit) {
        viewModelScope.launch {
            profileFlow.collect { encodedProfile ->
                if (encodedProfile.isNotEmpty()) {
                    kotlin.runCatching {
                        val profile = Json.decodeFromString<Profile>(encodedProfile)
                        val obj = mapOf(
                            "userID" to post.userID,
                            "createdAt" to post.postCreatedAt,
                            "userCreatedAt" to profile.createdAt
                        )
                        api.reportPost(token, obj)
                    }.onSuccess {
                        if (it.message == "report success") {
                            cb()
                        }
                    }.onFailure {

                    }
                } // if
                coroutineContext.job.cancel()
            } // collect
        } // launch
    }

    fun removeReportedPost(listIndex: Int, postIndex: Int) {
        postsList.value[listIndex].removeAt(postIndex)
    }
}