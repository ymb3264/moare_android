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
import kr.moare.android.utils.LocationDataStore
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserInfoDataStore
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = PostAPI()

    private val usernameFlow = userInfoDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    var username = ""

    val showSearchView = MutableStateFlow(false)

    val currentLocationFlow = locationDataStore.data.map {
        it[PreferencesKey.CURRENTLOCATION] ?: ""
    }

    val postsList = MutableStateFlow<MutableList<MutableList<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val postLike = MutableStateFlow(listOf<String>())

    val noPost = MutableStateFlow(false)
    val loading = MutableStateFlow(false)

//    var onePost = MutableStateFlow<Post>(
//        Post(username, "", "", "", listOf(), "", listOf(), "", "", "", null, null)
//    )

    init {
        viewModelScope.launch {
            currentLocationFlow.collect { currentLocation ->
                if (currentLocation.isNotEmpty()) {
                    loading.value = true
                    getPosts(Json.decodeFromString(currentLocation))
                }
            }
        }
        viewModelScope.launch {
            usernameFlow.collect {
                username = it
            }
        }
    }

    suspend fun getPosts(currentLocation: UserDefaultLocation) {
        noPost.value = false
        val yearMonthFormatter = SimpleDateFormat("yyyy-MM", Locale.KOREA)

        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -1)
        val oneDayBeforeFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        val oneDayBefore = oneDayBeforeFormatter.format(cal.time)

        kotlin.runCatching {
            api.getPosts(yearMonthFormatter.format(Date()), currentLocation, username, oneDayBefore)
        }.onSuccess {
            if (it.isEmpty()) {
                noPost.value = true
                return
            }

            postsData = it.toMutableList()
            val num = if (postsData.count() <= 6) {
                postsData.count()
            } else {
                postNum
            }

            val newPosts = makeImageRequest(postsData.take(num).toMutableList())

            loading.value = false
            postsList.value.clear()
            postsList.value.add(newPosts)
            Log.d("success", "$it")
        }.onFailure {
            loading.value = false
            Log.d("FAIL", "message: $it")
        }
    }

    fun getMorePost() {
//        var newList = mutableListOf<Post>()
//        val count = allPost.count()
//
//        if (count in (num + 1) until (num + 6)) {
//            for (post in num until count) {
//                var post = allPost[post]
//                val image = post.mediaUrl.image
//                val video = post.mediaUrl.video
//                if (image.isNotEmpty()) {
//                    var imageList = mutableListOf<ImageRequest>()
//                    for (element in image) {
//                        val request = ImageRequest.Builder(context)
//                            .data(element.url)
//                            .crossfade(true)
//                            .build()
//                        imageList.add(request)
//                    }
//                    post.imageRequest = imageList
//                    newList.add(post)
//                }
//            }
//            postList.value.add(newList)
//            num += 6
//        } else if (count >= (num + 6)) {
//            for (post in num until num+6) {
//                var post = allPost[post]
//                val image = post.mediaUrl.image
//                val video = post.mediaUrl.video
//                if (image.isNotEmpty()) {
//                    var imageList = mutableListOf<ImageRequest>()
//                    for (element in image) {
//                        val request = ImageRequest.Builder(context)
//                            .data(element.url)
//                            .crossfade(true)
//                            .build()
//                        imageList.add(request)
//                    }
//                    post.imageRequest = imageList
//                    newList.add(post)
//                }
//            }
//            postList.value.add(newList)
//            num += 6
//        }
//        Log.d("post", "${postList.value[1]}")
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

    fun getPost(number: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getPost(number)
            }.onSuccess {
//                onePost.value = it
                Log.d("getPostSuccess", "$it")
            }.onFailure {
                Log.d("getPostFail", "message: $it")
            }
        }
    }

    fun like(listIndex: Int, postIndex: Int, post: Post) {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj(username, post.username, post.createdAt)
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
                val like = LikeObj(username, post.username, post.createdAt)
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

    fun removeToken() {
        encryptedSharedPreferences.edit().putString("token", "").apply()
        val token = encryptedSharedPreferences.getString("token", "") ?: ""
        Log.d("token", "$token")
    }
}